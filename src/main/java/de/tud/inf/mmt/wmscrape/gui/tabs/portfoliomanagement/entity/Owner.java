package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.MaritalState;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.State;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.interfaces.Changable;
import org.springframework.lang.NonNull;

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

        @Transient
        private boolean isChanged = false;

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

        @Override
        public boolean isChanged() {
            return isChanged;
        }

        @Override
        public void setChanged(boolean changed) {
            isChanged = changed;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
            isChanged = true;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
            isChanged = true;
        }

        public String getPlz() {
            return plz;
        }

        public void setPlz(String plz) {
            this.plz = plz;
            isChanged = true;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
            isChanged = true;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
            isChanged = true;
        }

        public String getStreetNumber() {
            return streetNumber;
        }

        public void setStreetNumber(String streetNumber) {
            this.streetNumber = streetNumber;
            isChanged = true;
        }
    }

    @Entity
    @Table(name = "inhaber_steuer_informationen")
    public static class TaxInformation implements Changable {

        @Transient
        private boolean isChanged = false;

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

        @Override
        public boolean isChanged() {
            return isChanged;
        }

        @Override
        public void setChanged(boolean changed) {
            isChanged = changed;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
            isChanged = true;
        }

        public String getTaxNumber() {
            return taxNumber;
        }

        public void setTaxNumber(String taxNumber) {
            this.taxNumber = taxNumber;
            isChanged = true;
        }

        public MaritalState getMaritalState() {
            return maritalState;
        }

        public void setMaritalState(MaritalState maritalState) {
            this.maritalState = maritalState;
            isChanged = true;
        }

        public double getTaxRate() {
            return taxRate.doubleValue();
        }

        public void setTaxRate(double taxRate) {
            this.taxRate = BigDecimal.valueOf(taxRate).setScale(2, RoundingMode.HALF_DOWN);
            isChanged = true;
        }

        public double getChurchTaxRate() {
            return churchTaxRate.doubleValue();
        }

        public void setChurchTaxRate(double churchTaxRate) {
            this.churchTaxRate = BigDecimal.valueOf(churchTaxRate).setScale(2, RoundingMode.HALF_DOWN);
            isChanged = true;
        }

        public double getCapitalGainsTaxRate() {
            return capitalGainsTaxRate.doubleValue();
        }

        public void setCapitalGainsTaxRate(double capitalGainsTaxRate) {
            this.capitalGainsTaxRate = BigDecimal.valueOf(capitalGainsTaxRate).setScale(2, RoundingMode.HALF_DOWN);
            isChanged = true;
        }

        public double getSolidaritySurchargeTaxRate() {
            return solidaritySurchargeTaxRate.doubleValue();
        }

        public void setSolidaritySurchargeTaxRate(double solidaritySurchargeTaxRate) {
            this.solidaritySurchargeTaxRate = BigDecimal.valueOf(solidaritySurchargeTaxRate).setScale(2, RoundingMode.HALF_DOWN);
            isChanged = true;
        }

        public BigDecimal getTaxRateBigDecimal() {
            return taxRate;
        }

        public BigDecimal getChurchTaxRateBigDecimal() {
            return churchTaxRate;
        }

        public BigDecimal getCapitalGainsTaxRateBigDecimal() {
            return capitalGainsTaxRate;
        }

        public BigDecimal getSolidaritySurchargeTaxRateBigDecimal() {
            return solidaritySurchargeTaxRate;
        }
    }
    // endregion

    @Transient
    private boolean isChanged = false;

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

    //@PostLoad
    @PostPersist
    @PostUpdate
    private void onSaveOrLoad() {
        setChanged(false);
    }

    @Override
    public boolean isChanged() {
        return isChanged || address.isChanged() || taxInformation.isChanged();
    }

    @Override
    public void setChanged(boolean changed) {
        address.setChanged(changed);
        taxInformation.setChanged(changed);
        isChanged = changed;
    }

    @Override
    public String toString() {
        return String.format("%s %s (Steuer-Nr.: %s)", forename, aftername, taxInformation.taxNumber);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Owner owner = (Owner) obj;
        return Objects.equals(id, owner.id);
    }

    // region Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
        isChanged = true;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;

        if (State.ACTIVATED.equals(state)) {
            setDeactivatedAt(null);
        } else if (State.DEACTIVATED.equals(state)) {
            setDeactivatedAt(Calendar.getInstance().getTime());
        }

        isChanged = true;
    }

    public String getForename() {
        return forename;
    }

    public void setForename(String forename) {
        this.forename = forename;
        isChanged = true;
    }

    public String getAftername() {
        return aftername;
    }

    public void setAftername(String aftername) {
        this.aftername = aftername;
        isChanged = true;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
        isChanged = true;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        isChanged = true;
    }

    public Date getDeactivatedAt() {
        return deactivatedAt;
    }

    public void setDeactivatedAt(Date deactivatedAt) {
        this.deactivatedAt = deactivatedAt;
        isChanged = true;
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
