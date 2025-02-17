package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.kontos.dialog;

import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabController;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Account;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.State;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.ComboBoxValidator;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.FieldValidator;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.FormatUtils;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextInputControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Controller
public class FixAccountInconcistenciesDialog extends CreateAccountDialog {

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
        if (account == null) throw new IllegalStateException("Account must be set before initializing dialog.");
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
        });

        inputOwner.getItems().setAll(ownerService.getOwnerRepository().findAllAsFake());
        inputPortfolio.getItems().setAll(portfolioService.getPortfolioRepository().findAllAsFake());

        // Set the owner's information
        inputDescription.setText(account.getDescription());
        inputType.getSelectionModel().select(account.getType());
        inputCurrencyCode.getSelectionModel().select(account.getCurrency());
        inputOwner.getSelectionModel().select(account.getOwner());
        inputPortfolio.getSelectionModel().select(account.getPortfolio());
        inputNotice.setText(account.getNotice());
        inputState.getSelectionModel().select(account.getState());
        inputBankName.setText(account.getBankName());
        inputIban.setText(account.getIban());
        inputKontoNumber.setText(account.getKontoNumber());
        inputInterestDays.setText(String.valueOf(account.getInterestDays()));
        inputInterestInterval.getSelectionModel().select(account.getInterestInterval());

        if (account.getCreatedAt() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(account.getCreatedAt());
            inputCreatedAt.setValue(LocalDate.of(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH)
            ));
            inputCreatedAt.getEditor().setText(inputCreatedAt.getValue().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        }

        if (account.getDeactivatedAt() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(account.getDeactivatedAt());
            inputDeactivatedAt.setValue(LocalDate.of(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH)
            ));
            inputDeactivatedAt.getEditor().setText(inputDeactivatedAt.getValue().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        }

        if (account.getBalanceBigDecimal() != null) {
            inputBalance.setText(FormatUtils.formatFloat((float) account.getBalance()));
        }
        if (account.getInterestRateBigDecimal() != null) {
            inputInterestRate.setText(FormatUtils.formatFloat((float) account.getInterestRate()));
        }

        // Highlight fields that are invalid
        areTextFieldsValid();
        areComboboxInputsValid();
    }

    @FXML
    private void onDelete() {
        accountService.deleteById(account.getId());
        portfolioManagementTabController.navigateBackAfterDeletion(account);
        onCancel();
    }

    @Override
    protected void onCancel() {
        inputDescription.getParent().getScene().getWindow().hide();
    }

    @Override
    protected void onSave() {
        // Validate first
        boolean areTextFieldsValid = areTextFieldsValid();
        boolean areComboboxInputsValid = areComboboxInputsValid();
        if (!areTextFieldsValid || !areComboboxInputsValid) return;

        // If everything is valid, we can create and save the new owner
        accountService.writeInput( account, false,
                inputDescription, inputType, inputCurrencyCode, inputBalance, inputOwner, inputPortfolio,
                inputNotice, inputBankName, inputIban, inputKontoNumber, inputInterestRate, inputInterestDays, inputInterestInterval
        );
        account.setState(inputState.getSelectionModel().getSelectedItem());
        account.setCreatedAt(java.sql.Date.valueOf(LocalDate.parse(
                inputCreatedAt.getEditor().getText(),
                DateTimeFormatter.ofPattern("dd.MM.yyyy")
        )));
        if (State.DEACTIVATED.equals(account.getState())) {
            account.setDeactivatedAt(java.sql.Date.valueOf(LocalDate.parse(
                    inputDeactivatedAt.getEditor().getText(),
                    DateTimeFormatter.ofPattern("dd.MM.yyyy")
            )));
        }

        if (!accountService.reSave(account)) return;
        onCancel();

        // Finally, show success-dialog
        PrimaryTabManager.showInfoDialog(
                "Konto aktualisiert",
                "Das Konto wurde erfolgreich aktualisiert.",
                inputState
        );
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }

    private boolean areTextFieldsValid() {
        inputCreatedAt.getEditor().tooltipProperty().unbind();
        inputDeactivatedAt.getEditor().tooltipProperty().unbind();

        List<TextInputControl> inputs = new ArrayList<>(List.of(
                inputBalance, inputCreatedAt.getEditor(), inputCreatedAt.getEditor(), inputBankName, inputIban,
                inputKontoNumber, inputInterestRate, inputInterestDays
        ));
        if (State.DEACTIVATED.equals(inputState.getSelectionModel().getSelectedItem())) {
            inputs.add(inputDeactivatedAt.getEditor());
        }
        return !FieldValidator.isInputEmpty(inputs.toArray(new TextInputControl[0]));
    }

    private boolean areComboboxInputsValid() {
        return ComboBoxValidator.areComboboxInputsValid(List.of(
                inputType, inputCurrencyCode, inputOwner, inputPortfolio, inputState, inputInterestInterval
        ));
    }
}
