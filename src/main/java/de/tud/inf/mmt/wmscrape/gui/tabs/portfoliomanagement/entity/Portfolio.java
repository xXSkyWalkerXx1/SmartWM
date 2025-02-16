package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.State;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.interfaces.Changable;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.kontos.AccountService;
import javafx.beans.property.*;
import org.springframework.dao.DataAccessException;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;

@Entity
@Table(name = "portfolio")
public class Portfolio extends FinancialAsset implements Changable {

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

    @Transient
    private final SimpleLongProperty idProperty = new SimpleLongProperty();
    @Transient
    private final SimpleStringProperty nameProperty = new SimpleStringProperty();
    @Transient
    private final ObjectProperty<Owner> ownerProperty = new SimpleObjectProperty<>();
    @Transient
    private final ObjectProperty<State> stateProperty = new SimpleObjectProperty<>(state);

    @Override
    @PostLoad
    public void onPostLoadEntity() {
        idProperty.set(id);
        nameProperty.set(name);
        ownerProperty.set(owner);
        stateProperty.set(state);
    }

    @Override
    public void onPrePersistOrUpdateOrRemoveEntity() {
        id = idProperty.get();
        name = nameProperty.get();
        owner = ownerProperty.get();
        state = stateProperty.get();
        investmentGuideline.onPrePersistOrUpdateOrRemoveEntity();
        owner.onPrePersistOrUpdateOrRemoveEntity();
    }

    @Override
    public String toString() {
        return nameProperty.get();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Portfolio that = (Portfolio) obj;
        return Objects.equals(idProperty.get(), that.getId());
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
        return investmentGuideline.isChanged()
                || !Objects.equals(id, idProperty.get())
                || !Objects.equals(name, nameProperty.get())
                || !Objects.equals(owner, ownerProperty.get())
                || !Objects.equals(state, stateProperty.get());
    }

    @Override
    public void restore() {
        onPostLoadEntity();
        investmentGuideline.restore();
    }

    @Override
    public Currency getValueCurrency() {
        return Currency.getInstance("EUR");
    }

    public void setId(Long id) {
        idProperty.set(id);
    }

    public Long getId() {
        return idProperty.get();
    }

    public String getName() {
        return nameProperty.get();
    }

    public void setName(String name) {
        nameProperty.set(name);
    }

    public Owner getOwner() {
        return ownerProperty.get();
    }

    public void setOwner(Owner owner) {
        ownerProperty.set(owner);
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

    public InvestmentGuideline getInvestmentGuideline() {
        return investmentGuideline;
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
