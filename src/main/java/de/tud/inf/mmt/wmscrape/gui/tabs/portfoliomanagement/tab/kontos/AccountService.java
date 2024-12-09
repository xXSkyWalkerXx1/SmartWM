package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.kontos;

import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Account;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Portfolio;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.AccountType;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.InterestInterval;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.interfaces.Openable;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.repository.AccountRepository;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.FormatUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Currency;
import java.util.List;

@Service
public class AccountService {

    @Autowired
    AccountRepository accountRepository;

    public List<Account> getAll() {
        return accountRepository.findAll();
    }

    public Account getAccountById(long id) {
        return accountRepository.findById(id).orElseThrow();
    }

    public boolean save(Account account) {
        try {
            accountRepository.save(account);
            return true;
        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
            PrimaryTabManager.showDialog(
                    Alert.AlertType.ERROR,
                    "Fehler",
                    "Das Konto konnte nicht gespeichert werden, da bereits ein Konto mit der selben IBAN bereits existiert.",
                    null
            );
        }
        return false;
    }

    /**
     * @param controller to refresh the view after deletion.
     */
    public void delete(Account account, @NonNull Openable controller) {
        PrimaryTabManager.showDialogWithAction(
                Alert.AlertType.WARNING,
                "Konto löschen",
                "Sind Sie sicher, dass Sie das Konto löschen möchten?\n" +
                        "Etwaige Beziehungen zu Transaktionen werden dabei nicht berücksichtigt und kann zu einem" +
                        " fehlerhaften Verhalten der Anwendung führen!;",
                null,
                o -> {
                    accountRepository.delete(account);
                    controller.open();
                }
        );
    }

    public void writeInput(@NonNull Account account, boolean isOnCreate,
                           @NonNull TextField inputDescription, @NonNull ComboBox<AccountType> inputType,
                           @NonNull ComboBox<Currency> inputCurrencyCode, @NonNull TextField inputBalance,
                           @NonNull ComboBox<Owner> inputOwner, @NonNull ComboBox<Portfolio> inputPortfolio,
                           @NonNull TextArea inputNotice, @NonNull TextField inputBankName, @NonNull TextField inputIban,
                           @NonNull TextField inputKontoNumber, @NonNull TextField inputInterestRate,
                           @NonNull TextField inputInterestDays, @NonNull ComboBox<InterestInterval> inputInterestInterval) {
        account.setDescription(inputDescription.getText());
        account.setType(inputType.getValue());
        account.setCurrencyCode(inputCurrencyCode.getValue());
        account.setOwner(inputOwner.getValue());
        account.setPortfolio(inputPortfolio.getValue());
        account.setNotice(inputNotice.getText());
        if (isOnCreate) account.setCreatedAt(Calendar.getInstance().getTime());
        account.setBankName(inputBankName.getText());
        account.setIban(inputIban.getText());
        account.setKontoNumber(inputKontoNumber.getText());
        account.setInterestDays(inputInterestDays.getText());
        account.setInterestInterval(inputInterestInterval.getValue());

        try {
            account.setBalance(FormatUtils.parseFloat(inputBalance.getText()));
            account.setInterestRate(FormatUtils.parseFloat(inputInterestRate.getText()));
        } catch (ParseException e) {
            throw new RuntimeException("Error while parsing balance and interest-rate. This should not happen here!");
        }
    }
}
