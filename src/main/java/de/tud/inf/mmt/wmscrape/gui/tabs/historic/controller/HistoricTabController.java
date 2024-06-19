package de.tud.inf.mmt.wmscrape.gui.tabs.historic.controller;

import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;

import static de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabController.createSubTab;

/**
 * initializes scraping menu
 */
@Controller
public class HistoricTabController {
    @FXML private TabPane historicSubTabPane;
    @Autowired private HistoricWebsiteTabController historicWebsiteTabController;
    @Autowired private HistoricWebsiteElementTabController historicWebsiteElementTabController;
    @Autowired private HistoricScrapeTabController historicScrapeTabController;

    /**
     * called when loading the fxml file
     */
    @FXML
    private void initialize() throws IOException {
        Tab tab;

        Parent parent = PrimaryTabManager.loadTabFxml("gui/tabs/historic/controller/historicScrapeTab.fxml", historicScrapeTabController);
        tab = createSubTab("Scrapen" , parent);
        tab.selectedProperty().addListener((o,ov,nv) -> historicScrapeTabController.refresh());
        historicSubTabPane.getTabs().add(tab);

        parent = PrimaryTabManager.loadTabFxml("gui/tabs/historic/controller/historicWebsitesTab.fxml", historicWebsiteTabController);
        tab = createSubTab("Webseiten" , parent);
        historicSubTabPane.getTabs().add(tab);

        parent = PrimaryTabManager.loadTabFxml("gui/tabs/historic/controller/historicElementsTab.fxml", historicWebsiteElementTabController);
        tab = createSubTab("Elemente" , parent);
        tab.selectedProperty().addListener((o,ov,nv) -> historicWebsiteElementTabController.refresh());
        historicSubTabPane.getTabs().add(tab);
    }
}
