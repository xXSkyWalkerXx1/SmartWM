package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners.owner;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabController;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Account;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Depot;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.interfaces.Openable;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.TableFactory;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;

@Controller
public class OwnerPortfoliosController implements Openable {

    Owner owner;

    @Autowired
    PortfolioManagementTabManager portfolioManagementManager;

    @FXML
    AnchorPane portfoliosTablePane;

    @FXML
    AnchorPane accountsTablePane;

    @FXML
    AnchorPane depotsTablePane;

    @Override
    public void open() {
        owner = (Owner) portfolioManagementManager
                .getPortfolioController()
                .getInhaberPortfoliosTab()
                .getProperties()
                .get(PortfolioManagementTabController.TAB_PROPERTY_ENTITY);

        // Show tables
        portfoliosTablePane.getChildren().add(TableFactory.createOwnerPortfoliosTable(
                portfoliosTablePane,
                accountsTablePane,
                depotsTablePane,
                owner.getPortfolios().stream().toList(),
                this
        ));

        setAccountTable(TableFactory.createOwnerPortfolioAccountTable(
                accountsTablePane,
                new ArrayList<>(),
                this
        ));

        setDepotTable(TableFactory.createOwnerPortfolioDepotTable(
                depotsTablePane,
                new ArrayList<>(),
                this
        ));
    }

    public void setAccountTable(TableView<Account> table) {
        accountsTablePane.getChildren().clear();
        accountsTablePane.getChildren().add(table);
    }

    public void setDepotTable(TableView<Depot> table) {
        depotsTablePane.getChildren().clear();
        depotsTablePane.getChildren().add(table);
    }

    public PortfolioManagementTabManager getPortfolioManagementManager() {
        return portfolioManagementManager;
    }
}
