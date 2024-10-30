package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.State;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "inhaber")
public class Owner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private State state;

    @Column(name = "forename", nullable = false)
    private String forename;

    @Column(name = "aftername", nullable = false)
    private String aftername;

    @Column(name = "notice")
    private String notice;

    @Column(name = "created_at", nullable = false)
    private String createdAt;

    @Column(name = "deactivated_at")
    private String deactivatedAt;

    @OneToOne
    @JoinColumn(name = "address")
    private Address address;

    @OneToOne
    @JoinColumn(name = "tax_information_id") // creates foreign-key
    private TaxInformation taxInformation;

    @OneToMany(mappedBy = "owner")
    private List<Portfolio> portfolios;

    public Long getId() {
        return id;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getForename() {
        return forename;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public String getAftername() {
        return aftername;
    }

    public void setAftername(String aftername) {
        this.aftername = aftername;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
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

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public TaxInformation getTaxInformation() {
        return taxInformation;
    }

    public void setTaxInformation(TaxInformation taxInformation) {
        this.taxInformation = taxInformation;
    }

    public List<Portfolio> getPortfolios() {
        return portfolios;
    }
}
