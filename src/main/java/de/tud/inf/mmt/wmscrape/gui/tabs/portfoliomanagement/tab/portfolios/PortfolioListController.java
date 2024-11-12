package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.portfolios;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Account;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Depot;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners.OwnerService;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.TableFactory;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;

@Controller
public class PortfolioListController {

    @Autowired
    private PortfolioService portfolioService;
    @Autowired
    private PortfolioManagementTabManager portfolioManagementTabManager;

    @FXML
    AnchorPane portfoliosTablePane;
    @FXML
    AnchorPane accountsTablePane;
    @FXML
    AnchorPane depotsTablePane;

    @FXML
    private void initialize() {
        portfoliosTablePane.getChildren().add(TableFactory.createPortfolioTable(
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

    }

    public void setAccountTable(TableView<Account> table){
        accountsTablePane.getChildren().clear();
        accountsTablePane.getChildren().add(table);
    }

    public void setDepotTable(TableView<Depot> table){
        depotsTablePane.getChildren().clear();
        depotsTablePane.getChildren().add(table);
    }

    public PortfolioManagementTabManager getPortfolioManagementManager() {
        return portfolioManagementTabManager;
    }
}
