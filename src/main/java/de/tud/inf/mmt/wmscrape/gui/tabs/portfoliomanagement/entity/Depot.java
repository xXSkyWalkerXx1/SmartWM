package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity;

import javax.persistence.*;

@Entity
@Table(name = "depot")
public class Depot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
