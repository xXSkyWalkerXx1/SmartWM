package de.tud.inf.mmt.wmscrape.gui.tabs.imports.controller;

import de.tud.inf.mmt.wmscrape.dynamicdb.stock.StockColumnRepository;
import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.dbdata.controller.DataTabController;
import de.tud.inf.mmt.wmscrape.gui.tabs.imports.data.ExcelCorrelation;
import de.tud.inf.mmt.wmscrape.gui.tabs.imports.data.ExcelSheet;
import de.tud.inf.mmt.wmscrape.gui.tabs.imports.management.CorrelationManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.imports.management.ImportTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.controller.ScrapingElementsTabController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Controller
public class ImportTabController {
    @FXML private ListView<ExcelSheet> excelSheetList;
    @FXML private TextField pathField;
    @FXML private PasswordField passwordField;
    @FXML private Spinner<Integer> titleRowSpinner;
    @FXML private TextField previewSelectionColField; // Ü-Par
    @FXML private TextField stockdataSelectionColField; // S-Par
    @FXML private TextField transactionSelectionColField; // T-Par
    @FXML private TextField watchListSelectionColField; // W-Par
    @FXML private TableView<List<String>> sheetPreviewTable;
    @FXML private TableView<ExcelCorrelation> stockDataCorrelationTable;
    @FXML private TableView<ExcelCorrelation> transactionCorrelationTable;
    @FXML private TableView<ExcelCorrelation> watchListCorrelationTable;
    @FXML private GridPane rightPanelBox;
    @FXML private SplitPane rootNode;

    @FXML private ProgressIndicator importProgressIndicator;
    @FXML private Button importAbortButton;

    // used to disallow certain actions while running a task
    @FXML private Button saveConfButton;
    @FXML private Button previewButton;
    @FXML private Button importButtom;
    @FXML private Button deleteConfButton;

    @Autowired
    private NewExcelPopupController newExcelPopupController;
    @Autowired
    private ImportTabManager importTabManager;
    @Autowired
    private ScrapingElementsTabController elementsTabController;
    @Autowired
    private DataTabController dataTabController;
    @Autowired
    private CorrelationManager correlationManager;

    private ObservableList<ExcelSheet> excelSheetObservableList;
    private boolean inlineValidation = false;
    private static final BorderPane noSelectionReplacement = new BorderPane(new Label(
            "Wählen Sie eine Excelkonfiguration aus oder erstellen Sie eine neue (unten links)"));

    @Autowired
    private StockColumnRepository stockColumnRepository;

    private TextArea logTextArea = new TextArea();
    private SimpleStringProperty logText;

    /**
     * called when loading the fxml file
     */
    @FXML
    private void initialize() {

        setRightPanelBoxVisible(false);
        sheetPreviewTable.setPlaceholder(getPlaceholder());
        stockDataCorrelationTable.setPlaceholder(getPlaceholder());
        transactionCorrelationTable.setPlaceholder(getPlaceholder());
        watchListCorrelationTable.setPlaceholder(getPlaceholder());

        excelSheetObservableList = importTabManager.initExcelSheetList(excelSheetList);
        excelSheetList.getSelectionModel().selectedItemProperty().addListener(
                (ov, oldSheet, newSheet) -> loadSpecificExcel(newSheet));

        pathField.textProperty().addListener((o,ov,nv) -> validPath());
        titleRowSpinner.valueProperty().addListener((o, ov, nv) -> validTitleColNr());
        previewSelectionColField.textProperty().addListener((o, ov, nv) -> emptyValidator(previewSelectionColField));
        stockdataSelectionColField.textProperty().addListener((o, ov, nv) -> emptyValidator(stockdataSelectionColField));
        transactionSelectionColField.textProperty().addListener((o, ov, nv) -> emptyValidator(transactionSelectionColField));

        logText = new SimpleStringProperty("");
        logTextArea = new TextArea();
        logTextArea.setPrefSize(350,400);
        logTextArea.textProperty().bind(logText);
        importTabManager.passLogText(logText);
        titleRowSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1,1));
        excelSheetList.getSelectionModel().selectFirst();

        importProgressIndicator.setProgress(-1);
    }

    /**
     * opens the new element popup
     */
    @FXML
    private void handleNewExcelSheetButton() {
        PrimaryTabManager.loadFxml(
                "gui/tabs/imports/controller/newExcelPopup.fxml",
                "Neue Konfiguration anlegen",
                excelSheetList,
                true, newExcelPopupController, false);
    }

    /**
     * deletes a excel configuration
     */
    @FXML
    private void handleDeleteExcelSheetButton() {
        //clearFields();
        ExcelSheet excelSheet = excelSheetList.getSelectionModel().getSelectedItem();

        if(excelSheet == null) {
            createAlert("Keine Excel zum löschen ausgewählt!",
                    "Wählen Sie eine Konfiguration aus der Liste aus um diese zu löschen.",
                    Alert.AlertType.ERROR);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Einstellungen löschen?");
        alert.setContentText("Soll diese Konfiguration gelöscht werden?");
        PrimaryTabManager.setAlertPosition(alert , pathField);
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        importTabManager.deleteSpecificExcel(excelSheet);
        reloadExcelList();
        setRightPanelBoxVisible(false);
        excelSheetList.getSelectionModel().selectFirst();
    }

    @FXML
    private void saveSpecificExcel() {
        if(excelIsNotSelected()) return;
        inlineValidation = true;
        if(!isValidInput()) return;

        ExcelSheet excelSheet = excelSheetList.getSelectionModel().getSelectedItem();

        excelSheet.setPath(pathField.getText());
        excelSheet.setPassword(passwordField.getText());
        excelSheet.setTitleRow(titleRowSpinner.getValue());
        excelSheet.setPreviewSelectionColTitle(previewSelectionColField.getText().trim());
        excelSheet.setStockSelectionColTitle(stockdataSelectionColField.getText().trim());
        excelSheet.setTransactionSelectionColTitle(transactionSelectionColField.getText().trim());
        excelSheet.setWatchListSelectionColTitle(watchListSelectionColField.getText().trim());

        importTabManager.saveExcelConfig(excelSheet);

        Alert alert = new Alert(
                Alert.AlertType.INFORMATION,
                "Die Excelkonfiguration wurde gespeichert.",
                ButtonType.OK);
        alert.setHeaderText("Daten gespeichert!");
        PrimaryTabManager.setAlertPosition(alert , pathField);
        alert.showAndWait();
    }

    /**
     * opens the file chooser
     */
    @FXML
    private void handleFileSelectionButton() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel Sheet", "*.xlsx"));
        File selectedFile = fileChooser.showOpenDialog(excelSheetList.getScene().getWindow());

        if(selectedFile != null) {
            pathField.setText(selectedFile.getPath());
        }
    }

    /**
     * starts the preview process -> parsing the Excel sheet, process the data, filling the preview tables
     */
    @FXML
    private void previewExcel() {
        // removes all changes if not saved before
        loadSpecificExcel(excelSheetList.getSelectionModel().getSelectedItem());

        if(excelIsNotSelected()) return;

        inlineValidation = true;
        if(!isValidInput()) return;

        logText.set("");

        ExcelSheet excelSheet = excelSheetList.getSelectionModel().getSelectedItem();
        if(!importTabManager.sheetExists(excelSheet)) {
            createAlert("Datei nicht gefunden!",
                    "Unter dem angegebenen Pfad wurde keine gültige Datei gefunden.",
                    Alert.AlertType.ERROR);
            return;
        }

        showProgress(true);

        // starts the preview task
        importTabManager.fillExcelPreview(sheetPreviewTable, excelSheet);
    }

    @FXML
    private void cancelTask() {
        importTabManager.cancelTask();
    }

    /**
     * starts the import process
     */
    @FXML
    private void importExcel() {
        logText.set("");

        if (transactionCorrelationTable.getItems().isEmpty() || stockDataCorrelationTable.getItems().size() == 0 || watchListCorrelationTable.getItems().isEmpty()
                || !importTabManager.excelHasContentForImport()) {
            createAlert("Vorschau nicht geladen!", "Die Vorschau muss vor dem Import geladen werden.",
                    Alert.AlertType.INFORMATION);
            return;
        }

        if (!importTabManager.correlationsHaveValidState()) {
            createAlert("Zuordnung unvollständig!",
                    """
                            Es sind nicht alles notwendigen Zuordnungen gesetzt. Notwendig sind
                            Stammdaten:   datum, isin
                            Transaktionen: wertpapier_isin, transaktions_datum, depot_name, transaktionstyp""",
                    Alert.AlertType.ERROR);
            return;
        }

        showProgress(true);

        importTabManager.startDataExtraction();
    }

    @FXML
    private void openLog() {

        Stage stage = new Stage();
        Scene scene = logTextArea.getScene();

        if (scene == null) {
            scene = new Scene(this.logTextArea);
            scene.getStylesheets().add("style.css");
        } else {
            logTextArea.getScene().getWindow().hide();
        }

        stage.setScene(scene);

        stage.initOwner(pathField.getScene().getWindow());
        stage.initModality(Modality.NONE);
        stage.show();

        stage.setTitle("Log");
    }

    private boolean excelIsNotSelected() {
        ExcelSheet excelSheet = excelSheetList.getSelectionModel().getSelectedItem();

        if(excelSheet == null) {
            createAlert("Keine Excel ausgewählt!",
                    "Wählen Sie eine Excelkonfiguration aus der Liste aus oder erstellen Sie eine neue, bevor Sie Speichern.",
                    Alert.AlertType.ERROR);
            return true;
        }
        return false;
    }

    public void selectLastExcel() {
        excelSheetList.getSelectionModel().selectLast();
    }

    public void reloadExcelList() {
        excelSheetObservableList.clear();
        excelSheetObservableList.addAll(importTabManager.getExcelSheets());
    }

    private void loadSpecificExcel(ExcelSheet excelSheet) {
        if (excelSheet == null) return;

        // just for safety
        cancelTask();

        setRightPanelBoxVisible(true);
        inlineValidation = false;
        logText.set("");

        pathField.setText(excelSheet.getPath());
        passwordField.setText(excelSheet.getPassword());
        titleRowSpinner.getValueFactory().setValue(excelSheet.getTitleRow());
        previewSelectionColField.setText(excelSheet.getPreviewSelectionColTitle());
        stockdataSelectionColField.setText(excelSheet.getStockSelectionColTitle());
        transactionSelectionColField.setText(excelSheet.getTransactionSelectionColTitle());
        watchListSelectionColField.setText(excelSheet.getWatchListSelectionColTitle());

        sheetPreviewTable.getColumns().clear();
        sheetPreviewTable.getItems().clear();
        stockDataCorrelationTable.getColumns().clear();
        stockDataCorrelationTable.getItems().clear();
        transactionCorrelationTable.getColumns().clear();
        transactionCorrelationTable.getItems().clear();
        watchListCorrelationTable.getColumns().clear();
        watchListCorrelationTable.getItems().clear();

        // here to remove eventually existing error styling
        isValidInput();
    }

    private boolean validPath() {
        return emptyValidator(pathField) && pathValidator(pathField);
    }

    private boolean validTitleColNr() {
        return titleRowSpinner.getValue() != null && titleRowSpinner.getValue() > 0;
    }

    private boolean isValidInput() {
        // need all methods executed to highlight errors
        boolean valid = validPath();
        valid &= validTitleColNr();
        valid &= emptyValidator(previewSelectionColField);
        valid &= emptyValidator(stockdataSelectionColField);
        valid &= emptyValidator(transactionSelectionColField);
        return valid;
    }

    private boolean emptyValidator(TextInputControl input) {
        boolean isValid = input.getText() != null && !input.getText().isBlank();
        PrimaryTabManager.decorateField(input, "Dieses Feld darf nicht leer sein!", isValid, inlineValidation);
        return isValid;
    }

    private boolean pathValidator(TextInputControl input) {
        boolean isValid = input.getText() != null && input.getText().matches("^.*\\.xlsx$");
        PrimaryTabManager.decorateField(input, "Dieses Feld darf nur auf xlsx Dateien verweisen!", isValid,
                                            inlineValidation);
        return isValid;
    }

    private Label getPlaceholder() {
        return new Label("Keine Vorschau geladen.");
    }

    /**
     * if no configuration exists, the normal view is hidden and replaced with an instruction window
     *
     * @param visible if true show the normal config winoow
     */
    private void setRightPanelBoxVisible(boolean visible) {
        if(!visible) {
            rootNode.getItems().remove(rightPanelBox);
            rootNode.getItems().add(noSelectionReplacement);
        } else {
            if(!rootNode.getItems().contains(rightPanelBox)) {
                rootNode.getItems().remove(noSelectionReplacement);
                rootNode.getItems().add(rightPanelBox);
                rootNode.setDividerPosition(0, 0.15);
            }
        }
    }

    private void createAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type, content, ButtonType.OK);
        alert.setHeaderText(title);
        PrimaryTabManager.setAlertPosition(alert , pathField);
        alert.showAndWait();
    }

    /**
     * reloads the correlation table if some new columns have been added to the database via the data tab
     */
    public void refreshCorrelationTables() {
        // nothing selected means no need to refresh
        ExcelSheet sheet = excelSheetList.getSelectionModel().getSelectedItem();
        if (sheet == null) return;

        loadSpecificExcel(sheet);
    }

    public ObservableList<ExcelCorrelation> getStockDataCorrelations() {
        return stockDataCorrelationTable.getItems();
    }

    public ObservableList<ExcelCorrelation> getTransactionCorrelations() {
        return transactionCorrelationTable.getItems();
    }

    public ObservableList<ExcelCorrelation> getWatchListCorrelations() {
        return watchListCorrelationTable.getItems();
    }

    /**
     * called after the preview task exits which is started with {@link #previewExcel()}
     *
     * @param result the result value from the task indicating errors or success
     */
    public void onPreviewTaskFinished(int result) {
        showProgress(false);

        ExcelSheet excelSheet = excelSheetList.getSelectionModel().getSelectedItem();

        if (showPreviewResultMsg(excelSheet, result)) return;

        importTabManager.preparePreviewTable(sheetPreviewTable, excelSheet);
        importTabManager.fillPreviewTable(sheetPreviewTable);

        // adds and validates the correlations
        boolean allValid = correlationManager.fillCorrelationTables(stockDataCorrelationTable,
                transactionCorrelationTable, watchListCorrelationTable, excelSheet);
        if(!allValid) {
            createAlert("Excel-Sheet-Spalten wurden verändert!",
                    "Nicht alle gespeicherten Abbildungen stimmen mit dem Excel-Sheet überein. Die betroffenen Spalten wurden" +
                            " zurückgesetzt. Genauere Informationen befinden sich im Log.",
                    Alert.AlertType.WARNING);
        }

        // refresh because otherwise the comboboxes are unreliable set
        stockDataCorrelationTable.refresh();
        transactionCorrelationTable.refresh();
        watchListCorrelationTable.refresh();
    }

    /**
     * outsourced the preview msg handling
     *
     * @param excelSheet the used Excel configuration
     * @param result the result value from the process
     * @return returns true if a critical error occurred and no preview will be displayed
     */
    private boolean showPreviewResultMsg(ExcelSheet excelSheet, int result) {
        Alert alert;
        switch (result) {
            case -1 -> {
                // wrong password
                createAlert("Falsches Passwort!",
                        "Das angegebene Passwort ist falsch. Speichern Sie bevor Sie die Vorschau laden.",
                        Alert.AlertType.ERROR);
                return true;
            }
            case -2 -> {
                // TitleRowError
                createAlert("Fehlerhafte Titelzeile!",
                        "Die Titelzeile liegt außerhalb der Begrenzung.",
                        Alert.AlertType.ERROR);
                return true;
            }
            case -3 -> {
                // no data in sheet
                createAlert("Keine Daten gefunden!",
                        "Die angegebene Datei enhält keine Daten.",
                        Alert.AlertType.ERROR);
                return true;
            }
            case -4 -> {
                // no data title row
                createAlert("Keine Daten gefunden!",
                        "In der angegebenen Titelzeile sind keine Daten.",
                        Alert.AlertType.ERROR);
                return true;
            }
            case -5 -> {
                // titles not unique
                createAlert("Titel nicht einzigartig!",
                        "Die Titelzeile enthält Elemente mit gleichen Namen. Mehr Informationen im Log",
                        Alert.AlertType.ERROR);
                return true;
            }
            case -6 -> {
                // Selection column not found
                createAlert("Übernahmespalte (Ü-Par) nicht gefunden!",
                        "In der Zeile " + excelSheet.getTitleRow() + " " +
                                "existiert keine Spalte mit dem Namen '" +
                                excelSheet.getStockSelectionColTitle() + "'",
                        Alert.AlertType.ERROR);
                return true;
            }
            case -7 -> {
                // Selection column not found
                createAlert("Stammdatenspalte (S-Par) nicht gefunden!",
                        "In der Zeile " + excelSheet.getTitleRow() + " " +
                                "existiert keine Spalte mit dem Namen '" +
                                excelSheet.getStockSelectionColTitle() + "'",
                        Alert.AlertType.ERROR);
                return true;
            }
            case -8 -> {
                // transaction column not found
                createAlert("Transaktionsspalte (T-Par) nicht gefunden!",
                        "In der Zeile " + excelSheet.getTitleRow() + " " +
                                "existiert keine Spalte mit dem Namen '" +
                                excelSheet.getTransactionSelectionColTitle() + "'",
                        Alert.AlertType.ERROR);
                return true;
            }
            case -9 -> {
                // Cell evaluation error
                alert = new Alert(
                        Alert.AlertType.WARNING,
                        "Einige Zellen konnten nicht evaluiert werden.",
                        ButtonType.OK);
                alert.setHeaderText("Fehler bei der Evaluierung!");
                TextArea textArea = new TextArea(
                        """
                                Einige Zellen konnten nicht evaluiert werden.
                                Genauere Informationen befinden sich im Log.
                                Die von POI unterstützten Funktionen können hier nachgeschlagen werden:\s

                                https://poi.apache.org/components/spreadsheet/eval-devguide.html""");
                textArea.setEditable(false);
                textArea.setWrapText(true);
                GridPane gridPane = new GridPane();
                gridPane.setMaxWidth(Double.MAX_VALUE);
                gridPane.add(textArea, 0, 0);
                alert.getDialogPane().setContent(gridPane);
                PrimaryTabManager.setAlertPosition(alert , pathField);
                alert.show();
                return false;
            }
            case -10 -> {
                // occurs only on a canceled task
                createAlert("Prozess abgebrochen!",
                        "Der laufende Prozess wurde durch den Abbruch-Button abgebrochen.",
                        Alert.AlertType.INFORMATION);
                return true;
            }
            case -11 -> {
                // occurs only on a failed task
                createAlert("Unbekannter Fehler!",
                        "Bei dem Erstellen der Vorschau kam es zu einem unbekannten Fehler.",
                        Alert.AlertType.ERROR);
                return true;
            }
            case -12 -> {
                // Selection column not found
                createAlert("Watch-Liste-Spalte (W-Par) nicht gefunden!",
                        "In der Zeile " + excelSheet.getTitleRow() + " " +
                                "existiert keine Spalte mit dem Namen '" +
                                excelSheet.getWatchListSelectionColTitle() + "'",
                        Alert.AlertType.ERROR);
                return true;
            }
        }
        return false;
    }

    /**
     * called after the import task exits which is started with {@link #importExcel()}
     *
     * @param result the result value from the task indicating errors or success
     */
    public void onImportTaskFinished(int result) {
        showProgress(false);

        switch (result) {
            case 0 -> createAlert("Import abgeschlossen!",
                    "Alle Stammmdaten, Transaktionen, und Watch-Liste Einträge wurden importiert.",
                    Alert.AlertType.INFORMATION);
            case -1 -> createAlert("Import unvollständig!", "Nicht alle Zellen wurden " +
                            "importiert. Der Log enthält mehr Informationen.",
                    Alert.AlertType.WARNING);
            case -2 -> createAlert("Fehler bei Sql-Statement erstellung.!",
                    "Bei der Erstellung der Sql-Statements kam es zu fehlern. Die Logs enthalten genauere Informationen.",
                    Alert.AlertType.ERROR);
            case -3 -> createAlert("Prozess abgebrochen!", // occurs only on a failed task
                    "Der laufende Prozess wurde durch den Abbruch-Button abgebrochen.",
                    Alert.AlertType.INFORMATION);
            case -4 -> createAlert("Unbekannter Fehler!", // occurs only on a failed task
                    "Bei dem Erstellen der Vorschau kam es zu einem unbekannten Fehler.",
                    Alert.AlertType.ERROR);
        }

        // add new stocks to the list in the other tabs
        elementsTabController.refresh();
        dataTabController.handleResetButton();
    }

    /**
     * makes the progress indicator visible and disables some buttons
     *
     * @param show if true the progress indicator will be visible
     */
    private void showProgress(boolean show) {
        importProgressIndicator.setVisible(show);
        importAbortButton.setVisible(show);

        saveConfButton.setDisable(show);
        previewButton.setDisable(show);
        importButtom.setDisable(show);
        deleteConfButton.setDisable(show);
    }
}
