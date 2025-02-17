package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners.dialog;

import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabController;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.MaritalState;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.State;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.ComboBoxValidator;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.FieldValidator;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.FormatUtils;
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
public class FixOwnerInconsistenciesDialog extends CreateOwnerDialog {

    Owner owner;

    @Autowired
    private PortfolioManagementTabController portfolioManagementTabController;

    @FXML
    ComboBox<State> inputState;
    @FXML
    DatePicker inputCreatedAt;
    @FXML
    DatePicker inputDeactivatedAt;

    @FXML
    protected void initialize() {
        if (owner == null) throw new IllegalStateException("Owner must be set before initializing dialog.");
        super.initialize();

        // Set content of combo-boxes
        inputState.getItems().setAll(State.values());
        inputState.getSelectionModel().selectedItemProperty().addListener((observableValue, state, t1) -> {
            if (t1 == State.DEACTIVATED) {
                inputDeactivatedAt.setDisable(false);
            } else if (t1 == State.ACTIVATED) {
                inputDeactivatedAt.setDisable(true);
                inputDeactivatedAt.setValue(null);
            }
            areComboboxInputsValid();
        });

        // Set the owner's information
        inputForename.setText(owner.getForename());
        inputAftername.setText(owner.getAftername());
        inputNotice.setText(owner.getNotice());
        inputState.getSelectionModel().select(owner.getState());
        inputCountry.getSelectionModel().select(owner.getAddress().getCountry());
        inputPlz.setText(owner.getAddress().getPlz());
        inputLocation.setText(owner.getAddress().getLocation());
        inputStreet.setText(owner.getAddress().getStreet());
        inputStreetNumber.setText(owner.getAddress().getStreetNumber());
        inputTaxNumber.setText(owner.getTaxInformation().getTaxNumber());
        inputMaritalState.getSelectionModel().select(owner.getTaxInformation().getMaritalState());

        if (owner.getCreatedAt() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(owner.getCreatedAt());
            inputCreatedAt.setValue(LocalDate.of(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH)
            ));
            inputCreatedAt.getEditor().setText(inputCreatedAt.getValue().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        }
        if (owner.getDeactivatedAt() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(owner.getCreatedAt());
            inputDeactivatedAt.setValue(LocalDate.of(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH)
            ));
            inputDeactivatedAt.getEditor().setText(inputDeactivatedAt.getValue().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        }
        if (owner.getTaxInformation().getTaxRateBigDecimal() != null) {
            inputTaxRate.setText(FormatUtils.formatFloat((float) owner.getTaxInformation().getTaxRate()));
        }
        if (owner.getTaxInformation().getChurchTaxRateBigDecimal() != null) {
            inputChurchTaxRate.setText(FormatUtils.formatFloat((float) owner.getTaxInformation().getChurchTaxRate()));
        }
        if (owner.getTaxInformation().getCapitalGainsTaxRateBigDecimal() != null) {
            inputCapitalGainsTaxRate.setText(FormatUtils.formatFloat((float) owner.getTaxInformation().getCapitalGainsTaxRate()));
        }
        if (owner.getTaxInformation().getSolidaritySurchargeTaxRateBigDecimal() != null) {
            inputSolidaritySurchargeTaxRate.setText(FormatUtils.formatFloat((float) owner.getTaxInformation().getSolidaritySurchargeTaxRate()));
        }

        // Highlight fields that are invalid
        areTextFieldsValid();
        areComboboxInputsValid();
    }

    @FXML
    private void onDelete() {
        ownerService.deleteById(owner.getId());
        portfolioManagementTabController.navigateBackAfterDeletion(owner);
        onCancel();
    }

    @Override
    protected void onCancel() {
        inputForename.getParent().getScene().getWindow().hide();
    }

    @Override
    protected void onSave() {
        // Validate first
        boolean areTextFieldsValid = areTextFieldsValid();
        boolean areComboboxInputsValid = areComboboxInputsValid();
        if (!areTextFieldsValid || !areComboboxInputsValid) return;

        // If everything is valid, we can create and save the new owner
        ownerService.writeInput(
                owner, false,
                inputForename, inputAftername, inputNotice,
                inputCountry, inputPlz, inputLocation, inputStreet, inputStreetNumber, inputTaxNumber,
                inputMaritalState, inputTaxRate,
                inputChurchTaxRate, inputCapitalGainsTaxRate, inputSolidaritySurchargeTaxRate
        );
        owner.setState(inputState.getSelectionModel().getSelectedItem());
        owner.setCreatedAt(java.sql.Date.valueOf(LocalDate.parse(
                inputCreatedAt.getEditor().getText(),
                DateTimeFormatter.ofPattern("dd.MM.yyyy")
        )));
        if (State.DEACTIVATED.equals(owner.getState())) {
            owner.setDeactivatedAt(java.sql.Date.valueOf(LocalDate.parse(
                    inputDeactivatedAt.getEditor().getText(),
                    DateTimeFormatter.ofPattern("dd.MM.yyyy")
            )));
        }

        if (!ownerService.reSave(owner)) return;
        onCancel();

        // Finally, show success-dialog
        PrimaryTabManager.showInfoDialog(
                "Inhaber aktualisiert",
                "Der Inhaber wurde erfolgreich aktualisiert.",
                inputForename
        );
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public Owner getOwner() {
        return owner;
    }

    private boolean areTextFieldsValid() {
        inputCreatedAt.getEditor().tooltipProperty().unbind();
        inputDeactivatedAt.getEditor().tooltipProperty().unbind();

        List<TextInputControl> inputs = new ArrayList<>(List.of(
                inputForename, inputAftername, inputCreatedAt.getEditor(), /*inputCountry,*/
                inputPlz, inputLocation, inputStreet, inputStreetNumber, inputTaxNumber, inputTaxRate, inputCapitalGainsTaxRate,
                inputSolidaritySurchargeTaxRate
        ));
        if (State.DEACTIVATED.equals(inputState.getSelectionModel().getSelectedItem())) {
            inputs.add(inputDeactivatedAt.getEditor());
        }
        return !FieldValidator.isInputEmpty(inputs.toArray(new TextInputControl[0]));
    }

    private boolean areComboboxInputsValid() {
        return ComboBoxValidator.areComboboxInputsValid(List.of(inputState, inputMaritalState, inputCountry));
    }
}
