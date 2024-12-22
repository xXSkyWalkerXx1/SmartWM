package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners.owner;

import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabController;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.MaritalState;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.State;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.interfaces.Openable;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners.OwnerService;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.FieldFormatter;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.FieldValidator;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.FormatUtils;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.PortfolioTreeView;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.text.ParseException;
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
    ComboBox<State> inputState;
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
        inputState.getItems().setAll(State.values());
        inputMaritalState.getItems().setAll(MaritalState.values());
    }

    @Override
    public void open() {
        owner = (Owner) portfolioManagementManager
                .getPortfolioController()
                .getInhaberÜbersichtTab()
                .getProperties()
                .get(PortfolioManagementTabController.TAB_PROPERTY_ENTITY);

        loadOwnerData();
        ownerTreeViewPane.getChildren().setAll(new PortfolioTreeView(
                ownerTreeViewPane,
                owner.getPortfolios().stream().toList(),
                portfolioManagementManager,
                false
        ));

        // Change TextFields so that they only accept integers
        FieldFormatter.setInputFloatRange(inputTaxRate, 0f, 100f, change -> {
            try {
                return ownerService.testTaxRatesOrShowError(
                        FormatUtils.parseFloat(change.getControlNewText()),
                        FormatUtils.parseFloat(inputChurchTaxRate.getText())
                                + FormatUtils.parseFloat(inputCapitalGainsTaxRate.getText())
                                + FormatUtils.parseFloat(inputSolidaritySurchargeTaxRate.getText()),
                        inputMaritalState
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
                                + FormatUtils.parseFloat(inputSolidaritySurchargeTaxRate.getText()),
                        inputMaritalState
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
                                + FormatUtils.parseFloat(inputSolidaritySurchargeTaxRate.getText()),
                        inputMaritalState
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
                                + FormatUtils.parseFloat(inputCapitalGainsTaxRate.getText()),
                        inputMaritalState
                );
            } catch (ParseException e) {
                return false;
            }
        });
    }

    @FXML
    private void onReset() {
        owner = ownerService.getOwnerById(owner.getId());
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
        ownerService.writeInput(
                owner, false,
                inputForename, inputAftername, inputNotice,
                inputCountry, inputPlz, inputLocation, inputStreet, inputStreetNumber,
                inputTaxNumber, inputMaritalState, inputTaxRate,
                inputChurchTaxRate, inputCapitalGainsTaxRate, inputSolidaritySurchargeTaxRate
        );
        owner.setState(inputState.getValue());

        if (!ownerService.save(owner)) return;
        loadOwnerData();

        // Finally, show success-dialog
        PrimaryTabManager.showInfoDialog(
                "Inhaber aktualisiert",
                "Der aktuelle Inhaber wurde erfolgreich aktualisiert.",
                inputForename
        );
    }

    private void loadOwnerData() {
        inputForename.setText(owner.getForename());
        inputAftername.setText(owner.getAftername());
        inputNotice.setText(owner.getNotice());
        inputState.getSelectionModel().select(owner.getState());
        outputCreatedAt.setText(owner.getCreatedAt().toLocaleString());
        outputDeactivatedAt.setText(owner.getDeactivatedAt() == null ? "" : owner.getDeactivatedAt().toLocaleString());
        inputCountry.setText(owner.getAddress().getCountry());
        inputPlz.setText(owner.getAddress().getPlz());
        inputLocation.setText(owner.getAddress().getLocation());
        inputStreet.setText(owner.getAddress().getStreet());
        inputStreetNumber.setText(owner.getAddress().getStreetNumber());
        inputTaxNumber.setText(owner.getTaxInformation().getTaxNumber());
        inputMaritalState.getSelectionModel().select(owner.getTaxInformation().getMaritalState());
        inputTaxRate.setText(FormatUtils.formatFloat((float) owner.getTaxInformation().getTaxRate()));
        inputChurchTaxRate.setText(FormatUtils.formatFloat((float) owner.getTaxInformation().getChurchTaxRate()));
        inputCapitalGainsTaxRate.setText(FormatUtils.formatFloat((float) owner.getTaxInformation().getCapitalGainsTaxRate()));
        inputSolidaritySurchargeTaxRate.setText(FormatUtils.formatFloat((float) owner.getTaxInformation().getSolidaritySurchargeTaxRate()));
    }
}
