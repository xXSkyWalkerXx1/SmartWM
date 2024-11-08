package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.AccountType;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.InterestInterval;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.State;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.interfaces.Valuable;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;

/**
 * Konto
 */
@Entity(name = "pAccount")
@Table(name = "pkonto")
public class Account extends FinancialAsset {

    @Id
    @Column(name = "iban", nullable = false)
    private String iban;

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
    private double balance;

    @OneToOne
    @JoinColumn(name = "owner_id")
    private Owner owner;

    @Column(name = "notice")
    private String notice;

    @Column(name = "bank_name", nullable = false)
    private String bankName;

    @Column(name = "konto_number", nullable = false)
    private String kontoNumber;

    @Column(name = "interest_rate", nullable = false)
    private double interestRate; // Zinssatz

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

    @Override
    public BigDecimal getValue() {
        return BigDecimal.valueOf(balance);
    }

    @Override
    public String toString() {
        return iban;
    }

    // region Getters & Setters
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
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public Currency getCurrency() {
        return Currency.getInstance(currencyCode);
    }

    public void setCurrencyCode(Currency currency) {
        this.currencyCode = currency.getCurrencyCode();
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
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

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
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
    // endregion
}
