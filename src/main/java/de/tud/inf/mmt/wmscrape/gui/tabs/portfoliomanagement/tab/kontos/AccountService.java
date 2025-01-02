package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.kontos;

import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Account;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.FinancialAsset;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Portfolio;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.AccountType;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.InterestInterval;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.interfaces.Openable;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.repository.AccountRepository;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.FormatUtils;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.*;

@Service
public class AccountService {

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Account> getAll() {
        try {
            return accountRepository.findAll();
        } catch (Exception e) {
            PrimaryTabManager.showDialog(
                    Alert.AlertType.ERROR,
                    "Fehler",
                    "Die Konten konnten nicht geladen werden.\nGrund: " + e.getMessage(),
                    null
            );
            return new ArrayList<>();
        }
    }

    /**
     * Deletes the account by id.
     * @param id id of the account to delete.
     */
    public void deleteById(long id) {
        accountRepository.deleteById(id);
    }

    public Account getAccountById(long id) {
        return accountRepository.findById(id).orElseThrow();
    }

    /**
     * Deletes the account and saves it again.
     * @return true if the account was successfully saved, false otherwise.
     */
    @Transactional
    public boolean reSave(Account account) {
        deleteById(account.getId());
        return save(account);
    }

    /**
     * @param fromCurrency f.e. USD to get exchange-course from USD to EUR.
     * @return the latest exchange course from EUR to the given currency.
     * @throws DataAccessException if the exchange course could not be retrieved.
     */
    public Double getLatestExchangeCourse(Currency fromCurrency) throws DataAccessException {
        String currency = fromCurrency.toString().toLowerCase();
        if ("eur".equals(currency)) return 1.0;
        return jdbcTemplate.queryForObject(String.format(
                "SELECT wk.eur_%s FROM wechselkurse wk WHERE wk.eur_%s IS NOT NULL ORDER BY wk.datum DESC LIMIT 1",
                        currency,
                        currency
                ),
                Double.class
        );
    }

    /**
     * @return all accounts where the currency has no exchange course.
     */
    public List<Long> findByCurrencyHasNoExchangeCourse() {
        List<Long> idsOfInvalidAccounts = new ArrayList<>();

        for (Long id : accountRepository.getAllIds()) {
            Optional<String> currencyCode = accountRepository.findCurrencyCodeBy(id);

            if (currencyCode.isPresent()) {
                try {
                    getLatestExchangeCourse(Currency.getInstance(currencyCode.get()));
                } catch (Exception e) {
                    idsOfInvalidAccounts.add(id);
                }
            } else {
                idsOfInvalidAccounts.add(id);
            }
        }
        return idsOfInvalidAccounts;
    }

    /**
     * @param account to save.
     * @return true if the account was successfully saved, false otherwise.
     */
    public boolean save(Account account) {
        try {
            if (!Currency.getInstance("EUR").equals(account.getCurrency())) {
                getLatestExchangeCourse(account.getCurrency());
            }
            accountRepository.save(account);
            return true;
        } catch (DataAccessException e) {
            PrimaryTabManager.showDialog(
                    Alert.AlertType.ERROR,
                    "Fehler",
                    String.format(
                            """
                                    Das Konto konnte aus folgenden möglichen Gründen nicht gespeichert werden:
                                        - Es existiert bereits ein Konto mit der selben IBAN oder Kontonummer.
                                        - Es existiert kein Wechselkurs 'eur_%s' für die Umrechnung der Währung.""",
                            account.getCurrency().toString().toLowerCase()
                    ),
                    null
            );
        } catch (Exception e) {
            PrimaryTabManager.showDialog(
                    Alert.AlertType.ERROR,
                    "Unerwarteter Fehler",
                    "Das Konto konnte nicht gespeichert werden.",
                    null
            );
        }
        return false;
    }

    /**
     * Shows a dialog to confirm the deletion of the account and if confirmed, deletes it.
     * @param controller to refresh the view after deletion. If null, no view will be refreshed.
     */
    public void delete(Account account, @Nullable Openable controller) {
        PrimaryTabManager.showDialogWithAction(
                Alert.AlertType.WARNING,
                "Konto löschen",
                "Sind Sie sicher, dass Sie das Konto löschen möchten?\n" +
                        "Etwaige Beziehungen zu Transaktionen werden dabei nicht berücksichtigt und kann zu einem" +
                        " fehlerhaften Verhalten der Anwendung führen!;",
                null,
                () -> {
                    accountRepository.delete(account);
                    if (controller == null) controller.open();
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
