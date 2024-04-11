package de.tud.inf.mmt.wmscrape.gui.tabs.dbdata.management;

import de.tud.inf.mmt.wmscrape.dynamicdb.ColumnDatatype;
import de.tud.inf.mmt.wmscrape.dynamicdb.DbTableColumn;
import de.tud.inf.mmt.wmscrape.dynamicdb.DbTableColumnRepository;
import de.tud.inf.mmt.wmscrape.dynamicdb.VisualDatatype;
import de.tud.inf.mmt.wmscrape.dynamicdb.transaction.TransactionColumn;
import de.tud.inf.mmt.wmscrape.dynamicdb.transaction.TransactionColumnRepository;
import de.tud.inf.mmt.wmscrape.dynamicdb.transaction.TransactionTableManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.dbdata.data.CustomCell;
import de.tud.inf.mmt.wmscrape.gui.tabs.dbdata.data.CustomRow;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TransactionDataManager extends DataManager {

    @Autowired
    TransactionTableManager transactionTableManager;
    @Autowired
    TransactionColumnRepository transactionColumnRepository;

    @Override
    protected PreparedStatement prepareUpdateStatements(String colName, Connection connection) throws SQLException{
        String sql = "INSERT INTO `"+ dbTableManger.getTableName()+"` (`"+colName+
                "`, transaktions_datum, depot_name, wertpapier_isin) VALUES(?,?,?,?) ON DUPLICATE KEY UPDATE `"
                +colName+"`=VALUES(`"+colName+"`);";
        return connection.prepareStatement(sql);
    }

    @Override
    protected void fillDeleteAllStatement(CustomRow row, PreparedStatement statement) throws SQLException {
        var cells = row.getCells();
        if(cells == null || cells.get("depot_name") == null) return;

        String depot = cells.get("depot_name").getDbData();
        fillByDataType(statement, depot, ColumnDatatype.TEXT, 1);
    }


    @Override
    protected void fillDeleteSelectionStatement(CustomRow row, PreparedStatement statement) throws SQLException {
        var cells = row.getCells();
        if(cells == null || cells.get("transaktions_datum") == null || cells.get("depot_name") == null ||
                cells.get("wertpapier_isin") == null) return;

        String date = cells.get("transaktions_datum").getDbData();
        String depot = cells.get("depot_name").getDbData();
        String isin = cells.get("wertpapier_isin").getDbData();
        fillByDataType(statement, date, ColumnDatatype.DATE, 1);
        fillByDataType(statement, depot, ColumnDatatype.TEXT, 2);
        fillByDataType(statement, isin, ColumnDatatype.TEXT, 3);
    }

    @Override
    protected PreparedStatement prepareDeleteSelectionStatement(Connection connection) throws SQLException {
        return connection.prepareStatement("DELETE FROM `"+ dbTableManger.getTableName()+"` " +
                "WHERE transaktions_datum=? AND depot_name=? AND wertpapier_isin=?");
    }

    @Override
    protected PreparedStatement prepareDeleteAllStatement(Connection connection) throws SQLException {
        return connection.prepareStatement("DELETE FROM `"+ dbTableManger.getTableName()+"` WHERE depot_name=?");
    }

    @Override
    protected Map<String, String> getKeyInformation(CustomRow row) {
        Map<String, String> keys = new HashMap<>();
        String date = row.getCells().getOrDefault("transaktions_datum",
                new CustomCell(null, null)).getDbData();
        String depot = row.getCells().getOrDefault("depot_name",
                new CustomCell(null, null)).getDbData();
        String isin = row.getCells().getOrDefault("wertpapier_isin",
                new CustomCell(null, null)).getDbData();

        if(date == null || depot == null || isin == null) return null;
        keys.put("transaktions_datum", date);
        keys.put("depot_name", depot);
        keys.put("wertpapier_isin", isin);
        return keys;
    }

    @Override
    protected void setStatementKeys(PreparedStatement stmt,
                                    Map<String, String> keys) throws SQLException{

        // 1 = data
        fillByDataType(stmt, keys.get("transaktions_datum"), ColumnDatatype.DATE, 2);
        fillByDataType(stmt, keys.get("depot_name"), ColumnDatatype.TEXT, 3);
        fillByDataType(stmt, keys.get("wertpapier_isin"), ColumnDatatype.TEXT, 4);
    }

    @SuppressWarnings("unused")
    @Override
    protected void setColumnRepositoryAndManager() {
        dbTableColumnRepository = transactionColumnRepository;
        dbTableManger = transactionTableManager;
    }

    @Override
    public boolean addRowForSelection(Object selection) {
        throw new UnsupportedOperationException("Adding rows to the transaction table is not implemented");
    }

    @Override
    protected <T extends DbTableColumn> List<? extends DbTableColumn> getTableColumns(DbTableColumnRepository<T, Integer> repository) {
        List<TransactionColumn> cols =  transactionColumnRepository.findAll();
        cols.add(new TransactionColumn("r_par", VisualDatatype.Int));
        cols.add(new TransactionColumn("name", VisualDatatype.Text));
        cols.add(new TransactionColumn("wkn", VisualDatatype.Text));
        cols.add(new TransactionColumn("typ", VisualDatatype.Text));
        return cols;
    }

    @Override
    protected String getSelectionStatement(LocalDate startDate, LocalDate endDate) {
        return "SELECT WP.* , TA.* FROM wertpapier WP RIGHT OUTER JOIN `"+ TransactionTableManager.TABLE_NAME+"` TA ON WP.isin = TA.wertpapier_isin" + getStartAndEndDateQueryPart(startDate, endDate, "transaktions_datum");
    }

    @Override
    protected String getSelectionStatementOnlyLatestRows() {
        return "SELECT WP.* , TA.* FROM wertpapier WP RIGHT OUTER JOIN(select * from `"+ TransactionTableManager.TABLE_NAME+"` DT inner join ( select wertpapier_isin as isin_kd, max(transaktions_datum) as MaxDate from `"+ TransactionTableManager.TABLE_NAME+"` group by wertpapier_isin) WPL on DT.wertpapier_isin = WPL.isin_kd and DT.transaktions_datum = WPL.MaxDate ) TA ON WP.isin = TA.wertpapier_isin";
    }

    @Override
    protected void setDataTableInitialSort(TableView<CustomRow> dataTable) {
        dataTable.getSortOrder().clear();
        dataTable.getColumns().forEach(c ->  {
            if(c.getText().equals("transaktions_datum") ) {
                c.setSortType(TableColumn.SortType.DESCENDING);
                dataTable.getSortOrder().add(c);
            } else if (c.getText().equals("r_par")) dataTable.getSortOrder().add(c);
        });
    }
}
