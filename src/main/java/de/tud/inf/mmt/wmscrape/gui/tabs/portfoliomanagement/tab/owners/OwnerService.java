package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners;

import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabController;
import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabController;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.BreadcrumbElementType;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.MaritalState;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.interfaces.Openable;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.repository.OwnerRepository;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.FormatUtils;
import javafx.scene.control.*;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import java.sql.Timestamp;
import java.util.*;

@Service
public class OwnerService {

    @Autowired
    private OwnerRepository ownerRepository;
    @Autowired
    private PortfolioManagementTabController portfolioManagementTabController;

    @PersistenceContext
    private EntityManager entityManager;

    public List<Owner> getAll(){
        try {
            portfolioManagementTabController.checkForInconsistencies();
            return ownerRepository.findAll();
        } catch (Exception e) {
            PrimaryTabManager.showDialog(
                    Alert.AlertType.ERROR,
                    "Fehler",
                    "Die Inhaber konnten nicht geladen werden.\nGrund: " + e.getMessage(),
                    null
            );
            return new ArrayList<>();
        }
    }

    /**
     * Searches for inconsistencies in the database and tries to fix them. After that it returns the owner with the id, if it exists.
     * @throws NoSuchElementException if the owner with the given id does not exist.
     */
    public Owner getOwnerById(long id) throws NoSuchElementException {
        HashMap<Long, Long> idMapping = portfolioManagementTabController
                .checkForInconsistencies()
                .getOrDefault(BreadcrumbElementType.OWNER, new HashMap<>());
        if (idMapping.containsKey(id)) id = idMapping.get(id);
        return ownerRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    public boolean save(Owner owner) {
        try {
            // test before saving
            Optional<Long> id = ownerRepository.getOwnerBy(owner.getTaxInformation().getTaxNumber());
            if (id.isPresent() && !id.get().equals(owner.getId()))
                throw new DataIntegrityViolationException("");

            owner.onPrePersistOrUpdateOrRemoveEntity();
            Owner persistedOwner = ownerRepository.save(owner);
            // Update the owner with the id from the database.
            persistedOwner.onPostLoadEntity();
            owner.setId(persistedOwner.getId());
            return true;
        } catch (DataIntegrityViolationException integrityViolationException) {
            PrimaryTabManager.showDialog(
                    Alert.AlertType.ERROR,
                    "Fehler",
                    "Der Inhaber konnte nicht gespeichert werden, da bereits ein Inhaber mit der selben Steuernummer bereits existiert.",
                    null
            );
        } catch (Exception e) {
            PrimaryTabManager.showDialog(
                    Alert.AlertType.ERROR,
                    "Unerwarteter Fehler",
                    "Der Inhaber konnte nicht gespeichert werden.\nGrund: " + e.getMessage(),
                    null
            );
        }
        return false;
    }

    /**
     * Shows a dialog to confirm the deletion of the owner and if confirmed, deletes it.
     * @param controller to refresh the view after deletion. If null, no view will be refreshed.
     */
    public void delete(Owner owner, @Nullable Openable controller) {
        PrimaryTabManager.showDialogWithAction(
                Alert.AlertType.WARNING,
                "Inhaber löschen",
                "Sind Sie sicher, dass Sie den Inhaber löschen möchten?\n" +
                        "Etwaige Beziehungen werden dabei nicht berücksichtigt und kann zu einem" +
                        " fehlerhaften Verhalten der Anwendung führen!",
                null,
                () -> {
                    ownerRepository.delete(owner);
                    if (controller != null) controller.open();
                }
        );
    }

    /**
     * Deletes the owner by id.
     * @param id id of the owner to delete.
     */
    public void deleteById(long id) {
        ownerRepository.deleteById(id);
    }

    /**
     * @return all available locales as string array.
     */
    @NonNull
    public static Collection<String> getLocales() {
        var locales = Locale.getISOCountries();
        for (int i = 0; i < locales.length; i++) {
            locales[i] = new Locale("", locales[i]).getDisplayCountry();
        }
        return List.of(Arrays.stream(locales).sorted().toArray(String[]::new));
    }

    public void writeInput(@NonNull Owner owner, boolean isOnCreate,
                           @NonNull TextField inputForename, @NonNull TextField inputAftername, @NonNull TextArea inputNotice,
                           @NonNull ComboBox<String> inputCountry, @NonNull TextField inputPlz, @NonNull TextField inputLocation,
                           @NonNull TextField inputStreet, @NonNull TextField inputStreetNumber, @NonNull TextField inputTaxNumber,
                           @NonNull ComboBox<MaritalState> inputMaritalState, @NonNull TextField inputTaxRate,
                           @NonNull TextField inputChurchTaxRate, @NonNull TextField inputCapitalGainsTaxRate,
                           @NonNull TextField inputSolidaritySurchargeTaxRate) {
        owner.setForename(inputForename.getText());
        owner.setAftername(inputAftername.getText());
        owner.setNotice(inputNotice.getText());
        if (isOnCreate) owner.setCreatedAt(Calendar.getInstance().getTime());

        Owner.Address ownerAddress = owner.getAddress();
        ownerAddress.setCountry(inputCountry.getSelectionModel().getSelectedItem());
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
     * @return true if the sum of the input and the sum is less than 100, false otherwise.
     */
    public boolean testTaxRatesOrShowError(float input, float sum) {
        if (input + sum > 100) {
            PrimaryTabManager.showDialog(
                    Alert.AlertType.ERROR,
                    "Fehler",
                    String.format(
                            "Die Summe der Steuer-Sätze kann in Summe nur maximal 100 ergeben.\n" +
                                    "Maximal verbleibende Eingabe: %s | Eingegeben: %s",
                            100 - sum, input
                    ),
                    null
            );
            return false;
        }
        return true;
    }

    public OwnerRepository getOwnerRepository() {
        return ownerRepository;
    }

    /**
     * Updates the owner via native-sql-queries instead of via hibernate.
     * @return true if the owner was successfully updated, false otherwise.
     */
    @Transactional
    @Modifying
    public boolean updateOwnerNatively(@NonNull Owner owner) {
        owner.onPrePersistOrUpdateOrRemoveEntity();
        String sqlQuery;
        List<Long> ids;
        Query query;

        // region Adress
        ids = ownerRepository.findAllByAddressIsInvalid();

        if (ids.contains(owner.getId())) {
            sqlQuery = "INSERT INTO inhaber_adresse (country, location, plz, street, street_number) " +
                    "VALUES (:country, :location, :plz, :street, :street_number)";
            query = entityManager.createNativeQuery(sqlQuery);
        } else {
            sqlQuery = "UPDATE inhaber_adresse " +
                    "SET country = :country, location = :location, plz = :plz, street = :street, street_number = :street_number " +
                    "WHERE id = :id";
            query = entityManager.createNativeQuery(sqlQuery);
            query.setParameter("id", owner.getAddress().getId());
        }

        query.setParameter("country", owner.getAddress().getCountry());
        query.setParameter("location", owner.getAddress().getLocation());
        query.setParameter("plz", owner.getAddress().getPlz());
        query.setParameter("street", owner.getAddress().getStreet());
        query.setParameter("street_number", owner.getAddress().getStreetNumber());

        try {
            int insertedRows = query.executeUpdate();

            if (insertedRows > 0) {
                // update id
                if (ids.contains(owner.getId())) {
                    Query idQuery = entityManager.createNativeQuery("SELECT LAST_INSERT_ID()");
                    Long newId = ((Number) idQuery.getSingleResult()).longValue();
                    owner.getAddress().setId(newId);
                }
            } else {
                throw new Exception("Die Adresse konnte nicht persistiert werden.");
            }
        } catch (Exception e) {
            PrimaryTabController.unknownErrorMsg.accept(e);
            return false;
        }
        // endregion

        // region Tax Information
        ids = ownerRepository.findAllByTaxInformationIsInvalid();

        if (ids.contains(owner.getId())) {
            sqlQuery = "INSERT INTO inhaber_steuer_informationen (capital_gainstax_rate, church_tax_rate, marital_state, " +
                    "solidarity_surcharge_tax_rate, tax_number, tax_rate) " +
                    "VALUES (:capital_gains_tax_rate, :church_tax_rate, :marital_state, :solidarity_surcharge_tax_rate, " +
                    ":tax_number, :tax_rate)";
            query = entityManager.createNativeQuery(sqlQuery);
        } else {
            sqlQuery = "UPDATE inhaber_steuer_informationen " +
                    "SET capital_gainstax_rate = :capital_gains_tax_rate, church_tax_rate = :church_tax_rate, " +
                    "marital_state = :marital_state, solidarity_surcharge_tax_rate = :solidarity_surcharge_tax_rate, " +
                    "tax_number = :tax_number, tax_rate = :tax_rate " +
                    "WHERE id = :id";
            query = entityManager.createNativeQuery(sqlQuery);
            query.setParameter("id", owner.getTaxInformation().getId());
        }

        query.setParameter("capital_gains_tax_rate", owner.getTaxInformation().getCapitalGainsTaxRateBigDecimal());
        query.setParameter("church_tax_rate", owner.getTaxInformation().getChurchTaxRateBigDecimal());
        query.setParameter("marital_state", owner.getTaxInformation().getMaritalState().name());
        query.setParameter("solidarity_surcharge_tax_rate", owner.getTaxInformation().getSolidaritySurchargeTaxRateBigDecimal());
        query.setParameter("tax_number", owner.getTaxInformation().getTaxNumber());
        query.setParameter("tax_rate", owner.getTaxInformation().getTaxRateBigDecimal());

        try {
            int insertedRows = query.executeUpdate();

            if (insertedRows > 0) {
                // update id
                if (ids.contains(owner.getId())) {
                    Query idQuery = entityManager.createNativeQuery("SELECT LAST_INSERT_ID()");
                    Long newId = ((Number) idQuery.getSingleResult()).longValue();
                    owner.getTaxInformation().setId(newId);
                }
            } else throw new Exception("Die Steuer-Informationen konnten nicht persistiert werden.");
        } catch (PersistenceException e) {
            if (e.getCause() instanceof ConstraintViolationException) {
                PrimaryTabManager.showDialog(
                        Alert.AlertType.ERROR,
                        "Fehler",
                        "Der Inhaber konnte nicht gespeichert werden, da bereits ein Inhaber mit der selben Steuernummer existiert.",
                        null
                );
            } else PrimaryTabController.unknownErrorMsg.accept(e);
            return false;
        } catch (Exception e) {
            PrimaryTabController.unknownErrorMsg.accept(e);
            return false;
        }
        // endregion

        // region update the owner
        sqlQuery = "UPDATE inhaber " +
                "SET aftername = :aftername, created_at = :created_at, deactivated_at = :deactivated_at, forename = :forename, " +
                "notice = :notice, state = :state, address_id = :address_id, tax_information_id = :tax_information_id " +
                "WHERE id = :id";
        query = entityManager.createNativeQuery(sqlQuery);

        query.setParameter("aftername", owner.getAftername());
        query.setParameter("created_at", new Timestamp(owner.getCreatedAt().getTime()));
        query.setParameter("deactivated_at", owner.getDeactivatedAt() == null ? null : new Timestamp(owner.getDeactivatedAt().getTime()));
        query.setParameter("forename", owner.getForename());
        query.setParameter("notice", owner.getNotice());
        query.setParameter("state", owner.getState().name());
        query.setParameter("address_id", owner.getAddress().getId());
        query.setParameter("tax_information_id", owner.getTaxInformation().getId());
        query.setParameter("id", owner.getId());

        try {
            int updatedRows = query.executeUpdate();
            return updatedRows > 0;
        } catch (Exception e) {
            PrimaryTabController.unknownErrorMsg.accept(e);
        }
        // endregion

        return false;
    }
}
