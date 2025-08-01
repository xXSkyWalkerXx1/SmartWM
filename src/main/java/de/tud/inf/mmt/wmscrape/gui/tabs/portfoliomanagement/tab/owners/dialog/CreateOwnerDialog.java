package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners.dialog;

import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.MaritalState;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners.OwnerService;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.FieldFormatter;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.FieldValidator;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.FormatUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.text.ParseException;
import java.util.Locale;

@Controller
public class CreateOwnerDialog {

    @Autowired
    OwnerService ownerService;
    @Autowired
    private PortfolioManagementTabManager portfolioManagementTabManager;

    @FXML
    TextField inputForename;
    @FXML
    TextField inputAftername;
    @FXML
    TextArea inputNotice;
    @FXML
    ComboBox<String> inputCountry;
    @FXML
    TextField inputPlz;
    @FXML
    TextField inputLocation;
    @FXML
    TextField inputStreet;
    @FXML
    TextField inputStreetNumber;
    @FXML
    TextField inputTaxNumber;
    @FXML
    ComboBox<MaritalState> inputMaritalState;
    @FXML
    TextField inputTaxRate;
    @FXML
    TextField inputChurchTaxRate;
    @FXML
    TextField inputCapitalGainsTaxRate;
    @FXML
    TextField inputSolidaritySurchargeTaxRate;

    @FXML
    protected void initialize() {
        inputCountry.getItems().setAll(OwnerService.getLocales());
        inputCountry.getSelectionModel().select(Locale.getDefault().getDisplayCountry());

        inputMaritalState.getItems().setAll(MaritalState.values());
        inputMaritalState.getSelectionModel().selectFirst();

        // Change TextFields so that they only accept integers
        FieldFormatter.setInputFloatRange(inputTaxRate, 0f, 100f, change -> {
            try {
                return ownerService.testTaxRatesOrShowError(
                        FormatUtils.parseFloat(change.getControlNewText()),
                        FormatUtils.parseFloat(inputChurchTaxRate.getText())
                                + FormatUtils.parseFloat(inputCapitalGainsTaxRate.getText())
                                + FormatUtils.parseFloat(inputSolidaritySurchargeTaxRate.getText())
                );
            } catch (ParseException e) {
                return false;
            }
        });
        FieldFormatter.setInputFloatRange(inputChurchTaxRate, 0f, 100f, change -> {
            try {
                return ownerService.testTaxRatesOrShowError(
                        FormatUtils.parseFloat(change.getControlNewText()),
                        FormatUtils.parseFloat(inputTaxRate.getText())
                                + FormatUtils.parseFloat(inputCapitalGainsTaxRate.getText())
                                + FormatUtils.parseFloat(inputSolidaritySurchargeTaxRate.getText())
                );
            } catch (ParseException e) {
                return false;
            }
        });
        FieldFormatter.setInputFloatRange(inputCapitalGainsTaxRate, 0f, 100f, change -> {
            try {
                return ownerService.testTaxRatesOrShowError(
                        FormatUtils.parseFloat(change.getControlNewText()),
                        FormatUtils.parseFloat(inputTaxRate.getText())
                                + FormatUtils.parseFloat(inputChurchTaxRate.getText())
                                + FormatUtils.parseFloat(inputSolidaritySurchargeTaxRate.getText())
                );
            } catch (ParseException e) {
                return false;
            }
        });
        FieldFormatter.setInputFloatRange(inputSolidaritySurchargeTaxRate, 0f, 100f, change -> {
            try {
                return ownerService.testTaxRatesOrShowError(
                        FormatUtils.parseFloat(change.getControlNewText()),
                        FormatUtils.parseFloat(inputTaxRate.getText())
                                + FormatUtils.parseFloat(inputChurchTaxRate.getText())
                                + FormatUtils.parseFloat(inputCapitalGainsTaxRate.getText())
                );
            } catch (ParseException e) {
                return false;
            }
        });
    }

    @FXML
    protected void onCancel() {
        portfolioManagementTabManager.getPortfolioController().getOwnerController().open();
        inputForename.getParent().getScene().getWindow().hide();
    }

    @FXML
    protected void onSave() {
        // Validate first
        if (FieldValidator.isInputEmpty(
                inputForename, inputAftername, inputPlz, inputLocation, inputStreet, inputStreetNumber,
                inputTaxNumber, inputTaxRate, inputCapitalGainsTaxRate, inputSolidaritySurchargeTaxRate
        )) return;

        // If everything is valid, we can create and save the new owner
        Owner owner = new Owner();
        ownerService.writeInput(
                owner, true,
                inputForename, inputAftername, inputNotice,
                inputCountry, inputPlz, inputLocation, inputStreet, inputStreetNumber, inputTaxNumber,
                inputMaritalState, inputTaxRate,
                inputChurchTaxRate, inputCapitalGainsTaxRate, inputSolidaritySurchargeTaxRate
        );
        if (!ownerService.save(owner)) return;
        onCancel();

        // Finally, show success-dialog
        PrimaryTabManager.showInfoDialog(
                "Inhaber angelegt",
                "Der neue Inhaber wurde erfolgreich angelegt.",
                inputForename
        );
    }
}
