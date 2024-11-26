package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.MaritalState;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.State;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Entity
@Table(name = "inhaber")
public class Owner {

    // region Entities as inner-classes
    @Entity
    @Table(name = "inhaber_adresse")
    public static class Address {

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

        public Long getId() {
            return id;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getPlz() {
            return plz;
        }

        public void setPlz(String plz) {
            this.plz = plz;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getStreetNumber() {
            return streetNumber;
        }

        public void setStreetNumber(String streetNumber) {
            this.streetNumber = streetNumber;
        }
    }

    @Entity
    @Table(name = "inhaber_steuer_informationen")
    public static class TaxInformation {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "tax_number", nullable = false)
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

        public Long getId() {
            return id;
        }

        public String getTaxNumber() {
            return taxNumber;
        }

        public void setTaxNumber(String taxNumber) {
            this.taxNumber = taxNumber;
        }

        public MaritalState getMaritalState() {
            return maritalState;
        }

        public void setMaritalState(MaritalState maritalState) {
            this.maritalState = maritalState;
        }

        public double getTaxRate() {
            return taxRate.doubleValue();
        }

        public void setTaxRate(double taxRate) {
            this.taxRate = BigDecimal.valueOf(taxRate).setScale(2, RoundingMode.HALF_DOWN);;
        }

        public double getChurchTaxRate() {
            return churchTaxRate.doubleValue();
        }

        public void setChurchTaxRate(double churchTaxRate) {
            this.churchTaxRate = BigDecimal.valueOf(churchTaxRate).setScale(2, RoundingMode.HALF_DOWN);;
        }

        public double getCapitalGainsTaxRate() {
            return capitalGainsTaxRate.doubleValue();
        }

        public void setCapitalGainsTaxRate(double capitalGainsTaxRate) {
            this.capitalGainsTaxRate = BigDecimal.valueOf(capitalGainsTaxRate).setScale(2, RoundingMode.HALF_DOWN);;
        }

        public double getSolidaritySurchargeTaxRate() {
            return solidaritySurchargeTaxRate.doubleValue();
        }

        public void setSolidaritySurchargeTaxRate(double solidaritySurchargeTaxRate) {
            this.solidaritySurchargeTaxRate = BigDecimal.valueOf(solidaritySurchargeTaxRate).setScale(2, RoundingMode.HALF_DOWN);;
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
    @JoinColumn(name = "address_id")
    private Address address = new Address();

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "tax_information_id") // creates foreign-key
    private TaxInformation taxInformation = new TaxInformation();

    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER)
    private Set<Portfolio> portfolios = Collections.emptySet();

    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER)
    private Set<Account> accounts = Collections.emptySet();

    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER)
    private Set<Depot> depots = Collections.emptySet();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "deactivated_at")
    private Date deactivatedAt;

    @Override
    public String toString() {
        return String.format("%s %s (ID: %s)", forename, aftername, id);
    }

    // region Getters & Setters
    public Long getId() {
        return id;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getForename() {
        return forename;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public String getAftername() {
        return aftername;
    }

    public void setAftername(String aftername) {
        this.aftername = aftername;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
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
        this.deactivatedAt = deactivatedAt;
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

    /*
    ToDo:
     Remove!
     Be aware! This setters are only used to create a dummy-owner.
     */
    public void setPortfolios(Set<Portfolio> portfolios) {
        this.portfolios = portfolios;
    }
    public void setAccounts(Set<Account> accounts) {
        this.accounts = accounts;
    }
    public void setDepots(Set<Depot> depots) {
        this.depots = depots;
    }
    // endregion
}
