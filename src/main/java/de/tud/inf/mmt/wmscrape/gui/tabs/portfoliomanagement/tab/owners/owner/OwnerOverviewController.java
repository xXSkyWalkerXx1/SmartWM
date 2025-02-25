package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners.owner;

import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabController;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.MaritalState;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.State;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.interfaces.Openable;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners.OwnerService;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.*;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.text.ParseException;
import java.util.Locale;
import java.util.NoSuchElementException;

@Controller
public class OwnerOverviewController extends EditableView implements Openable {

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
    AnchorPane ownerTreeViewPane;

    @FXML
    private void initialize() {
        inputCountry.getItems().setAll(OwnerService.getLocales());
        inputCountry.getSelectionModel().select(Locale.getDefault().getDisplayCountry());

        inputState.getItems().setAll(State.values());
        inputMaritalState.getItems().setAll(MaritalState.values());

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

        // Set listeners

        /* notice for future reference: use the focusedProperty instead of the textProperty and just compare the values in your setters directly!
        inputForename.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                System.out.println("Focus changed: " + t1);
            }
        });
         */
        inputForename.textProperty().addListener((observableValue, s, t1) -> owner.setForename(t1));
        inputAftername.textProperty().addListener((observableValue, s, t1) -> owner.setAftername(t1));
        inputNotice.textProperty().addListener((observableValue, s, t1) -> owner.setNotice(t1));
        inputState.valueProperty().addListener((observableValue, state, t1) -> owner.setState(t1));
        inputCountry.getSelectionModel().selectedItemProperty().addListener((observableValue, s, t1) -> owner.getAddress().setCountry(t1));
        inputPlz.textProperty().addListener((observableValue, s, t1) -> owner.getAddress().setPlz(t1));
        inputLocation.textProperty().addListener((observableValue, s, t1) -> owner.getAddress().setLocation(t1));
        inputStreet.textProperty().addListener((observableValue, s, t1) -> owner.getAddress().setStreet(t1));
        inputStreetNumber.textProperty().addListener((observableValue, s, t1) -> owner.getAddress().setStreetNumber(t1));
        inputTaxNumber.textProperty().addListener((observableValue, s, t1) -> owner.getTaxInformation().setTaxNumber(t1));
        inputMaritalState.valueProperty().addListener((observableValue, maritalState, t1) -> owner.getTaxInformation().setMaritalState(t1));
        inputTaxRate.textProperty().addListener((observableValue, s, t1) -> {
            try {
                owner.getTaxInformation().setTaxRate(FormatUtils.parseFloat(t1));
            } catch (ParseException ignore) {} // this should not happen here due the text-formatters
        });
        inputChurchTaxRate.textProperty().addListener((observableValue, s, t1) -> {
            try {
                owner.getTaxInformation().setChurchTaxRate(FormatUtils.parseFloat(t1));
            } catch (ParseException ignore) {}
        });
        inputCapitalGainsTaxRate.textProperty().addListener((observableValue, s, t1) -> {
            try {
                owner.getTaxInformation().setCapitalGainsTaxRate(FormatUtils.parseFloat(t1));
            } catch (ParseException ignore) {}
        });
        inputSolidaritySurchargeTaxRate.textProperty().addListener((observableValue, s, t1) -> {
            try {
                owner.getTaxInformation().setSolidaritySurchargeTaxRate(FormatUtils.parseFloat(t1));
            } catch (ParseException ignore) {}
        });
    }

    @Override
    public void open() {
        owner = (Owner) portfolioManagementManager
                .getPortfolioController()
                .getInhaberÜbersichtTab()
                .getProperties()
                .get(PortfolioManagementTabController.TAB_PROPERTY_ENTITY);
        if (!owner.isChanged()) {
            try {
                owner = ownerService.getOwnerById(owner.getId());
            } catch (NoSuchElementException e) {
                /*
                Should only happen, in the following example:
                1. you open an owner
                2. you navigate to an account of him
                3. after that you create an inconsistency in the database (f.e. via SQL-Browser like HeidiSQL)
                4. from the account you navigate to the owner of this account (which is the same as in 1.)
                5. it detects the inconsistency, but you choose to delete the owner instead of fixing the inconsistency
                6. now the owner is deleted and the owner is not found in the database
                This happens only because of the complexity of the navigation.
                 */
                return;
            }
        }
        loadOwnerData();

        var treeView = new PortfolioTreeView(
                ownerTreeViewPane,
                owner.getPortfolios().stream().toList(),
                portfolioManagementManager
        );
        treeView.setTooltip(new Tooltip("Auflistung der dem Inhaber zugeordneten Portfolios, Konten und Depots."));
        ownerTreeViewPane.getChildren().setAll(treeView);

        // Initialize onUnsavedChangesAction-dialog
        super.initialize(
                owner,
                portfolioManagementManager,
                this::onSave,
                this::onReset
        );
    }

    @FXML
    private void onReset() {
        owner.restore();
        loadOwnerData();
    }

    @FXML
    private void onRemove() {
        ownerService.delete(owner, null);
        portfolioManagementManager.getPortfolioController().navigateBackAfterDeletion(owner);
    }

    @FXML
    private void onSave() {
        // Validate first
        if (FieldValidator.isInputEmpty(
                inputForename, inputAftername, inputPlz, inputLocation, inputStreet, inputStreetNumber,
                inputTaxNumber, inputTaxRate, inputCapitalGainsTaxRate, inputSolidaritySurchargeTaxRate
        )) return;

        // If everything is valid, we can save the owner
        if (!ownerService.save(owner)) return;
        // Check for inconsistencies
        owner = ownerService.getOwnerById(owner.getId());
        loadOwnerData();

        // Finally, show success-dialog
        PrimaryTabManager.showInfoDialog(
                "Inhaber aktualisiert",
                "Der aktuelle Inhaber wurde erfolgreich aktualisiert.",
                inputForename
        );
    }

    private void loadOwnerData() {
        portfolioManagementManager.getPortfolioController().refreshCrumbs();

        inputForename.setText(owner.getForename());
        inputAftername.setText(owner.getAftername());
        inputNotice.setText(owner.getNotice());
        inputState.getSelectionModel().select(owner.getState());
        outputCreatedAt.setText(owner.getCreatedAt().toLocaleString());
        outputDeactivatedAt.setText(owner.getDeactivatedAt() == null ? "" : owner.getDeactivatedAt().toLocaleString());
        inputCountry.getSelectionModel().select(owner.getAddress().getCountry());
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
