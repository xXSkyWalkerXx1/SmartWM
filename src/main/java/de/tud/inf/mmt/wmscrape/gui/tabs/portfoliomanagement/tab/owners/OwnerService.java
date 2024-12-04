package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners;

import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.MaritalState;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.repository.OwnerRepository;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
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

    public void save(Owner owner) {
        ownerRepository.save(owner);
    }

    /**
     * @param control Some element inside the controller class used as a reference to get the stage/ scene.
     */
    public void delete(Owner owner, @NonNull Control control) {
        PrimaryTabManager.showDialogWithAction(
                Alert.AlertType.WARNING,
                "Inhaber löschen",
                "Sind Sie sicher, dass Sie den Inhaber löschen möchten?\n" +
                        "Etwaige Beziehungen zu Konten oder Depots werden dabei nicht berücksichtig und kann zu einem " +
                        "fehlerhaften Verhalten der Anwendung führen!;",
                control,
                o -> ownerRepository.delete(owner)
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
        ownerTaxInfo.setTaxRate(Double.parseDouble(inputTaxRate.getText()));
        ownerTaxInfo.setChurchTaxRate(Double.parseDouble(inputChurchTaxRate.getText().isBlank() ? "0" : inputChurchTaxRate.getText()));
        ownerTaxInfo.setCapitalGainsTaxRate(Double.parseDouble(inputCapitalGainsTaxRate.getText()));
        ownerTaxInfo.setSolidaritySurchargeTaxRate(Double.parseDouble(inputSolidaritySurchargeTaxRate.getText()));
    }
}
