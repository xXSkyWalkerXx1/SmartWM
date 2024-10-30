package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.State;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.interfaces.Valuable;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "portfolio")
public class Portfolio implements Valuable {

    @Id
    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private State state;

    @Column(name = "created_at", nullable = false)
    private String createdAt;

    @Column(name = "deactivated_at")
    private String deactivatedAt;

    @OneToOne
    @JoinColumn(name = "investment_guideline_id", nullable = false)
    private InvestmentGuideline investmentGuideline;

    @OneToMany
    @JoinColumn(name = "depot_id", nullable = false)
    private List<Depot> depots = new ArrayList<>();

    @OneToMany
    @JoinColumn(name = "account_id", nullable = false)
    private List<Account> accounts = new ArrayList<>();

    // region Getters & Setters
    @Override
    public BigDecimal getValue() {
        BigDecimal sum = new BigDecimal(0);

        for (Depot depot : depots){
            sum = sum.add(depot.getValue());
        }

        for (Account account : accounts){
            sum = sum.add(account.getValue());
        }

        return sum;
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

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDeactivatedAt() {
        return deactivatedAt;
    }

    public void setDeactivatedAt(String deactivatedAt) {
        this.deactivatedAt = deactivatedAt;
    }

    public InvestmentGuideline getInvestmentGuideline() {
        return investmentGuideline;
    }

    public void setInvestmentGuideline(InvestmentGuideline investmentGuideline) {
        this.investmentGuideline = investmentGuideline;
    }

    public List<Depot> getDepots() {
        return depots;
    }

    public void setDepots(List<Depot> depots) {
        this.depots = depots;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }
    // endregion
}
