package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.portfolios;

import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabController;
import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabController;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Account;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.InvestmentGuideline;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Portfolio;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.BreadcrumbElementType;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.interfaces.Openable;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.repository.PortfolioRepository;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.FieldValidator;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.sql.Timestamp;
import java.util.*;
import java.util.function.Consumer;

@Service
public class PortfolioService {

    @Autowired
    private PortfolioRepository portfolioRepository;
    @Autowired
    private PortfolioManagementTabController portfolioManagementTabController;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Searches for inconsistencies in the database and tries to fix them. After that it returns the portfolio with the id, if it exists.
     * @throws NoSuchElementException if the portfolio with the given id does not exist.
     */
    public Portfolio findById(long id) throws NoSuchElementException {
        HashMap<Long, Long> idMapping = portfolioManagementTabController
                .checkForInconsistencies()
                .getOrDefault(BreadcrumbElementType.PORTFOLIO, new HashMap<>());
        if (idMapping.containsKey(id)) id = idMapping.get(id);
        return portfolioRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    /**
     * Deletes the portfolio by id.
     * @param id id of the account to delete.
     */
    public void deleteById(long id) {
        portfolioRepository.deleteById(id);
    }

    public List<Portfolio> getAll() {
        try {
            portfolioManagementTabController.checkForInconsistencies();
            return portfolioRepository.findAll();
        } catch (Exception e) {
            PrimaryTabManager.showDialog(
                    Alert.AlertType.ERROR,
                    "Fehler",
                    "Die Portfolios konnten nicht geladen werden.\nGrund: " + e.getMessage(),
                    null
            );
            return new ArrayList<>();
        }
    }

    /**
     * @param portfolio to save.
     * @return true if the portfolio was successfully saved, false otherwise.
     */
    public boolean save(Portfolio portfolio) {
        try {
            Optional<Long> id = portfolioRepository.getPortfolioBy(portfolio.getName());
            if (id.isPresent() && !id.get().equals(portfolio.getId()))
                throw new DataIntegrityViolationException("");

            portfolio.onPrePersistOrUpdateOrRemoveEntity();
            Portfolio persistedPortfolio = portfolioRepository.save(portfolio);
            // Update the owner with the id from the database.
            persistedPortfolio.onPostLoadEntity();
            portfolio.setId(persistedPortfolio.getId());
            return true;
        } catch (DataIntegrityViolationException integrityViolationException) {
            PrimaryTabManager.showDialog(
                    Alert.AlertType.ERROR,
                    "Fehler",
                    "Das Portfolio konnte nicht gespeichert werden, da bereits ein Portfolio mit dem selben Namen existiert.",
                    null
            );
        } catch (Exception e) {
            PrimaryTabManager.showDialog(
                    Alert.AlertType.ERROR,
                    "Unerwarteter Fehler",
                    "Das Portfolio konnte nicht gespeichert werden.\nGrund: " + e.getMessage(),
                    null
            );
        }
        return false;
    }

    /**
     * Shows a dialog to confirm the deletion of the portfolio and if confirmed, deletes it.
     * @param controller to refresh the view after deletion. If null, no view will be refreshed.
     */
    public void delete(Portfolio portfolio, @Nullable Openable controller) {
        PrimaryTabManager.showDialogWithAction(
                Alert.AlertType.WARNING,
                "Portfolio löschen",
                "Sind Sie sicher, dass Sie das Portfolio löschen möchten?\n" +
                        "Etwaige Beziehungen werden dabei nicht berücksichtigt und kann zu einem" +
                        " fehlerhaften Verhalten der Anwendung führen!",
                null,
                () -> {
                    portfolioRepository.delete(portfolio);
                    if (controller != null) controller.open();
                }
        );
    }

    public void writeInput(@NonNull Portfolio portfolio, boolean isOnCreate, @NonNull TextField inputPortfolioName,
                           @NonNull ComboBox<Owner> inputOwner) {
        portfolio.setName(inputPortfolioName.getText());
        portfolio.setOwner(inputOwner.getValue());
        if (isOnCreate) portfolio.setCreatedAt(Calendar.getInstance().getTime());
    }

    public boolean isInputInvalid(@NonNull TextField inputPortfolioName, @NonNull Portfolio portfolio, @NonNull Control control) {
        if (FieldValidator.isInputEmpty(inputPortfolioName)) return true;

        float sum_ = 0;
        for (InvestmentGuideline.Entry entry : portfolio.getInvestmentGuideline().getEntries()) {
            if (!entry.getType().isChild()) {
                var assetAlloc = entry.getAssetAllocation();
                sum_ += assetAlloc;

                float childsSum = 0;
                for (InvestmentGuideline.Entry child : entry.getChildEntries()) childsSum += child.getAssetAllocation();
                if (!entry.getChildEntries().isEmpty() && assetAlloc != 0 && childsSum != 100) {
                    PrimaryTabManager.showDialog(
                            Alert.AlertType.ERROR,
                            "Fehler",
                            String.format("Die Aufteilung des Gesamtvermögens für %s muss in Summe 100 ergeben (Ist: %s).", entry.getType(), childsSum),
                            control
                    );
                    return true;
                }
            }
        }
        if (sum_ != 100) {
            //commissionSchemeTablePane.getChildren().get(0).setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            PrimaryTabManager.showDialog(
                    Alert.AlertType.ERROR,
                    "Fehler",
                    String.format("Die Aufteilung des Gesamtvermögens muss in Summe 100 ergeben (Ist: %s).", sum_),
                    control
            );
            return true;
        }

        var divByLoc = portfolio.getInvestmentGuideline().getDivisionByLocation();
        float sumDivByLoc = divByLoc.getSum();
        if (sumDivByLoc != 100) {
            //commissionSchemeLocationTablePane.getChildren().get(0).setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            PrimaryTabManager.showDialog(
                    Alert.AlertType.ERROR,
                    "Fehler",
                    String.format("Die Aufteilung des Gesamtvermögens nach Ländern bzw. Regionen muss in Summe 100 ergeben (Ist: %s).", sumDivByLoc),
                    control
            );
            return true;
        }

        var divByCurr = portfolio.getInvestmentGuideline().getDivisionByCurrency();
        float sumDivByCurr = divByCurr.getSum();
        if (sumDivByCurr != 100) {
            //commissionSchemeCurrencyTablePane.getChildren().get(0).setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            PrimaryTabManager.showDialog(
                    Alert.AlertType.ERROR,
                    "Fehler",
                    String.format("Die Aufteilung des Gesamtvermögens nach Währung muss in Summe 100 ergeben (Ist: %s).", sumDivByCurr),
                    control
            );
            return true;
        }
        return false;
    }

    public PortfolioRepository getPortfolioRepository() {
        return portfolioRepository;
    }

    /**
     * Updates the portfolio via native-sql-queries instead of via hibernate.
     * @return true if the portfolio was successfully updated, false otherwise.
     */
    @Transactional
    @Modifying
    public boolean updatePortfolioNatively(@NonNull Portfolio portfolio) {
        portfolio.onPrePersistOrUpdateOrRemoveEntity();
        String sqlQuery;
        List<Long> ids;
        Query query;

        // region Update division by location
        ids = portfolioRepository.findAllByDivisionByLocationIsInvalid();

        if (ids.contains(portfolio.getId())) {
            sqlQuery = "INSERT INTO anlagen_richtlinie_unterteilung_ort (asia_without_china, china, emergine_markets, europe_without_brd, germany, japan, northamerica_with_usa) " +
                    "VALUES (:asia_without_china, :china, :emergine_markets, :europe_without_brd, :germany, :japan, :northamerica_with_usa)";
            query = entityManager.createNativeQuery(sqlQuery);
        } else {
            sqlQuery = "UPDATE anlagen_richtlinie_unterteilung_ort " +
                    "SET asia_without_china = :asia_without_china, china = :china, emergine_markets = :emergine_markets, europe_without_brd = :europe_without_brd, germany = :germany, japan = :japan, northamerica_with_usa = :northamerica_with_usa " +
                    "WHERE id = :id";
            query = entityManager.createNativeQuery(sqlQuery);
            query.setParameter("id", portfolio.getInvestmentGuideline().getDivisionByLocation().getId());
        }

        query.setParameter("asia_without_china", portfolio.getInvestmentGuideline().getDivisionByLocation().asiaWithoutChinaProperty.get());
        query.setParameter("china", portfolio.getInvestmentGuideline().getDivisionByLocation().chinaProperty.get());
        query.setParameter("emergine_markets", portfolio.getInvestmentGuideline().getDivisionByLocation().emergineMarketsProperty.get());
        query.setParameter("europe_without_brd", portfolio.getInvestmentGuideline().getDivisionByLocation().europeWithoutBrdProperty.get());
        query.setParameter("germany", portfolio.getInvestmentGuideline().getDivisionByLocation().germanyProperty.get());
        query.setParameter("japan", portfolio.getInvestmentGuideline().getDivisionByLocation().japanProperty.get());
        query.setParameter("northamerica_with_usa", portfolio.getInvestmentGuideline().getDivisionByLocation().northAmericaWithUsaProperty.get());

        try {
            int insertedRows = query.executeUpdate();

            if (insertedRows > 0) {
                // update id
                if (ids.contains(portfolio.getId())) {
                    Query idQuery = entityManager.createNativeQuery("SELECT LAST_INSERT_ID()");
                    Long newId = ((Number) idQuery.getSingleResult()).longValue();
                    portfolio.getInvestmentGuideline().getDivisionByLocation().setId(newId);
                }
            } else throw new Exception("Die Unterteilung nach Ort konnte nicht gespeichert werden.");
        } catch (Exception e) {
            PrimaryTabController.unknownErrorMsg.accept(e);
            return false;
        }
        // endregion

        // region Update division by currency
        ids = portfolioRepository.findAllByDivisionByCurrencyIsInvalid();

        if (ids.contains(portfolio.getId())) {
            sqlQuery = "INSERT INTO anlagen_richtlinie_unterteilung_währung (asia_currencies, chf, euro, gbp, others, usd, yen) " +
                    "VALUES (:asia_currencies, :chf, :euro, :gbp, :others, :usd, :yen)";
            query = entityManager.createNativeQuery(sqlQuery);
        } else {
            sqlQuery = "UPDATE anlagen_richtlinie_unterteilung_währung " +
                    "SET asia_currencies = :asia_currencies, chf = :chf, euro = :euro, gbp = :gbp, others = :others, usd = :usd, yen = :yen " +
                    "WHERE id = :id";
            query = entityManager.createNativeQuery(sqlQuery);
            query.setParameter("id", portfolio.getInvestmentGuideline().getDivisionByCurrency().getId());
        }

        query.setParameter("asia_currencies", portfolio.getInvestmentGuideline().getDivisionByCurrency().asiaCurrenciesProperty.get());
        query.setParameter("chf", portfolio.getInvestmentGuideline().getDivisionByCurrency().chfProperty.get());
        query.setParameter("euro", portfolio.getInvestmentGuideline().getDivisionByCurrency().euroProperty.get());
        query.setParameter("gbp", portfolio.getInvestmentGuideline().getDivisionByCurrency().gbpProperty.get());
        query.setParameter("others", portfolio.getInvestmentGuideline().getDivisionByCurrency().othersProperty.get());
        query.setParameter("usd", portfolio.getInvestmentGuideline().getDivisionByCurrency().usdProperty.get());
        query.setParameter("yen", portfolio.getInvestmentGuideline().getDivisionByCurrency().yenProperty.get());

        try {
            int insertedRows = query.executeUpdate();

            if (insertedRows > 0) {
                // update id
                if (ids.contains(portfolio.getId())) {
                    Query idQuery = entityManager.createNativeQuery("SELECT LAST_INSERT_ID()");
                    Long newId = ((Number) idQuery.getSingleResult()).longValue();
                    portfolio.getInvestmentGuideline().getDivisionByCurrency().setId(newId);
                }
            } else throw new Exception("Die Unterteilung nach Ort konnte nicht persistiert werden.");
        } catch (Exception e) {
            PrimaryTabController.unknownErrorMsg.accept(e);
            return false;
        }
        // endregion

        // region Update investment guideline
        ids = portfolioRepository.findAllByInvestmentguidelineIsInvalid();

        if (ids.contains(portfolio.getId())) {
            sqlQuery = "INSERT INTO anlagen_richtlinie (division_by_location_id, division_by_currency_id) " +
                    "VALUES (:division_by_location_id, :division_by_currency_id)";
            query = entityManager.createNativeQuery(sqlQuery);
        } else {
            sqlQuery = "UPDATE anlagen_richtlinie " +
                    "SET division_by_location_id = :division_by_location_id, division_by_currency_id = :division_by_currency_id " +
                    "WHERE id = :id";
            query = entityManager.createNativeQuery(sqlQuery);
            query.setParameter("id", portfolio.getInvestmentGuideline().getId());
        }

        query.setParameter("division_by_location_id", portfolio.getInvestmentGuideline().getDivisionByLocation().getId());
        query.setParameter("division_by_currency_id", portfolio.getInvestmentGuideline().getDivisionByCurrency().getId());

        try {
            int insertedRows = query.executeUpdate();

            if (insertedRows > 0) {
                // update id
                if (ids.contains(portfolio.getId())) {
                    Query idQuery = entityManager.createNativeQuery("SELECT LAST_INSERT_ID()");
                    Long newId = ((Number) idQuery.getSingleResult()).longValue();
                    portfolio.getInvestmentGuideline().setId(newId);
                }
            } else throw new Exception("Die Anlagenichtlinie konnte nicht persistiert werden.");
        } catch (Exception e) {
            PrimaryTabController.unknownErrorMsg.accept(e);
            return false;
        }
        // endregion

        // region Re-create investment guideline entries
        portfolioRepository.deleteInvestmentGuidelineEntries(portfolio);

        for (InvestmentGuideline.Entry parentEntry : portfolio.getInvestmentGuideline().getEntries()) {
            sqlQuery = "INSERT INTO anlagen_richtlinie_eintrag (asset_allocation, chance_risk_number, max_riskclass, max_volatility, " +
                    "performance, rendite, investment_type, entry_id, child_entry_id) " +
                    "VALUES (:asset_allocation, :chance_risk_number, :max_riskclass, :max_volatility, :performance, :rendite, " +
                    ":investment_type, :entry_id, :child_entry_id)";

            // Try to re-create the parent first
            query = entityManager.createNativeQuery(sqlQuery);

            query.setParameter("asset_allocation", parentEntry.assetAllocationProperty.get());
            query.setParameter("chance_risk_number", parentEntry.chanceRiskNumberProperty.get());
            query.setParameter("max_riskclass", parentEntry.maxRiskclassProperty.get());
            query.setParameter("max_volatility", parentEntry.maxVolatilityProperty.get());
            query.setParameter("performance", parentEntry.performanceProperty.get());
            query.setParameter("rendite", parentEntry.performanceSinceBuyProperty.get());
            query.setParameter("investment_type", parentEntry.getType().name());
            query.setParameter("entry_id", portfolio.getInvestmentGuideline().getId());
            query.setParameter("child_entry_id", null);

            try {
                int insertedRows = query.executeUpdate();

                if (insertedRows > 0) {
                    // update id
                    Query idQuery = entityManager.createNativeQuery("SELECT LAST_INSERT_ID()");
                    Long newId = ((Number) idQuery.getSingleResult()).longValue();
                    parentEntry.setId(newId);
                } else throw new Exception("Die Einträge der Anlagenichtlinie konnten nicht persistiert werden.");
            } catch (Exception e) {
                e.printStackTrace();
                PrimaryTabController.unknownErrorMsg.accept(e);
                return false;
            }

            // If it was successful, try to re-create the children
            for (InvestmentGuideline.Entry childEntry : parentEntry.getChildEntries()) {
                query = entityManager.createNativeQuery(sqlQuery);

                query.setParameter("asset_allocation", childEntry.assetAllocationProperty.get());
                query.setParameter("chance_risk_number", childEntry.chanceRiskNumberProperty.get());
                query.setParameter("max_riskclass", childEntry.maxRiskclassProperty.get());
                query.setParameter("max_volatility", childEntry.maxVolatilityProperty.get());
                query.setParameter("performance", childEntry.performanceProperty.get());
                query.setParameter("rendite", childEntry.performanceSinceBuyProperty.get());
                query.setParameter("investment_type", childEntry.getType().name());
                query.setParameter("entry_id", null);
                query.setParameter("child_entry_id", parentEntry.getId());

                try {
                    int insertedRows = query.executeUpdate();
                    if (insertedRows == 0) throw new Exception("Die Einträge der Anlagenichtlinie konnten nicht persistiert werden.");
                } catch (Exception e) {
                    e.printStackTrace();
                    PrimaryTabController.unknownErrorMsg.accept(e);
                    return false;
                }
            }
        }
        // endregion

        // region Update portfolio
        sqlQuery = "UPDATE portfolio " +
                "SET created_at = :created_at, deactivated_at = :deactivated_at, name = :name, state = :state, " +
                "investment_guideline_id = :investment_guideline_id, owner_id = :owner_id " +
                "WHERE id = :id";

        query = entityManager.createNativeQuery(sqlQuery);

        query.setParameter("created_at", new Timestamp(portfolio.getCreatedAt().getTime()));
        query.setParameter("deactivated_at", portfolio.getDeactivatedAt() == null ? null : new Timestamp(portfolio.getDeactivatedAt().getTime()));
        query.setParameter("name", portfolio.getName());
        query.setParameter("state", portfolio.getState().name());
        query.setParameter("investment_guideline_id", portfolio.getInvestmentGuideline().getId());
        query.setParameter("owner_id", portfolio.getOwner().getId());
        query.setParameter("id", portfolio.getId());

        try {
            int insertedRows = query.executeUpdate();
            return insertedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            PrimaryTabController.unknownErrorMsg.accept(e);
            return false;
        }
        // endregion
    }
}
