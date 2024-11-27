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
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.Calendar;
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
    Pane commissionSchemeTablePane;
    @FXML
    Pane commissionSchemeLocationTablePane;
    @FXML
    Pane commissionSchemeCurrencyTablePane;
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
        portfolio = portfolioService.findById(portfolio.getName()).orElseThrow();
        loadPortfolioData();
    }

    @FXML
    private void onSave() {
        // Validate first
        if (portfolioService.isInputInvalid(inputPortfolioName, portfolio, outputDeactivatedAt)) return;

        // If everything is valid, we can create and save the new portfolio
        portfolioService.writeInput(portfolio, false, inputPortfolioName, inputOwner);
        portfolio.setState(inputState.getValue());
        portfolioService.save(portfolio);

        // Finally, show success-dialog
        PrimaryTabManager.showInfoDialog(
                "Portfolio aktualisiert",
                "Das aktuelle Portfolio wurde erfolgreich aktualisiert.",
                inputPortfolioName
        );
    }

    private void loadPortfolioData() {
        inputPortfolioName.setText(portfolio.getName());

        inputOwner.getItems().setAll(ownerService.getAll());
        inputOwner.getSelectionModel().select(portfolio.getOwner());

        inputState.getItems().setAll(State.values());
        inputState.getSelectionModel().select(portfolio.getState());

        outputCreatedAt.setText(portfolio.getCreatedAt().toLocaleString());
        outputDeactivatedAt.setText(portfolio.getDeactivatedAt() != null ? portfolio.getDeactivatedAt().toLocaleString() : "");

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
