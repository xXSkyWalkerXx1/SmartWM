package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.MaritalState;

import javax.persistence.*;

@Entity
@Table(name = "owner_tax_information")
public class TaxInformation {
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
