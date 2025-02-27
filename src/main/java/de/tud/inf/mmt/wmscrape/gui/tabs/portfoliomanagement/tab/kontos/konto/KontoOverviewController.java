package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.kontos.konto;

import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.Navigator;
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
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.*;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.text.ParseException;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
public class KontoOverviewController extends EditableView implements Openable {

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
        FieldFormatter.setInputFloatRange(inputInterestRate, 0f, 100f);
        FieldFormatter.setInputIntRange(inputInterestDays, 0, 366);

        // Initialize elements with values
        inputType.getItems().setAll(AccountType.values());
        inputCurrencyCode.getItems().setAll(Currency.getAvailableCurrencies().stream()
                .sorted(Comparator.comparing(Currency::getCurrencyCode))
                .toList()
        );
        inputState.getItems().setAll(State.values());
        inputInterestInterval.getItems().setAll(InterestInterval.values());

        // Initialize listeners
        inputDescription.textProperty().addListener((observable, oldValue, newValue) -> {
            account.setDescription(newValue);
        });
        inputType.valueProperty().addListener((observable, oldValue, newValue) -> {
            account.setType(newValue);
        });
        inputCurrencyCode.valueProperty().addListener((observable, oldValue, newValue) -> {
            account.setCurrencyCode(newValue);
        });
        inputBalance.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                account.setBalance(FormatUtils.parseFloat(inputBalance.getText()));
            } catch (ParseException e) {
                throw new RuntimeException("Error while parsing balance. This should not happen here!");
            }
        });
        inputOwner.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null && newValue != null) account.setOwner(newValue);
        });
        inputPortfolio.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null && newValue != null) account.setPortfolio(newValue);
        });
        inputNotice.textProperty().addListener((observable, oldValue, newValue) -> {
            account.setNotice(newValue);
        });
        inputState.valueProperty().addListener((observable, oldValue, newValue) -> {
            account.setState(newValue);
        });
        inputBankName.textProperty().addListener((observable, oldValue, newValue) -> {
            account.setBankName(newValue);
        });
        inputIban.textProperty().addListener((observable, oldValue, newValue) -> {
            account.setIban(newValue);
        });
        inputKontoNumber.textProperty().addListener((observable, oldValue, newValue) -> {
            account.setKontoNumber(newValue);
        });
        inputInterestRate.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                account.setInterestRate(FormatUtils.parseFloat(inputInterestRate.getText()));
            } catch (ParseException e) {
                throw new RuntimeException("Error while parsing interest-rate. This should not happen here!");
            }
        });
        inputInterestDays.textProperty().addListener((observable, oldValue, newValue) -> {
            account.setInterestDays(newValue);
        });
        inputInterestInterval.valueProperty().addListener((observable, oldValue, newValue) -> {
            account.setInterestInterval(newValue);
        });
    }

    @Override
    public void open() {
        account = (Account) portfolioManagementTabManager
                .getPortfolioController()
                .getKontoOverviewTab()
                .getProperties()
                .get(PortfolioManagementTabController.TAB_PROPERTY_ENTITY);
        if (!account.isChanged()) {
            try {
                account = accountService.getAccountById(account.getId());
            } catch (NoSuchElementException ignore) {
                return;
            }
        }
        loadAccountData();

        // Initialize onUnsavedChangesAction-dialog
        super.initialize(
                account,
                portfolioManagementTabManager,
                this::onSave,
                this::onReset
        );
    }

    @FXML
    private void onReset() {
        account.restore();
        loadAccountData();
    }

    @FXML
    private void onOpenOwner() {
        Navigator.navigateToOwner(portfolioManagementTabManager, account.getOwner(), true);
    }

    @FXML
    private void onRemove() {
        accountService.delete(account, null);
        portfolioManagementTabManager.getPortfolioController().navigateBackAfterDeletion(account);
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
        if (!accountService.save(account)) return;
        // Refresh data
        account = accountService.getAccountById(account.getId());
        loadAccountData();

        // Finally, show success-dialog
        PrimaryTabManager.showInfoDialog(
                "Konto aktualisiert",
                "Das Konto wurde erfolgreich aktualisiert.",
                inputDescription
        );
    }

    private void loadAccountData() {
        portfolioManagementTabManager.getPortfolioController().refreshCrumbs();

        inputOwner.getItems().setAll(ownerService.getAll());
        inputPortfolio.getItems().setAll(portfolioService.getAll());

        inputDescription.setText(account.getDescription());
        inputType.getSelectionModel().select(account.getType());
        inputCurrencyCode.getSelectionModel().select(account.getCurrency());
        inputBalance.setText(FormatUtils.formatFloat((float) account.getBalance()));
        inputOwner.getSelectionModel().select(account.getOwner());
        inputPortfolio.getSelectionModel().select(account.getPortfolio());
        inputNotice.setText(account.getNotice());
        inputState.getSelectionModel().select(account.getState());
        outputCreatedAt.setText(account.getCreatedAt().toLocaleString());
        outputDeactivatedAt.setText(account.getDeactivatedAt() != null ? account.getDeactivatedAt().toLocaleString() : "");
        inputBankName.setText(account.getBankName());
        inputIban.setText(account.getIban());
        inputKontoNumber.setText(account.getKontoNumber());
        inputInterestRate.setText(FormatUtils.formatFloat((float) account.getInterestRate()));
        inputInterestDays.setText(String.valueOf(account.getInterestDays()));
        inputInterestInterval.getSelectionModel().select(account.getInterestInterval());

        // Show depots if account is a clearing account
        if (AccountType.CLEARING_ACCOUNT.equals(account.getType())) {
            prepareTableData();
            var treeView = new PortfolioTreeView(
                    accountDepotsTablePane,
                    List.of(account.getPortfolio()),
                    portfolioManagementTabManager,
                    true
            );
            treeView.setTooltip(new Tooltip("Auflistung der Depots, denen dieses Konto als Verrechnungskonto zugeordnet ist."));
            treeView.setShowRoot(false);
            accountDepotsTablePane.getChildren().setAll(treeView);
        } else {
            accountDepotsTablePane.getChildren().clear();
        }
    }

    private void prepareTableData() {
        Portfolio portfolio = account.getPortfolio();
        portfolio.setAccounts(List.of());
        portfolio.setDepots(account.getMappedDepots());
    }

}
