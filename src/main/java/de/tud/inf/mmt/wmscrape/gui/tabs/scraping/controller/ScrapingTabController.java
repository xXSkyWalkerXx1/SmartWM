package de.tud.inf.mmt.wmscrape.gui.tabs.scraping.controller;

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
 * the controller for the complete scraping tab adding the sub-tabs
 */
@Controller
public class ScrapingTabController {
    @FXML private TabPane scrapingSubTabPane;
    @Autowired private ScrapingWebsiteTabController scrapingWebsiteTabController;
    @Autowired private ScrapingElementsTabController scrapingElementsTabController;
    @Autowired private ScrapingScrapeTabController scrapingScrapeTabController;

    /**
     * called when loading the fxml file
     */
    @FXML
    private void initialize() throws IOException {
        Tab tab;

        Parent parent = PrimaryTabManager.loadTabFxml("gui/tabs/scraping/controller/scrapingScrapeTab.fxml", scrapingScrapeTabController);
        tab = createSubTab("Scrapen" , parent);
        tab.selectedProperty().addListener((o,ov,nv) -> scrapingScrapeTabController.refresh());
        scrapingSubTabPane.getTabs().add(tab);

        parent = PrimaryTabManager.loadTabFxml("gui/tabs/scraping/controller/scrapingWebsitesTab.fxml", scrapingWebsiteTabController);
        tab = createSubTab("Webseiten" , parent);
        scrapingSubTabPane.getTabs().add(tab);

        parent = PrimaryTabManager.loadTabFxml("gui/tabs/scraping/controller/scrapingElementsTab.fxml", scrapingElementsTabController);
        tab = createSubTab("Elemente" , parent);

        tab.selectedProperty().addListener((o,ov,nv) -> {
            if(nv) {
                scrapingElementsTabController.refresh();
            }
        });
        scrapingSubTabPane.getTabs().add(tab);
    }
}
