package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.State;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.interfaces.Valuable;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;

@Entity
@Table(name = "portfolio")
public class Portfolio extends FinancialAsset {

    @Id
    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private State state = State.ACTIVATED;

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "investment_guideline_id", nullable = false)
    private InvestmentGuideline investmentGuideline = new InvestmentGuideline();

    @OneToMany(orphanRemoval = true, cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @JoinColumn(name = "depot_id", nullable = false)
    private Set<Depot> depots = Collections.emptySet();

    @OneToMany(mappedBy = "portfolio", orphanRemoval = true, cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private Set<Account> accounts = Collections.emptySet();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "deactivated_at")
    private Date deactivatedAt;

    @Override
    public String toString() {
        return name;
    }

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

        if (State.ACTIVATED.equals(state)) {
            setDeactivatedAt(null);
        } else {
            setDeactivatedAt(Calendar.getInstance().getTime());
        }
    }

    public InvestmentGuideline getInvestmentGuideline() {
        return investmentGuideline;
    }

    public void setInvestmentGuideline(InvestmentGuideline investmentGuideline) {
        this.investmentGuideline = investmentGuideline;
    }

    public List<Depot> getDepots() {
        return depots.stream().toList();
    }

    public void setDepots(List<Depot> depots) {
        this.depots = new HashSet<>(depots);
    }

    public List<Account> getAccounts() {
        return accounts.stream().toList();
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = new HashSet<>(accounts);
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
