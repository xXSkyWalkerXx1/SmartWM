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

    @Query(value = "SELECT a.id " +
            "FROM pkonto a " +
            "LEFT JOIN inhaber o ON a.owner_id = o.id " +
            "LEFT JOIN portfolio p ON a.portfolio_id = p.id" +
            "WHERE o.id IS NULL OR p.id IS NULL OR a.owner_id IS NULL OR a.portfolio_id IS NULL", nativeQuery = true)
    List<Long> findAllByOwnerAndPortfolioIsInvalid();

    @Query("SELECT a.iban FROM pAccount a WHERE a.id = :id")
    Optional<String> findIbanBy(Long id);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM pkonto p WHERE p.id = :id", nativeQuery = true)
    void deleteById(@Param("id") Long id);

}
