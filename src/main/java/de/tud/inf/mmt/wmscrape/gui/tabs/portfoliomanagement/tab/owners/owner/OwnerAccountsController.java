package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners.owner;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabController;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.interfaces.Openable;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners.OwnerService;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.TableFactory;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.NoSuchElementException;

@Controller
public class OwnerAccountsController implements Openable {

    Owner owner;

    @Autowired
    PortfolioManagementTabManager portfolioManagementTabManager;
    @Autowired
    OwnerService ownerService;

    @FXML
    AnchorPane accountsTablePane;

    @Override
    public void open() {
        owner = (Owner) portfolioManagementTabManager
                .getPortfolioController()
                .getInhaberKontosTab()
                .getProperties()
                .get(PortfolioManagementTabController.TAB_PROPERTY_ENTITY);
        try {
            owner = ownerService.getOwnerById(owner.getId());
        } catch (NoSuchElementException e) {
            return;
        }

        accountsTablePane.getChildren().add(TableFactory.createOwnerAccountsTable(
                accountsTablePane,
                owner.getAccounts().stream().toList(),
                portfolioManagementTabManager
        ));
    }
}
