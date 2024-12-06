package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners.dialog;

import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.MaritalState;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners.OwnerService;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.FieldFormatter;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.FieldValidator;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

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
    TextField inputCountry;
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
    private void initialize() {
        // Set values for MaritalState and select the first one as default one
        inputMaritalState.getItems().setAll(MaritalState.values());
        inputMaritalState.getSelectionModel().selectFirst();

        // Change TextFields so that they only accept integers
        FieldFormatter.setInputFloatRange(inputTaxRate, 0, 100, change -> ownerService.testTaxRatesOrShowError(
                Float.parseFloat(change.getControlNewText()),
                Float.parseFloat(inputChurchTaxRate.getText())
                        + Float.parseFloat(inputCapitalGainsTaxRate.getText())
                        + Float.parseFloat(inputSolidaritySurchargeTaxRate.getText()),
                inputMaritalState
        ));
        FieldFormatter.setInputFloatRange(inputChurchTaxRate, 0, 100, change -> ownerService.testTaxRatesOrShowError(
                Float.parseFloat(change.getControlNewText()),
                Float.parseFloat(inputTaxRate.getText())
                        + Float.parseFloat(inputCapitalGainsTaxRate.getText())
                        + Float.parseFloat(inputSolidaritySurchargeTaxRate.getText()),
                inputMaritalState
        ));
        FieldFormatter.setInputFloatRange(inputCapitalGainsTaxRate, 0, 100, change -> ownerService.testTaxRatesOrShowError(
                Float.parseFloat(change.getControlNewText()),
                Float.parseFloat(inputTaxRate.getText())
                        + Float.parseFloat(inputChurchTaxRate.getText())
                        + Float.parseFloat(inputSolidaritySurchargeTaxRate.getText()),
                inputMaritalState
        ));
        FieldFormatter.setInputFloatRange(inputSolidaritySurchargeTaxRate, 0, 100, change -> ownerService.testTaxRatesOrShowError(
                Float.parseFloat(change.getControlNewText()),
                Float.parseFloat(inputTaxRate.getText())
                        + Float.parseFloat(inputChurchTaxRate.getText())
                        + Float.parseFloat(inputCapitalGainsTaxRate.getText()),
                inputMaritalState
        ));
    }

    @FXML
    private void onCancel() {
        portfolioManagementTabManager.getPortfolioController().getOwnerController().open();
        inputForename.getParent().getScene().getWindow().hide();
    }

    @FXML
    private void onSave() {
        // Validate first
        if (FieldValidator.isInputEmpty(
                inputForename, inputAftername, inputCountry, inputPlz, inputLocation, inputStreet, inputStreetNumber,
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
