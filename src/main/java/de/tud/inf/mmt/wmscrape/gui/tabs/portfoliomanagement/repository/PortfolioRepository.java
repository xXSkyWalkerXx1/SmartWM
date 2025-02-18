package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.repository;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.InvestmentGuideline;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Portfolio;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.InvestmentType;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.*;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    @Query(value = "SELECT p.id FROM portfolio p", nativeQuery = true)
    List<Long> getAllIds();

    /**
     * @return all portfolios as fake portfolios. A fake portfolio is a portfolio with only the id and name set (if available).
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

    // region Queries to check for inconsistencies
    /**
     * Fast way to check if any inconsistency in portfolios exists.
     * @return true if any inconsistent portfolios exists, otherwise false.
     */
    default boolean inconsistentPortfoliosExists() {
        return !getInconsistentPortfolioIds().isEmpty();
    }

    /**
     * @return all portfolios ids where inconsistencies exist.
     */
    default Set<Long> getInconsistentPortfolioIds() {
        Set<Long> inconsistentPortfolioIds = new HashSet<>();
        inconsistentPortfolioIds.addAll(findAllByNameIsNullOrCreatedAtIsNull());
        inconsistentPortfolioIds.addAll(findAllByOwnerOrInvestmentguidelineIsInvalid());
        inconsistentPortfolioIds.addAll(findAllByStateNotIn(State.getValuesAsString()));
        inconsistentPortfolioIds.addAll(findAllByStateIsDeactivatedButDeactivatedAtIsNull());
        inconsistentPortfolioIds.addAll(findAllByInvalidInvestmentGuidelineEntries());
        inconsistentPortfolioIds.addAll(findAllBySumOfDivisionByLocationIsNot100());
        inconsistentPortfolioIds.addAll(findAllBySumOfDivisionByCurrencyIsNot100());
        inconsistentPortfolioIds.addAll(findAllByCreatedAtIsInFutureOrDeactivatedAtIsBeforeCreatedAtOrIsInFuture());
        return inconsistentPortfolioIds;
    }

    /**
     * @return all portfolios where the name is null or empty.
     */
    @Query(value = "SELECT p.id " +
            "FROM portfolio p " +
            "WHERE p.name IS NULL OR TRIM(p.name) = '' " +
            "OR p.created_at IS NULL", nativeQuery = true)
    List<Long> findAllByNameIsNullOrCreatedAtIsNull();

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

    /**
     * @param states pass always {@code State.getValuesAsString()}.
     * @return all portfolios where the state is not valid.
     */
    @Query(value = "SELECT p.id " +
            "FROM Portfolio p " +
            "WHERE p.state IS NULL OR p.state NOT IN :states", nativeQuery = true)
    List<Long> findAllByStateNotIn(List<String> states);

    @Query(value = "SELECT p.id " +
            "FROM portfolio p " +
            "WHERE p.state = 'DEACTIVATED' AND p.deactivated_at IS NULL", nativeQuery = true)
    List<Long> findAllByStateIsDeactivatedButDeactivatedAtIsNull();

    /**
     * @return all portfolios where value-ranges are not valid (f.e. asset_allocation is 300%).
     */
    @Query(value = "SELECT DISTINCT p.id " +
            "FROM portfolio p " +
            "JOIN anlagen_richtlinie g ON g.id = p.investment_guideline_id " +
            "JOIN anlagen_richtlinie_eintrag e ON e.entry_id = g.id " +
            "LEFT JOIN anlagen_richtlinie_eintrag c ON c.child_entry_id = e.id " +
            "WHERE e.asset_allocation NOT BETWEEN 0 AND 100 OR c.asset_allocation NOT BETWEEN 0 AND 100 " +
            "OR e.chance_risk_number < 0 OR c.chance_risk_number < 0 OR e.chance_risk_number != c.chance_risk_number " +
            "OR e.max_riskclass NOT BETWEEN 1 AND 12 OR c.max_riskclass NOT BETWEEN 1 AND 12 OR e.max_riskclass != c.max_riskclass " +
            "OR e.max_volatility NOT BETWEEN 0 AND 100 OR c.max_volatility NOT BETWEEN 0 AND 100 OR e.max_volatility != c.max_volatility " +
            "OR e.performance < 0 OR c.performance < 0 OR e.performance != c.performance " +
            "OR e.rendite < 0 OR c.rendite < 0 OR e.rendite != c.rendite" , nativeQuery = true)
    List<Long> findAllByInvalidValues();

    /**
     * @return all portfolios where the sum of asset_allocation of the parent entries is not 100%.
     */
    @Query(value = "SELECT p.id " +
            "FROM portfolio p " +
            "JOIN anlagen_richtlinie g ON g.id = p.investment_guideline_id " +
            "JOIN anlagen_richtlinie_eintrag e ON e.entry_id = g.id " +
            "GROUP BY p.id " +
            "HAVING SUM(e.asset_allocation) != 100", nativeQuery = true)
    List<Long> findAllByParentSumIsNot100();

    /**
     *
     * @return all portfolios where the sum of asset_allocation of the child entries is not 0 and 100%.
     */
    @Query(value = "SELECT DISTINCT p.id " +
            "FROM portfolio p " +
            "JOIN anlagen_richtlinie g ON g.id = p.investment_guideline_id " +
            "JOIN anlagen_richtlinie_eintrag e ON e.entry_id = g.id " +
            "LEFT JOIN anlagen_richtlinie_eintrag c ON c.child_entry_id = e.id " +
            "GROUP BY p.id, e.investment_type " +
            "HAVING SUM(c.asset_allocation) != 0 AND SUM(c.asset_allocation) != 100",
            nativeQuery = true)
    List<Long> findAllByChildSumIsNot0And100();

    @Query(value = "SELECT e.investment_type AS parent_investment_type, c.investment_type AS child_investment_type " +
            "FROM portfolio p " +
            "JOIN anlagen_richtlinie g ON g.id = p.investment_guideline_id " +
            "JOIN anlagen_richtlinie_eintrag e ON e.entry_id = g.id " +
            "LEFT JOIN anlagen_richtlinie_eintrag c ON c.child_entry_id = e.id " +
            "WHERE p.id = :id", nativeQuery = true)
    List<String[]> findAllUsedInvestmentTypesBy(@Param("id") Long portfolioId);

    /**
     *
     * @param types pass always {@code InvestmentType.getValuesAsString()}.
     * @return all portfolios where any investment type in the investment-guideline is not valid.
     */
    @Query(value = "SELECT p.id " +
            "FROM portfolio p " +
            "JOIN anlagen_richtlinie g ON g.id = p.investment_guideline_id " +
            "JOIN anlagen_richtlinie_eintrag e ON e.entry_id = g.id " +
            "LEFT JOIN anlagen_richtlinie_eintrag c ON c.child_entry_id = e.id " +
            "WHERE e.investment_type NOT IN :types OR c.investment_type NOT IN :types", nativeQuery = true)
    List<Long> findAllByInvalidInvestmentTypes(@Param("types") List<String> types);

    /**
     * @return all portfolios where any entry in the investment-guideline is invalid. Invalid means here that the sum of
     * the asset_allocation is not 100%, values are not within their range (f.e. risk-class is not between 1 and 12) or
     * the investment type is not valid / not all investment types are used or more than one time used.
     */
    default List<Long> findAllByInvalidInvestmentGuidelineEntries() {
        List<Long> invalidEntryIds = new ArrayList<>();
        invalidEntryIds.addAll(findAllByInvalidValues());
        invalidEntryIds.addAll(findAllByParentSumIsNot100());
        invalidEntryIds.addAll(findAllByChildSumIsNot0And100());
        invalidEntryIds.addAll(findAllByInvalidInvestmentTypes(InvestmentType.getValuesAsString()));

        // Check if all investment types are used
        for (Long portfolioId: getAllIds()) {
            List<String[]> resultSet = findAllUsedInvestmentTypesBy(portfolioId);
            List<String> parentsInvestmentTypes = resultSet.stream().map(strings -> strings[0]).distinct().toList();
            List<String> childInvestmentTypes = resultSet.stream().map(strings -> strings[1]).toList();

            Arrays.stream(InvestmentType.values()).forEach(investmentType -> {
                if (!investmentType.isChild()) {
                    if (Collections.frequency(parentsInvestmentTypes, investmentType.name()) != 1) {
                        invalidEntryIds.add(portfolioId);
                    }
                } else {
                    if (Collections.frequency(childInvestmentTypes, investmentType.name()) != 1) {
                        invalidEntryIds.add(portfolioId);
                    }
                }
            });

            // Check that parent-entries contains only parent-literals (see InvestmentType) and same with child-entries
            for (String[] result : resultSet) {
                try {
                    InvestmentType parentType = InvestmentType.valueOf(result[0]);
                    InvestmentType childType = result[1] != null ? InvestmentType.valueOf(result[1]) : null;

                    if (parentType.isChild()) {
                        invalidEntryIds.add(portfolioId);
                    }
                    if (childType != null && (!childType.isChild()) || !parentType.getChilds().contains(childType)) {
                        invalidEntryIds.add(portfolioId);
                    }
                } catch (Exception ignore) {} // mapping-error is already handled above
            }
        }
        // return without duplicates
        return invalidEntryIds.stream().distinct().toList();
    }

    // If any value is null, the sum is null too.
    @Query(value = "SELECT p.id " +
            "FROM portfolio p " +
            "JOIN anlagen_richtlinie g ON g.id = p.investment_guideline_id " +
            "JOIN anlagen_richtlinie_unterteilung_ort gl ON gl.id = g.division_by_location_id " +
            "GROUP BY p.id " +
            "HAVING SUM(gl.asia_without_china) + SUM(gl.china) + SUM(gl.emergine_markets) + SUM(gl.europe_without_brd) + " +
            "SUM(gl.germany) + SUM(gl.japan) + SUM(gl.northamerica_with_usa) IS NULL " +
            "OR SUM(gl.asia_without_china) + SUM(gl.china) + SUM(gl.emergine_markets) + SUM(gl.europe_without_brd) + " +
            "SUM(gl.germany) + SUM(gl.japan) + SUM(gl.northamerica_with_usa) != 100",
            nativeQuery = true)
    List<Long> findAllBySumOfDivisionByLocationIsNot100();

    @Query(value = "SELECT p.id " +
            "FROM portfolio p " +
            "JOIN anlagen_richtlinie g ON g.id = p.investment_guideline_id " +
            "JOIN anlagen_richtlinie_unterteilung_währung gc ON gc.id = g.division_by_currency_id " +
            "GROUP BY p.id " +
            "HAVING SUM(gc.asia_currencies) + SUM(gc.chf) + SUM(gc.euro) + SUM(gc.gbp) + SUM(gc.others) + SUM(gc.usd) + SUM(gc.yen) IS NULL " +
            "OR SUM(gc.asia_currencies) + SUM(gc.chf) + SUM(gc.euro) + SUM(gc.gbp) + SUM(gc.others) + SUM(gc.usd) + SUM(gc.yen) != 100",
            nativeQuery = true)
    List<Long> findAllBySumOfDivisionByCurrencyIsNot100();

    @Query(value = "SELECT p.id " +
            "FROM portfolio p " +
            "WHERE p.created_at > NOW() " +
            "OR (p.deactivated_at IS NOT NULL AND p.deactivated_at < p.created_at) " +
            "OR (p.deactivated_at IS NOT NULL AND p.deactivated_at > NOW())", nativeQuery = true)
    List<Long> findAllByCreatedAtIsInFutureOrDeactivatedAtIsBeforeCreatedAtOrIsInFuture();
    // endregion

    // region Transaction to reconstruct a portfolio
    @Transactional
    @NonNull
    default Portfolio reconstructPortfolio(Long id) {
        Portfolio reconstructedPortfolio = new Portfolio();
        reconstructedPortfolio.setId(id);
        reconstructedPortfolio.getInvestmentGuideline().initializeEntries();
        reconstructedPortfolio.setName(findNameBy(id).orElse(null));
        reconstructedPortfolio.setCreatedAt(findCreatedAtBy(id).orElse(null));
        reconstructedPortfolio.setDeactivatedAt(findDeactivatedAtBy(id).orElse(null));

        reconstructedPortfolio.setState(findStateBy(id).map(s -> {
            try {
                return State.valueOf(s);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }).orElse(null));
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
            reconstructedPortfolio.setOwner(fakeOwner);
        });
        findInvestmentGuidelineBy(id).ifPresent(investmentGuidelineId -> {
            reconstructedPortfolio.getInvestmentGuideline().setId(investmentGuidelineId);

            // Load parent-entries here
            for (InvestmentGuideline.Entry parentEntry : reconstructedPortfolio.getInvestmentGuideline().getEntries()) {
                assert parentEntry.getType() != null; // because we have initialized the entries before

                findInvestmentGuidelineParentEntriesBy(investmentGuidelineId, parentEntry.getType().name())
                        .ifPresent(resultSet_ -> {
                            if (resultSet_.length == 0) return;
                            Object[] resultSet = (Object[]) resultSet_[0];
                            if (resultSet[1] != null) parentEntry.setAssetAllocation(((BigDecimal) resultSet[1]).floatValue());
                            if (resultSet[2] != null) parentEntry.setChanceRiskNumber(((BigDecimal) resultSet[2]).floatValue());
                            if (resultSet[3] != null) parentEntry.setMaxRiskclass((Integer) resultSet[3]);
                            if (resultSet[4] != null) parentEntry.setMaxVolatility(((BigDecimal) resultSet[4]).floatValue());
                            if (resultSet[5] != null) parentEntry.setPerformance(((BigDecimal) resultSet[5]).floatValue());
                            if (resultSet[6] != null) parentEntry.setRendite(((BigDecimal) resultSet[6]).floatValue());

                            // Load child-entries of parent here
                            for (InvestmentGuideline.Entry childEntry : parentEntry.getChildEntries()) {
                                assert childEntry.getType() != null; // because we have initialized the entries before

                                findInvestmentGuidelineChildEntryBy(((BigInteger) resultSet[0]).longValue(), childEntry.getType().name())
                                        .ifPresent(childResultSet_ -> {
                                            if (childResultSet_.length == 0) return;
                                            Object[] childResultSet = (Object[]) childResultSet_[0];
                                            if (childResultSet[0] != null) childEntry.setAssetAllocation(((BigDecimal) childResultSet[0]).floatValue());
                                            if (childResultSet[1] != null) childEntry.setChanceRiskNumber(((BigDecimal) childResultSet[1]).floatValue());
                                            if (childResultSet[2] != null) childEntry.setMaxRiskclass((Integer) childResultSet[2]);
                                            if (childResultSet[3] != null) childEntry.setMaxVolatility(((BigDecimal) childResultSet[3]).floatValue());
                                            if (childResultSet[4] != null) childEntry.setPerformance(((BigDecimal) childResultSet[4]).floatValue());
                                            if (childResultSet[5] != null) childEntry.setRendite(((BigDecimal) childResultSet[5]).floatValue());
                                        });
                            }
                        });
            }

            // Load division-by-location here
            findDivisionByLocationBy(investmentGuidelineId).ifPresent(divisionByLocationId -> {
                reconstructedPortfolio.getInvestmentGuideline().getDivisionByLocation().setId(divisionByLocationId);
                findDivisionByLocationValuesBy(divisionByLocationId).ifPresent(bigDecimals_ -> {
                    if (bigDecimals_.length == 0) return;
                    Object[] bigDecimals = (Object[]) bigDecimals_[0];
                    InvestmentGuideline.DivisionByLocation divisionByLocation = reconstructedPortfolio.getInvestmentGuideline().getDivisionByLocation();
                    if (bigDecimals[0] != null) divisionByLocation.setAsia_without_china(((BigDecimal) bigDecimals[0]).floatValue());
                    if (bigDecimals[1] != null) divisionByLocation.setChina(((BigDecimal) bigDecimals[1]).floatValue());
                    if (bigDecimals[2] != null) divisionByLocation.setEmergine_markets(((BigDecimal) bigDecimals[2]).floatValue());
                    if (bigDecimals[3] != null) divisionByLocation.setEurope_without_brd(((BigDecimal) bigDecimals[3]).floatValue());
                    if (bigDecimals[4] != null) divisionByLocation.setGermany(((BigDecimal) bigDecimals[4]).floatValue());
                    if (bigDecimals[5] != null) divisionByLocation.setJapan(((BigDecimal) bigDecimals[5]).floatValue());
                    if (bigDecimals[6] != null) divisionByLocation.setNorthamerica_with_usa(((BigDecimal) bigDecimals[6]).floatValue());
                });
            });

            // Load division-by-currency here
            findDivisionByCurrencyBy(investmentGuidelineId).ifPresent(divisionByCurrencyId -> {
                reconstructedPortfolio.getInvestmentGuideline().getDivisionByCurrency().setId(divisionByCurrencyId);
                findDivisionByCurrencyValuesBy(divisionByCurrencyId).ifPresent(bigDecimals_ -> {
                    if (bigDecimals_.length == 0) return;
                    Object[] bigDecimals = (Object[]) bigDecimals_[0];
                    InvestmentGuideline.DivisionByCurrency divisionByCurrency = reconstructedPortfolio.getInvestmentGuideline().getDivisionByCurrency();
                    if (bigDecimals[0] != null) divisionByCurrency.setAsia_currencies(((BigDecimal) bigDecimals[0]).floatValue());
                    if (bigDecimals[1] != null) divisionByCurrency.setChf(((BigDecimal) bigDecimals[1]).floatValue());
                    if (bigDecimals[2] != null) divisionByCurrency.setEuro(((BigDecimal) bigDecimals[2]).floatValue());
                    if (bigDecimals[3] != null) divisionByCurrency.setGbp(((BigDecimal) bigDecimals[3]).floatValue());
                    if (bigDecimals[4] != null) divisionByCurrency.setOthers(((BigDecimal) bigDecimals[4]).floatValue());
                    if (bigDecimals[5] != null) divisionByCurrency.setUsd(((BigDecimal) bigDecimals[5]).floatValue());
                    if (bigDecimals[6] != null) divisionByCurrency.setYen(((BigDecimal) bigDecimals[6]).floatValue());
                });
            });
        });
        return reconstructedPortfolio;
    }

    @Query(value = "SELECT p.name FROM portfolio p WHERE p.id = :id", nativeQuery = true)
    Optional<String> findNameBy(Long id);
    @Query(value = "SELECT p.state FROM portfolio p WHERE p.id = :id", nativeQuery = true)
    Optional<String> findStateBy(Long id);
    @Query(value = "SELECT p.owner_id FROM portfolio p WHERE p.id = :id", nativeQuery = true)
    Optional<Long> findOwnerBy(Long id);
    @Query(value = "SELECT o.forename FROM inhaber o WHERE o.id = :id", nativeQuery = true)
    Optional<String> findForenameById(Long id);
    @Query(value = "SELECT o.aftername FROM inhaber o WHERE o.id = :id", nativeQuery = true)
    Optional<String> findAfternameById(Long id);
    @Query(value = "SELECT p.created_at FROM portfolio p WHERE p.id = :id", nativeQuery = true)
    Optional<Timestamp> findCreatedAtBy(Long id);
    @Query(value = "SELECT p.deactivated_at FROM portfolio p WHERE p.id = :id", nativeQuery = true)
    Optional<Timestamp> findDeactivatedAtBy(Long id);
    @Query(value = "SELECT p.investment_guideline_id FROM portfolio p WHERE p.id = :id", nativeQuery = true)
    Optional<Long> findInvestmentGuidelineBy(Long id);
    @Query(value = "SELECT g.division_by_location_id FROM anlagen_richtlinie g WHERE g.id = :id", nativeQuery = true)
    Optional<Long> findDivisionByLocationBy(@Param("id") Long investmentGuidelineId);
    @Query(value = "SELECT g.division_by_currency_id FROM anlagen_richtlinie g WHERE g.id = :id", nativeQuery = true)
    Optional<Long> findDivisionByCurrencyBy(@Param("id") Long investmentGuidelineId);

    @Query(value = "SELECT gl.asia_without_china, gl.china, gl.emergine_markets, gl.europe_without_brd, gl.germany, gl.japan, gl.northamerica_with_usa " +
            "FROM anlagen_richtlinie_unterteilung_ort gl " +
            "WHERE gl.id = :id", nativeQuery = true)
    Optional<Object[]> findDivisionByLocationValuesBy(@Param("id") Long divisionByLocationId);

    @Query(value = "SELECT gc.asia_currencies, gc.chf, gc.euro, gc.gbp, gc.others, gc.usd, gc.yen " +
            "FROM anlagen_richtlinie_unterteilung_währung gc " +
            "WHERE gc.id = :id", nativeQuery = true)
    Optional<Object[]> findDivisionByCurrencyValuesBy(@Param("id") Long divisionByCurrencyId);

    @Query(value = "SELECT e.id, e.asset_allocation, e.chance_risk_number, e.max_riskclass, e.max_volatility, e.performance, e.rendite " +
            "FROM anlagen_richtlinie_eintrag e " +
            "WHERE e.entry_id = :id AND e.investment_type = :type " +
            "LIMIT 1", nativeQuery = true)
    Optional<Object[]> findInvestmentGuidelineParentEntriesBy(@Param("id") Long investmentGuidelineId, @Param("type") String investmentTypeName);

    @Query(value = "SELECT e.asset_allocation, e.chance_risk_number, e.max_riskclass, e.max_volatility, e.performance, e.rendite " +
            "FROM anlagen_richtlinie_eintrag e " +
            "WHERE e.child_entry_id = :id AND e.investment_type = :type " +
            "LIMIT 1", nativeQuery = true)
    Optional<Object[]> findInvestmentGuidelineChildEntryBy(@Param("id") Long parentEntryId, @Param("type") String investmentTypeName);
    // endregion

    // region Transaction to delete portfolio natively
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
    // endregion
}
