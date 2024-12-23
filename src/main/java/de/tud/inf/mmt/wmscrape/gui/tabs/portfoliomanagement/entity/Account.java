package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.AccountType;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.InterestInterval;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.State;
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
public class Account extends FinancialAsset {

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
        if (Currency.getInstance("EUR").equals(getValueCurrency())) {
            return balance;
        }
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

    // region Getters & Setters
    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        } else {
            setDeactivatedAt(Calendar.getInstance().getTime());
        }
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public Currency getCurrency() {
        if (currencyCode == null) return null;
        return Currency.getInstance(currencyCode);
    }

    public void setCurrencyCode(Currency currency) {
        this.currencyCode = currency.getCurrencyCode();
    }

    public double getBalance() {
        return balance.doubleValue();
    }

    public BigDecimal getBalanceBigDecimal() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = BigDecimal.valueOf(balance).setScale(2, RoundingMode.HALF_DOWN);
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
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

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getKontoNumber() {
        return kontoNumber;
    }

    public void setKontoNumber(String kontoNumber) {
        this.kontoNumber = kontoNumber;
    }

    public BigDecimal getInterestRateBigDecimal() {
        return interestRate;
    }

    public double getInterestRate() {
        return interestRate.doubleValue();
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = BigDecimal.valueOf(interestRate).setScale(2, RoundingMode.HALF_DOWN);
    }

    public String getInterestDays() {
        return interestDays;
    }

    public void setInterestDays(String interestDays) {
        this.interestDays = interestDays;
    }

    public InterestInterval getInterestInterval() {
        return interestInterval;
    }

    public void setInterestInterval(InterestInterval interestInterval) {
        this.interestInterval = interestInterval;
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

    public List<Depot> getMappedDepots() {
        return mappedDepots.stream().toList();
    }
    // endregion

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Account account = (Account) obj;
        return Objects.equals(id, account.id);
    }
}
