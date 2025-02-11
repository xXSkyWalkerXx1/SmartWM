package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.State;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.interfaces.Changable;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.kontos.AccountService;
import org.springframework.dao.DataAccessException;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;

@Entity
@Table(name = "portfolio")
public class Portfolio extends FinancialAsset implements Changable {

    @Transient
    private boolean isChanged = false;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
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

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private Set<Account> accounts = Collections.emptySet();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "deactivated_at")
    private Date deactivatedAt;

    @PostPersist
    @PostUpdate
    private void onSaveOrLoad() {
        setChanged(false);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Portfolio that = (Portfolio) obj;
        return Objects.equals(id, that.id);
    }

    // region Getters & Setters
    @Override
    public BigDecimal getValue(@NonNull AccountService accountService) throws DataAccessException {
        BigDecimal sum = BigDecimal.ZERO;

        for (Depot depot : depots){
            sum = sum.add(depot.getValue(accountService));
        }

        for (Account account : accounts){
            sum = sum.add(account.getValue(accountService));
        }

        return sum;
    }

    @Override
    public boolean isChanged() {
        return isChanged || investmentGuideline.isChanged();
    }

    @Override
    public void setChanged(boolean changed) {
        investmentGuideline.setChanged(changed);
        isChanged = changed;
    }

    @Override
    public Currency getValueCurrency() {
        return Currency.getInstance("EUR");
    }

    public void setId(Long id) {
        this.id = id;
        isChanged = true;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        isChanged = true;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
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

    public InvestmentGuideline getInvestmentGuideline() {
        return investmentGuideline;
    }

    public List<Depot> getDepots() {
        return depots.stream().toList();
    }

    public void setDepots(List<Depot> depots) {
        this.depots = new HashSet<>(depots);
        isChanged = true;
    }

    public List<Account> getAccounts() {
        return accounts.stream().toList();
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = new HashSet<>(accounts);
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
    // endregion
}
