package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.kontos;

import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabController;
import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabController;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Account;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Portfolio;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.AccountType;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.BreadcrumbElementType;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.InterestInterval;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.interfaces.Openable;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.repository.AccountRepository;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.FormatUtils;
import javafx.scene.control.*;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private PortfolioManagementTabController portfolioManagementTabController;

    @PersistenceContext
    private EntityManager entityManager;

    public List<Account> getAll() {
        try {
            portfolioManagementTabController.checkForInconsistencies();
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

    /**
     * Searches for inconsistencies in the database and tries to fix them. After that it returns the account with the id, if it exists.
     * @throws NoSuchElementException if the account with the given id does not exist.
     */
    public Account getAccountById(long id) throws NoSuchElementException {
        HashMap<Long, Long> idMapping = portfolioManagementTabController
                .checkForInconsistencies()
                .getOrDefault(BreadcrumbElementType.ACCOUNT, new HashMap<>());
        if (idMapping.containsKey(id)) id = idMapping.get(id);
        return accountRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    /**
     * @param toCurrency f.e. USD to get exchange-course from EUR to USD.
     * @return the latest exchange course from EUR to the given currency.
     * @throws DataAccessException if the exchange course could not be retrieved.
     */
    public Double getLatestExchangeCourse(Currency toCurrency) throws DataAccessException {
        String currency = toCurrency.toString().toLowerCase();
        if ("eur".equals(currency)) return 1.0;
        return jdbcTemplate.queryForObject(String.format(
                "SELECT wk.eur_%s " +
                        "FROM wechselkurse wk " +
                        "WHERE wk.eur_%s IS NOT NULL " +
                        "ORDER BY wk.datum DESC " +
                        "LIMIT 1",
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
            Optional<Long> id;
            id = accountRepository.findByKontoNumber(account.getKontoNumber());
            if (id.isPresent() && !id.get().equals(account.getId())) throw new TransientDataAccessResourceException("");

            id = accountRepository.findByIban(account.getIban());
            if (id.isPresent() && !id.get().equals(account.getId())) throw new TransientDataAccessResourceException("");

            // check if there exists an exchange course for the currency
            getLatestExchangeCourse(account.getCurrency());

            // persist the account
            account.onPrePersistOrUpdateOrRemoveEntity();
            Account persistedAccount = accountRepository.save(account);

            // Update the owner with the id from the database.
            persistedAccount.onPostLoadEntity();
            account.setId(persistedAccount.getId());

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
                    "Das Konto konnte nicht gespeichert werden.\nGrund: " + e.getMessage(),
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
                        "Etwaige Beziehungen werden dabei nicht berücksichtigt und kann zu einem" +
                        " fehlerhaften Verhalten der Anwendung führen!",
                null,
                () -> {
                    accountRepository.delete(account);
                    if (controller != null) controller.open();
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

    /**
     * Updates the account via native-sql-queries instead of via hibernate.
     * @return true if the account was successfully updated, false otherwise.
     */
    @Transactional
    @Modifying
    public boolean updateAccountNatively(@NonNull Account account) {
        account.onPrePersistOrUpdateOrRemoveEntity();

        // check if there exists an exchange course for the currency
        try {
            getLatestExchangeCourse(account.getCurrency());
        } catch (DataAccessException e) {
            PrimaryTabManager.showDialog(
                    Alert.AlertType.ERROR,
                    "Fehler",
                    String.format(
                            "Für die Konto-Währung existiert kein Wechselkurs 'eur_%s' zur Umrechnung der Währung.",
                            account.getCurrency().toString().toLowerCase()
                    ),
                    null
            );
            return false;
        }

        // create the query
        String sqlQuery = "UPDATE pkonto " +
                "SET balance = :balance, bank_name = :bank_name, created_at = :created_at, currency_code = :currency_code, " +
                "deactivated_at = :deactivated_at, description = :description, iban = :iban, interest_days = :interest_days, " +
                "interest_interval = :interest_interval, interest_rate = :interest_rate, konto_number = :konto_number, " +
                "notice = :notice, state = :state, type = :type, owner_id = :owner_id, portfolio_id = :portfolio_id " +
                "WHERE id = :id";
        Query query = entityManager.createNativeQuery(sqlQuery);

        // however, you can ignore the red warnings, because the parameters are set correctly
        query.setParameter("id", account.getId());
        query.setParameter("balance", account.getBalanceBigDecimal());
        query.setParameter("bank_name", account.getBankName());
        query.setParameter("created_at", new Timestamp(account.getCreatedAt().getTime()));
        query.setParameter("deactivated_at", account.getDeactivatedAt() == null ? null : new Timestamp(account.getDeactivatedAt().getTime()));
        query.setParameter("currency_code", account.getCurrency().getCurrencyCode());
        query.setParameter("description", account.getDescription());
        query.setParameter("iban", account.getIban());
        query.setParameter("interest_days", account.getInterestDays());
        query.setParameter("interest_interval", account.getInterestInterval().name());
        query.setParameter("interest_rate", account.getInterestRateBigDecimal());
        query.setParameter("konto_number", account.getKontoNumber());
        query.setParameter("notice", account.getNotice());
        query.setParameter("state", account.getState().name());
        query.setParameter("type", account.getType().name());
        query.setParameter("owner_id", account.getOwner().getId());
        query.setParameter("portfolio_id", account.getPortfolio().getId());

        // query to update the account
        try {
            int updatedRows = query.executeUpdate();
            return updatedRows > 0;
        } catch (PersistenceException e) {
            if (e.getCause() instanceof ConstraintViolationException) {
                PrimaryTabManager.showDialog(
                        Alert.AlertType.ERROR,
                        "Fehler",
                        "Das Konto konnte nicht gespeichert werden, da bereits ein Konto mit der selben IBAN oder Kontonummer existiert.",
                        null
                );
            } else {
                PrimaryTabController.unknownErrorMsg.accept(e);
            }
        } catch (Exception e) {
            PrimaryTabController.unknownErrorMsg.accept(e);
        }
        return false;
    }
}
