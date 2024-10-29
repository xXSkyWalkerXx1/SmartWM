package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity;

import javax.persistence.*;

@Entity
@Table(name = "portfolio")
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
