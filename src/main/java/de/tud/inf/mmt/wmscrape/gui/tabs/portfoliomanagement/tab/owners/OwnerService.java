package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.MaritalState;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.repository.OwnerRepository;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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
        ownerTaxInfo.setChurchTaxRate(Double.parseDouble(inputChurchTaxRate.getText() == "" ? "0" : inputChurchTaxRate.getText()));
        ownerTaxInfo.setCapitalGainsTaxRate(Double.parseDouble(inputCapitalGainsTaxRate.getText()));
        ownerTaxInfo.setSolidaritySurchargeTaxRate(Double.parseDouble(inputSolidaritySurchargeTaxRate.getText()));
    }
}
