package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners;

import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.MaritalState;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.FieldFormatter;
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
        inputMaritalState.getItems().addAll(MaritalState.values());
        inputMaritalState.getSelectionModel().selectFirst();

        // Change TextFields so that they only accept integers
        FieldFormatter.setInputFloatRange(inputTaxRate, 0, 100);
        FieldFormatter.setInputFloatRange(inputChurchTaxRate, 0, 100);
        FieldFormatter.setInputFloatRange(inputCapitalGainsTaxRate, 0, 100);
        FieldFormatter.setInputFloatRange(inputSolidaritySurchargeTaxRate, 0, 100);
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
        Owner newOwner = new Owner();
        newOwner.setForename(inputForename.getText());
        newOwner.setAftername(inputAftername.getText());
        newOwner.setNotice(inputNotice.getText());
        newOwner.setCreatedAt(Calendar.getInstance().getTime());

        Owner.Address ownerAddress = newOwner.getAddress();
        ownerAddress.setCountry(inputCountry.getText());
        ownerAddress.setPlz(inputPlz.getText());
        ownerAddress.setLocation(inputLocation.getText());
        ownerAddress.setStreet(inputStreet.getText());
        ownerAddress.setStreetNumber(inputStreetNumber.getText());

        Owner.TaxInformation ownerTaxInfo = newOwner.getTaxInformation();
        ownerTaxInfo.setTaxNumber(inputTaxNumber.getText());
        ownerTaxInfo.setMaritalState(inputMaritalState.getValue());
        ownerTaxInfo.setTaxRate(Double.parseDouble(inputTaxRate.getText()));
        ownerTaxInfo.setChurchTaxRate(Double.parseDouble(inputChurchTaxRate.getText() == "" ? "0" : inputChurchTaxRate.getText()));
        ownerTaxInfo.setCapitalGainsTaxRate(Double.parseDouble(inputCapitalGainsTaxRate.getText()));
        ownerTaxInfo.setSolidaritySurchargeTaxRate(Double.parseDouble(inputSolidaritySurchargeTaxRate.getText()));

        ownerService.saveOwner(newOwner);
        onCancel();

        // Finally, show success-dialog
        PrimaryTabManager.showInfoDialog(
                "Inhaber angelegt",
                "Der neue Inhaber wurde erfolgreich angelegt.",
                inputForename
        );
    }
}
