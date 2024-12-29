package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.repository;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Account;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Portfolio;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.AccountType;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.InterestInterval;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository("pAccountRepository")
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query(value = "SELECT a.id FROM pkonto a", nativeQuery = true)
    List<Long> getAllIds();

    // region Queries to check for inconsistencies
    /**
     * Fast way to check if any inconsistency in accounts exists.
     * @return true if any inconsistency in accounts exists, otherwise false.
     */
    default boolean inconsistentAccountsExists() {
        return !findAllByOwnerAndPortfolioIsInvalid().isEmpty()
                || !findByCurrencyCodeIsNullOrBalanceIsNullOrBankNameIsNullOrKontoNumberIsNullOrInterestRateIsNullOrIbanIsNullOrCreatedAtIsNull().isEmpty()
                || !findByStateNotInOrTypeNotInOrInterestIntervalNotIn(
                        State.getValuesAsString(),
                        AccountType.getValuesAsString(),
                        InterestInterval.getValuesAsString()).isEmpty()
                || !findByInterestRateIsNotBetween0And100().isEmpty()
                || !findByInterestDaysIsNotBetween0And366().isEmpty()
                || !findByCurrencyIsNotIn(Currency.getAvailableCurrencies().stream()
                .map(Currency::getCurrencyCode)
                .collect(Collectors.toList()))
                .isEmpty();
    }

    /**
     * @return all accounts where the owner or the portfolio is null or invalid.
     */
    @Query(value = "SELECT a.id " +
            "FROM pkonto a " +
            "LEFT JOIN inhaber o ON a.owner_id = o.id " +
            "LEFT JOIN portfolio p ON a.portfolio_id = p.id " +
            "WHERE o.id IS NULL OR p.id IS NULL OR a.owner_id IS NULL OR a.portfolio_id IS NULL", nativeQuery = true)
    List<Long> findAllByOwnerAndPortfolioIsInvalid();

    /**
     * @return all accounts where the currency code, balance, bank name, konto number, interest rate, iban or the created_at date is null or empty.
     */
    @Query(value = "SELECT a.id " +
            "FROM pkonto a " +
            "WHERE a.currency_code IS NULL OR TRIM(a.currency_code) = '' " +
            "OR a.balance IS NULL " +
            "OR a.bank_name IS NULL OR TRIM(a.bank_name) = '' " +
            "OR a.konto_number IS NULL OR TRIM(a.konto_number) = '' " +
            "OR a.interest_rate IS NULL " +
            "OR a.iban IS NULL OR TRIM(a.iban) = '' " +
            "OR a.created_at IS NULL", nativeQuery = true)
    List<Long> findByCurrencyCodeIsNullOrBalanceIsNullOrBankNameIsNullOrKontoNumberIsNullOrInterestRateIsNullOrIbanIsNullOrCreatedAtIsNull();

    /**
     * @param state pass always {@code State.getValuesAsString()}.
     * @param type pass always {@code AccountType.getValuesAsString()}.
     * @param interestInterval pass always {@code InterestInterval.getValuesAsString()}.
     * @return all accounts where the state, type or interest interval is not mappable to a literal of the enum.
     */
    @Query(value = "SELECT a.id " +
            "FROM pkonto a " +
            "WHERE a.state IS NULL OR a.state NOT IN :state " +
            "OR a.type IS NULL OR a.type NOT IN :type " +
            "OR a.interest_interval IS NULL OR a.interest_interval NOT IN :interestInterval", nativeQuery = true)
    List<Long> findByStateNotInOrTypeNotInOrInterestIntervalNotIn(Collection<String> state,
                                                                  Collection<String> type,
                                                                  Collection<String> interestInterval);

    /**
     * @return all accounts where the interest rate is null or not between 0 and 100.
     */
    @Query(value = "SELECT a.id " +
            "FROM pkonto a " +
            "WHERE a.interest_rate IS NULL OR a.interest_rate NOT BETWEEN 0 AND 100", nativeQuery = true)
    List<Long> findByInterestRateIsNotBetween0And100();

    /**
     * @return all accounts where interest days is null or not between 0 and 365.
     */
    @Query(value = "SELECT a.id " +
            "FROM pkonto a " +
            "WHERE a.interest_days IS NULL OR a.interest_days NOT BETWEEN 0 AND 366", nativeQuery = true)
    List<Long> findByInterestDaysIsNotBetween0And366();

    /**
     * @param currencies pass always {@code Currency.getAvailableCurrencies()} as strings.
     * @return Accounts with an invalid currency.
     */
    @Query(value = "SELECT a.id " +
            "FROM pkonto a " +
            "WHERE a.currency_code IS NULL OR a.currency_code NOT IN :currencies", nativeQuery = true)
    List<Long> findByCurrencyIsNotIn(Collection<String> currencies);
    // endregion

    // region Transaction to reconstruct an account
    /**
     * @param id the id of the account to reconstruct.
     * @return the account with the given id, reconstructed (but possible with invalid values) from the database by native queries instead of via JPA.
     */
    @Transactional
    @NonNull
    default Account reconstructAccount(Long id) {
        Account reconstructedAccount = new Account();
        reconstructedAccount.setId(id);
        reconstructedAccount.setBankName(findBankNameBy(id).orElse(null));
        reconstructedAccount.setCreatedAt(findCreatedAtBy(id).orElse(null));
        reconstructedAccount.setDeactivatedAt(findDeactivatedAtBy(id).orElse(null));
        reconstructedAccount.setDescription(findDescriptionBy(id).orElse(null));
        reconstructedAccount.setIban(findIbanBy(id).orElse(null));
        reconstructedAccount.setInterestDays(findInterestDaysBy(id).orElse(null));
        reconstructedAccount.setKontoNumber(findKontoNumberBy(id).orElse(null));
        reconstructedAccount.setNotice(findNoticeBy(id).orElse(null));

        findBalanceBy(id).ifPresent(balance
                -> reconstructedAccount.setBalance(balance.doubleValue())
        );
        findCurrencyCodeBy(id).ifPresent(currencyCode -> {
            try {
                reconstructedAccount.setCurrencyCode(Currency.getInstance(currencyCode));
            } catch (Exception ignore) {
                // Currency code is invalid
            }
                }
        );
        findInterestRateBy(id).ifPresent(interestRate
                -> reconstructedAccount.setInterestRate(interestRate.doubleValue())
        );
        findOwnerBy(id).ifPresent(ownerId -> {
            String forename = findForenameById(ownerId).orElse(null);
            String aftername = findAfternameById(ownerId).orElse(null);

            // Means owner is inconsistent or the foreign-key to it is invalid
            if (forename == null || aftername == null) return;

            /*
            Be aware! This owner is not fully reconstructed, so we can't use it for further operations.
            But it's enough to display the owner in the GUI.
             */
            Owner fakeOwner = new Owner();
            fakeOwner.setId(ownerId);
            fakeOwner.setForename(forename);
            fakeOwner.setAftername(aftername);
            reconstructedAccount.setOwner(fakeOwner);
        });
        findPortfolioBy(id).ifPresent(portfolioId -> {
            // This portfolio is not fully reconstructed, so we can't use it for further operations.
            // But it's enough to display the portfolio in the GUI.
            String portfolioName = findPortfolioNameById(portfolioId).orElse(null);

            // Means portfolio is inconsistent or the foreign-key to it is invalid
            if (portfolioName == null) return;

            Portfolio fakePortfolio = new Portfolio();
            fakePortfolio.setId(portfolioId);
            fakePortfolio.setName(portfolioName);
            reconstructedAccount.setPortfolio(fakePortfolio);
        });

        reconstructedAccount.setState(findStateBy(id).map(s -> {
            try {
                return State.valueOf(s);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }).orElse(null)
        );
        reconstructedAccount.setType(findAccountTypeBy(id).map(s -> {
            try {
                return AccountType.valueOf(s);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }).orElse(null)
        );
        reconstructedAccount.setInterestInterval(findInterestIntervalBy(id).map(s -> {
            try {
                return InterestInterval.valueOf(s);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }).orElse(null)
        );

        return reconstructedAccount;
    }

    @Query(value = "SELECT a.balance FROM pkonto a WHERE a.id = :id", nativeQuery = true)
    Optional<BigDecimal> findBalanceBy(Long id);
    @Query(value = "SELECT a.bank_name FROM pkonto a WHERE a.id = :id", nativeQuery = true)
    Optional<String> findBankNameBy(Long id);
    @Query(value = "SELECT a.created_at FROM pkonto a WHERE a.id = :id", nativeQuery = true)
    Optional<Timestamp> findCreatedAtBy(Long id);
    @Query(value = "SELECT a.deactivated_at FROM pkonto a WHERE a.id = :id", nativeQuery = true)
    Optional<Timestamp> findDeactivatedAtBy(Long id);
    @Query(value = "SELECT a.currency_code FROM pkonto a WHERE a.id = :id", nativeQuery = true)
    Optional<String> findCurrencyCodeBy(Long id);
    @Query(value = "SELECT a.description FROM pkonto a WHERE a.id = :id", nativeQuery = true)
    Optional<String> findDescriptionBy(Long id);
    @Query(value = "SELECT a.iban FROM pkonto a WHERE a.id = :id", nativeQuery = true)
    Optional<String> findIbanBy(Long id);
    @Query(value = "SELECT a.interest_days FROM pkonto a WHERE a.id = :id", nativeQuery = true)
    Optional<String> findInterestDaysBy(Long id);
    @Query(value = "SELECT a.interest_interval FROM pkonto a WHERE a.id = :id", nativeQuery = true)
    Optional<String> findInterestIntervalBy(Long id);
    @Query(value = "SELECT a.interest_rate FROM pkonto a WHERE a.id = :id", nativeQuery = true)
    Optional<BigDecimal> findInterestRateBy(Long id);
    @Query(value = "SELECT a.konto_number FROM pkonto a WHERE a.id = :id", nativeQuery = true)
    Optional<String> findKontoNumberBy(Long id);
    @Query(value = "SELECT a.notice FROM pkonto a WHERE a.id = :id", nativeQuery = true)
    Optional<String> findNoticeBy(Long id);
    @Query(value = "SELECT a.state FROM pkonto a WHERE a.id = :id", nativeQuery = true)
    Optional<String> findStateBy(Long id);
    @Query(value = "SELECT a.type FROM pkonto a WHERE a.id = :id", nativeQuery = true)
    Optional<String> findAccountTypeBy(Long id);
    @Query(value = "SELECT a.owner_id FROM pkonto a WHERE a.id = :id", nativeQuery = true)
    Optional<Long> findOwnerBy(Long id);
    @Query(value = "SELECT o.forename FROM inhaber o WHERE o.id = :id", nativeQuery = true)
    Optional<String> findForenameById(Long id);
    @Query(value = "SELECT o.aftername FROM inhaber o WHERE o.id = :id", nativeQuery = true)
    Optional<String> findAfternameById(Long id);
    @Query(value = "SELECT a.portfolio_id FROM pkonto a WHERE a.id = :id", nativeQuery = true)
    Optional<Long> findPortfolioBy(Long id);
    @Query(value = "SELECT p.name FROM portfolio p WHERE p.id = :id", nativeQuery = true)
    Optional<String> findPortfolioNameById(Long id);
    // endregion

    // region Transaction to delete owner natively
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM pkonto p WHERE p.id = :id", nativeQuery = true)
    void deleteById(@NonNull @Param("id") Long id);
    // endregion
}
