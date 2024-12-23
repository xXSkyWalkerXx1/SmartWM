package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.repository;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    @Query(value = "SELECT p.id FROM portfolio p", nativeQuery = true)
    List<Long> getAllIds();

    /**
     * @return all portfolios as fake portfolios. A fake portfolio is an portfolio with only the id and name set (if available).
     */
    default List<Portfolio> findAllAsFake() {
        List<Portfolio> fakePortfolios = new ArrayList<>();

        for (Long portfolioId: getAllIds()) {
            Portfolio fakePortfolio = new Portfolio();
            fakePortfolio.setId(portfolioId);
            fakePortfolio.setName(findNameBy(portfolioId).orElse(null));
            fakePortfolios.add(fakePortfolio);
        }
        return fakePortfolios;
    }

    /**
     * @return all portfolios where the owner or the investment guideline is null or invalid or contains null or invalid foreign-keys.
     */
    @Query(value = "SELECT p.id " +
            "FROM portfolio p " +
            "LEFT JOIN inhaber o ON o.id = p.owner_id " +
            "LEFT JOIN anlagen_richtlinie g ON g.id = p.investment_guideline_id " +
            "LEFT JOIN anlagen_richtlinie_unterteilung_ort gl ON gl.id = g.division_by_location_id " +
            "LEFT JOIN anlagen_richtlinie_unterteilung_währung gc ON gc.id = g.division_by_currency_id " +
            "WHERE p.owner_id IS NULL OR p.investment_guideline_id IS NULL OR o.id IS NULL OR g.id IS NULL " +
            "OR g.division_by_currency_id IS NULL OR g.division_by_location_id IS NULL OR gl.id IS NULL OR gc.id IS NULL",
            nativeQuery = true)
    List<Long> findAllByOwnerOrInvestmentguidelineIsInvalid();
    // ToDo: implement if foreign keys are null/invalid

    @Query(value = "SELECT p.id " +
            "FROM portfolio p " +
            "JOIN anlagen_richtlinie g ON g.id = p.investment_guideline_id " +
            "JOIN anlagen_richtlinie_eintrag e ON e.entry_id = g.id " +
            "LEFT JOIN anlagen_richtlinie_eintrag c ON c.child_entry_id = e.id " +
            "GROUP BY p.id " +
            "HAVING COUNT(p.id) != 21", nativeQuery = true)
    List<Long> findAllInvalidInvestmentguidelines();

    /**
     * @return all portfolios where the name or the created_at date is null.
     */
    @Query("SELECT p.id " +
            "FROM Portfolio p " +
            "WHERE p.name IS NULL OR p.createdAt IS NULL")
    List<Long> findAllByNullEntry();

    /**
     * @return all portfolios where the state is not 'ACTIVATED' or 'DEACTIVATED'.
     */
    @Query("SELECT p.id " +
            "FROM Portfolio p " +
            "WHERE p.state != 'ACTIVATED' AND p.state != 'DEACTIVATED'")
    List<Long> findAllByInvalidLiteral();

    /**
     * @return the name of the portfolio with the given id.
     */
    @Query("SELECT p.name FROM Portfolio p WHERE p.id = :id")
    Optional<String> findNameBy(Long id);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM anlagen_richtlinie_unterteilung_ort dl " +
            "WHERE dl.id IN (SELECT g.division_by_location_id " +
            "FROM portfolio p " +
            "JOIN anlagen_richtlinie g ON p.investment_guideline_id = g.id " +
            "WHERE p.id = :id)",
            nativeQuery = true)
    void deleteDivisionByLocation(@Param("id") Long portfolioId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM anlagen_richtlinie_unterteilung_währung dc " +
            "WHERE dc.id IN (SELECT g.division_by_currency_id " +
            "FROM portfolio p " +
            "JOIN anlagen_richtlinie g ON p.investment_guideline_id = g.id " +
            "WHERE p.id = :id)", nativeQuery = true)
    void deleteDivisionByCurrency(@Param("id") Long portfolioId);

    @Transactional
    @Modifying
    @Query(value = "DELETE e, c " +
            "FROM portfolio p " +
            "JOIN anlagen_richtlinie g ON g.id = p.investment_guideline_id " +
            "JOIN anlagen_richtlinie_eintrag e ON e.entry_id = g.id " +
            "LEFT JOIN anlagen_richtlinie_eintrag c ON c.child_entry_id = e.id " +
            "WHERE p.id = :id", nativeQuery = true)
    void deleteInvestmentGuidelineEntries(@Param("id") Long portfolioId);

    @Transactional
    @Modifying
    @Query(value = "DELETE g " +
            "FROM portfolio p " +
            "JOIN anlagen_richtlinie g ON g.id = p.investment_guideline_id " +
            "WHERE p.id = :id", nativeQuery = true)
    void deleteInvestmentGuideline(@Param("id") Long portfolioId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM portfolio p WHERE p.id = :id", nativeQuery = true)
    void deletePortfolio(@Param("id") Long portfolioId);

    @Modifying
    @Transactional
    @Query(value = "SET FOREIGN_KEY_CHECKS = 0", nativeQuery = true)
    void disableForeignKeyChecks();

    @Modifying
    @Transactional
    @Query(value = "SET FOREIGN_KEY_CHECKS = 1", nativeQuery = true)
    void enableForeignKeyChecks();

    @Transactional
    @Modifying
    default void deleteById(@NonNull @Param("id") Long id) {
        disableForeignKeyChecks();

        deleteDivisionByLocation(id);
        deleteDivisionByCurrency(id);
        deleteInvestmentGuidelineEntries(id);
        deleteInvestmentGuideline(id);
        deletePortfolio(id);

        enableForeignKeyChecks();
    }
}
