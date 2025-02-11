package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.AccountType;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.InterestInterval;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.State;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.interfaces.Changable;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.kontos.AccountService;
import org.springframework.dao.DataAccessException;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;

/**
 * Konto
 */
@Entity(name = "pAccount")
@Table(name = "pkonto")
public class Account extends FinancialAsset implements Changable {

    @Transient
    private boolean isChanged = false;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private AccountType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private State state = State.ACTIVATED;

    @Column(name = "currency_code", nullable = false)
    private String currencyCode;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance = BigDecimal.valueOf(0);

    @Column(name = "iban", nullable = false, unique = true)
    private String iban;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    @ManyToOne
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @Column(name = "notice")
    private String notice;

    @Column(name = "bank_name", nullable = false)
    private String bankName;

    @Column(name = "konto_number", nullable = false, unique = true)
    private String kontoNumber;

    @Column(name = "interest_rate", nullable = false)
    private BigDecimal interestRate = BigDecimal.valueOf(0); // Zinssatz

    @Column(name = "interest_days", nullable = false)
    private String interestDays; // Zinstage (an denen die Zinsen ausgezahlt werden)

    @Enumerated(EnumType.STRING)
    @Column(name = "interest_interval", nullable = false)
    private InterestInterval interestInterval;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "deactivated_at")
    private Date deactivatedAt;

    @ManyToMany(mappedBy = "billingAccounts", fetch = FetchType.EAGER)
    private Set<Depot> mappedDepots = Collections.emptySet();

    @Override
    public BigDecimal getValue(@NonNull AccountService accountService) throws DataAccessException {
        if (Currency.getInstance("EUR").equals(getValueCurrency())) return balance;
        Double latestExchangeCourse = accountService.getLatestExchangeCourse(getValueCurrency());
        return balance.divide(BigDecimal.valueOf(latestExchangeCourse), BigDecimal.ROUND_HALF_DOWN);
    }

    @Override
    public Currency getValueCurrency() {
        return getCurrency();
    }

    @Override
    public String toString() {
        return iban;
    }

    @Override
    public void setChanged(boolean changed) {
        isChanged = changed;
    }

    @Override
    public boolean isChanged() {
        return isChanged;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Account account = (Account) obj;
        return Objects.equals(id, account.id);
    }

    // region Getters & Setters
    public void setId(Long id) {
        this.id = id;
        isChanged = true;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        isChanged = true;
    }

    public AccountType getType() {
        return type;
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

    public void setType(AccountType type) {
        this.type = type;
        isChanged = true;
    }

    public Currency getCurrency() {
        if (currencyCode == null) return null;
        return Currency.getInstance(currencyCode);
    }

    public void setCurrencyCode(Currency currency) {
        this.currencyCode = currency.getCurrencyCode();
        isChanged = true;
    }

    public double getBalance() {
        return balance.doubleValue();
    }

    public BigDecimal getBalanceBigDecimal() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = BigDecimal.valueOf(balance).setScale(2, RoundingMode.HALF_DOWN);
        isChanged = true;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
        isChanged = true;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
        isChanged = true;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
        isChanged = true;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
        isChanged = true;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
        isChanged = true;
    }

    public String getKontoNumber() {
        return kontoNumber;
    }

    public void setKontoNumber(String kontoNumber) {
        this.kontoNumber = kontoNumber;
        isChanged = true;
    }

    public BigDecimal getInterestRateBigDecimal() {
        return interestRate;
    }

    public double getInterestRate() {
        return interestRate.doubleValue();
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = BigDecimal.valueOf(interestRate).setScale(2, RoundingMode.HALF_DOWN);
        isChanged = true;
    }

    public String getInterestDays() {
        return interestDays;
    }

    public void setInterestDays(String interestDays) {
        this.interestDays = interestDays;
        isChanged = true;
    }

    public InterestInterval getInterestInterval() {
        return interestInterval;
    }

    public void setInterestInterval(InterestInterval interestInterval) {
        this.interestInterval = interestInterval;
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

    public List<Depot> getMappedDepots() {
        return mappedDepots.stream().toList();
    }
    // endregion
}
