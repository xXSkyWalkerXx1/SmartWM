package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.State;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "portfolio")
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
}
