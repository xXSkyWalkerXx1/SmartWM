package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity;

import javax.persistence.*;

/**
 * Konto
 */
@Entity
@Table(name = "konto")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
