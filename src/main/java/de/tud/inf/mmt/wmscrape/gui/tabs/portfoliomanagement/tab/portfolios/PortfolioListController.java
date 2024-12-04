package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.portfolios;

import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Account;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Depot;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.interfaces.Openable;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners.OwnerService;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.portfolios.dialog.CreatePortfolioDialog;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.TableFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;

@Controller
public class PortfolioListController implements Openable {

    @Autowired
    private PortfolioService portfolioService;
    @Autowired
    private OwnerService ownerService;
    @Autowired
    private PortfolioManagementTabManager portfolioManagementTabManager;
    @Autowired
    private CreatePortfolioDialog createPortfolioDialog;

    @FXML
    Button createPortfolioButton;
    @FXML
    AnchorPane portfoliosTablePane;
    @FXML
    AnchorPane accountsTablePane;
    @FXML
    AnchorPane depotsTablePane;

    public void open() {
        createPortfolioButton.setDisable(ownerService.getAll().isEmpty());

        portfoliosTablePane.getChildren().setAll(TableFactory.createPortfolioTable(
                portfoliosTablePane,
                accountsTablePane,
                depotsTablePane,
                portfolioService.getAll(),
                this
        ));
        setAccountTable(TableFactory.createOwnerPortfolioAccountTable(
                accountsTablePane,
                new ArrayList<>(),
                portfolioManagementTabManager
        ));
        setDepotTable(TableFactory.createOwnerPortfolioDepotTable(
                depotsTablePane,
                new ArrayList<>(),
                portfolioManagementTabManager
        ));
    }

    @FXML
    private void onClickCreatePortfolio() {
        PrimaryTabManager.loadFxml(
                "gui/tabs/portfoliomanagement/tab/portfolios/dialog/create_portfolio_dialog.fxml",
                "Neues Portfolio erstellen",
                createPortfolioButton,
                true,
                createPortfolioDialog,
                false
        );
        createPortfolioDialog.initialize();
    }

    public void setAccountTable(TableView<Account> table){
        accountsTablePane.getChildren().setAll(table);
    }

    public void setDepotTable(TableView<Depot> table){
        depotsTablePane.getChildren().setAll(table);
    }

    public PortfolioManagementTabManager getPortfolioManagementManager() {
        return portfolioManagementTabManager;
    }
}
