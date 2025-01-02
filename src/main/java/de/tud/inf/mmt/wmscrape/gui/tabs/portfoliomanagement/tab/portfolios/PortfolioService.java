package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.portfolios;

import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Account;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.InvestmentGuideline;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Portfolio;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.interfaces.Openable;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.repository.PortfolioRepository;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.FieldValidator;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sound.sampled.Port;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service
public class PortfolioService {

    @Autowired
    PortfolioRepository portfolioRepository;

    public Portfolio findById(long id) {
        return portfolioRepository.findById(id).orElseThrow();
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

    public boolean save(Portfolio portfolio) {
        try {
            portfolioRepository.save(portfolio);
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
                    "Das Portfolio konnte nicht gespeichert werden.",
                    null
            );
        }
        return false;
    }

    /**
     * Deletes the portfolio and saves it again.
     * @return true if the portfolio was successfully saved, false otherwise.
     */
    @Transactional
    public boolean reSave(Portfolio portfolio) {
        deleteById(portfolio.getId());
        return save(portfolio);
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
                        "Etwaige Beziehungen zu Konten und Depots werden dabei nicht berücksichtigt und kann zu einem" +
                        " fehlerhaften Verhalten der Anwendung führen!;",
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
}
