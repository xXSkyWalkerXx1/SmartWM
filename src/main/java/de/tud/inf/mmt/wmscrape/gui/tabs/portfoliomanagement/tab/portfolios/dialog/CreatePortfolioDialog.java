package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.portfolios.dialog;

import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Portfolio;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners.OwnerService;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.portfolios.PortfolioService;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.InvestmentGuidelineTable;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.TableFactory;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class CreatePortfolioDialog {

    Portfolio portfolio = new Portfolio();

    @Autowired
    OwnerService ownerService;
    @Autowired
    PortfolioService portfolioService;
    @Autowired
    private PortfolioManagementTabManager portfolioManagementTabManager;

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

    CreatePortfolioDialog() {
        portfolio.getInvestmentGuideline().initializeEntries();
    }

    @FXML
    public void initialize() {
        inputOwner.getItems().addAll(ownerService.getAllOwners());
        inputOwner.getSelectionModel().selectFirst();

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
    private void onCancel() {
        portfolioManagementTabManager.getPortfolioController().getPortfolioListController().open();
        inputPortfolioName.getScene().getWindow().hide();
    }

    @FXML
    private void onSave() {
        // Validate first
        if (portfolioService.isPortfolioInputInvalid(
                inputPortfolioName, portfolio,
                (Control) commissionSchemeTablePane.getChildren().get(0)
        )) return;

        // If everything is valid, we can create and save the new portfolio
        portfolioService.savePortfolio(portfolio, true, inputPortfolioName, inputOwner);
        onCancel();

        // Finally, show success-dialog
        PrimaryTabManager.showInfoDialog(
                "Portfolio angelegt",
                "Das neue Portfolio wurde erfolgreich angelegt.",
                inputPortfolioName
        );
    }
}
