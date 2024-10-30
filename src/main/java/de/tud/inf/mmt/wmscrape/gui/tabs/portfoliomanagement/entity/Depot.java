package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.interfaces.Valuable;
import org.apache.poi.ss.formula.eval.NotImplementedFunctionException;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "depot")
public class Depot implements Valuable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    @OneToMany
    @JoinColumn(name = "billing_account_id", nullable = false)
    private List<Account> billingAccounts = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "commission_scheme_id", nullable = false)
    private CommissionScheme commissionScheme;

    // region Getters & Setters
    @Override
    public BigDecimal getValue() {
        throw new NotImplementedFunctionException("Muss noch implementiert werden");
    }

    public Long getId() {
        return id;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
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
    // endregion
}
