package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.portfolios;

import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Account;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.InvestmentGuideline;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Portfolio;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.interfaces.Openable;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.repository.PortfolioRepository;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.FieldValidator;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.InvestmentGuidelineTable;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import javax.sound.sampled.Port;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Service
public class PortfolioService {

    @Autowired
    PortfolioRepository portfolioRepository;

    public Portfolio findById(long id) {
        return portfolioRepository.findById(id).orElseThrow();
    }

    public List<Portfolio> getAll() {
        return portfolioRepository.findAll();
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
        }
        return false;
    }

    /**
     * @param controller to refresh the view after deletion.
     */
    public void delete(Portfolio portfolio, @NonNull Openable controller) {
        PrimaryTabManager.showDialogWithAction(
                Alert.AlertType.WARNING,
                "Portfolio löschen",
                "Sind Sie sicher, dass Sie das Portfolio löschen möchten?\n" +
                        "Etwaige Beziehungen zu Konten und Depots werden dabei nicht berücksichtigt und kann zu einem" +
                        " fehlerhaften Verhalten der Anwendung führen!;",
                null,
                o -> {
                    portfolioRepository.delete(portfolio);
                    controller.open();
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
        float sumDivByLoc = sum(
                divByLoc.getGermany(), divByLoc.getEurope_without_brd(), divByLoc.getNorthamerica_with_usa(),
                divByLoc.getAsia_without_china(), divByLoc.getChina(), divByLoc.getJapan(), divByLoc.getEmergine_markets()
        );
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
        float sumDivByCurr = sum(
                divByCurr.getEuro(), divByCurr.getUsd(), divByCurr.getGbp(), divByCurr.getYen(),
                divByCurr.getAsiaCurrencies(), divByCurr.getOthers()
        );
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

    /**
     * Helper method; sums up the given values.
     */
    private float sum(Float... floats) {
        float sum = 0;
        for (Float value : floats) {
            sum += value;
        }
        return sum;
    }
}
