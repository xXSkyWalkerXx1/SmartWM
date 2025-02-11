package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.portfolios.portfolio;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.BreadcrumbElement;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.BreadcrumbElementType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class PortfolioKontosController {

    @FXML
    PortfolioManagementTabManager portfolioManagementTabManager;
    @FXML
    VBox kontoListContainer;
    @Autowired
    public PortfolioKontosController(PortfolioManagementTabManager portfolioManagementTabManager) {
        this.portfolioManagementTabManager = portfolioManagementTabManager;
    }

    @FXML
    private void initialize() {
    }
}
