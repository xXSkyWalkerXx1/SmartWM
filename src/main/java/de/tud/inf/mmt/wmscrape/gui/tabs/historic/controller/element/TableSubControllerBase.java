package de.tud.inf.mmt.wmscrape.gui.tabs.historic.controller.element;

import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.controller.ScrapingElementsTabController;
import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.correlation.description.ElementDescCorrelation;
import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.correlation.identification.ElementIdentCorrelation;
import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.element.WebsiteElement;
import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.selection.ElementSelection;
import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.management.gui.ElementManagerCourseAndExchange;
import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.management.gui.ElementManagerTable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class TableSubControllerBase {

    protected WebsiteElement websiteElement;

    @FXML
    protected TableView<ElementSelection> selectionTable;
    @FXML
    protected TableView<ElementDescCorrelation> elementDescCorrelationTableView;

    @Autowired
    protected ScrapingElementsTabController scrapingElementsTabController;
    @Autowired
    protected ElementManagerCourseAndExchange manager;
    @Autowired
    protected ElementManagerTable scrapingTableManager;

    @FXML
    protected void initialize() {}

    // region Getters
    public ObservableList<ElementSelection> getSelections() {
        return selectionTable.getItems();
    }

    public ObservableList<ElementIdentCorrelation> getDbCorrelations() {
        return FXCollections.observableArrayList();
    }
    // endregion
}
