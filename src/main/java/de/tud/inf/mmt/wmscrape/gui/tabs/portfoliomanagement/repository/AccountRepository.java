package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.repository;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository("pAccountRepository")
public interface AccountRepository extends JpaRepository<Account, Long> {

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
    @Query("SELECT a.id " +
            "FROM pAccount a " +
            "WHERE a.currencyCode IS NULL OR a.balance IS NULL OR a.bankName IS NULL OR a.kontoNumber IS NULL " +
            "OR a.interestRate IS NULL OR a.iban IS NULL OR a.createdAt IS NULL")
    List<Long> findAllByNullEntry();

    /**
     * @return all accounts with an invalid state, type or interest interval.
     */
    @Query("SELECT a.id " +
            "FROM pAccount a " +
            "WHERE a.state != 'ACTIVATED' AND a.state != 'DEACTIVATED' " +
            "OR a.type != 'CLEARING_ACCOUNT' AND a.type != 'CHECKING_ACCOUNT' AND a.type != 'FIXED_TERM_DEPOSIT_ACCOUNT' " +
            "OR a.interestInterval != 'MONTHLY' AND a.interestInterval != 'QUARTERLY' AND a.interestInterval != 'YEARLY'")
    List<Long> findAllByInvalidLiteral();

    @Query("SELECT a.iban FROM pAccount a WHERE a.id = :id")
    Optional<String> findIbanBy(Long id);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM pkonto p WHERE p.id = :id", nativeQuery = true)
    void deleteById(@Param("id") Long id);

}
