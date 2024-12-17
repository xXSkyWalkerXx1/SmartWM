package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.repository;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {

    /**
     * @return all portfolios where the owner or the investment guideline is null or invalid.
     */
    @Query(value = "SELECT o.id " +
            "FROM inhaber o " +
            "LEFT JOIN inhaber_adresse a ON o.address_id = a.id " +
            "LEFT JOIN inhaber_steuer_informationen t ON o.tax_information_id = t.id " +
            "WHERE a.id IS NULL OR t.id IS NULL OR o.address_id IS NULL OR o.tax_information_id IS NULL", nativeQuery = true)
    List<Long> findAllByAddressOrTaxinformationIsInvalid();

    /**
     * @return all owners where the state is not 'ACTIVATED' or 'DEACTIVATED'.
     */
    @Query("SELECT o.id " +
            "FROM Owner o " +
            "WHERE o.state != 'ACTIVATED' AND o.state != 'DEACTIVATED'")
    List<Long> findAllByInvalidLiteral();

    /**
     * @return all owners where the forename, aftername or the created_at date is null.
     */
    @Query("SELECT o.id " +
            "FROM Owner o " +
            "WHERE o.forename IS NULL OR o.aftername IS NULL OR o.createdAt IS NULL")
    List<Long> findAllByNullEntry();

    @Transactional
    @Modifying
    @Query(value = "DELETE o, a, t " +
            "FROM inhaber o " +
            "JOIN inhaber_adresse a ON o.address_id = a.id " +
            "JOIN inhaber_steuer_informationen t ON o.tax_information_id = t.id " +
            "WHERE o.id = :id", nativeQuery = true)
    void deleteById(@Param("id") Long id);
}
