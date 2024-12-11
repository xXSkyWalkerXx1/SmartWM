package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners;

import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.MaritalState;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.interfaces.Openable;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.repository.OwnerRepository;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.FormatUtils;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;

@Service
public class OwnerService {

    @Autowired
    private OwnerRepository ownerRepository;

    public List<Owner> getAll(){
        return ownerRepository.findAll();
    }

    public Owner getOwnerById(long id){
        return ownerRepository.findById(id).orElseThrow();
    }

    public boolean save(Owner owner) {
        try {
            ownerRepository.save(owner);
            return true;
        } catch (DataIntegrityViolationException integrityViolationException) {
            PrimaryTabManager.showDialog(
                    Alert.AlertType.ERROR,
                    "Fehler",
                    "Der Inhaber konnte nicht gespeichert werden, da bereits ein Inhaber mit der selben Steuernummer bereits existiert.",
                    null
            );
        }
        return false;
    }

    /**
     * @param controller to refresh the view after deletion.
     */
    public void delete(Owner owner, @NonNull Openable controller) {
        PrimaryTabManager.showDialogWithAction(
                Alert.AlertType.WARNING,
                "Inhaber löschen",
                "Sind Sie sicher, dass Sie den Inhaber löschen möchten?\n" +
                        "Etwaige Beziehungen zu Konten oder Depots werden dabei nicht berücksichtigt und kann zu einem" +
                        " fehlerhaften Verhalten der Anwendung führen!",
                null,
                o -> {
                    ownerRepository.delete(owner);
                    controller.open();
                }
        );
    }

    public void writeInput(@NonNull Owner owner, boolean isOnCreate,
                           @NonNull TextField inputForename, @NonNull TextField inputAftername, @NonNull TextArea inputNotice,
                           @NonNull TextField inputCountry, @NonNull TextField inputPlz, @NonNull TextField inputLocation,
                           @NonNull TextField inputStreet, @NonNull TextField inputStreetNumber, @NonNull TextField inputTaxNumber,
                           @NonNull ComboBox<MaritalState> inputMaritalState, @NonNull TextField inputTaxRate,
                           @NonNull TextField inputChurchTaxRate, @NonNull TextField inputCapitalGainsTaxRate,
                           @NonNull TextField inputSolidaritySurchargeTaxRate) {
        owner.setForename(inputForename.getText());
        owner.setAftername(inputAftername.getText());
        owner.setNotice(inputNotice.getText());
        if (isOnCreate) owner.setCreatedAt(Calendar.getInstance().getTime());

        Owner.Address ownerAddress = owner.getAddress();
        ownerAddress.setCountry(inputCountry.getText());
        ownerAddress.setPlz(inputPlz.getText());
        ownerAddress.setLocation(inputLocation.getText());
        ownerAddress.setStreet(inputStreet.getText());
        ownerAddress.setStreetNumber(inputStreetNumber.getText());

        Owner.TaxInformation ownerTaxInfo = owner.getTaxInformation();
        ownerTaxInfo.setTaxNumber(inputTaxNumber.getText());
        ownerTaxInfo.setMaritalState(inputMaritalState.getValue());

        try {
            ownerTaxInfo.setTaxRate(FormatUtils.parseFloat(inputTaxRate.getText()));
            ownerTaxInfo.setChurchTaxRate(FormatUtils.parseFloat(inputChurchTaxRate.getText()));
            ownerTaxInfo.setCapitalGainsTaxRate(FormatUtils.parseFloat(inputCapitalGainsTaxRate.getText()));
            ownerTaxInfo.setSolidaritySurchargeTaxRate(FormatUtils.parseFloat(inputSolidaritySurchargeTaxRate.getText()));
        } catch (Exception e) {
            throw new RuntimeException("Error while parsing tax rates. This should not happen here!");
        }

    }

    /**
     * @param input Input only for one of these fields.
     * @param sum Sum of the other fields.
     * @param region some element inside the controller class used as a reference to get the stage/ scene.
     * @return true if the sum of the input and the sum is less than 100, false otherwise.
     */
    public boolean testTaxRatesOrShowError(float input, float sum, @NonNull Region region) {
        if (input + sum > 100) {
            PrimaryTabManager.showDialog(
                    Alert.AlertType.ERROR,
                    "Fehler",
                    String.format(
                            "Die Summe der Steuer-Sätze kann in Summe nur maximal 100 ergeben.\n" +
                                    "Maximal verbleibende Eingabe: %s | Eingegeben: %s",
                            100 - sum, input
                    ),
                    region
            );
            return false;
        }
        return true;
    }
}
