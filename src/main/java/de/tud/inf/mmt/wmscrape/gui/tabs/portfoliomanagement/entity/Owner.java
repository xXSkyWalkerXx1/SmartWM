package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.MaritalState;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.State;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

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
        private double taxRate;

        @Column(name = "church_tax_rate", nullable = false)
        private double churchTaxRate;

        @Column(name = "capital_gainstax_rate", nullable = false)
        private double capitalGainsTaxRate;

        @Column(name = "solidarity_surcharge_tax_rate", nullable = false)
        private double solidaritySurchargeTaxRate;

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
            return taxRate;
        }

        public void setTaxRate(double taxRate) {
            this.taxRate = taxRate;
        }

        public double getChurchTaxRate() {
            return churchTaxRate;
        }

        public void setChurchTaxRate(double churchTaxRate) {
            this.churchTaxRate = churchTaxRate;
        }

        public double getCapitalGainsTaxRate() {
            return capitalGainsTaxRate;
        }

        public void setCapitalGainsTaxRate(double capitalGainsTaxRate) {
            this.capitalGainsTaxRate = capitalGainsTaxRate;
        }

        public double getSolidaritySurchargeTaxRate() {
            return solidaritySurchargeTaxRate;
        }

        public void setSolidaritySurchargeTaxRate(double solidaritySurchargeTaxRate) {
            this.solidaritySurchargeTaxRate = solidaritySurchargeTaxRate;
        }
    }
    // endregion

    // ToDo: implement "Zugangspasswort" and so on?

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

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "address") // ToDo: rename to adress_id
    private Address address;

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "tax_information_id") // creates foreign-key
    private TaxInformation taxInformation;

    @OneToMany(mappedBy = "owner")
    private List<Portfolio> portfolios;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "deactivated_at")
    private Date deactivatedAt;

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

    public void setAddress(Address address) {
        this.address = address;
    }

    public TaxInformation getTaxInformation() {
        return taxInformation;
    }

    public void setTaxInformation(TaxInformation taxInformation) {
        this.taxInformation = taxInformation;
    }

    public List<Portfolio> getPortfolios() {
        return portfolios;
    }
    // endregion
}
