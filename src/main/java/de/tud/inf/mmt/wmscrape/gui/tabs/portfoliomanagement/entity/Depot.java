package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.interfaces.Valuable;
import org.apache.poi.ss.formula.eval.NotImplementedFunctionException;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(name = "pDepot")
@Table(name = "pdepot")
public class Depot extends FinancialAsset {

    // region Entity as inner-class
    @Entity
    @Table(name = "depot_bank")
    public static class DepotBank {

        @Id
        @Column(name = "name", nullable = false)
        private String name; // bank-name

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
    // endregion

    @Id
    @Column(name = "name", nullable = false)
    private String name;

    @OneToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "bank_id", nullable = false)
    private DepotBank bank;

    @OneToOne
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @Column(name = "notice")
    private String notice;

    @OneToMany
    @JoinColumn(name = "billing_account_id", nullable = false)
    private List<Account> billingAccounts = new ArrayList<>(); // Verrechnungskonten

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "commission_scheme_id", nullable = false)
    private CommissionScheme commissionScheme; // Provision-schema

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "deactivated_at")
    private Date deactivatedAt;

    // region Getters & Setters
    @Override
    public BigDecimal getValue() {
        throw new NotImplementedFunctionException("Muss noch implementiert werden");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public DepotBank getBank() {
        return bank;
    }

    public void setBank(DepotBank bank) {
        this.bank = bank;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public List<Account> getBillingAccounts() {
        return billingAccounts;
    }

    public void setBillingAccounts(List<Account> billingAccounts) {
        this.billingAccounts = billingAccounts;
    }

    public CommissionScheme getCommissionScheme() {
        return commissionScheme;
    }

    public void setCommissionScheme(CommissionScheme commissionScheme) {
        this.commissionScheme = commissionScheme;
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
    // endregion
}
