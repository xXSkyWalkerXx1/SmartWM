package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.repository;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.MaritalState;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {

    @Query(value = "SELECT o.id FROM inhaber o", nativeQuery = true)
    List<Long> getAllIds();

    /**
     * @return all owners as fake owners. A fake owner is an owner with only the id, forename and aftername set (if available).
     */
    default List<Owner> findAllAsFake() {
        List<Owner> fakeOwners = new ArrayList<>();

        for (Long ownerId: getAllIds()) {
            Owner fakeOwner = new Owner();
            fakeOwner.setId(ownerId);
            fakeOwner.setForename(findForenameById(ownerId).orElse(null));
            fakeOwner.setAftername(findAfternameById(ownerId).orElse(null));
            fakeOwner.getTaxInformation().setTaxNumber(findTaxNumberByTaxInformationId(
                    findTaxInformationIdByOwnerId(ownerId).orElse(null)).orElse(null)
            );
            fakeOwners.add(fakeOwner);
        }
        return fakeOwners;
    }

    // region Queries to check for inconsistencies
    /**
     * @return all owners where the address or the tax information is null or invalid.
     */
    @Query(value = "SELECT o.id " +
            "FROM inhaber o " +
            "LEFT JOIN inhaber_adresse a ON o.address_id = a.id " +
            "LEFT JOIN inhaber_steuer_informationen t ON o.tax_information_id = t.id " +
            "WHERE a.id IS NULL OR t.id IS NULL OR o.address_id IS NULL OR o.tax_information_id IS NULL", nativeQuery = true)
    List<Long> findAllByAddressOrTaxInformationIsInvalid();

    /**
     *
     * @param states pass always {@code State.getValuesAsString()}.
     * @return all owners where the state is not in the given states.
     */
    @Query(value = "SELECT o.id " +
            "FROM inhaber o " +
            "WHERE o.state IS NULL OR o.state NOT IN :states", nativeQuery = true)
    List<Long> findAllByStateIsNotIn(@Param("states") Collection<String> states);

    /**
     * @return all owners where the forename, aftername or the created_at date is null.
     */
    @Query(value = "SELECT o.id " +
            "FROM inhaber o " +
            "WHERE o.forename IS NULL OR TRIM(o.forename) = '' " +
            "OR o.aftername IS NULL OR TRIM(o.aftername) = '' " +
            "OR o.created_at IS NULL", nativeQuery = true)
    List<Long> findAllByForenameIsNullOrAfternameIsNullOrCreatedAtIsNull();

    /**
     * @return all owners where the address contains null values.
     */
    @Query(value = "SELECT o.id " +
            "FROM inhaber o " +
            "JOIN inhaber_adresse a ON o.address_id = a.id " +
            "WHERE a.country IS NULL OR TRIM(a.country) = '' " +
            "OR a.plz IS NULL OR TRIM(a.plz) = '' " +
            "OR a.location IS NULL OR TRIM(a.location) = '' " +
            "OR a.street IS NULL OR TRIM(a.street) = '' " +
            "OR a.street_number IS NULL OR TRIM(a.street_number) = ''",
            nativeQuery = true)
    List<Long> findAllByAddressContainingNullValues();

    /**
     * @param maritalStates pass always {@code MaritalState.getValuesAsString()}.
     * @return all owners where the tax information contains null or invalid values or an invalid literal.
     */
    @Query(value = "SELECT o.id " +
            "FROM inhaber o " +
            "JOIN inhaber_steuer_informationen t ON o.tax_information_id = t.id " +
            "WHERE t.tax_number IS NULL OR TRIM(t.tax_number) = '' " +
            "OR t.capital_gainstax_rate IS NULL OR t.capital_gainstax_rate NOT BETWEEN 0 AND 100 " +
            "OR t.church_tax_rate IS NULL OR t.church_tax_rate NOT BETWEEN 0 AND 100 " +
            "OR t.marital_state IS NULL OR t.marital_state NOT IN :maritalStates " +
            "OR t.solidarity_surcharge_tax_rate IS NULL OR t.solidarity_surcharge_tax_rate NOT BETWEEN 0 AND 100 " +
            "OR t.tax_rate IS NULL OR t.tax_rate NOT BETWEEN 0 AND 100", nativeQuery = true)
    List<Long> findAllByTaxInformationContainingNullOrInvalidValues(@Param("maritalStates") Collection<String> maritalStates);

    /**
     * Fast way to check if any inconsistency in owners exists.
     * @return true if any inconsistency in owners exists, otherwise false.
     */
    @Transactional
    default boolean inconsistentOwnerExists() {
        return !findAllByAddressOrTaxInformationIsInvalid().isEmpty()
                || !findAllByForenameIsNullOrAfternameIsNullOrCreatedAtIsNull().isEmpty()
                || !findAllByAddressContainingNullValues().isEmpty()
                || !findAllByTaxInformationContainingNullOrInvalidValues(MaritalState.getValuesAsString()).isEmpty()
                || !findAllByStateIsNotIn(State.getValuesAsString()).isEmpty();
    }
    // endregion

    // region Transaction to reconstruct an owner
    /**
     * @param id the id of the owner to reconstruct.
     * @return the owner with the given id, reconstructed (but possible with invalid values) from the database by native queries instead of via JPA.
     */
    @Transactional
    @NonNull
    default Owner reconstructOwner(Long id) {
        Owner reconstructedOwner = new Owner();
        reconstructedOwner.setId(id);
        reconstructedOwner.setForename(findForenameById(id).orElse(null));
        reconstructedOwner.setAftername(findAfternameById(id).orElse(null));
        reconstructedOwner.setState(findStateById(id).map(s -> {
            try {
                return State.valueOf(s);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }).orElse(null));
        reconstructedOwner.setCreatedAt(findCreatedAtById(id).orElse(null));
        reconstructedOwner.setDeactivatedAt(findDeactivatedAtById(id).orElse(null));
        reconstructedOwner.setNotice(findNoticeById(id).orElse(null));

        Optional<Long> addressId = findAddressIdByOwnerId(id);
        if (addressId.isPresent()) {
            Owner.Address address = reconstructedOwner.getAddress();
            address.setId(addressId.get());
            address.setCountry(findCountryByAddressId(addressId.get()).orElse(null));
            address.setLocation(findLocationByAddressId(addressId.get()).orElse(null));
            address.setPlz(findPlzByAddressId(addressId.get()).orElse(null));
            address.setStreet(findStreetByAddressId(addressId.get()).orElse(null));
            address.setStreetNumber(findStreetNumberByAddressId(addressId.get()).orElse(null));
        }

        Optional<Long> taxInformationId = findTaxInformationIdByOwnerId(id);
        if (taxInformationId.isPresent()) {
            Owner.TaxInformation taxInformation = reconstructedOwner.getTaxInformation();
            taxInformation.setId(taxInformationId.get());
            taxInformation.setTaxNumber(findTaxNumberByTaxInformationId(taxInformationId.get()).orElse(null));

            findCapitalGainsTaxRateByTaxInformationId(taxInformationId.get()).ifPresent(capitalGainsTaxRate
                    -> taxInformation.setCapitalGainsTaxRate(capitalGainsTaxRate.doubleValue())
            );
            findChurchTaxRateByTaxInformationId(taxInformationId.get()).ifPresent(churchTaxRate
                    -> taxInformation.setChurchTaxRate(churchTaxRate.doubleValue())
            );
            findMaritalStateByTaxInformationId(taxInformationId.get()).ifPresent(maritalState
                    -> {
                try {
                    taxInformation.setMaritalState(MaritalState.valueOf(maritalState));
                } catch (IllegalArgumentException e) {
                    taxInformation.setMaritalState(null);
                }}
            );
            findSolidaritySurchargeTaxRateByTaxInformationId(taxInformationId.get()).ifPresent(solidaritySurchargeTaxRate
                    -> taxInformation.setSolidaritySurchargeTaxRate(solidaritySurchargeTaxRate.doubleValue())
            );
            findTaxRateByTaxInformationId(taxInformationId.get()).ifPresent(taxRate
                    -> taxInformation.setTaxRate(taxRate.doubleValue())
            );
        }
        return reconstructedOwner;
    }

    @Query(value = "SELECT o.forename FROM inhaber o WHERE o.id = :id", nativeQuery = true)
    Optional<String> findForenameById(Long id);
    @Query(value = "SELECT o.aftername FROM inhaber o WHERE o.id = :id", nativeQuery = true)
    Optional<String> findAfternameById(Long id);
    @Query(value = "SELECT o.notice FROM inhaber o WHERE o.id = :id", nativeQuery = true)
    Optional<String> findNoticeById(Long id);
    @Query(value = "SELECT o.state FROM inhaber o WHERE o.id = :id", nativeQuery = true)
    Optional<String> findStateById(Long id);
    @Query(value = "SELECT o.created_at FROM inhaber o WHERE o.id = :id", nativeQuery = true)
    Optional<Timestamp> findCreatedAtById(Long id);
    @Query(value = "SELECT o.deactivated_at FROM inhaber o WHERE o.id = :id", nativeQuery = true)
    Optional<Timestamp> findDeactivatedAtById(Long id);
    @Query(value = "SELECT o.address_id FROM inhaber o WHERE o.id = :id", nativeQuery = true)
    Optional<Long> findAddressIdByOwnerId(Long id);
    @Query(value = "SELECT a.country FROM inhaber_adresse a WHERE a.id = :id", nativeQuery = true)
    Optional<String> findCountryByAddressId(Long id);
    @Query(value = "SELECT a.location FROM inhaber_adresse a WHERE a.id = :id", nativeQuery = true)
    Optional<String> findLocationByAddressId(Long id);
    @Query(value = "SELECT a.plz FROM inhaber_adresse a WHERE a.id = :id", nativeQuery = true)
    Optional<String> findPlzByAddressId(Long id);
    @Query(value = "SELECT a.street FROM inhaber_adresse a WHERE a.id = :id", nativeQuery = true)
    Optional<String> findStreetByAddressId(Long id);
    @Query(value = "SELECT a.street_number FROM inhaber_adresse a WHERE a.id = :id", nativeQuery = true)
    Optional<String> findStreetNumberByAddressId(Long id);
    @Query(value = "SELECT o.tax_information_id FROM inhaber o WHERE o.id = :id", nativeQuery = true)
    Optional<Long> findTaxInformationIdByOwnerId(Long id);
    @Query(value = "SELECT a.capital_gainstax_rate FROM inhaber_steuer_informationen a WHERE a.id = :id", nativeQuery = true)
    Optional<BigDecimal> findCapitalGainsTaxRateByTaxInformationId(Long id);
    @Query(value = "SELECT a.church_tax_rate FROM inhaber_steuer_informationen a WHERE a.id = :id", nativeQuery = true)
    Optional<BigDecimal> findChurchTaxRateByTaxInformationId(Long id);
    @Query(value = "SELECT a.marital_state FROM inhaber_steuer_informationen a WHERE a.id = :id", nativeQuery = true)
    Optional<String> findMaritalStateByTaxInformationId(Long id);
    @Query(value = "SELECT a.solidarity_surcharge_tax_rate FROM inhaber_steuer_informationen a WHERE a.id = :id", nativeQuery = true)
    Optional<BigDecimal> findSolidaritySurchargeTaxRateByTaxInformationId(Long id);
    @Query(value = "SELECT a.tax_number FROM inhaber_steuer_informationen a WHERE a.id = :id", nativeQuery = true)
    Optional<String> findTaxNumberByTaxInformationId(Long id);
    @Query(value = "SELECT a.tax_rate FROM inhaber_steuer_informationen a WHERE a.id = :id", nativeQuery = true)
    Optional<BigDecimal> findTaxRateByTaxInformationId(Long id);
    // endregion

    // region Transaction to delete owner natively
    @Modifying
    @Transactional
    @Query(value = "SET FOREIGN_KEY_CHECKS = 0", nativeQuery = true)
    void disableForeignKeyChecks();

    @Modifying
    @Transactional
    @Query(value = "SET FOREIGN_KEY_CHECKS = 1", nativeQuery = true)
    void enableForeignKeyChecks();

    @Transactional
    @Modifying
    @Query(value = "DELETE o, a, t " +
            "FROM inhaber o " +
            "LEFT JOIN inhaber_adresse a ON o.address_id = a.id " +
            "LEFT JOIN inhaber_steuer_informationen t ON o.tax_information_id = t.id " +
            "WHERE o.id = :id", nativeQuery = true)
    void deleteOwner(@Param("id") Long id);

    @Transactional
    @Modifying
    default void deleteById(@NonNull @Param("id") Long id) {
        disableForeignKeyChecks();
        deleteOwner(id);
        enableForeignKeyChecks();
    }
    // endregion
}
