package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.repository;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository("pAccountRepository")
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query(value = "SELECT a.id FROM pkonto a", nativeQuery = true)
    List<Long> getAllIds();

    // region Queries to check for inconsistencies
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
     * @return all accounts where the currency code, balance, bank name, konto number, interest rate, iban or the created_at date is null.
     */
    @Query(value = "SELECT a.id " +
            "FROM pkonto a " +
            "WHERE a.currency_code IS NULL OR a.balance IS NULL OR a.bank_name IS NULL OR a.konto_number IS NULL " +
            "OR a.interest_rate IS NULL OR a.iban IS NULL OR a.created_at IS NULL", nativeQuery = true)
    List<Long> findByCurrencyCodeIsNullOrBalanceIsNullOrBankNameIsNullOrKontoNumberIsNullOrInterestRateIsNullOrIbanIsNullOrCreatedAtIsNull();

    /**
     * @param state pass always {@code State.getValuesAsString()}.
     * @param type pass always {@code AccountType.values()}.
     * @param interestInterval pass always {@code InterestInterval.values()}.
     * @return all accounts where the state, type or interest interval is not mappable to a literal of the enum.
     */
    @Query(value = "SELECT a.id " +
            "FROM pkonto a " +
            "WHERE a.state NOT IN :state " +
            "OR a.type NOT IN :type " +
            "OR a.interest_interval NOT IN :interestInterval", nativeQuery = true)
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
            "WHERE a.interest_days IS NULL OR a.interest_days NOT BETWEEN 0 AND 365", nativeQuery = true)
    List<Long> findByInterestDaysIsNotBetween0And365();

    /**
     * @param currencies pass always {@code Currency.getAvailableCurrencies()}.
     * @return Accounts with an invalid currency.
     */
    @Query(value = "SELECT a.id " +
            "FROM pkonto a " +
            "WHERE a.currency_code IS NULL OR a.currency_code NOT IN :currencies", nativeQuery = true)
    List<Long> findByCurrencyIsNotIn(Collection<String> currencies);
    // endregion



    @Query(value = "SELECT a.currency_code FROM pkonto a WHERE a.id = :id", nativeQuery = true)
    Optional<String> findCurrencyCodeBy(Long id);

    @Query(value = "SELECT a.iban FROM pkonto a WHERE a.id = :id", nativeQuery = true)
    Optional<String> findIbanBy(Long id);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM pkonto p WHERE p.id = :id", nativeQuery = true)
    void deleteById(@Param("id") Long id);

}
