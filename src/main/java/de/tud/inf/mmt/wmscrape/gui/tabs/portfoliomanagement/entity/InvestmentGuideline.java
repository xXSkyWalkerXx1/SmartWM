package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity;

import javax.persistence.*;

/**
 * Anlagen-Richtlinie
 */
@Entity
@Table(name = "anlagen_richtlinie")
public class InvestmentGuideline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

}
