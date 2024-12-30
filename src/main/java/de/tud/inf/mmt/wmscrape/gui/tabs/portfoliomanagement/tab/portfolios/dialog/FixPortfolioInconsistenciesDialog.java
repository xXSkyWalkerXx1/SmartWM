package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.portfolios.dialog;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Portfolio;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.State;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.ComboBoxValidator;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.FieldValidator;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextInputControl;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Controller
public class FixPortfolioInconsistenciesDialog extends CreatePortfolioDialog {

    @FXML
    ComboBox<State> inputState;
    @FXML
    DatePicker inputCreatedAt;
    @FXML
    DatePicker inputDeactivatedAt;

    @Override
    protected void initialize() {
        if (portfolio == null) throw new IllegalStateException("Portfolio must be set before initializing dialog.");
        super.initialize();

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

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
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
