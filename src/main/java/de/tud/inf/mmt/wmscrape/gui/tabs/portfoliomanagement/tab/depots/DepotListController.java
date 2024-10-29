package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.depots;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.BreadcrumbElement;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;


@Controller
public class DepotListController {
    @FXML
    private Button switchToDepot1Button;
    @FXML
    private Button switchToDepot2Button;
    @FXML
    private Button switchToDepot3Button;
    @FXML
    private PortfolioManagementTabManager portfolioManagementTabManager;

    @Autowired
    public DepotListController(PortfolioManagementTabManager portfolioManagementTabManager) {
        this.portfolioManagementTabManager = portfolioManagementTabManager;
    }

    @FXML
    private void initialize() {
        switchToDepot1Button.setOnAction(actionEvent -> {
            portfolioManagementTabManager.showDepotTabs();
            portfolioManagementTabManager.setCurrentlyDisplayedElement(new BreadcrumbElement("Depot 1", "depot"));
        });
        switchToDepot2Button.setOnAction(actionEvent -> {
            portfolioManagementTabManager.showDepotTabs();
            portfolioManagementTabManager.setCurrentlyDisplayedElement(new BreadcrumbElement("Depot 2", "depot"));
        });
        switchToDepot3Button.setOnAction(actionEvent -> {
            portfolioManagementTabManager.showDepotTabs();
            portfolioManagementTabManager.setCurrentlyDisplayedElement(new BreadcrumbElement("Depot 3", "depot"));
        });
    }

}
