package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners.owner;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabController;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Account;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.interfaces.Openable;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners.OwnerService;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.TableFactory;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.NoSuchElementException;

@Controller
public class OwnerDepotsController implements Openable {

    Owner owner;

    @Autowired
    PortfolioManagementTabManager portfolioManagementManager;
    @Autowired
    OwnerService ownerService;

    @FXML
    AnchorPane depotsTablePane;

    @FXML
    AnchorPane depotAccountsTablePane;

    @Override
    public void open() {
        owner = (Owner) portfolioManagementManager
                .getPortfolioController()
                .getInhaberDepotsTab()
                .getProperties()
                .get(PortfolioManagementTabController.TAB_PROPERTY_ENTITY);
        try {
            owner = ownerService.getOwnerById(owner.getId());
        } catch (NoSuchElementException e) {
            return;
        }

        depotsTablePane.getChildren().add(TableFactory.createOwnerDepotsTable(
                depotsTablePane,
                depotAccountsTablePane,
                owner.getDepots().stream().toList(),
                this
        ));

        setDepotAccountsTable(TableFactory.createOwnerDepotAccountsTable(
                depotAccountsTablePane,
                new ArrayList<>(),
                portfolioManagementManager
        ));
    }

    public void setDepotAccountsTable(TableView<Account> table) {
        depotAccountsTablePane.getChildren().clear();
        depotAccountsTablePane.getChildren().add(table);
    }

    public PortfolioManagementTabManager getPortfolioManagementManager() {
        return portfolioManagementManager;
    }
}
