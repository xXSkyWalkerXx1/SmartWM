package de.tud.inf.mmt.wmscrape.gui.tabs;

import de.tud.inf.mmt.wmscrape.gui.tabs.dbdata.controller.DataTabController;
import de.tud.inf.mmt.wmscrape.gui.tabs.historic.controller.HistoricTabController;
import de.tud.inf.mmt.wmscrape.gui.tabs.imports.controller.ImportTabController;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.ScrapingTabsController;
import de.tud.inf.mmt.wmscrape.gui.tabs.visualization.controller.VisualizationTabController;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabController;
import de.tud.inf.mmt.wmscrape.springdata.SpringIndependentData;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
public class PrimaryTabController {

    @FXML
    private Button logoutButton;
    @FXML
    private TabPane primaryTabPane;
    @FXML
    private Label currentUserLabel;

    @Autowired
    ConfigurableApplicationContext applicationContext;

    @Autowired
    private ImportTabController importTabController;
    @Autowired
    private ScrapingTabsController scrapingTabsController;
    @Autowired
    private DataTabController dataTabController;
    @Autowired
    private HistoricTabController historicTabController;
    @Autowired
    private VisualizationTabController visualizationTabController;
    @Autowired
    private PortfolioManagementTabController portfolioManagementTabController;

    /**
     * called when loading the fxml file
     */
    @FXML
    private void initialize() throws IOException {
        currentUserLabel.setText("Aktueller Nutzer: " + SpringIndependentData.getUsername());

        Parent parent = PrimaryTabManager.loadTabFxml("gui/tabs/dbdata/controller/dataTab.fxml", dataTabController);
        Tab dataTab = createPrimaryTab("Daten", parent);
        primaryTabPane.getTabs().add(dataTab);

        parent = PrimaryTabManager.loadTabFxml("gui/tabs/imports/controller/importTab.fxml", importTabController);
        Tab importTab = createPrimaryTab("Import", parent);
        primaryTabPane.getTabs().add(importTab);

        parent = PrimaryTabManager.loadTabFxml("gui/tabs/scraping/controller/scrapingTabs.fxml", scrapingTabsController);
        Tab tab = createPrimaryTab("Scraping", parent);
        primaryTabPane.getTabs().add(tab);

        parent = PrimaryTabManager.loadTabFxml("gui/tabs/visualization/controller/visualizeTab.fxml", visualizationTabController);
        Tab visualizeTab = createPrimaryTab("Darstellung", parent);
        primaryTabPane.getTabs().add(visualizeTab);

        parent = PrimaryTabManager.loadTabFxml("gui/tabs/portfoliomanagement/portfolioManagement.fxml", portfolioManagementTabController);
        Tab managementTab = createPrimaryTab("Portfoliomanagement", parent);
        primaryTabPane.getTabs().add(managementTab);

        primaryTabPane.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> {
            if (nv.equals(dataTab)) dataTabController.handleResetButton();
            if (nv.equals(importTab)) importTabController.refreshCorrelationTables();
            if (nv.equals(visualizeTab)) visualizationTabController.fillSelectionTables();
            if (nv.equals(managementTab)) portfolioManagementTabController.showPortfolioManagementTabs();
        });

        primaryTabPane.setStyle("-fx-tab-min-height: 30px;" + "-fx-tab-max-height: 30px;" + "-fx-tab-min-width: 150px;" + "-fx-tab-max-width: 150px;" + "-fx-alignment: CENTER;");
    }

    /**
     * Create a styled primary tab, f.e. as root.
     */
    public static Tab createPrimaryTab(String title, Parent parent) {
        return createStyledTab(title, parent, "#0064C7", "#014180");
    }

    /**
     * Create a styled sub or secondary tab, f.e. as leave.
     */
    public static Tab createSubTab(String title, Parent parent) {
        return createStyledTab(title, parent, "#b4dafd", "#50a9fb");
    }

    /**
     * Base-method to create a styled tab. Colors are in hex, f.e.: '#9accfe'.
     */
    private static Tab createStyledTab(String title, Parent parent, String colorDefault, String colorOnSelected) {
        String style = "-fx-background-color: %s; -fx-background-insets: 0, 1; -fx-background-radius: 0, 0 0 0 0;";
        Tab tab = new Tab(title, parent);

        tab.setStyle(String.format(style, colorDefault));
        tab.setOnSelectionChanged(event -> {
            if (tab.isSelected()) {
                tab.setStyle(String.format(style, colorOnSelected));
            } else {
                tab.setStyle(String.format(style, colorDefault));
            }
        });
        return tab;
    }

    /**
     * closes the spring application context and returns to the login menu
     */
    @FXML
    private void handleLogoutButton() {
        applicationContext.close();
        PrimaryTabManager.loadFxml("gui/login/controller/existingUserLogin.fxml", "Login", logoutButton, false, null, false);
    }

    public ConfigurableApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
