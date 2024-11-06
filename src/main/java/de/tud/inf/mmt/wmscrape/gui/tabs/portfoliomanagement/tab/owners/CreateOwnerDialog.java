package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners;

import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.MaritalState;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.FieldValidator;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.Calendar;

@Controller
public class CreateOwnerDialog {

    @Autowired
    OwnerService ownerService;

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
        inputMaritalState.getItems().addAll(MaritalState.values());
        inputMaritalState.getSelectionModel().selectFirst();

        // Change TextFields so that they only accept integers
        PrimaryTabManager.setInputOnlyDecimalNumbers(inputTaxRate);
        PrimaryTabManager.setInputOnlyDecimalNumbers(inputChurchTaxRate);
        PrimaryTabManager.setInputOnlyDecimalNumbers(inputCapitalGainsTaxRate);
        PrimaryTabManager.setInputOnlyDecimalNumbers(inputSolidaritySurchargeTaxRate);
    }

    @FXML
    private void onCancel() {
        inputForename.getParent().getScene().getWindow().hide();
    }

    @FXML
    private void onSave() {
        // Validate first
        if (FieldValidator.isInputEmpty(
                inputForename, inputAftername, inputCountry, inputPlz, inputLocation, inputStreet, inputStreetNumber,
                inputTaxNumber, inputTaxRate, inputCapitalGainsTaxRate, inputSolidaritySurchargeTaxRate
        )) return;

        if (!FieldValidator.isInRange(
                0,
                100,
                inputTaxRate, inputCapitalGainsTaxRate, inputSolidaritySurchargeTaxRate
        )) return;

        // If everything is valid, we can create and save the new owner
        Owner newOwner = new Owner();
        newOwner.setForename(inputForename.getText());
        newOwner.setAftername(inputAftername.getText());
        newOwner.setNotice(inputNotice.getText());
        newOwner.setCreatedAt(Calendar.getInstance().getTime());

        Owner.Address ownerAddress = new Owner.Address();
        ownerAddress.setCountry(inputCountry.getText());
        ownerAddress.setPlz(inputPlz.getText());
        ownerAddress.setLocation(inputLocation.getText());
        ownerAddress.setStreet(inputStreet.getText());
        ownerAddress.setStreetNumber(inputStreetNumber.getText());

        Owner.TaxInformation ownerTaxInfo = new Owner.TaxInformation();
        ownerTaxInfo.setTaxNumber(inputTaxNumber.getText());
        ownerTaxInfo.setMaritalState(inputMaritalState.getValue());
        ownerTaxInfo.setTaxRate(Double.parseDouble(inputTaxRate.getText()));
        ownerTaxInfo.setChurchTaxRate(Double.parseDouble(inputChurchTaxRate.getText() == "" ? "0" : inputChurchTaxRate.getText()));
        ownerTaxInfo.setCapitalGainsTaxRate(Double.parseDouble(inputCapitalGainsTaxRate.getText()));
        ownerTaxInfo.setSolidaritySurchargeTaxRate(Double.parseDouble(inputSolidaritySurchargeTaxRate.getText()));

        newOwner.setAddress(ownerAddress);
        newOwner.setTaxInformation(ownerTaxInfo);
        ownerService.saveOwner(newOwner);
        onCancel();

        // Finally, show success-dialog
        Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Der neue Inhaber wurde erfolgreich angelegt.", ButtonType.OK);
        successAlert.setTitle("Inhaber angelegt");
        PrimaryTabManager.setAlertPosition(successAlert, inputForename);
        successAlert.show();
    }
}
