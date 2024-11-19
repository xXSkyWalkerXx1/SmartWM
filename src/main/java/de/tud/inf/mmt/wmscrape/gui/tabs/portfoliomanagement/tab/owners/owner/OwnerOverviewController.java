package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners.owner;

import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabController;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.MaritalState;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.interfaces.Openable;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners.OwnerService;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.FieldFormatter;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.FieldValidator;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.PortfolioTreeView;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.Calendar;

@Controller
public class OwnerOverviewController implements Openable {

    Owner owner;

    @Autowired
    OwnerService ownerService;
    @Autowired
    PortfolioManagementTabManager portfolioManagementManager;

    @FXML
    TextField inputForename;
    @FXML
    TextField inputAftername;
    @FXML
    TextArea inputNotice;
    @FXML
    TextField outputCreatedAt;
    @FXML
    TextField outputDeactivatedAt;
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
    AnchorPane ownerTreeViewPane;

    @FXML
    private void initialize() {
        inputMaritalState.getItems().addAll(MaritalState.values());

        // Change TextFields so that they only accept integers
        FieldFormatter.setInputFloatRange(inputTaxRate, 0, 100);
        FieldFormatter.setInputFloatRange(inputChurchTaxRate, 0, 100);
        FieldFormatter.setInputFloatRange(inputCapitalGainsTaxRate, 0, 100);
        FieldFormatter.setInputFloatRange(inputSolidaritySurchargeTaxRate, 0, 100);
    }

    @Override
    public void open() {
        owner = (Owner) portfolioManagementManager
                .getPortfolioController()
                .getInhaberÜbersichtTab()
                .getProperties()
                .get(PortfolioManagementTabController.TAB_PROPERTY_ENTITY);

        loadOwnerData();
        ownerTreeViewPane.getChildren().add(new PortfolioTreeView(
                ownerTreeViewPane,
                owner.getPortfolios().stream().toList(),
                portfolioManagementManager,
                false
        ));
    }

    @FXML
    private void onReset() {
        loadOwnerData();
    }

    @FXML
    private void onSave() {
        // Validate first
        if (FieldValidator.isInputEmpty(
                inputForename, inputAftername, inputCountry, inputPlz, inputLocation, inputStreet, inputStreetNumber,
                inputTaxNumber, inputTaxRate, inputCapitalGainsTaxRate, inputSolidaritySurchargeTaxRate
        )) return;

        // If everything is valid, we can update and save the owner
        owner.setForename(inputForename.getText());
        owner.setAftername(inputAftername.getText());
        owner.setNotice(inputNotice.getText());

        Owner.Address ownerAddress = owner.getAddress();
        ownerAddress.setCountry(inputCountry.getText());
        ownerAddress.setPlz(inputPlz.getText());
        ownerAddress.setLocation(inputLocation.getText());
        ownerAddress.setStreet(inputStreet.getText());
        ownerAddress.setStreetNumber(inputStreetNumber.getText());

        Owner.TaxInformation ownerTaxInfo = owner.getTaxInformation();
        ownerTaxInfo.setTaxNumber(inputTaxNumber.getText());
        ownerTaxInfo.setMaritalState(inputMaritalState.getValue());
        ownerTaxInfo.setTaxRate(Double.parseDouble(inputTaxRate.getText()));
        ownerTaxInfo.setChurchTaxRate(Double.parseDouble(inputChurchTaxRate.getText() == "" ? "0" : inputChurchTaxRate.getText()));
        ownerTaxInfo.setCapitalGainsTaxRate(Double.parseDouble(inputCapitalGainsTaxRate.getText()));
        ownerTaxInfo.setSolidaritySurchargeTaxRate(Double.parseDouble(inputSolidaritySurchargeTaxRate.getText()));

        ownerService.saveOwner(owner);

        // Finally, show success-dialog
        Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Der aktuelle Inhaber wurde erfolgreich aktualisiert.", ButtonType.OK);
        successAlert.setTitle("Inhaber aktualisiert");
        PrimaryTabManager.setAlertPosition(successAlert, inputForename);
        successAlert.show();

    }

    private void loadOwnerData() {
        inputForename.setText(owner.getForename());
        inputAftername.setText(owner.getAftername());
        inputNotice.setText(owner.getNotice());
        outputCreatedAt.setText(owner.getCreatedAt().toLocaleString());
        outputDeactivatedAt.setText(owner.getDeactivatedAt() == null ? "" : owner.getDeactivatedAt().toString());
        inputCountry.setText(owner.getAddress().getCountry());
        inputPlz.setText(owner.getAddress().getPlz());
        inputLocation.setText(owner.getAddress().getLocation());
        inputStreet.setText(owner.getAddress().getStreet());
        inputStreetNumber.setText(owner.getAddress().getStreetNumber());
        inputTaxNumber.setText(owner.getTaxInformation().getTaxNumber());
        inputMaritalState.getSelectionModel().select(owner.getTaxInformation().getMaritalState());
        inputTaxRate.setText(String.valueOf(owner.getTaxInformation().getTaxRate()));
        inputChurchTaxRate.setText(String.valueOf(owner.getTaxInformation().getChurchTaxRate()));
        inputCapitalGainsTaxRate.setText(String.valueOf(owner.getTaxInformation().getCapitalGainsTaxRate()));
        inputSolidaritySurchargeTaxRate.setText(String.valueOf(owner.getTaxInformation().getSolidaritySurchargeTaxRate()));
    }
}
