package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.portfolios.dialog;

import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Portfolio;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.interfaces.Openable;
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
public class CreatePortfolioDialog implements Openable {

    Portfolio portfolio;

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

    @FXML
    protected void initialize() {
        if (portfolio == null) return; // Portfolio is not set yet

        // Create and show tables
        commissionSchemeTablePane.getChildren().setAll(new InvestmentGuidelineTable(
                commissionSchemeTablePane,
                portfolio.getInvestmentGuideline().getEntries()
        ));
        commissionSchemeLocationTablePane.getChildren().setAll(TableFactory.createPortfolioDivisionByLocationTable(
                commissionSchemeLocationTablePane,
                portfolio.getInvestmentGuideline().getDivisionByLocation()
        ));
        commissionSchemeCurrencyTablePane.getChildren().setAll(TableFactory.createPortfolioDivisionByCurrencyTable(
                commissionSchemeCurrencyTablePane,
                portfolio.getInvestmentGuideline().getDivisionByCurrency()
        ));
    }

    @Override
    public void open() {
        // Initialize the portfolio
        portfolio = new Portfolio();
        portfolio.getInvestmentGuideline().initializeEntries();
        initialize();

        // Initialize combo-box with all owners
        inputOwner.getItems().setAll(ownerService.getAll());
        inputOwner.getSelectionModel().selectFirst();
    }

    @FXML
    protected void onCancel() {
        portfolioManagementTabManager.getPortfolioController().getPortfolioListController().open();
        inputPortfolioName.getScene().getWindow().hide();
    }

    @FXML
    protected void onSave() {
        // Validate first
        if (portfolioService.isInputInvalid(
                inputPortfolioName, portfolio,
                (Control) commissionSchemeTablePane.getChildren().get(0)
        )) return;

        // If everything is valid, we can create and save the new portfolio
        portfolioService.writeInput(portfolio, true, inputPortfolioName, inputOwner);
        if (!portfolioService.save(portfolio)) return;
        onCancel();

        // Finally, show success-dialog
        PrimaryTabManager.showInfoDialog(
                "Portfolio angelegt",
                "Das neue Portfolio wurde erfolgreich angelegt.",
                inputPortfolioName
        );
    }
}
