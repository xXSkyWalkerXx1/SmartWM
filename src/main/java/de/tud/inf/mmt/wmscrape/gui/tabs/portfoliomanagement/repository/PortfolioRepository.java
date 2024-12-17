package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.repository;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    @Query(value = "SELECT p.id FROM portfolio p LEFT JOIN inhaber o ON p.owner_id = o.id WHERE o.id IS NULL OR p.owner_id IS NULL", nativeQuery = true)
    List<Long> findAllByOwnerIsInvalid();

    @Query("SELECT p.name FROM Portfolio p WHERE p.id = :id")
    Optional<String> findNameBy(Long id);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM pkonto a WHERE a.portfolio_id = :id", nativeQuery = true)
    void deleteAccountsById(@Param("id") Long portfolioId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM portfolio p WHERE p.id = :id", nativeQuery = true)
    void deleteById(@Param("id") Long portfolioId);

    @Transactional
    @Modifying
    default void deleteWithDependenciesById(Long id) {
        deleteAccountsById(id);
        // ToDo: remove investment_guideline too!
        deleteById(id);
    }
}
