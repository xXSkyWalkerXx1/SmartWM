package de.tud.inf.mmt.wmscrape.gui.tabs.historic.controller;

import de.tud.inf.mmt.wmscrape.WMScrape;
import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.historic.controller.element.HistoricTableSubController;
import de.tud.inf.mmt.wmscrape.gui.tabs.historic.controller.element.NewHistoricElementPopupController;
import de.tud.inf.mmt.wmscrape.gui.tabs.historic.data.SecuritiesType;
import de.tud.inf.mmt.wmscrape.gui.tabs.historic.data.SecuritiesTypeCorrContainer;
import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.controller.ScrapingElementsTabController;
import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.Website;
import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.correlation.identification.ElementIdentCorrelation;
import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.element.WebsiteElement;
import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.management.gui.ElementManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.management.gui.ElementManagerCourseAndExchange;
import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.management.gui.ElementManagerTable;
import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.management.gui.WebsiteManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * handles the user interaction with the element tab
 */
@Controller
public class HistoricWebsiteElementTabController {

    @FXML private ListView<WebsiteElement> elementList;
    @FXML private ChoiceBox<Website> websiteChoiceBox;
    @FXML private BorderPane subPane;
    @FXML private BorderPane rightPanelBox;
    @FXML private SplitPane rootNode;

    @Autowired
    private WebsiteManager websiteManager;
    @Autowired
    private ElementManagerTable scrapingTableManager;
    @Autowired
    private ElementManagerCourseAndExchange scrapingCourseAndExchangeManager;
    @Autowired
    private NewHistoricElementPopupController newHistoricElementPopupController;
    @Autowired
    private HistoricTableSubController historicTableSubController;
    @Autowired
    private ScrapingElementsTabController scrapingElementsTabController;

    private ObservableList<WebsiteElement> elementObservableList;
    private final ObservableList<Website> websiteObservableList = FXCollections.observableArrayList();
    private static final BorderPane noSelectionReplacement = new BorderPane(new Label(
            "Wählen Sie eine Konfiguration aus oder erstellen Sie eine neue (unten links)"));

    private boolean inlineValidation = false;

    /**
     * called when loading the fxml file
     */
    @FXML
    private void initialize() {
        setRightPanelBoxVisible(false);

        // load and show element-configs
        elementObservableList = scrapingTableManager.initWebsiteElementList(elementList, true);

        // load and set websites for which we can create an element-config
        reloadWebsiteList();
        websiteChoiceBox.setItems(websiteObservableList);

        // set listeners
        elementList.getSelectionModel()
                .selectedItemProperty()
                .addListener((ov, oldWs, newWs) -> loadSpecificElement(newWs));

        websiteChoiceBox.getSelectionModel()
                .selectedItemProperty()
                .addListener(x -> nullValidator(websiteChoiceBox));

        // select initial first element-config
        elementList.getSelectionModel().selectFirst();
    }

    /**
     * opens the new element config popup
     */
    @FXML
    private void handleNewElementButton() {
        PrimaryTabManager.loadFxml(
                "gui/tabs/historic/controller/element/newElementPopup.fxml",
                "Neues Element anlegen",
                elementList,
                true, newHistoricElementPopupController, false);
    }

    @FXML
    private void handleDeleteElementButton() {
        WebsiteElement element = getSelectedElement();

        if(element == null) {
            createAlert("Kein Element zum löschen ausgewählt!",
                    "Wählen Sie ein Element aus der Liste aus um dieses zu löschen."
            );
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Einstellungen löschen?");
        alert.setContentText("Bitte bestätigen Sie, dass sie dieses Element löschen möchten.");
        var window = websiteChoiceBox.getScene().getWindow();
        alert.setX(window.getX()+(window.getWidth()/2)-200);
        alert.setY(window.getY()+(window.getHeight()/2)-200);
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        scrapingTableManager.deleteSpecificElement(element);
        clearFields();
        reloadElementList();
        setRightPanelBoxVisible(false);
        elementList.getSelectionModel().selectFirst();

        scrapingElementsTabController.refresh();
    }

    @FXML
    private void handleSaveButton() {
        if(!elementIsSelected()) return;
        inlineValidation = true;
        if(!isValidInput()) return;

        WebsiteElement websiteElement = getSelectedElement();
        websiteElement.setWebsite(websiteChoiceBox.getValue());
        websiteElement.setElementCorrelations(historicTableSubController.getSecuritiesTypeCorrContainers().stream()
                .flatMap((Function<SecuritiesTypeCorrContainer, Stream<ElementIdentCorrelation>>) c -> c.getCorrelations().stream())
                .collect(Collectors.toList())
        );

        scrapingTableManager.saveTableSettings(websiteElement);

        Alert alert = new Alert(
                Alert.AlertType.INFORMATION,
                "Die Elementeinstellungen wurden gespeichert.",
                ButtonType.OK);
        alert.setHeaderText("Daten gespeichert!");
        PrimaryTabManager.setAlertPosition(alert , websiteChoiceBox);
        alert.showAndWait();

        scrapingElementsTabController.refresh();
    }

    /**
     * resets the input fields by reloading the configuration
     */
    @FXML
    private void handleResetButton() {
        loadSpecificElement(getSelectedElement());
    }

    /**
     * called when selecting a configuration inside the selection list
     * @param staleElement the element from the list (stale because it has no proxy session attached)
     */
    private void loadSpecificElement(WebsiteElement staleElement) {
        if (staleElement == null) return;

        inlineValidation = false;
        setRightPanelBoxVisible(true);

        scrapingTableManager.resetElementRepresentation(null, websiteChoiceBox, staleElement); // ToDo: contains bug?

        loadTable(staleElement);
    }

    public void reloadElementList() {
        elementObservableList.clear();
        elementObservableList.addAll(scrapingTableManager.getElements(true));
    }

    private void reloadWebsiteList() {
        websiteObservableList.clear();
        websiteObservableList.addAll(websiteManager.getWebsites(true));
    }

    /**
     * called from other controllers when relevant data changes and the view has to be refreshed
     */
    public void refresh() {
        WebsiteElement selected = getSelectedElement();
        reloadWebsiteList();
        reloadElementList();

        if(selected != null && elementList.getItems().contains(selected)) {
            elementList.getSelectionModel().select(selected);
        } else {
            elementList.getSelectionModel().selectFirst();
        }
    }

    public void selectElement(WebsiteElement element) {
        elementList.getSelectionModel().select(element);

        // it's not guaranteed to be the last in the list
        // nor is it guaranteed that it's the same object
        // match by id
        for(WebsiteElement wse : elementList.getItems()) {
            if(element.getId() == wse.getId()) elementList.getSelectionModel().select(wse);
        }
    }

    private boolean elementIsSelected() {
        if(getSelectedElement() == null) {
            createAlert("Kein Element ausgewählt!",
                    "Wählen Sie ein Element aus der Liste aus oder" +
                            " erstellen Sie ein neues bevor Sie Speichern."
            );
            return false;
        }
        return true;
    }

    public WebsiteElement getSelectedElement() {
        return elementList.getSelectionModel().getSelectedItem();
    }

    private void clearFields() {
        subPane.getChildren().clear();
        websiteChoiceBox.setValue(null);
    }

    private void createAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR, content, ButtonType.OK);
        alert.setHeaderText(title);
        var window = websiteChoiceBox.getScene().getWindow();
        alert.setY(window.getY() + (window.getHeight() / 2) - 200);
        alert.setX(window.getX() + (window.getWidth() / 2) - 200);
        alert.showAndWait();
    }

    private void loadTable(WebsiteElement websiteElement) {
        // load selection-table and db-description-table
        ElementManager.loadSubMenu(historicTableSubController,
                "gui/tabs/historic/controller/element/tableSubmenu.fxml", subPane);

        // load tab-menu with correlation-tables for each securities-type
        for (SecuritiesType securitiesType : SecuritiesType.values()){
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(WMScrape.class.getResource(
                        "gui/tabs/historic/controller/element/tableCorrelation.fxml"
                ));

                SecuritiesTypeCorrContainer corrContainer = new SecuritiesTypeCorrContainer(securitiesType, websiteElement, scrapingTableManager);
                historicTableSubController.getSecuritiesTypeCorrContainers().add(corrContainer);
                fxmlLoader.setControllerFactory(param -> corrContainer);

                // add tab to tab-pane
                historicTableSubController.getSecuritiesTypeTabPane()
                        .getTabs()
                        .add(new Tab(securitiesType.getDisplayText(), fxmlLoader.load()));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private boolean isValidInput() {
        inlineValidation = true;
        return nullValidator(websiteChoiceBox);
    }

    private boolean emptyValidator(TextInputControl input) {
        boolean isValid = input.getText() != null && !input.getText().isBlank();
        PrimaryTabManager.decorateField(input, "Dieses Feld darf nicht leer sein!", isValid, inlineValidation);
        return isValid;
    }

    private boolean nullValidator(ChoiceBox<Website> choiceBox) {
        boolean isValid = choiceBox.getValue() != null;
        PrimaryTabManager.decorateField(choiceBox, "Es muss eine Auswahl getroffen werden!", isValid,
                inlineValidation);
        return isValid;
    }

    private boolean urlValidator(TextInputControl input) {
        String value = input.getText();
        if(value==null) return true;

        boolean isValid = value.matches("^(https?://.+)$");
        PrimaryTabManager.decorateField(input, "Die URL muss mit http:// oder https:// beginnen!", isValid,
                inlineValidation);
        return isValid;
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
