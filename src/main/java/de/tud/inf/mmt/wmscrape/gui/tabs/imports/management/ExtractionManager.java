package de.tud.inf.mmt.wmscrape.gui.tabs.imports.management;

import de.tud.inf.mmt.wmscrape.dynamicdb.ColumnDatatype;
import de.tud.inf.mmt.wmscrape.dynamicdb.stock.StockColumnRepository;
import de.tud.inf.mmt.wmscrape.dynamicdb.stock.StockTableManager;
import de.tud.inf.mmt.wmscrape.dynamicdb.transaction.TransactionColumnRepository;
import de.tud.inf.mmt.wmscrape.dynamicdb.transaction.TransactionTableManager;
import de.tud.inf.mmt.wmscrape.dynamicdb.watchlist.WatchListColumnRepository;
import de.tud.inf.mmt.wmscrape.dynamicdb.watchlist.WatchListTableManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.dbdata.data.Stock;
import de.tud.inf.mmt.wmscrape.gui.tabs.dbdata.data.StockRepository;
import de.tud.inf.mmt.wmscrape.gui.tabs.depots.data.Depot;
import de.tud.inf.mmt.wmscrape.gui.tabs.depots.data.DepotRepository;
import de.tud.inf.mmt.wmscrape.gui.tabs.imports.controller.ImportTabController;
import de.tud.inf.mmt.wmscrape.gui.tabs.imports.data.ExcelCorrelation;
import javafx.concurrent.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Lazy
public class ExtractionManager {
    // POI: index 0, EXCEL Index: 1
    private final static int OFFSET = 1;
    private static final List<String> ignoreInStockData = List.of("datum", "isin", "wkn", "name", "typ", "r_par");
    private static final List<String> ignoreInWatchListData = List.of("datum", "isin");

    @Autowired
    private ImportTabManager importTabManager;
    @Autowired
    private ImportTabController importTabController;
    @Autowired
    private DbTransactionManager dbTransactionManager;
    @Autowired
    private StockTableManager stockTableManager;
    @Autowired
    private WatchListTableManager watchListTableManager;
    @Autowired
    private ParsingManager parsingManager;
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private DepotRepository depotRepository;
    @Autowired
    private TransactionColumnRepository transactionColumnRepository;
    @Autowired
    private TransactionTableManager transactionTableManager;
    @Autowired
    private StockColumnRepository stockColumnRepository;
    @Autowired
    private WatchListColumnRepository watchListColumnRepository;

    private final HashMap<String, HashMap<String, String>> potentialNewStocks = new HashMap<>();

    /**
     * defines the order of the import processes
     *
     * @param task the task the process is running in. only used for reacting to task cancellation
     * @return integer value containing error information
     */
    public int startDataExtraction(Task<Integer> task) {

        int stockExtractionResult = extractStockData(task);
        // 0-OK, -1-SilentError -> below other error
        // don't break execution if silent
        // only silent and ok can pass
        if (stockExtractionResult < -1) return stockExtractionResult;
        if (task.isCancelled()) return -3;

        int watchListExtractionResult = extractWatchListData(task);
        // 0-OK, -1-SilentError -> below other error
        // don't break execution if silent
        // only silent and ok can pass
        if (watchListExtractionResult < -1) return watchListExtractionResult;
        if (task.isCancelled()) return -3;

        int transactionExtractionResult = extractTransactionData(task);
        // 0-OK, -1-SilentError -> below other error
        // only ok can pass
        if (transactionExtractionResult != 0) return transactionExtractionResult;
        if(task.isCancelled()) return -3;

        // transaction ok but stock had a silent error
        if (stockExtractionResult == -1) return -1;
        return 0;
    }

    /**
     * does the complete stockdata import procedure including creating statements, filling them, executing them
     * and creating {@link de.tud.inf.mmt.wmscrape.gui.tabs.dbdata.data.Stock} entities
     *
     * @param task the task the process is running in. only used for reacting to task cancellation
     * @return error information as integer value
     */
    private int extractStockData(Task<Integer> task) {

        importTabManager.addToLog("##### Start Stammdaten-Import #####\n");

        // execution is not stopped at a silent error but a log message is added
        boolean silentError = false;
        Connection connection = stockTableManager.getConnection();
        HashMap<String, PreparedStatement> statements = dbTransactionManager.createDataStatements(
                                                            stockTableManager, stockColumnRepository, connection);
        potentialNewStocks.clear();

        var excelSheetRows = parsingManager.getExcelSheetRows();
        var stockColumnRelations = importTabController.getStockDataCorrelations();
        var selected = parsingManager.getSelectedStockDataRows();


        if (statements == null) return -2;

        // go through all rows
        for (int row : excelSheetRows.keySet()) {
            if(task.isCancelled()) return -3;

            // skip rows if not selected
            if (!(selected.get(row).get())) continue;

            // the columns for one row
            ArrayList<String> rowData = excelSheetRows.get(row);

            int isinCol = parsingManager.getColNrByName("isin", stockColumnRelations);
            int dateCol = parsingManager.getColNrByName("datum", stockColumnRelations);

            // check if isin valid
            String isin = rowData.get(isinCol);
            if (isin == null || isin.isBlank() || isin.length() >= 50) {
                silentError = true;
                importTabManager.addToLog("ERR:\t\tIsin der Zeile " + (row+OFFSET) + " leer oder länger als 50 Zeichen. ->'" + isin + "'");
                continue;
            }

            // validate stockdata date value
            String date = rowData.get(dateCol);
            if (date == null || date.isBlank() || notMatchingDataType(ColumnDatatype.DATE, date)) {
                importTabManager.addToLog("ERR:\t\tStammdaten-Datum '"+date+"' der Zeile "+(row+OFFSET)+" ist fehlerhaft oder leer.");
                silentError = true;
                continue;
            }

            // pick one column per relation from row
            for (ExcelCorrelation correlation : stockColumnRelations) {
                if(task.isCancelled()) return -3;

                String dbColName = correlation.getDbColTitle();

                int correlationColNumber = correlation.getExcelColNumber();
                String colData;

                // -1 is default and can't be set another way meaning it's not set
                if (correlationColNumber == -1) {
                    //addToLog("INFO:\tDie Spalte '" + dbColName +"' hat keine Zuordnung.");
                    colData = null;
                } else {
                    colData = rowData.get(correlationColNumber);
                    if (colData.isBlank()) colData = null;
                }

                ColumnDatatype datatype = correlation.getDbColDataType();

                if (datatype == null) {
                    silentError = true;
                    importTabManager.addToLog("ERR:\t\tDer Datenbankspalte " + dbColName
                            + " ist kein Datentyp zugeordnet.");
                    continue;
                }

                if (notMatchingDataType(datatype, colData)) {
                    silentError = true;
                    importTabManager.addToLog("ERR:\t\tDer Datentyp der Zeile " + (row+OFFSET) + " in der Spalte '" + correlation.getExcelColTitle() +
                            "', stimmt nicht mit dem der Datenbankspalte " + dbColName + " vom Typ " + datatype.name() +
                            " überein. Zellendaten: '" + colData + "'");
                    continue;
                }

                // continue bcs the key value is not inserted with a prepared statement
                // isin wkn name typ
                if (ignoreInStockData.contains(dbColName)) {
                    //if(colData == null) continue; uncomment if stock values should not contain null

                    HashMap<String,String> newStocksData;
                    newStocksData = potentialNewStocks.getOrDefault(isin, new HashMap<>());
                    newStocksData.put(dbColName, colData);
                    potentialNewStocks.put(isin,newStocksData);
                    continue;
                }

                // statement exists bcs if there is an error at statement creation
                // the program does not reach this line
                PreparedStatement statement = statements.getOrDefault(dbColName, null);

                if (statement == null) {
                    silentError = true;
                    importTabManager.addToLog("ERR:\t\tSql-Statement für die Spalte '" + correlation.getExcelColTitle() +
                            "' nicht gefunden");
                    continue;
                }

                silentError |= dbTransactionManager.fillStockStatementAddToBatch(isin, date, statement, colData, datatype);
            }

        }

        // create stocks if not existing based on prior imported stock data
        // only returns false if the task is canceled
        if(!createMissingStocks(task)) return -3;


        silentError |= dbTransactionManager.executeStatements(connection, statements);
        importTabManager.addToLog("\n##### Ende Stammdaten-Import #####\n");

        if (silentError) return -1;
        return 0;
    }

    /**
     * does the complete watch list data import procedure including creating statements, filling them and executing them
     *
     * @param task the task the process is running in. only used for reacting to task cancellation
     * @return error information as integer value
     */
    private int extractWatchListData(Task<Integer> task) {

        importTabManager.addToLog("##### Start Watch-Liste-Import #####\n");

        // execution is not stopped at a silent error but a log message is added
        boolean silentError = false;
        Connection connection = watchListTableManager.getConnection();
        HashMap<String, PreparedStatement> statements = dbTransactionManager.createDataStatements(
                watchListTableManager, watchListColumnRepository, connection);

        var excelSheetRows = parsingManager.getExcelSheetRows();
        var watchListColumnRelations = importTabController.getWatchListCorrelations();
        var selected = parsingManager.getSelectedWatchListDataRows();


        if (statements == null) return -2;

        // go through all rows
        for (int row : excelSheetRows.keySet()) {
            if(task.isCancelled()) return -3;

            // skip rows if not selected
            if (!(selected.get(row).get())) continue;

            // the columns for one row
            ArrayList<String> rowData = excelSheetRows.get(row);

            int isinCol = parsingManager.getColNrByName("isin", watchListColumnRelations);
            int dateCol = parsingManager.getColNrByName("datum", watchListColumnRelations);

            // check if isin valid
            String isin = rowData.get(isinCol);
            if (isin == null || isin.isBlank() || isin.length() >= 50) {
                silentError = true;
                importTabManager.addToLog("ERR:\t\tIsin der Zeile " + (row+OFFSET) + " leer oder länger als 50 Zeichen. ->'" + isin + "'");
                continue;
            }

            // validate stockdata date value
            String date = rowData.get(dateCol);
            if (date == null || date.isBlank() || notMatchingDataType(ColumnDatatype.DATE, date)) {
                importTabManager.addToLog("ERR:\t\tDatum '"+date+"' der Zeile "+(row+OFFSET)+" ist fehlerhaft oder leer.");
                silentError = true;
                continue;
            }

            // pick one column per relation from row
            for (ExcelCorrelation correlation : watchListColumnRelations) {
                if(task.isCancelled()) return -3;

                String dbColName = correlation.getDbColTitle();

                int correlationColNumber = correlation.getExcelColNumber();
                String colData;

                // -1 is default and can't be set another way meaning it's not set
                if (correlationColNumber == -1) {
                    //addToLog("INFO:\tDie Spalte '" + dbColName +"' hat keine Zuordnung.");
                    colData = null;
                } else {
                    colData = rowData.get(correlationColNumber);
                    if (colData.isBlank()) colData = null;
                }

                ColumnDatatype datatype = correlation.getDbColDataType();

                if (datatype == null) {
                    silentError = true;
                    importTabManager.addToLog("ERR:\t\tDer Datenbankspalte " + dbColName
                            + " ist kein Datentyp zugeordnet.");
                    continue;
                }

                if (notMatchingDataType(datatype, colData)) {
                    silentError = true;
                    importTabManager.addToLog("ERR:\t\tDer Datentyp der Zeile " + (row+OFFSET) + " in der Spalte '" + correlation.getExcelColTitle() +
                            "', stimmt nicht mit dem der Datenbankspalte " + dbColName + " vom Typ " + datatype.name() +
                            " überein. Zellendaten: '" + colData + "'");
                    continue;
                }

                // continue bcs the key value is not inserted with a prepared statement
                // isin, datum
                if (ignoreInWatchListData.contains(dbColName)) {
                    continue;
                }

                // statement exists bcs if there is an error at statement creation
                // the program does not reach this line
                PreparedStatement statement = statements.getOrDefault(dbColName, null);

                if (statement == null) {
                    silentError = true;
                    importTabManager.addToLog("ERR:\t\tSql-Statement für die Spalte '" + correlation.getExcelColTitle() +
                            "' nicht gefunden");
                    continue;
                }

                silentError |= dbTransactionManager.fillStockStatementAddToBatch(isin, date, statement, colData, datatype);
            }

        }

        silentError |= dbTransactionManager.executeStatements(connection, statements);
        importTabManager.addToLog("\n##### Ende Watch-Liste-Import #####\n");

        if (silentError) return -1;
        return 0;
    }

    /**
     * does the complete depot transaction import procedure including creating statements, filling them, executing them
     * and creating {@link de.tud.inf.mmt.wmscrape.gui.tabs.depots.data.Depot} entities
     *
     * @param task the task the process is running in. only used for reacting to task cancellation
     * @return error information as integer value
     */
    private int extractTransactionData(Task<Integer> task) {
        importTabManager.addToLog("##### Start Transaktions Import #####\n");

        // execution is not stopped at a silent error but a log message is added
        boolean silentError = false;

        Map<String, Stock> knownStockIsins = getKnownStocks();
        Set<String> knownDepots = getDepotNames();


        Connection connection = stockTableManager.getConnection();
        HashMap<String, PreparedStatement> statements = dbTransactionManager.createDataStatements(
                                                        transactionTableManager,transactionColumnRepository,connection);

        var excelSheetRows = parsingManager.getExcelSheetRows();
        var transactionColumnRelations = importTabController.getTransactionCorrelations();
        var selected = parsingManager.getSelectedTransactionRows();


        if (statements == null) return -2;

        // transaction keys
        int isinCol = parsingManager.getColNrByName("wertpapier_isin", transactionColumnRelations);
        int dateCol = parsingManager.getColNrByName("transaktions_datum", transactionColumnRelations);
        int depotNameCol = parsingManager.getColNrByName("depot_name", transactionColumnRelations);

        // go through all rows
        for (int row : excelSheetRows.keySet()) {
            if(task.isCancelled()) return -3;

            // skip rows if not selected
            if (!selected.get(row).get()) continue;

            // the columns for one row
            ArrayList<String> rowData = excelSheetRows.get(row);

            String depotName = rowData.get(depotNameCol);
            if (depotName == null || depotName.isBlank() || depotName.length() >= 500) {
                importTabManager.addToLog("ERR:\t\tDepotname der Zeile "+(row+OFFSET)+" fehlerhaft oder leer. Wert: '"
                        + depotName + "' ");
                silentError = true;
                continue;
            }

            String isin = rowData.get(isinCol);
            if (isin == null || isin.isBlank() || isin.length() >= 50) {
                importTabManager.addToLog("ERR:\t\tIsin der Zeile "+(row+OFFSET)+
                        " fehlerhaft, leer oder länger als 50 Zeichen. Wert: '"+isin+"'");
                silentError = true;
                continue;
            }

            String date = rowData.get(dateCol);
            if (date == null || date.isBlank() || notMatchingDataType(ColumnDatatype.DATE, date)) {
                importTabManager.addToLog("ERR:\t\tTransaktionsdatum '"+date+"' der Zeile "+(row+OFFSET)+" ist fehlerhaft oder leer.");
                silentError = true;
                continue;
            }

            // stocks are created beforehand
            if (!knownStockIsins.containsKey(isin)) {
                stockRepository.saveAndFlush(new Stock(isin, null, null,null,-1));
                importTabManager.addToLog("WARN:\tFür das Wertpapier der Transaktion aus Zeile "+(row+OFFSET)+
                        " wurden zuvor keine Stammdaten importiert. \n\t\t" +
                        "Ein neues Wertpapier mit der ISIN: '"+isin+"' wurde angelegt, um die Transaktionsdaten importieren zu können. \n\t\t" +
                        "Die restlichen Wertpapierdaten (Name, WKN, R-Par, Typ) können im Daten-Bereich ergänzt werden.");
                silentError = true;
            }

            // search if the depot already exists or creates a new one
            if (!knownDepots.contains(depotName)) {
                importTabManager.addToLog("INFO:\tErstelle Depot mit dem Namen: " + depotName);
                depotRepository.saveAndFlush(new Depot(depotName));
                knownDepots.add(depotName);
            }

            for (ExcelCorrelation correlation : transactionColumnRelations) {
                if(task.isCancelled()) return -3;

                String dbColName = correlation.getDbColTitle();

                // already set before don't do it again
                if (dbColName.equals("depot_name") || dbColName.equals("transaktions_datum") || dbColName.equals("wertpapier_isin")) {
                    continue;
                }

                int correlationColNumber = correlation.getExcelColNumber();
                String colData;

                // -1 is default and can't be set another way meaning it's not set
                if (correlationColNumber == -1) {
                    colData = null;
                } else {
                    colData = rowData.get(correlationColNumber);
                    // change to null to override possible existing values
                    if (colData.isBlank()) colData = null;
                }

                ColumnDatatype colDatatype = correlation.getDbColDataType();

                if (notMatchingDataType(colDatatype, colData)) {
                    importTabManager.addToLog("ERR:\t\tDer Wert der Zelle in der Zeile: " + row + " Spalte: '"
                            + correlation.getExcelColTitle() + "' hat nicht den passenden Datentyp für '"
                            + dbColName + "' vom Typ '" + colDatatype + "'. Wert: '" + colData + "'");
                    silentError = true;
                    continue;
                }

                PreparedStatement statement = statements.getOrDefault(dbColName, null);

                if (statement == null) {
                    silentError = true;
                    importTabManager.addToLog("ERR:\t\tSql-Statment für die Spalte '" + correlation.getExcelColTitle() +
                            "' nicht gefunden");
                    continue;
                }

                silentError |= dbTransactionManager.fillTransactionStatementAddToBatch(depotName, isin, date, statement, colData, colDatatype);
            }
        }

        silentError |= dbTransactionManager.executeStatements(connection, statements);

        importTabManager.addToLog("\n##### Ende Transaktions Import #####\n");
        if (silentError) return -1;
        return 0;
    }

    /**
     * evaluates if the text data matches the given datatype
     *
     * @param colDatatype the datatype to test against
     * @param colData the text data
     * @return false if the datatype matches
     */
    private boolean notMatchingDataType(ColumnDatatype colDatatype, String colData) {
        if (colDatatype == null) {
            return true;
        } else if (colData == null) {
            // null is valid in order to override values that may have been set in the wrong column
            return false;
        } else if (colDatatype == ColumnDatatype.INTEGER && colData.matches("^[\\-+]?[0-9]+(\\.0{6})?$")) {
            // normal format would be "^-?[0-9]+$" but because of
            // String.format("%.6f", cell.getNumericCellValue()).replace(",",".");
            // 6 zeros are added to int
            return false;
        } else if (colDatatype == ColumnDatatype.DOUBLE && colData.matches("^[\\-+]?[0-9]+([.]?[0-9]+)?$")) {
            return false;
        } else if (colDatatype == ColumnDatatype.DATE && colData.matches("^[1-9][0-9]{3}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}$")) {
            // yyyy-MM-dd HH:mm:ss
            return false;
        } else return colDatatype != ColumnDatatype.TEXT;
    }


    /**
     * creates missing {@link {@link de.tud.inf.mmt.wmscrape.gui.tabs.dbdata.data.Stock} entities.
     * note that these have to be created before the data can be inserted, otherwise the constaint will not be fullfilled.
     * constraint between the "wertpapier" table and the "wertpapier_stammdaten" table.
     * the latter refers to entities in the first.
     * 
     * PS: existing stocks values are now also overridden as there is no longer a check if a stock exists.
     *      the reason is to update the r-par value
     *
     * @param task the task the process is running in. only used for reacting to task cancellation
     */
    private boolean createMissingStocks(Task<Integer> task) {
        Map<String, Stock> knownStocks = getKnownStocks();

        for(var ks : potentialNewStocks.entrySet()) {
            if(task.isCancelled()) return false;

            String wkn = ks.getValue().getOrDefault("wkn", null);
            String name = ks.getValue().getOrDefault("name", null);
            String typ = ks.getValue().getOrDefault("typ", null);
            Integer r = getNullInteger(ks.getValue().getOrDefault("r_par", null));
            Stock s;

            if(knownStocks.containsKey(ks.getKey())) {
                // update values
                s = knownStocks.get(ks.getKey());
                s.set_wkn(wkn);
                s.set_name(name);
                s.set_stockType(typ);
                s.set_sortOrder(r);
            } else {
                s = new Stock(ks.getKey(), wkn, name, typ, r);
            }
            stockRepository.save(s);
        }

        stockRepository.flush();
        return true;
    }

    private Map<String, Stock> getKnownStocks() {
        Map<String, Stock> map = new HashMap<>();
        stockRepository.findAll().forEach(s -> map.put(s.getIsin(), s));
        return map;
    }

    private Set<String> getDepotNames() {
        return depotRepository.findAll().stream().map(Depot::getName).collect(Collectors.toSet());
    }

    /**
     *
     * @param value the value fetched from the Excel sheet
     * @return null if the input is null or empty
     */
    private Integer getNullInteger(String value) {
        if(value == null || value.isBlank()) return null;
        return Double.valueOf(value).intValue();
    }
}