package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.State;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.interfaces.Valuable;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.kontos.AccountService;
import org.apache.poi.ss.formula.eval.NotImplementedFunctionException;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;

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

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private State state = State.ACTIVATED;

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "bank_id", nullable = false)
    private DepotBank bank;

    @OneToOne
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @Column(name = "notice")
    private String notice;

    @ManyToMany
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

    @Override
    public BigDecimal getValue(@NonNull AccountService accountService) {
        return BigDecimal.ZERO;
        //throw new NotImplementedFunctionException("Muss noch implementiert werden");
    }

    @Override
    public Currency getValueCurrency() {
        return Currency.getInstance("EUR");
    }

    @Override
    public String toString() {
        return name;
    }

    // region Getters & Setters
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

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
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
