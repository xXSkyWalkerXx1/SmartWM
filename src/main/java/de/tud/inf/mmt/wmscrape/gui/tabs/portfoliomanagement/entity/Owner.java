package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.MaritalState;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.State;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.interfaces.Changable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Entity
@Table(name = "inhaber")
public class Owner implements Changable {

    // region Entities as inner-classes
    @Entity
    @Table(name = "inhaber_adresse")
    public static class Address implements Changable {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        @Column(name = "country", nullable = false)
        private String country;
        @Column(name = "plz", nullable = false)
        private String plz;
        @Column(name = "location", nullable = false)
        private String location; // Stadt/Dorf
        @Column(name = "street", nullable = false)
        private String street;
        @Column(name = "street_number", nullable = false)
        private String streetNumber;

        @Transient
        private final SimpleLongProperty idProperty = new SimpleLongProperty();
        @Transient
        private final SimpleStringProperty countryProperty = new SimpleStringProperty();
        @Transient
        private final SimpleStringProperty plzProperty = new SimpleStringProperty();
        @Transient
        private final SimpleStringProperty locationProperty = new SimpleStringProperty();
        @Transient
        private final SimpleStringProperty streetProperty = new SimpleStringProperty();
        @Transient
        private final SimpleStringProperty streetNumberProperty = new SimpleStringProperty();

        @Override
        @PostLoad
        public void onPostLoadEntity() {
            idProperty.set(id);
            countryProperty.set(country);
            plzProperty.set(plz);
            locationProperty.set(location);
            streetProperty.set(street);
            streetNumberProperty.set(streetNumber);
        }

        @Override
        public void onPrePersistOrUpdateOrRemoveEntity() {
            id = idProperty.get();
            country = countryProperty.get();
            plz = plzProperty.get();
            location = locationProperty.get();
            street = streetProperty.get();
            streetNumber = streetNumberProperty.get();
        }

        @Override
        public boolean isChanged() {
            return !Objects.equals(id, idProperty.get())
                    || !Objects.equals(country, countryProperty.get())
                    || !Objects.equals(plz, plzProperty.get())
                    || !Objects.equals(location, locationProperty.get())
                    || !Objects.equals(street, streetProperty.get())
                    || !Objects.equals(streetNumber, streetNumberProperty.get());
        }

        @Override
        public void restore() {
            onPostLoadEntity();
        }

        public Long getId() {
            return idProperty.get();
        }

        public void setId(Long id) {
            idProperty.set(id);
        }

        public String getCountry() {
            return countryProperty.get();
        }

        public void setCountry(String country) {
            countryProperty.set(country);
        }

        public String getPlz() {
            return plzProperty.get();
        }

        public void setPlz(String plz) {
            plzProperty.set(plz);
        }

        public String getLocation() {
            return locationProperty.get();
        }

        public void setLocation(String location) {
            locationProperty.set(location);
        }

        public String getStreet() {
            return streetProperty.get();
        }

        public void setStreet(String street) {
            streetProperty.set(street);
        }

        public String getStreetNumber() {
            return streetNumberProperty.get();
        }

        public void setStreetNumber(String streetNumber) {
            streetNumberProperty.set(streetNumber);
        }
    }

    @Entity
    @Table(name = "inhaber_steuer_informationen")
    public static class TaxInformation implements Changable {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        @Column(name = "tax_number", nullable = false, unique = true)
        private String taxNumber;
        @Enumerated(EnumType.STRING)
        @Column(name = "marital_state", nullable = false)
        private MaritalState maritalState;
        @Column(name = "tax_rate", nullable = false)
        private BigDecimal taxRate = BigDecimal.valueOf(0);
        @Column(name = "church_tax_rate", nullable = false)
        private BigDecimal churchTaxRate = BigDecimal.valueOf(0);
        @Column(name = "capital_gainstax_rate", nullable = false)
        private BigDecimal capitalGainsTaxRate = BigDecimal.valueOf(0);
        @Column(name = "solidarity_surcharge_tax_rate", nullable = false)
        private BigDecimal solidaritySurchargeTaxRate = BigDecimal.valueOf(0);

        @Transient
        private final SimpleLongProperty idProperty = new SimpleLongProperty();
        @Transient
        private final SimpleStringProperty taxNumberProperty = new SimpleStringProperty();
        @Transient
        private final ObjectProperty<MaritalState> maritalStateProperty = new SimpleObjectProperty<>();
        @Transient
        private final ObjectProperty<BigDecimal> taxRateProperty = new SimpleObjectProperty<>();
        @Transient
        private final ObjectProperty<BigDecimal> churchTaxRateProperty = new SimpleObjectProperty<>();
        @Transient
        private final ObjectProperty<BigDecimal> capitalGainsTaxRateProperty = new SimpleObjectProperty<>();
        @Transient
        private final ObjectProperty<BigDecimal> solidaritySurchargeTaxRateProperty = new SimpleObjectProperty<>();

        @Override
        @PostLoad
        public void onPostLoadEntity() {
            idProperty.set(id);
            taxNumberProperty.set(taxNumber);
            maritalStateProperty.set(maritalState);
            taxRateProperty.set(taxRate);
            churchTaxRateProperty.set(churchTaxRate);
            capitalGainsTaxRateProperty.set(capitalGainsTaxRate);
            solidaritySurchargeTaxRateProperty.set(solidaritySurchargeTaxRate);
        }

        @Override
        public void onPrePersistOrUpdateOrRemoveEntity() {
            id = idProperty.get();
            taxNumber = taxNumberProperty.get();
            maritalState = maritalStateProperty.get();
            taxRate = taxRateProperty.get();
            churchTaxRate = churchTaxRateProperty.get();
            capitalGainsTaxRate = capitalGainsTaxRateProperty.get();
            solidaritySurchargeTaxRate = solidaritySurchargeTaxRateProperty.get();
        }

        @Override
        public boolean isChanged() {
            return !Objects.equals(id, idProperty.get())
                    || !Objects.equals(taxNumber, taxNumberProperty.get())
                    || !Objects.equals(maritalState, maritalStateProperty.get())
                    || !Objects.equals(taxRate, taxRateProperty.get())
                    || !Objects.equals(churchTaxRate, churchTaxRateProperty.get())
                    || !Objects.equals(capitalGainsTaxRate, capitalGainsTaxRateProperty.get())
                    || !Objects.equals(solidaritySurchargeTaxRate, solidaritySurchargeTaxRateProperty.get());
        }

        @Override
        public void restore() {
            onPostLoadEntity();
        }

        public Long getId() {
            return idProperty.get();
        }

        public void setId(Long id) {
            idProperty.set(id);
        }

        public String getTaxNumber() {
            return taxNumberProperty.get();
        }

        public void setTaxNumber(String taxNumber) {
            taxNumberProperty.set(taxNumber);
        }

        public MaritalState getMaritalState() {
            return maritalStateProperty.get();
        }

        public void setMaritalState(MaritalState maritalState) {
            maritalStateProperty.set(maritalState);
        }

        public double getTaxRate() {
            return taxRateProperty.get().doubleValue();
        }

        public void setTaxRate(double taxRate) {
            taxRateProperty.set(BigDecimal.valueOf(taxRate).setScale(2, RoundingMode.HALF_DOWN));
        }

        public double getChurchTaxRate() {
            return churchTaxRateProperty.get().doubleValue();
        }

        public void setChurchTaxRate(double churchTaxRate) {
            churchTaxRateProperty.set(BigDecimal.valueOf(churchTaxRate).setScale(2, RoundingMode.HALF_DOWN));
        }

        public double getCapitalGainsTaxRate() {
            return capitalGainsTaxRateProperty.get().doubleValue();
        }

        public void setCapitalGainsTaxRate(double capitalGainsTaxRate) {
            capitalGainsTaxRateProperty.set(BigDecimal.valueOf(capitalGainsTaxRate).setScale(2, RoundingMode.HALF_DOWN));
        }

        public double getSolidaritySurchargeTaxRate() {
            return solidaritySurchargeTaxRateProperty.get().doubleValue();
        }

        public void setSolidaritySurchargeTaxRate(double solidaritySurchargeTaxRate) {
            solidaritySurchargeTaxRateProperty.set(BigDecimal.valueOf(solidaritySurchargeTaxRate).setScale(2, RoundingMode.HALF_DOWN));
        }

        public BigDecimal getTaxRateBigDecimal() {
            return taxRateProperty.get();
        }

        public BigDecimal getChurchTaxRateBigDecimal() {
            return churchTaxRateProperty.get();
        }

        public BigDecimal getCapitalGainsTaxRateBigDecimal() {
            return capitalGainsTaxRateProperty.get();
        }

        public BigDecimal getSolidaritySurchargeTaxRateBigDecimal() {
            return solidaritySurchargeTaxRateProperty.get();
        }
    }
    // endregion

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private State state = State.ACTIVATED;
    @Column(name = "forename", nullable = false)
    private String forename;
    @Column(name = "aftername", nullable = false)
    private String aftername;
    @Column(name = "notice")
    private String notice;
    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address = new Address();
    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "tax_information_id", nullable = false) // creates foreign-key
    private TaxInformation taxInformation = new TaxInformation();
    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private Set<Portfolio> portfolios = Collections.emptySet();
    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private Set<Account> accounts = Collections.emptySet();
    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private Set<Depot> depots = Collections.emptySet();
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date createdAt;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "deactivated_at")
    private Date deactivatedAt;

    @Transient
    public final SimpleLongProperty idProperty = new SimpleLongProperty();
    @Transient
    public final ObjectProperty<State> stateProperty = new SimpleObjectProperty<>(state);
    @Transient
    public final SimpleStringProperty forenameProperty = new SimpleStringProperty();
    @Transient
    public final SimpleStringProperty afternameProperty = new SimpleStringProperty();
    @Transient
    public final SimpleStringProperty noticeProperty = new SimpleStringProperty();
    @Transient
    public final ObjectProperty<Date> deactivatedAtProperty = new SimpleObjectProperty<>();

    @Override
    @PostLoad
    public void onPostLoadEntity() {
        idProperty.set(id);
        stateProperty.set(state);
        forenameProperty.set(forename);
        afternameProperty.set(aftername);
        noticeProperty.set(notice);
        deactivatedAtProperty.set(deactivatedAt);
    }

    @Override
    public void onPrePersistOrUpdateOrRemoveEntity() {
        address.onPrePersistOrUpdateOrRemoveEntity();
        taxInformation.onPrePersistOrUpdateOrRemoveEntity();
        id = idProperty.get();
        state = stateProperty.get();
        forename = forenameProperty.get();
        aftername = afternameProperty.get();
        notice = noticeProperty.get();
        deactivatedAt = deactivatedAtProperty.get();
    }

    @Override
    public boolean isChanged() {
        return address.isChanged()
                || taxInformation.isChanged()
                || !Objects.equals(id, idProperty.get())
                || !Objects.equals(state, stateProperty.get())
                || !Objects.equals(forename, forenameProperty.get())
                || !Objects.equals(aftername, afternameProperty.get())
                || !Objects.equals(notice, noticeProperty.get());
    }

    @Override
    public void restore() {
        onPostLoadEntity();
        address.restore();
        taxInformation.restore();
    }

    @Override
    public String toString() {
        return String.format("%s %s (Steuer-Nr.: %s)", getForename(), getAftername(), taxInformation.getTaxNumber());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Owner owner = (Owner) obj;
        return Objects.equals(idProperty.get(), owner.getId());
    }

    // region Getters & Setters
    public Long getId() {
        return idProperty.get();
    }

    public SimpleLongProperty idProperty() {
        return idProperty;
    }

    public void setId(Long id) {
        idProperty.set(id);
    }

    public State getState() {
        return stateProperty.get();
    }

    public void setState(State state) {
        stateProperty.set(state);

        if (State.ACTIVATED.equals(state)) {
            setDeactivatedAt(null);
        } else if (State.DEACTIVATED.equals(state)) {
            setDeactivatedAt(Calendar.getInstance().getTime());
        }
    }

    public String getForename() {
        return forenameProperty.get();
    }

    public void setForename(String forename) {
        forenameProperty.set(forename);
    }

    public String getAftername() {
        return afternameProperty.get();
    }

    public void setAftername(String aftername) {
        afternameProperty.set(aftername);
    }

    public String getNotice() {
        return noticeProperty.get();
    }

    public void setNotice(String notice) {
        noticeProperty.set(notice);
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getDeactivatedAt() {
        return deactivatedAt;
    }

    public void setDeactivatedAt(Date deactivatedAt) {
        this.deactivatedAtProperty.set(deactivatedAt);
    }

    public Address getAddress() {
        return address;
    }

    public TaxInformation getTaxInformation() {
        return taxInformation;
    }

    public Set<Portfolio> getPortfolios() {
        return portfolios;
    }

    public Set<Account> getAccounts() {
        return accounts;
    }

    public Set<Depot> getDepots() {
        return depots;
    }
    // endregion
}
