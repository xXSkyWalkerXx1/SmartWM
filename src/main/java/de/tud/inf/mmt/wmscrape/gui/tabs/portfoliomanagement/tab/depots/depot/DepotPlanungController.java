package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.depots.depot;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabManager;
import javafx.fxml.FXML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class DepotPlanungController {

    @FXML
    PortfolioManagementTabManager portfolioManagementTabManager;

    @Autowired
    public DepotPlanungController(PortfolioManagementTabManager portfolioManagementTabManager) {
        this.portfolioManagementTabManager = portfolioManagementTabManager;
    }

    @FXML
    private void initialize() {

    }

}
