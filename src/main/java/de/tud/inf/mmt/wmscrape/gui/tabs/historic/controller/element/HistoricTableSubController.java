package de.tud.inf.mmt.wmscrape.gui.tabs.historic.controller.element;

import de.tud.inf.mmt.wmscrape.gui.tabs.historic.controller.HistoricWebsiteElementTabController;
import de.tud.inf.mmt.wmscrape.gui.tabs.historic.data.SecuritiesTypeCorrContainer;
import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.correlation.description.ElementDescCorrelation;
import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.correlation.identification.ElementIdentCorrelation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

/**
 * controller which handles the configuration of the identifier correlation of
 * database columns and website table columns
 */
@Controller
public class HistoricTableSubController extends TableSubControllerBase {

    private List<SecuritiesTypeCorrContainer> securitiesTypeCorrContainers = new ArrayList<>();

    @FXML
    private TabPane securitiesTypeTabPane;

    @Autowired
    private HistoricWebsiteElementTabController historicWebsiteElementTabController;

    @FXML
    @Override
    protected void initialize() {
        websiteElement = historicWebsiteElementTabController.getSelectedElement();

        scrapingTableManager.initStockSelectionTable(websiteElement, selectionTable, false);
        scrapingTableManager.initCourseOrStockDescCorrelationTable(websiteElement, elementDescCorrelationTableView);
    }

    // region Getters
    public List<SecuritiesTypeCorrContainer> getSecuritiesTypeCorrContainers() {
        return securitiesTypeCorrContainers;
    }

    public TabPane getSecuritiesTypeTabPane() {
        return securitiesTypeTabPane;
    }

    public ObservableList<ElementDescCorrelation> getElementDescCorrelations() {
        return elementDescCorrelationTableView.getItems();
    }

    /***
     * @return Mixed collection of all correlations (contains of all securities-type).
     */
    @Override
    public ObservableList<ElementIdentCorrelation> getDbCorrelations() {
        final List<ElementIdentCorrelation> elementIdentCorrelations = new ArrayList<>();
        securitiesTypeCorrContainers.forEach(corrContainer -> elementIdentCorrelations.addAll(corrContainer.getCorrelations()));
        return FXCollections.observableArrayList(elementIdentCorrelations);
    }
    // endregion

    public void setSecuritiesTypeCorrContainers(List<SecuritiesTypeCorrContainer> corrContainers) {
        this.securitiesTypeCorrContainers = corrContainers;
    }
}
