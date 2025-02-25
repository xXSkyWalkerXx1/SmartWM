package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.portfolios.dialog;

import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabController;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.InvestmentGuideline;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Portfolio;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.State;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.repository.PortfolioRepository;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.ComboBoxValidator;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.FieldFormatter;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.FieldValidator;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Controller
public class FixPortfolioInconsistenciesDialog extends CreatePortfolioDialog {

    @Autowired
    private final PortfolioRepository portfolioRepository;
    @Autowired
    private PortfolioManagementTabController portfolioManagementTabController;

    @FXML
    ComboBox<State> inputState;
    @FXML
    DatePicker inputCreatedAt;
    @FXML
    DatePicker inputDeactivatedAt;

    public FixPortfolioInconsistenciesDialog(PortfolioRepository portfolioRepository) {
        super();
        this.portfolioRepository = portfolioRepository;
    }

    @Override
    protected void initialize() {
        if (portfolio == null) throw new IllegalStateException("Portfolio must be set before initializing dialog.");
        super.initialize();

        // Expand all nodes of the guideline table
        investmentGuidelineTableTreeTableView.getRoot().getChildren().forEach(child -> child.setExpanded(true));

        // Deactivated at should not be before created at
        FieldFormatter.setActivatedAtFormatter(inputCreatedAt);
        FieldFormatter.setDeactivatedAtFormatter(inputCreatedAt, inputDeactivatedAt);

        // Set content of combo-boxes
        inputOwner.getItems().setAll(ownerService.getOwnerRepository().findAllAsFake());

        inputState.getItems().setAll(State.values());
        inputState.getSelectionModel().selectedItemProperty().addListener((observableValue, state, t1) -> {
            if (t1 == State.DEACTIVATED) {
                inputDeactivatedAt.setDisable(false);
            } else if (t1 == State.ACTIVATED) {
                inputDeactivatedAt.setDisable(true);
                inputDeactivatedAt.setValue(null);
            }
        });

        // Set the owner's information
        inputPortfolioName.setText(portfolio.getName());
        inputOwner.getSelectionModel().select(portfolio.getOwner());
        inputState.getSelectionModel().select(portfolio.getState());

        if (portfolio.getCreatedAt() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(portfolio.getCreatedAt());
            inputCreatedAt.setValue(LocalDate.of(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH)
            ));
            inputCreatedAt.getEditor().setText(inputCreatedAt.getValue().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        }

        if (portfolio.getDeactivatedAt() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(portfolio.getDeactivatedAt());
            inputDeactivatedAt.setValue(LocalDate.of(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH)
            ));
            inputDeactivatedAt.getEditor().setText(inputDeactivatedAt.getValue().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        }

        // Highlight fields that are invalid
        areTextFieldsValid();
        areComboboxInputsValid();

    }

    @FXML
    private void onDelete() {
        portfolioRepository.deleteById(portfolio.getId());
        portfolioManagementTabController.navigateBackAfterDeletion(portfolio);
        onCancel();
    }

    @Override
    protected void onCancel() {
        inputPortfolioName.getParent().getScene().getWindow().hide();
    }

    @Override
    protected void onSave() {
        // Validate first
        boolean areTextFieldsValid = areTextFieldsValid();
        boolean areComboboxInputsValid = areComboboxInputsValid();
        if (!areTextFieldsValid || !areComboboxInputsValid) return;

        if (!areInvestmentGuidelineEntriesValid()) {
            PrimaryTabManager.showDialog(
                    Alert.AlertType.ERROR,
                    "Fehlerhafte Eingabe",
                    "Die Eingaben für die Anlage-Richtlinien sind fehlerhaft.",
                    inputPortfolioName
            );
            return;
        }

        if (!areDivisionByLocationInputsValid()) {
            PrimaryTabManager.showDialog(
                    Alert.AlertType.ERROR,
                    "Fehlerhafte Eingabe",
                    "Die Eingaben für die Aufteilung nach Standort sind fehlerhaft.",
                    inputPortfolioName
            );
            return;
        }

        if (!areDivisionByCurrencyInputsValid()) {
            PrimaryTabManager.showDialog(
                    Alert.AlertType.ERROR,
                    "Fehlerhafte Eingabe",
                    "Die Eingaben für die Aufteilung nach Währung sind fehlerhaft.",
                    inputPortfolioName
            );
            return;
        }

        if (portfolioService.isInputInvalid(
                inputPortfolioName, portfolio,
                (Control) commissionSchemeTablePane.getChildren().get(0)
        )) return;

        // If everything is valid, we can create and save the new owner
        portfolioService.writeInput(portfolio, false, inputPortfolioName, inputOwner);
        portfolio.setState(inputState.getSelectionModel().getSelectedItem());
        portfolio.setCreatedAt(java.sql.Date.valueOf(LocalDate.parse(
                inputCreatedAt.getEditor().getText(),
                DateTimeFormatter.ofPattern("dd.MM.yyyy")
        )));
        if (State.DEACTIVATED.equals(portfolio.getState())) {
            portfolio.setDeactivatedAt(java.sql.Date.valueOf(LocalDate.parse(
                    inputDeactivatedAt.getEditor().getText(),
                    DateTimeFormatter.ofPattern("dd.MM.yyyy")
            )));
        }

        if (!portfolioService.updatePortfolioNatively(portfolio)) return;
        onCancel();

        // Finally, show success-dialog
        PrimaryTabManager.showAndWaitInfoDialog(
                "Portfolio aktualisiert",
                "Das Portfolio wurde erfolgreich aktualisiert.",
                inputState
        );
    }

    private boolean areInvestmentGuidelineEntriesValid() {
        boolean isValid = true;

        for (InvestmentGuideline.Entry parentEntry : portfolio.getInvestmentGuideline().getEntries()) {
             isValid &= FieldValidator.isInRange(
                     0f,
                     100f,
                     parentEntry.getAssetAllocation(), parentEntry.getMaxVolatility()
             );
             isValid &= FieldValidator.isInRange(
                     0f,
                     12f,
                     parentEntry.getMaxRiskclass()
             );
             isValid &= FieldValidator.isInRange(
                     0f,
                     null,
                     parentEntry.getPerformance(), parentEntry.getPerformanceSinceBuy(), parentEntry.getChanceRiskNumber()
             );

             // We don't need to check the other values, because they will be updated with the parent entry before persisting.
             for (InvestmentGuideline.Entry childEntry : parentEntry.getChildEntries()) {
                    isValid &= FieldValidator.isInRange(
                            0f,
                            100f,
                            childEntry.getAssetAllocation()
                    );
             }
        }
        return isValid;
    }

    private boolean areDivisionByLocationInputsValid() {
        var division = portfolio.getInvestmentGuideline().getDivisionByLocation();
        return FieldValidator.isInRange(
                0f,
                100f,
                division.getAsia_without_china(), division.getChina(), division.getEmergine_markets(),
                division.getEurope_without_brd(), division.getGermany(), division.getJapan(), division.getNorthamerica_with_usa()
        );
    }

    private boolean areDivisionByCurrencyInputsValid() {
        var division = portfolio.getInvestmentGuideline().getDivisionByCurrency();
        return FieldValidator.isInRange(
                0f,
                100f,
                division.getAsiaCurrencies(), division.getChf(), division.getEuro(), division.getGbp(),
                division.getOthers(), division.getUsd(), division.getYen()
        );
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    private boolean areTextFieldsValid() {
        inputCreatedAt.getEditor().tooltipProperty().unbind();
        inputDeactivatedAt.getEditor().tooltipProperty().unbind();

        List<TextInputControl> inputs = new ArrayList<>(List.of(inputPortfolioName, inputCreatedAt.getEditor(), inputCreatedAt.getEditor()));
        if (State.DEACTIVATED.equals(inputState.getSelectionModel().getSelectedItem())) {
            inputs.add(inputDeactivatedAt.getEditor());
        }
        return !FieldValidator.isInputEmpty(inputs.toArray(new TextInputControl[0]));
    }

    private boolean areComboboxInputsValid() {
        return ComboBoxValidator.areComboboxInputsValid(List.of(inputState, inputOwner));
    }
}
