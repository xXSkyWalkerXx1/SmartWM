package de.tud.inf.mmt.wmscrape.gui.tabs.historic.controller;

import de.tud.inf.mmt.wmscrape.WMScrape;
import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.historic.controller.website.HistoricWebsiteTestPopupController;
import de.tud.inf.mmt.wmscrape.gui.tabs.historic.controller.website.NewHistoricWebsitePopupController;
import de.tud.inf.mmt.wmscrape.gui.tabs.historic.data.SecuritiesType;
import de.tud.inf.mmt.wmscrape.gui.tabs.historic.data.SecuritiesTypeDataContainer;
import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.HistoricWebsiteIdentifiers;
import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.Website;
import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.enums.IdentType;
import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.management.gui.WebsiteManager;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.enums.IdentTypes.*;

/**
 * handles the user interaction with the website tab
 */
@Controller
@Lazy
public class HistoricWebsiteTabController {

    @FXML private ListView<Website> websiteList;

    @FXML private TextField urlField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ChoiceBox<IdentType> usernameIdentChoiceBox;
    @FXML private TextField usernameIdentField;
    @FXML private ChoiceBox<IdentType> passwordIdentChoiceBox;
    @FXML private TextField passwordIdentField;
    @FXML private ChoiceBox<IdentType> loginIdentChoiceBox;
    @FXML private TextField loginIdentField;
    @FXML private ChoiceBox<IdentType> logoutIdentChoiceBox;
    @FXML private TextField logoutIdentField;
    @FXML private ChoiceBox<IdentType> cookieConfigIdentChoiceBox;
    @FXML private TextField cookieConfigIdentField;
    @FXML private VBox rightPanelBox;
    @FXML private SplitPane rootNode;

    @FXML private ChoiceBox<IdentType> notificationDeclineIdentChoiceBox;
    @FXML private TextField notificationDeclineIdentField;

    @FXML private TextField informationUrlField;

    @FXML private ChoiceBox<IdentType> searchIdentChoiceBox;
    @FXML private TextField searchIdentField;
    @FXML private ChoiceBox<IdentType> searchButtonIdentChoiceBox;
    @FXML private TextField searchButtonIdentField;

    @FXML private TextField dateFromField;
    @FXML private TextField dateUntilField;

    @FXML private VBox securitiesTypesTitledPaneList;
    private final List<SecuritiesTypeDataContainer> securitiesTypeDataContainers = new ArrayList<>();

    private ObservableList<Website> websiteObservableList;
    private boolean inlineValidation = false;
    private static final BorderPane noSelectionReplacement = new BorderPane(new Label(
            "Wählen Sie eine Konfiguration aus oder erstellen Sie eine neue (unten links)"));

    @Autowired
    private WebsiteManager websiteManager;
    @Autowired
    private NewHistoricWebsitePopupController newHistoricWebsitePopupController;
    @Autowired
    private HistoricWebsiteTestPopupController historicWebsiteTestPopupController;

    public HistoricWebsiteTabController() {}

    /**
     * called when loading the fxml file
     */
    @FXML
    private void initialize() {
        setRightPanelBoxVisible(false);
        websiteObservableList = websiteManager.initWebsiteList(websiteList, true);
        websiteList.getSelectionModel().selectedItemProperty().addListener(
                (ov, oldWs, newWs) -> loadSpecificWebsite(newWs));

        // add titled-panes for each securities-type
        for (SecuritiesType type : SecuritiesType.values()){
            securitiesTypesTitledPaneList.getChildren().add(createTitledPaneSecuritiesType(type));
        }

        // set listener for validation
        urlField.textProperty().addListener((o,ov,nv) -> emptyValidator(urlField));
        usernameField.textProperty().addListener((o,ov,nv) -> emptyValidator(usernameField));
        passwordField.textProperty().addListener((o,ov,nv) -> emptyValidator(passwordField));
        usernameIdentField.textProperty().addListener((o,ov,nv) -> validIdentField(usernameIdentField, true));
        passwordIdentField.textProperty().addListener((o,ov,nv) -> validIdentField(passwordIdentField, true));
        loginIdentField.textProperty().addListener((o,ov,nv) -> validIdentField(loginIdentField, true));
        logoutIdentField.textProperty().addListener((o,ov,nv) -> escapeValidator(logoutIdentField));
        cookieConfigIdentField.textProperty().addListener((o, ov, nv) -> escapeValidator(cookieConfigIdentField));
        notificationDeclineIdentField.textProperty().addListener((o,ov,nv) -> escapeValidator(notificationDeclineIdentField));
        informationUrlField.textProperty().addListener((o, ov, nv) -> validUrlField(informationUrlField));
        searchIdentField.textProperty().addListener((o, ov, nv) -> validIdentField(searchIdentField, false));
        searchButtonIdentField.textProperty().addListener((o, ov, nv) -> validIdentField(searchButtonIdentField, false));

        // add choice-options
        addTypeToChoiceBox(usernameIdentChoiceBox, IDENT_TYPE_DEACTIVATED);
        addTypeToChoiceBox(passwordIdentChoiceBox, IDENT_TYPE_DEACTIVATED);
        addTypeToChoiceBox(loginIdentChoiceBox, IDENT_TYPE_DEACTIVATED_ENTER);
        addTypeToChoiceBox(logoutIdentChoiceBox, IDENT_TYPE_DEACTIVATED_URL);
        addTypeToChoiceBox(cookieConfigIdentChoiceBox, IDENT_TYPE_DEACTIVATED);
        addTypeToChoiceBox(notificationDeclineIdentChoiceBox, IDENT_TYPE_DEACTIVATED);
        addTypeToChoiceBox(searchButtonIdentChoiceBox, IDENT_TYPE_DEACTIVATED_ENTER);
        addTypeToChoiceBox(searchIdentChoiceBox, IDENT_TYPE_DEACTIVATED_ENTER);

        // handle deselecting
        addDeselectListener(usernameIdentChoiceBox);
        addDeselectListener(passwordIdentChoiceBox);

        loginIdentChoiceBox.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> {
            loginIdentField.setEditable(nv != IdentType.ENTER);
            if(nv == IdentType.ENTER) loginIdentField.setText("-");
        });

        // set initial configuration to show
        websiteList.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleNewWebsiteButton() {
        PrimaryTabManager.loadFxml(
                "gui/tabs/scraping/controller/website/newWebsitePopup.fxml",
                "Neue Webseite anlegen",
                websiteList,
                true, newHistoricWebsitePopupController, false);
    }

    private TitledPane createTitledPaneSecuritiesType(SecuritiesType type){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(WMScrape.class.getResource(
                    "gui/tabs/historic/controller/historicWebsitesSecuritiesType.fxml"
            ));
            fxmlLoader.setControllerFactory(param -> new SecuritiesTypeDataContainer(type));

            securitiesTypeDataContainers.add(fxmlLoader.getController());

            TitledPane titledPane = fxmlLoader.load();
            titledPane.setText(type.getDisplayText());
            return titledPane;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @FXML
    private void handleDeleteWebsiteButton() {
        Website website = getSelectedWebsite();

        if(website == null) {
            createAlert("Keine Webseite zum löschen ausgewählt!",
                    "Wählen Sie eine Webseite aus der Liste aus um diese zu löschen."
            );
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Einstellungen löschen?");
        alert.setContentText("Bitte bestätigen Sie, dass sie diese Webseitenkonfiguration löschen möchten.");
        PrimaryTabManager.setAlertPosition(alert , urlField);
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        websiteManager.deleteSpecificWebsite(website);
        clearFields();
        reloadWebsiteList();
        setRightPanelBoxVisible(false);
        websiteList.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleSaveButton() {
        // validate inputs
        if(noWebsiteSelected()) return;
        inlineValidation = true;
        if(!isValidInput()) return;

        // save and refresh
        Website website = websiteList.getSelectionModel().getSelectedItem();
        setFieldDataToWebsite(website);
        websiteManager.saveWebsite(website);

        // show dialog
        Alert alert = new Alert(
                Alert.AlertType.INFORMATION,
                "Die Webseitenkonfiguration wurde gespeichert.",
                ButtonType.OK);
        alert.setHeaderText("Daten gespeichert!");
        PrimaryTabManager.setAlertPosition(alert , urlField);
        alert.showAndWait();
    }

    private void showAlertDialog(Alert.AlertType alertType, String title, String msg){
        Alert alert = new Alert(alertType, msg, ButtonType.OK);
        alert.setHeaderText(title);
        alert.showAndWait();
    }

    @FXML
    private void handleResetButton() {
        loadSpecificWebsite(getSelectedWebsite());
    }

    @FXML
    private void handleTestButton() {
        if(noWebsiteSelected()) return;
        inlineValidation = true;
//        if(!isValidInput() || urlField.getText().equals("-")) return;

        PrimaryTabManager.loadFxml(
                "gui/tabs/scraping/controller/website/websiteTestPopup.fxml",
                "Login Test",
                websiteList,
                true, historicWebsiteTestPopupController, false);
    }

    private boolean noWebsiteSelected() {
        if(getSelectedWebsite() == null) {
            createAlert("Keine Webseite ausgewählt!",
                    "Wählen Sie eine Webseite aus der Liste aus oder" +
                            " erstellen Sie eine neue bevor Sie Speichern."
            );
            return true;
        }
        return false;
    }

    public Website getSelectedWebsite() {
        return websiteList.getSelectionModel().getSelectedItem();
    }

    private void clearFields() {
        clearTopFields();

        cookieConfigIdentField.clear();
        usernameIdentChoiceBox.setValue(null);
        passwordIdentChoiceBox.setValue(null);
        loginIdentChoiceBox.setValue(null);
        logoutIdentChoiceBox.setValue(null);
        cookieConfigIdentChoiceBox.setValue(null);

        notificationDeclineIdentField.clear();
        notificationDeclineIdentChoiceBox.setValue(null);

        informationUrlField.clear();

        searchIdentField.clear();
        searchIdentChoiceBox.setValue(null);

        searchButtonIdentField.clear();
        searchButtonIdentChoiceBox.setValue(null);

        dateFromField.clear();
        dateUntilField.clear();

        securitiesTypeDataContainers.forEach(SecuritiesTypeDataContainer::clearAll);
    }

    private void clearTopFields() {
        urlField.clear();
        usernameField.clear();
        passwordField.clear();
        usernameIdentField.clear();
        passwordIdentField.clear();
        loginIdentField.clear();
        logoutIdentField.clear();
    }

    private void setFieldDataToWebsite(@NonNull Website website) {
        website.setLoginUrl(urlField.getText());
        website.setUsername(usernameField.getText());
        website.setPassword(passwordField.getText());
        website.setUsernameIdentType(usernameIdentChoiceBox.getValue());
        website.setUsernameIdent(usernameIdentField.getText());
        website.setPasswordIdentType(passwordIdentChoiceBox.getValue());
        website.setPasswordIdent(passwordIdentField.getText());
        website.setLoginButtonIdentType(loginIdentChoiceBox.getValue());
        website.setLoginButtonIdent(loginIdentField.getText());
        website.setLogoutIdentType(logoutIdentChoiceBox.getValue());
        website.setLogoutIdent(logoutIdentField.getText());
        website.setCookieAcceptIdentType(cookieConfigIdentChoiceBox.getValue());
        website.setCookieAcceptIdent(cookieConfigIdentField.getText());
        website.setDeclineNotificationIdentType(notificationDeclineIdentChoiceBox.getValue());
        website.setNotificationDeclineIdent(notificationDeclineIdentField.getText());

        website.setHistoric(true);

        website.setSearchUrl(informationUrlField.getText());
        website.setSearchFieldIdentType(searchIdentChoiceBox.getValue());
        website.setSearchButtonIdent(searchButtonIdentField.getText());
        website.setSearchButtonIdentType(searchButtonIdentChoiceBox.getValue());
        website.setSearchFieldIdent(searchIdentField.getText());

        website.setDateFrom(dateFromField.getText());
        website.setDateUntil(dateUntilField.getText());

        for (var dataContainer : securitiesTypeDataContainers){
            // if everything or none is filled in only then we save it
            if (dataContainer.areInputsCompletedOrEmpty()){
                HistoricWebsiteIdentifiers typeIdents = website.getHistoricIdentifiersByType(dataContainer.getType());

                // create new entity, if not exists
                if (typeIdents == null){
                    typeIdents = new HistoricWebsiteIdentifiers();
                    typeIdents.setWebsiteId(website.getId());
                    website.addSecuritiestypeIdentifiers(typeIdents);
                }

                // write data
                dataContainer.writeTo(typeIdents);
            } else {
                showAlertDialog(
                        Alert.AlertType.WARNING,
                        String.format(
                                "Eingaben zu %s nicht gültig und werden daher ignoriert.\n" +
                                        "Erlaubt ist nur komplett oder gar nicht ausfüllen!",
                                dataContainer.getType()
                        ),
                        "Ungültige Eingabe"
                );
            }
        }
    }

    public void selectWebsite(Website website) {
        websiteList.getSelectionModel().select(website);

        // it's not guaranteed to be the last in the list
        // nor is it guaranteed that it's the same object
        // match by id
        for(Website ws : websiteList.getItems()) {
            if(website.getId() == ws.getId()) websiteList.getSelectionModel().select(ws);
        }
    }

    public void reloadWebsiteList() {
        websiteObservableList.clear();
        websiteObservableList.addAll(websiteManager.getWebsites(true));
    }

    private void loadSpecificWebsite(Website website) {
        if (website == null) return;

        inlineValidation = false;

        setRightPanelBoxVisible(true);

        deselectOnDeactivated(passwordIdentChoiceBox.getValue(), website.getPasswordIdentType());

        urlField.setText(website.getLoginUrl());
        usernameField.setText(website.getUsername());
        passwordField.setText(website.getPassword());
        usernameIdentChoiceBox.setValue(website.getUsernameIdentType());
        usernameIdentField.setText(website.getUsernameIdent());
        passwordIdentChoiceBox.setValue(website.getPasswordIdentType());
        passwordIdentField.setText(website.getPasswordIdent());
        loginIdentChoiceBox.setValue(website.getLoginButtonIdentType());
        loginIdentField.setText(website.getLoginButtonIdent());
        logoutIdentChoiceBox.setValue(website.getLogoutIdentType());
        logoutIdentField.setText(website.getLogoutIdent());
        cookieConfigIdentChoiceBox.setValue(website.getCookieAcceptIdentType());
        cookieConfigIdentField.setText(website.getCookieAcceptIdent());

        notificationDeclineIdentChoiceBox.setValue(website.getDeclineNotificationIdentType());
        notificationDeclineIdentField.setText(website.getNotificationDeclineIdent());
        informationUrlField.setText(website.getSearchUrl());
        searchIdentChoiceBox.setValue(website.getSearchFieldIdentType());
        searchIdentField.setText(website.getSearchFieldIdent());
        searchButtonIdentChoiceBox.setValue(website.getSearchButtonIdentType());
        searchButtonIdentField.setText(website.getSearchButtonIdent());

        dateFromField.setText(website.getDateFrom());
        dateUntilField.setText(website.getDateUntil());

        for (var typeIdents : website.getHistoricWebsiteIdentifiers()){
            for (var dataContainer : securitiesTypeDataContainers){
                if (typeIdents.getSecuritiesType().equals(dataContainer.getType())) dataContainer.writeFrom(typeIdents);
            }
        }

        // just here to remove eventually existing error style attributes
        isValidInput();
    }

    private void createAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR, content, ButtonType.OK);
        alert.setHeaderText(title);
        PrimaryTabManager.setAlertPosition(alert , urlField);
        alert.showAndWait();
    }

    private boolean isValidInput() {
        // evaluate all to highlight all
        boolean valid = validUrlField(urlField)
                && emptyValidator(usernameField)
                && emptyValidator(passwordField)
                && validIdentField(usernameIdentField, true)
                && validIdentField(passwordIdentField, true)
                && validIdentField(loginIdentField, true)
                && escapeValidator(logoutIdentField)
                && escapeValidator(cookieConfigIdentField)
                && validUrlField(informationUrlField)
                && validIdentField(searchIdentField, false);

        for (var typeIdents : securitiesTypeDataContainers){
            if (!typeIdents.areInputsCompletedOrEmpty()) continue;
            valid &= validIdentField(typeIdents.getFieldHistoryCourse(), false)
                    && validIdentField(typeIdents.getFieldDateFromDay(), false)
                    && validIdentField(typeIdents.getFieldDateFromMonth(), true)
                    && validIdentField(typeIdents.getFieldDateFromYear(), true)
                    && validIdentField(typeIdents.getFieldDateToDay(), false)
                    && validIdentField(typeIdents.getFieldDateToMonth(), true)
                    && validIdentField(typeIdents.getFieldDateToYear(), true)
                    && validIdentField(typeIdents.getFieldButtonLoad(), false)
                    && validIdentField(typeIdents.getFieldButtonNextPage(), true)
                    && validIdentField(typeIdents.getFieldCountPages(), true);
        }
        return valid;
    }

    private boolean validUrlField(TextField identField) {
        return emptyValidator(identField) && urlValidator(identField);
    }

    private boolean validIdentField(TextField identField, boolean canBeEmpty) {
        return (canBeEmpty || emptyValidator(identField)) && escapeValidator(identField);
    }

    private boolean emptyValidator(TextInputControl input) {
        boolean isValid = input.getText() != null && !input.getText().isBlank();
        PrimaryTabManager.decorateField(input, "Dieses Feld darf nicht leer sein!", isValid, inlineValidation);
        return isValid;
    }

    private boolean urlValidator(TextInputControl input) {
        String value = input.getText();
        if(value==null) return true;

        boolean isValid = value.matches("^(https?://.+|-)$"); // "-" to allow saving with deactivated fields
        PrimaryTabManager.decorateField(input, "Die URL muss mit http:// oder https:// beginnen!", isValid,
                inlineValidation);
        return isValid;
    }

    private boolean escapeValidator(TextInputControl input) {
        String value = input.getText();
        if(value==null) return true;

        boolean isValid = !value.matches("^.*[\"´`]+.*$");
        PrimaryTabManager.decorateField(input, "Die Symbole \",´,` sind nicht erlaubt!", isValid, inlineValidation);
        return isValid;
    }

    private void addDeselectListener(ChoiceBox<IdentType> choiceBox) {
        choiceBox.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> deselectOnDeactivated(ov, nv));
    }

    public static void addTypeToChoiceBox(ChoiceBox<IdentType> choiceBox, IdentType[] identType) {
        choiceBox.getItems().addAll(identType);
    }

    /**
     * if the password or username identification is set to deactivate some fields are no longer editable
     *
     * @param oldIdentType the previous set {@link de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.enums.IdentType}
     * @param newIdentType the now set {@link de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.enums.IdentType}
     */
    private void deselectOnDeactivated(IdentType oldIdentType,IdentType newIdentType) {
        if(newIdentType == IdentType.DEAKTIVIERT) {
            usernameIdentChoiceBox.setValue(IdentType.DEAKTIVIERT);
            passwordIdentChoiceBox.setValue(IdentType.DEAKTIVIERT);
            loginIdentChoiceBox.setValue(IdentType.ENTER);
            logoutIdentChoiceBox.setValue(IdentType.DEAKTIVIERT);

            logoutIdentChoiceBox.getItems().clear();
            logoutIdentChoiceBox.getItems().add(IdentType.DEAKTIVIERT);
            logoutIdentChoiceBox.setValue(IdentType.DEAKTIVIERT);

            urlField.setText("-");
            usernameField.setText("-");
            passwordField.setText("-");
            usernameIdentField.setText("-");
            passwordIdentField.setText("-");
            loginIdentField.setText("-");

            for (SecuritiesTypeDataContainer dataContainer : securitiesTypeDataContainers){
                dataContainer.setIdTypeButtonNextPage(IdentType.DEAKTIVIERT);
                dataContainer.setIdContentButtonNextPage("-");
                dataContainer.setIdTypeCountPages(IdentType.DEAKTIVIERT);
                dataContainer.setIdContentCountPages("-");
            }

            setEditable(false);
        } else if (oldIdentType == IdentType.DEAKTIVIERT) {
            usernameIdentChoiceBox.setValue(newIdentType);
            passwordIdentChoiceBox.setValue(newIdentType);

            logoutIdentChoiceBox.getItems().clear();
            logoutIdentChoiceBox.getItems().addAll(IDENT_TYPE_DEACTIVATED_URL);
            logoutIdentChoiceBox.setValue(IdentType.ID);

            clearTopFields();
            setEditable(true);
        } else {
            setEditable(true);
        }
    }


    private void setEditable(boolean editable) {
        urlField.setEditable(editable);
        usernameField.setEditable(editable);
        passwordField.setEditable(editable);
        usernameIdentField.setEditable(editable);
        passwordIdentField.setEditable(editable);
        loginIdentField.setEditable(editable);
        logoutIdentField.setEditable(editable);
    }

    /**
     * creates a temporary copy of a website to use the data with the website tester without saving
     * @return the temporary website
     */
    public Website getWebsiteUnpersistedData() {
        Website newWebsite = new Website("test");
        setFieldDataToWebsite(newWebsite);
        return newWebsite;
    }

    /**
     * hides the usual input mask if no configuration exists
     *
     * @param visible if true the normal input mask for a configuration is shown
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

}