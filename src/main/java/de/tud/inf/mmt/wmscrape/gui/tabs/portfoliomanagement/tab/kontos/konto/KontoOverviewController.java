package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.kontos.konto;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.interfaces.Openable;
import javafx.fxml.FXML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class KontoOverviewController implements Openable {

    @FXML
    PortfolioManagementTabManager portfolioManagementTabManager;

    @Autowired
    public KontoOverviewController(PortfolioManagementTabManager portfolioManagementTabManager) {
        this.portfolioManagementTabManager = portfolioManagementTabManager;
    }

    @Override
    public void open() {

    }
}
