package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.portfolios.dialog;

import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.InvestmentGuideline;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Portfolio;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners.OwnerService;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.portfolios.PortfolioService;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.FieldValidator;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.InvestmentGuidelineTable;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.TableFactory;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.Calendar;

@Controller
public class CreatePortfolioDialog {

    Portfolio portfolio = new Portfolio();

    @Autowired
    OwnerService ownerService;
    @Autowired
    PortfolioService portfolioService;
    @Autowired
    private PortfolioManagementTabManager portfolioManagementTabManager;

    @FXML
    TextField inputPortfolioName;

    @FXML
    ComboBox<Owner> inputOwner;

    @FXML
    AnchorPane commissionSchemeTablePane;

    @FXML
    AnchorPane commissionSchemeLocationTablePane;

    @FXML
    AnchorPane commissionSchemeCurrencyTablePane;

    CreatePortfolioDialog() {
        portfolio.getInvestmentGuideline().initializeEntries();
    }

    @FXML
    private void initialize() {
        inputOwner.getItems().addAll(ownerService.getAllOwners());
        inputOwner.getSelectionModel().selectFirst();

        commissionSchemeTablePane.getChildren().add(new InvestmentGuidelineTable(
                commissionSchemeTablePane,
                portfolio.getInvestmentGuideline().getEntries()
        ));
        commissionSchemeLocationTablePane.getChildren().add(TableFactory.createPortfolioDivisionByLocationTable(
                commissionSchemeLocationTablePane,
                portfolio.getInvestmentGuideline().getDivisionByLocation()
        ));
        commissionSchemeCurrencyTablePane.getChildren().add(TableFactory.createPortfolioDivisionByCurrencyTable(
                commissionSchemeCurrencyTablePane,
                portfolio.getInvestmentGuideline().getDivisionByCurrency()
        ));
    }

    @FXML
    private void onCancel() {
        portfolioManagementTabManager.getPortfolioController().getPortfolioListController().open();
        inputPortfolioName.getScene().getWindow().hide();
    }

    @FXML
    private void onSave() {
        // Validate first
        if (FieldValidator.isInputEmpty(inputPortfolioName)) return;

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
                            (TreeTableView<?>) commissionSchemeTablePane.getChildren().get(0)
                    );
                    return;
                }
            }
        }
        if (sum_ != 100) {
            //commissionSchemeTablePane.getChildren().get(0).setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            PrimaryTabManager.showDialog(
                    Alert.AlertType.ERROR,
                    "Fehler",
                    String.format("Die Aufteilung des Gesamtvermögens muss in Summe 100 ergeben (Ist: %s).", sum_),
                    (TreeTableView<?>) commissionSchemeTablePane.getChildren().get(0)
            );
            return;
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
                    (TableView<?>) commissionSchemeLocationTablePane.getChildren().get(0)
            );
            return;
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
                    (TableView<?>) commissionSchemeCurrencyTablePane.getChildren().get(0)
            );
            return;
        }

        // If everything is valid, we can create and save the new portfolio
        portfolio.setName(inputPortfolioName.getText());
        portfolio.setOwner(inputOwner.getValue());
        portfolio.setCreatedAt(Calendar.getInstance().getTime());

        portfolioService.save(portfolio);
        onCancel();

        // Finally, show success-dialog
        PrimaryTabManager.showInfoDialog(
                "Portfolio angelegt",
                "Das neue Portfolio wurde erfolgreich angelegt.",
                inputPortfolioName
        );
    }

    private float sum(Float... attribute) {
        float sum = 0;
        for (Float value : attribute) {
            sum += value;
        }
        return sum;
    }
}
