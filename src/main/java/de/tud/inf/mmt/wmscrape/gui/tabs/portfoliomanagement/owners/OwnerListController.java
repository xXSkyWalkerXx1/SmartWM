package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.owners;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.BreadcrumbElement;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class OwnerListController {

    @FXML
    PortfolioManagementTabManager portfolioManagementTabManager;

    @FXML
    Button switchToOwner1Button;
    @FXML
    Button switchToOwner2Button;

    @Autowired
    public OwnerListController(PortfolioManagementTabManager portfolioManagementTabManager) {
        this.portfolioManagementTabManager = portfolioManagementTabManager;
    }

    @FXML
    private void initialize() {
        switchToOwner1Button.setOnAction(actionEvent -> {
            portfolioManagementTabManager.showInhaberTabs();
            portfolioManagementTabManager.setCurrentlyDisplayedElement(new BreadcrumbElement("Inhaber 1", "owner"));
        });
        switchToOwner2Button.setOnAction(actionEvent -> {
            portfolioManagementTabManager.showInhaberTabs();
            portfolioManagementTabManager.setCurrentlyDisplayedElement(new BreadcrumbElement("Inhaber 2", "konto"));
        });
    }

}
