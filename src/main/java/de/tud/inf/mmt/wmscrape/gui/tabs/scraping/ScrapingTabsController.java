package de.tud.inf.mmt.wmscrape.gui.tabs.scraping;

import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.historic.controller.HistoricTabController;
import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.controller.ScrapingTabController;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;

import static de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabController.createPrimaryTab;
import static de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabController.createSubTab;

@Controller
public class ScrapingTabsController {

    @FXML
    private TabPane scrapingTabPane = new TabPane();
    @Autowired
    private ScrapingTabController scrapingTabController;
    @Autowired
    private HistoricTabController historicTabController;

    @FXML
    private void initialize() throws IOException {
        Tab tab;

        Parent parent = PrimaryTabManager.loadTabFxml("gui/tabs/scraping/controller/scrapingTab.fxml", scrapingTabController);
        tab = createSubTab("Aktuell", parent);
        scrapingTabPane.getTabs().add(tab);

        parent = PrimaryTabManager.loadTabFxml("gui/tabs/historic/controller/historicTab.fxml", historicTabController);
        Tab historicTab = createSubTab("Historisch", parent);
        scrapingTabPane.getTabs().add(historicTab);

        scrapingTabPane.setStyle("-fx-tab-min-height: 30px;" + "-fx-tab-max-height: 30px;" + "-fx-tab-min-width: 150px;" + "-fx-tab-max-width: 150px;" + "-fx-alignment: CENTER;");
    }
}
