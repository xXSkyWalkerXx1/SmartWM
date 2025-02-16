package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.AccountType;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.InterestInterval;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.State;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.interfaces.Changable;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.kontos.AccountService;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import org.springframework.dao.DataAccessException;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Konto
 */
@Entity(name = "pAccount")
@Table(name = "pkonto")
public class Account extends FinancialAsset implements Changable {

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

    @Transient
    private final SimpleLongProperty idProperty = new SimpleLongProperty();
    @Transient
    private final SimpleStringProperty descriptionProperty = new SimpleStringProperty();
    @Transient
    private final SimpleObjectProperty<AccountType> typeProperty = new SimpleObjectProperty<>();
    @Transient
    private final SimpleObjectProperty<State> stateProperty = new SimpleObjectProperty<>(state);
    @Transient
    private final SimpleStringProperty currencyProperty = new SimpleStringProperty();
    @Transient
    private final SimpleObjectProperty<BigDecimal> balanceProperty = new SimpleObjectProperty<>(balance);
    @Transient
    private final SimpleObjectProperty<Owner> ownerProperty = new SimpleObjectProperty<>();
    @Transient
    private final SimpleObjectProperty<Portfolio> portfolioProperty = new SimpleObjectProperty<>();
    @Transient
    private final SimpleStringProperty noticeProperty = new SimpleStringProperty();
    @Transient
    private final SimpleStringProperty bankNameProperty = new SimpleStringProperty();
    @Transient
    private final SimpleStringProperty ibanProperty = new SimpleStringProperty();
    @Transient
    private final SimpleStringProperty kontoNumberProperty = new SimpleStringProperty();
    @Transient
    private final SimpleObjectProperty<BigDecimal> interestRateProperty = new SimpleObjectProperty<>(interestRate);
    @Transient
    private final SimpleStringProperty interestDaysProperty = new SimpleStringProperty();
    @Transient
    private final SimpleObjectProperty<InterestInterval> interestIntervalProperty = new SimpleObjectProperty<>();

    @Override
    @PostLoad
    public void onPostLoadEntity() {
        idProperty.set(id);
        descriptionProperty.set(description);
        typeProperty.set(type);
        stateProperty.set(state);
        currencyProperty.set(currencyCode);
        balanceProperty.set(balance);
        ownerProperty.set(owner);
        portfolioProperty.set(portfolio);
        noticeProperty.set(notice);
        bankNameProperty.set(bankName);
        ibanProperty.set(iban);
        kontoNumberProperty.set(kontoNumber);
        interestRateProperty.set(interestRate);
        interestDaysProperty.set(interestDays);
        interestIntervalProperty.set(interestInterval);
    }

    @Override
    public void onPrePersistOrUpdateOrRemoveEntity() {
        id = idProperty.get();
        description = descriptionProperty.get();
        type = typeProperty.get();
        state = stateProperty.get();
        currencyCode = currencyProperty.get();
        balance = balanceProperty.get();
        owner = ownerProperty.get();
        portfolio = portfolioProperty.get();
        notice = noticeProperty.get();
        bankName = bankNameProperty.get();
        iban = ibanProperty.get();
        kontoNumber = kontoNumberProperty.get();
        interestRate = interestRateProperty.get();
        interestDays = interestDaysProperty.get();
        interestInterval = interestIntervalProperty.get();
        owner.onPrePersistOrUpdateOrRemoveEntity();
        portfolio.onPrePersistOrUpdateOrRemoveEntity();
    }

    @Override
    public BigDecimal getValue(@NonNull AccountService accountService) throws DataAccessException {
        if (Currency.getInstance("EUR").equals(getValueCurrency())) return balanceProperty.get();
        Double latestExchangeCourse = accountService.getLatestExchangeCourse(getValueCurrency());
        return balanceProperty.get().divide(BigDecimal.valueOf(latestExchangeCourse), BigDecimal.ROUND_HALF_DOWN);
    }

    @Override
    public Currency getValueCurrency() {
        return getCurrency();
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", getIban(), getDescription());
    }

    @Override
    public boolean isChanged() {
        return !Objects.equals(id, idProperty.get())
                || !Objects.equals(description, descriptionProperty.get())
                || !Objects.equals(type, typeProperty.get())
                || !Objects.equals(state, stateProperty.get())
                || !Objects.equals(currencyCode, currencyProperty.get())
                || !Objects.equals(balance, balanceProperty.get())
                || !Objects.equals(owner, ownerProperty.get())
                || !Objects.equals(portfolio, portfolioProperty.get())
                || !Objects.equals(notice, noticeProperty.get())
                || !Objects.equals(bankName, bankNameProperty.get())
                || !Objects.equals(iban, ibanProperty.get())
                || !Objects.equals(kontoNumber, kontoNumberProperty.get())
                || !Objects.equals(interestRate, interestRateProperty.get())
                || !Objects.equals(interestDays, interestDaysProperty.get())
                || !Objects.equals(interestInterval, interestIntervalProperty.get());
    }

    @Override
    public void restore() {
        onPostLoadEntity();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Account account = (Account) obj;
        return Objects.equals(idProperty.get(), account.getId());
    }

    // region Getters & Setters
    public void setId(Long id) {
        idProperty.set(id);
    }

    public Long getId() {
        return idProperty.get();
    }

    public String getDescription() {
        return descriptionProperty.get();
    }

    public void setDescription(String description) {
        descriptionProperty.set(description);
    }

    public AccountType getType() {
        return typeProperty.get();
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

    public void setType(AccountType type) {
        typeProperty.set(type);
    }

    public Currency getCurrency() {
        if (currencyProperty.get() == null) return null;
        return Currency.getInstance(currencyProperty.get());
    }

    public void setCurrencyCode(Currency currency) {
        currencyProperty.set(currency.getCurrencyCode());
    }

    public double getBalance() {
        return balanceProperty.get().doubleValue();
    }

    public BigDecimal getBalanceBigDecimal() {
        return balanceProperty.get();
    }

    public void setBalance(double balance) {
        balanceProperty.set(BigDecimal.valueOf(balance).setScale(2, RoundingMode.HALF_DOWN));
    }

    public Owner getOwner() {
        return ownerProperty.get();
    }

    public void setOwner(Owner owner) {
        ownerProperty.set(owner);
    }

    public Portfolio getPortfolio() {
        return portfolioProperty.get();
    }

    public void setPortfolio(Portfolio portfolio) {
        portfolioProperty.set(portfolio);
    }

    public String getNotice() {
        return noticeProperty.get();
    }

    public void setNotice(String notice) {
        noticeProperty.set(notice);
    }

    public String getBankName() {
        return bankNameProperty.get();
    }

    public void setBankName(String bankName) {
        bankNameProperty.set(bankName);
    }

    public String getIban() {
        return ibanProperty.get();
    }

    public void setIban(String iban) {
        ibanProperty.set(iban);
    }

    public String getKontoNumber() {
        return kontoNumberProperty.get();
    }

    public void setKontoNumber(String kontoNumber) {
        kontoNumberProperty.set(kontoNumber);
    }

    public BigDecimal getInterestRateBigDecimal() {
        return interestRateProperty.get();
    }

    public double getInterestRate() {
        return interestRateProperty.get().doubleValue();
    }

    public void setInterestRate(double interestRate) {
        interestRateProperty.set(BigDecimal.valueOf(interestRate).setScale(2, RoundingMode.HALF_DOWN));
    }

    public String getInterestDays() {
        return interestDaysProperty.get();
    }

    public void setInterestDays(String interestDays) {
        interestDaysProperty.set(interestDays);
    }

    public InterestInterval getInterestInterval() {
        return interestIntervalProperty.get();
    }

    public void setInterestInterval(InterestInterval interestInterval) {
        interestIntervalProperty.set(interestInterval);
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
}
