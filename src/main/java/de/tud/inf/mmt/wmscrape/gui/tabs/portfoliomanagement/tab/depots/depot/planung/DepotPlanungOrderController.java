package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.depots.depot.planung;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabManager;
import javafx.fxml.FXML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class DepotPlanungOrderController {

    @FXML
    PortfolioManagementTabManager portfolioManagementTabManager;

    @Autowired
    public DepotPlanungOrderController(PortfolioManagementTabManager portfolioManagementTabManager) {
        this.portfolioManagementTabManager = portfolioManagementTabManager;
    }
}
