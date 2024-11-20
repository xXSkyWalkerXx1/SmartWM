package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.portfolios.portfolio;

import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabController;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Portfolio;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.State;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.interfaces.Openable;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners.OwnerService;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.portfolios.PortfolioService;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.InvestmentGuidelineTable;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.PortfolioTreeView;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.TableFactory;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

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
    ComboBox<State> inputState;
    @FXML
    TextField outputCreatedAt;
    @FXML
    TextField outputDeactivatedAt;
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

        loadPortfolioData();
    }

    @FXML
    private void onReset() {
        loadPortfolioData();
    }

    @FXML
    private void onSave() {
        // Validate first
        if (portfolioService.isPortfolioInputInvalid(
                inputPortfolioName, portfolio,
                (Control) commissionSchemeTablePane.getChildren().get(0)
        )) return;

        // If everything is valid, we can create and save the new portfolio
        portfolioService.savePortfolio(portfolio, false, inputPortfolioName, inputOwner);

        // Finally, show success-dialog
        PrimaryTabManager.showInfoDialog(
                "Portfolio aktualisiert",
                "Das aktuelle Portfolio wurde erfolgreich aktualisiert.",
                inputPortfolioName
        );
    }

    private void loadPortfolioData() {
        inputPortfolioName.setText(portfolio.getName());

        inputOwner.getItems().addAll(ownerService.getAllOwners());
        inputOwner.getSelectionModel().select(portfolio.getOwner());

        inputState.getItems().addAll(State.values());
        inputState.getSelectionModel().select(portfolio.getState());

        outputCreatedAt.setText(portfolio.getCreatedAt().toString());
        outputDeactivatedAt.setText(portfolio.getDeactivatedAt() != null ? portfolio.getDeactivatedAt().toString() : "");

        commissionSchemeTablePane.getChildren().clear();
        commissionSchemeTablePane.getChildren().add(new InvestmentGuidelineTable(
                commissionSchemeTablePane,
                portfolio.getInvestmentGuideline().getEntries()
        ));

        commissionSchemeLocationTablePane.getChildren().clear();
        commissionSchemeLocationTablePane.getChildren().add(TableFactory.createPortfolioDivisionByLocationTable(
                commissionSchemeLocationTablePane,
                portfolio.getInvestmentGuideline().getDivisionByLocation()
        ));

        commissionSchemeCurrencyTablePane.getChildren().clear();
        commissionSchemeCurrencyTablePane.getChildren().add(TableFactory.createPortfolioDivisionByCurrencyTable(
                commissionSchemeCurrencyTablePane,
                portfolio.getInvestmentGuideline().getDivisionByCurrency()
        ));

        var portfolioTreeView = new PortfolioTreeView(
                portfolioTreeViewPane,
                List.of(portfolio),
                portfolioManagementManager,
                false
        );
        portfolioTreeView.setShowRoot(false);
        portfolioTreeViewPane.getChildren().clear();
        portfolioTreeViewPane.getChildren().add(portfolioTreeView);
    }
}
