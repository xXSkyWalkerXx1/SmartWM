package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.portfolios.portfolio;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabController;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Portfolio;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.interfaces.Openable;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners.OwnerService;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.portfolios.PortfolioService;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.InvestmentGuidelineTable;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.TableFactory;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class PortfolioOverviewController implements Openable {

    Portfolio portfolio;

    @Autowired
    OwnerService ownerService;
    @Autowired
    PortfolioService portfolioService;
    @Autowired
    PortfolioManagementTabManager portfolioManagementManager;

    @FXML
    TextField inputPortfolioName;
    @FXML
    ComboBox<Owner> inputOwner;
    @FXML
    AnchorPane commissionSchemeTablePane;
    @FXML
    AnchorPane commissionSchemeLocationTablePane;
    @FXML
    AnchorPane commissionSchemeCurrencyTablePane;
    @FXML
    AnchorPane portfolioTreeViewPane;

    @Override
    public void open() {
        portfolio = (Portfolio) portfolioManagementManager
                .getPortfolioController()
                .getPortfolioOverviewTab()
                .getProperties()
                .get(PortfolioManagementTabController.TAB_PROPERTY_ENTITY);

        commissionSchemeTablePane.getChildren().add(new InvestmentGuidelineTable(
                commissionSchemeTablePane,
                portfolio.getInvestmentGuideline().getEntries()
        ));
        commissionSchemeLocationTablePane.getChildren().add(TableFactory.createPortfolioDivisionByLocationTable(
                commissionSchemeLocationTablePane,
                portfolio.getInvestmentGuideline().getDivisionByLocation()
        ));
        commissionSchemeCurrencyTablePane.getChildren().add(TableFactory.createPortfolioDivisionByCurrencyTable(
                commissionSchemeCurrencyTablePane,
                portfolio.getInvestmentGuideline().getDivisionByCurrency()
        ));
    }

    @FXML
    private void onReset() {
    }

    @FXML
    private void onSave() {
    }

    private void loadPortfolioData() {
    }
}
