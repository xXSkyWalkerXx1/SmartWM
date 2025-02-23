package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.portfolios.portfolio;

import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.Navigator;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabController;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Portfolio;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.State;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.interfaces.Openable;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners.OwnerService;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.portfolios.PortfolioService;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.EditableView;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.InvestmentGuidelineTable;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.PortfolioTreeView;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.TableFactory;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.NoSuchElementException;

@Controller
public class PortfolioOverviewController extends EditableView implements Openable {

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

    @FXML
    private void initialize() {
        inputState.getItems().setAll(State.values());

        inputPortfolioName.textProperty().addListener((observable, oldValue, newValue) -> {
            portfolio.setName(newValue);
        });
        inputOwner.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) portfolio.setOwner(newValue);
        });
        inputState.valueProperty().addListener((observable, oldValue, newValue) -> {
            portfolio.setState(newValue);
        });
    }

    @Override
    public void open() {
        portfolio = (Portfolio) portfolioManagementManager
                .getPortfolioController()
                .getPortfolioOverviewTab()
                .getProperties()
                .get(PortfolioManagementTabController.TAB_PROPERTY_ENTITY);
        if (!portfolio.isChanged()) {
            try {
                portfolio = portfolioService.findById(portfolio.getId());
            } catch (NoSuchElementException e) {
                e.printStackTrace();
            }
        }
        // notice: if you call 'setAll' the last processed/added item seems to be selected
        inputOwner.getItems().setAll(ownerService.getAll());
        loadPortfolioData();

        // Initialize onUnsavedChangesAction-dialog
        super.initialize(
                portfolio,
                portfolioManagementManager,
                this::onSave,
                this::onReset
        );
    }

    @FXML
    private void onOpenOwner() {
        Navigator.navigateToOwner(portfolioManagementManager, portfolio.getOwner(), true);
    }

    @FXML
    private void onReset() {
        portfolio.restore();
        loadPortfolioData();
    }

    @FXML
    private void onRemove() {
        portfolioService.delete(portfolio, null);
        portfolioManagementManager.getPortfolioController().navigateBackAfterDeletion(portfolio);
    }

    @FXML
    private void onSave() {
        // Validate first
        if (portfolioService.isInputInvalid(inputPortfolioName, portfolio, outputDeactivatedAt)) return;

        // If everything is valid, we can save the portfolio
        if (!portfolioService.save(portfolio)) return;
        // Refresh data and check for inconsistencies
        portfolio = portfolioService.findById(portfolio.getId());
        loadPortfolioData();

        // Finally, show success-dialog
        PrimaryTabManager.showInfoDialog(
                "Portfolio aktualisiert",
                "Das aktuelle Portfolio wurde erfolgreich aktualisiert.",
                inputPortfolioName
        );
    }

    private void loadPortfolioData() {
        portfolioManagementManager.getPortfolioController().refreshCrumbs();

        // Load data
        inputPortfolioName.setText(portfolio.getName());
        inputOwner.getSelectionModel().select(portfolio.getOriginalOwner());
        inputState.getSelectionModel().select(portfolio.getState());
        outputCreatedAt.setText(portfolio.getCreatedAt().toLocaleString());
        outputDeactivatedAt.setText(portfolio.getDeactivatedAt() != null ? portfolio.getDeactivatedAt().toLocaleString() : "");

        // Load tables
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
                portfolioManagementManager
        );
        portfolioTreeView.setTooltip(new Tooltip("Auflistung der im Portfolio enthaltenen Konten und Depots."));
        portfolioTreeView.setShowRoot(false);
        portfolioTreeViewPane.getChildren().setAll(portfolioTreeView);
    }
}
