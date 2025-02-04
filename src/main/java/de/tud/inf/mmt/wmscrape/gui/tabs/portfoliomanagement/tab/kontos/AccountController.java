package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.kontos;

import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Account;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Depot;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.interfaces.Openable;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.kontos.dialog.CreateAccountDialog;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners.OwnerService;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.portfolios.PortfolioService;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.FormatUtils;
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
import java.util.stream.DoubleStream;

@Controller
public class AccountController implements Openable {

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

        // Load tables
        accountTablePane.getChildren().setAll(TableFactory.createAccountsTable(
                accountTablePane,
                accountDepotsTablePane,
                accounts,
                this,
                portfolioManagementTabManager,
                accountService
        ));

        var accountDepotsTable = TableFactory.createAccountDepotsTable(
                accountDepotsTablePane,
                List.of(),
                portfolioManagementTabManager
        );
        accountDepotsTable.setPlaceholder(new Label("Kein Verrechnungskonto ausgewählt"));
        setDepotTable(accountDepotsTable);

        // Calculate sum of all account-balances in EUR
        sumLabel.setText(String.format(
                "%s €",
                FormatUtils.formatFloat((float) accounts.stream()
                        .flatMapToDouble(account -> DoubleStream.of(account.getValue(accountService).doubleValue()))
                        .sum()
                )));
    }

    @FXML
    private void onClickShowAccountHistory() {
        throw new NotImplementedException("Not implemented yet");
    }

    @FXML
    private void onClickCreateAccount() {
        if (ownerService.getAll().isEmpty() || portfolioService.getAll().isEmpty()) {
            PrimaryTabManager.showInfoDialog(
                    "Keine Inhaber/Portfolios vorhanden",
                    "Es muss mindestens ein Inhaber und ein Portfolio vorhanden sein, dem das Konto zugeordnet werden kann.",
                    createAccountButton
            );
            return;
        }
        PrimaryTabManager.loadFxml(
                "gui/tabs/portfoliomanagement/tab/kontos/dialog/create_account_dialog.fxml",
                "Neues Konto erstellen",
                createAccountButton,
                true,
                createAccountDialog,
                false
        );
        createAccountDialog.open();
    }

    /**
     * Set the table of depots for the account. If the table is null, the table will be hidden.
     */
    public void setDepotTable(TableView<Depot> table) {
        accountDepotsTablePane.getChildren().setAll(table);
    }
}
