package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.kontos.dialog;

import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Account;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Portfolio;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.AccountType;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.InterestInterval;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.kontos.AccountService;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners.OwnerService;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.portfolios.PortfolioService;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.FieldFormatter;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.FieldValidator;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.Comparator;
import java.util.Currency;

@Controller
public class CreateAccountDialog {

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
    public void initialize() {
        account = new Account();

        // Format input fields
        FieldFormatter.setInputOnlyDecimalNumbers(inputBalance);
        FieldFormatter.setInputFloatRange(inputInterestRate, 0, 100);

        // Initialize elements with values
        inputType.getItems().setAll(AccountType.values());
        inputType.getSelectionModel().selectFirst();

        inputCurrencyCode.getItems().setAll(Currency.getAvailableCurrencies().stream()
                .sorted(Comparator.comparing(Currency::getCurrencyCode))
                .toList()
        );
        inputCurrencyCode.getSelectionModel().selectFirst();

        inputOwner.getItems().setAll(ownerService.getAllOwners());
        inputOwner.getSelectionModel().selectFirst();

        inputPortfolio.getItems().setAll(portfolioService.getAll());
        inputPortfolio.getSelectionModel().selectFirst();

        inputInterestInterval.getItems().setAll(InterestInterval.values());
        inputInterestInterval.getSelectionModel().selectFirst();
    }

    @FXML
    private void onCancel() {
        portfolioManagementTabManager.getPortfolioController().getAccountMenuController().open();
        inputType.getScene().getWindow().hide();
    }

    @FXML
    private void onSave() {
        // Validate first
        if (FieldValidator.isInputEmpty(
                inputDescription, inputBalance, inputBankName, inputIban,
                inputKontoNumber, inputInterestRate, inputInterestDays)) {
            return;
        }

        // If everything is valid, we can create and save the new account
        accountService.writeInput( account, true,
                inputDescription, inputType, inputCurrencyCode, inputBalance, inputOwner, inputPortfolio,
                inputNotice, inputBankName, inputIban, inputKontoNumber, inputInterestRate, inputInterestDays, inputInterestInterval
        );
        accountService.save(account);
        onCancel();

        // Finally, show success-dialog
        PrimaryTabManager.showInfoDialog(
                "Konto angelegt",
                "Das neue Konto wurde erfolgreich angelegt.",
                inputDescription
        );
    }
}
