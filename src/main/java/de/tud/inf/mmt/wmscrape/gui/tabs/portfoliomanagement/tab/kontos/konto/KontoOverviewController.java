package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.kontos.konto;

import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabController;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Account;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Portfolio;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.AccountType;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.InterestInterval;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.State;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.interfaces.Openable;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.kontos.AccountService;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners.OwnerService;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.portfolios.PortfolioService;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.FieldFormatter;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.FieldValidator;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.PortfolioTreeView;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;

@Controller
public class KontoOverviewController implements Openable {

    Account account;

    @Autowired
    AccountService accountService;
    @Autowired
    OwnerService ownerService;
    @Autowired
    PortfolioService portfolioService;
    @Autowired
    PortfolioManagementTabManager portfolioManagementTabManager;

    @FXML
    TextField inputDescription;
    @FXML
    ComboBox<AccountType> inputType;
    @FXML
    ComboBox<Currency> inputCurrencyCode;
    @FXML
    TextField inputBalance;
    @FXML
    ComboBox<Owner> inputOwner;
    @FXML
    ComboBox<Portfolio> inputPortfolio;
    @FXML
    TextArea inputNotice;
    @FXML
    ComboBox<State> inputState;
    @FXML
    TextField outputCreatedAt;
    @FXML
    TextField outputDeactivatedAt;
    @FXML
    TextField inputBankName;
    @FXML
    TextField inputIban;
    @FXML
    TextField inputKontoNumber;
    @FXML
    TextField inputInterestRate;
    @FXML
    TextField inputInterestDays;
    @FXML
    ComboBox<InterestInterval> inputInterestInterval;
    @FXML
    AnchorPane accountDepotsTablePane;

    @FXML
    private void initialize() {
        // Format input fields
        FieldFormatter.setInputOnlyDecimalNumbers(inputBalance);
        FieldFormatter.setInputFloatRange(inputInterestRate, 0, 100);

        // Initialize elements with values
        inputType.getItems().setAll(AccountType.values());
        inputCurrencyCode.getItems().setAll(Currency.getAvailableCurrencies().stream()
                .sorted(Comparator.comparing(Currency::getCurrencyCode))
                .toList()
        );
        inputState.getItems().setAll(State.values());
        inputInterestInterval.getItems().setAll(InterestInterval.values());
    }

    @Override
    public void open() {
        account = (Account) portfolioManagementTabManager
                .getPortfolioController()
                .getKontoOverviewTab()
                .getProperties()
                .get(PortfolioManagementTabController.TAB_PROPERTY_ENTITY);

        loadAccountData();
    }

    public void onReset() {
        loadAccountData();
    }

    public void onSave() {
        // Validate first
        if (FieldValidator.isInputEmpty(
                inputDescription, inputBalance, inputBankName, inputIban,
                inputKontoNumber, inputInterestRate, inputInterestDays)) {
            return;
        }

        // If everything is valid, we can create and save the new account
        accountService.writeInput( account, false,
                inputDescription, inputType, inputCurrencyCode, inputBalance, inputOwner, inputPortfolio,
                inputNotice, inputBankName, inputIban, inputKontoNumber, inputInterestRate, inputInterestDays, inputInterestInterval
        );
        account.setState(inputState.getValue());
        if (State.DEACTIVATED.equals(inputState.getSelectionModel().getSelectedItem())) {
            account.setDeactivatedAt(Calendar.getInstance().getTime());
        }
        accountService.save(account);

        // Finally, show success-dialog
        PrimaryTabManager.showInfoDialog(
                "Konto aktualisiert",
                "Das Konto wurde erfolgreich aktualisiert.",
                inputDescription
        );
    }

    private void loadAccountData() {
        inputOwner.getItems().setAll(ownerService.getAll());
        inputPortfolio.getItems().setAll(portfolioService.getAll());

        inputDescription.setText(account.getDescription());
        inputType.getSelectionModel().select(account.getType());
        inputCurrencyCode.getSelectionModel().select(account.getCurrency());
        inputBalance.setText(String.valueOf(account.getBalance()));
        inputOwner.getSelectionModel().select(account.getOwner());
        inputPortfolio.getSelectionModel().select(account.getPortfolio());
        inputNotice.setText(account.getNotice());
        inputState.getSelectionModel().select(account.getState());
        outputCreatedAt.setText(account.getCreatedAt().toLocaleString());
        outputDeactivatedAt.setText(account.getDeactivatedAt() != null ? account.getDeactivatedAt().toLocaleString() : "");
        inputBankName.setText(account.getBankName());
        inputIban.setText(account.getIban());
        inputKontoNumber.setText(account.getKontoNumber());
        inputInterestRate.setText(String.valueOf(account.getInterestRate()));
        inputInterestDays.setText(String.valueOf(account.getInterestDays()));
        inputInterestInterval.getSelectionModel().select(account.getInterestInterval());

        prepareTableData();
        var depotsTable = new PortfolioTreeView(
                accountDepotsTablePane,
                List.of(account.getPortfolio()),
                portfolioManagementTabManager,
                false
        );
        depotsTable.setShowRoot(false);
        accountDepotsTablePane.getChildren().setAll(depotsTable);
    }

    private void prepareTableData() {
        Portfolio portfolio = account.getPortfolio();
        portfolio.setAccounts(List.of());
        portfolio.setDepots(account.getMappedDepots());
    }

}
