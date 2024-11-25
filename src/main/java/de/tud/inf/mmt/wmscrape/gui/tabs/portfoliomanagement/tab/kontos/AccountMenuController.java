package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.kontos;

import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Account;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Depot;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.interfaces.Openable;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.kontos.dialog.CreateAccountDialog;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners.OwnerService;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.portfolios.PortfolioService;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.TableFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class AccountMenuController implements Openable {

    @Autowired
    AccountService accountService;
    @Autowired
    OwnerService ownerService;
    @Autowired
    PortfolioService portfolioService;
    @Autowired
    PortfolioManagementTabManager portfolioManagementTabManager;
    @Autowired
    CreateAccountDialog createAccountDialog;

    @FXML
    Button createAccountButton;
    @FXML
    AnchorPane accountTablePane;
    @FXML
    AnchorPane accountDepotsTablePane;
    @FXML
    Label sumLabel;

    @Override
    public void open() {
        List<Account> accounts = accountService.getAll();

        createAccountButton.setDisable(ownerService.getAllOwners().isEmpty() && portfolioService.getAll().isEmpty());

        accountTablePane.getChildren().add(TableFactory.createAccountsTable(
                accountTablePane,
                accountDepotsTablePane,
                accounts,
                this,
                portfolioManagementTabManager
        ));
        accountDepotsTablePane.getChildren().add(TableFactory.createAccountDepotsTable(
                accountDepotsTablePane,
                List.of(),
                portfolioManagementTabManager
        ));

        sumLabel.setText(String.valueOf(accounts.stream().mapToDouble(Account::getBalance).sum()));
    }

    @FXML
    private void onClickShowAccountHistory() {
        throw new NotImplementedException("Not implemented yet");
    }

    @FXML
    private void onClickCreateAccount() {
        PrimaryTabManager.loadFxml(
                "gui/tabs/portfoliomanagement/tab/kontos/dialog/create_account_dialog.fxml",
                "Neues Konto erstellen",
                createAccountButton,
                true,
                createAccountDialog,
                false
        );
        createAccountDialog.initialize();
    }

    public void setDepotTable(TableView<Depot> table) {
        accountDepotsTablePane.getChildren().clear();
        accountDepotsTablePane.getChildren().add(table);
    }
}
