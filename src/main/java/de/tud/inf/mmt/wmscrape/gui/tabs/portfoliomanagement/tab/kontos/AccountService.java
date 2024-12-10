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
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Currency;
import java.util.List;

@Service
public class AccountService {

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Account> getAll() {
        return accountRepository.findAll();
    }

    public Account getAccountById(long id) {
        return accountRepository.findById(id).orElseThrow();
    }

    /**
     * @param fromCurrency f.e. USD to get exchange-course from USD to EUR.
     * @return the latest exchange course from EUR to the given currency.
     * @throws DataAccessException if the exchange course could not be retrieved.
     */
    public Double getLatestExchangeCourse(Currency fromCurrency) throws DataAccessException {
        String currency = fromCurrency.toString().toLowerCase();
        return jdbcTemplate.queryForObject(
                String.format(
                        "SELECT wk.eur_%s FROM wechselkurse wk WHERE wk.eur_%s IS NOT NULL ORDER BY wk.datum DESC LIMIT 1",
                        currency,
                        currency
                ),
                Double.class
        );
    }

    /**
     * @param missingExchangeCourses if the exchange course could not be retrieved, the currency will be added to this list.
     * @return the sum of all accounts in EUR.
     */
    public BigDecimal getSumOfAllAccountsInEur(@Nullable List<String> missingExchangeCourses) {
        BigDecimal sum = BigDecimal.ZERO;

        for (Account account : getAll()) {
            try {
                if (Currency.getInstance("EUR").equals(account.getCurrency())) {
                    sum = sum.add(BigDecimal.valueOf(account.getBalance()));
                } else {
                    Double latestExchangeCourse = getLatestExchangeCourse(account.getCurrency());
                    BigDecimal accountBalanceToEur = BigDecimal.valueOf(account.getBalance())
                            .divide(BigDecimal.valueOf(latestExchangeCourse), BigDecimal.ROUND_HALF_DOWN);
                    sum = sum.add(accountBalanceToEur);
                }
            } catch (Exception e) {
                //e.printStackTrace();

                if (missingExchangeCourses != null) {
                    missingExchangeCourses.add(String.format("eur_%s", account.getCurrency().toString().toLowerCase()));
                }
            }
        }
        return sum;
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
