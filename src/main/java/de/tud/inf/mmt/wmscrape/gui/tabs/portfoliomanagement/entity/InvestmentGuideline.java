package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity;

import javax.persistence.*;
import java.util.List;

/**
 * Anlagen-Richtlinie
 */
@Entity
@Table(name = "anlagen_richtlinie")
public class InvestmentGuideline {

    // region Entities as inner-classes
    @Entity
    @Table(name = "anlagen_richtlinie_eintrag")
    public static class Entry {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
    }

    @Entity
    @Table(name = "anlagen_richtlinie_unterteilung_ort")
    public static class DivisionByLocation {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private float germany;
        private float europe_without_brd;
        private float northamerica_with_usa;
        private float asia_without_china;
        private float china;
        private float japan;
        private float emergine_markets;

        public Long getId() {
            return id;
        }

        public float getGermany() {
            return germany;
        }

        public void setGermany(float germany) {
            this.germany = germany;
        }

        public float getEurope_without_brd() {
            return europe_without_brd;
        }

        public void setEurope_without_brd(float europe_without_brd) {
            this.europe_without_brd = europe_without_brd;
        }

        public float getNorthamerica_with_usa() {
            return northamerica_with_usa;
        }

        public void setNorthamerica_with_usa(float northamerica_with_usa) {
            this.northamerica_with_usa = northamerica_with_usa;
        }

        public float getAsia_without_china() {
            return asia_without_china;
        }

        public void setAsia_without_china(float asia_without_china) {
            this.asia_without_china = asia_without_china;
        }

        public float getChina() {
            return china;
        }

        public void setChina(float china) {
            this.china = china;
        }

        public float getJapan() {
            return japan;
        }

        public void setJapan(float japan) {
            this.japan = japan;
        }

        public float getEmergine_markets() {
            return emergine_markets;
        }

        public void setEmergine_markets(float emergine_markets) {
            this.emergine_markets = emergine_markets;
        }
    }

    @Entity
    @Table(name = "anlagen_richtlinie_unterteilung_währung")
    public static class DivisionByCurrency {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private float euro;
        private float usd;
        private float chf; // Schweizer Franken
        private float gbp; // Britische Pfund
        private float yen; // Japanischer Yen
        private float asia_currencies;
        private float others;

        public Long getId() {
            return id;
        }

        public float getEuro() {
            return euro;
        }

        public void setEuro(float euro) {
            this.euro = euro;
        }

        public float getUsd() {
            return usd;
        }

        public void setUsd(float usd) {
            this.usd = usd;
        }

        public float getChf() {
            return chf;
        }

        public void setChf(float chf) {
            this.chf = chf;
        }

        public float getGbp() {
            return gbp;
        }

        public void setGbp(float gbp) {
            this.gbp = gbp;
        }

        public float getYen() {
            return yen;
        }

        public void setYen(float yen) {
            this.yen = yen;
        }

        public float getAsiaCurrencies() {
            return asia_currencies;
        }

        public void setAsia_currencies(float asia_currencies) {
            this.asia_currencies = asia_currencies;
        }

        public float getOthers() {
            return others;
        }

        public void setOthers(float others) {
            this.others = others;
        }
    }
    // endregion

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ToDo: implement!
    @Transient
    private List<Entry> entries;

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "division_by_location_id")
    private DivisionByLocation divisionByLocation = new DivisionByLocation();

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "division_by_currency_id")
    private DivisionByCurrency divisionByCurrency = new DivisionByCurrency();

    // region Getters & Setters
    public Long getId() {
        return id;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }

    public DivisionByLocation getDivisionByLocation() {
        return divisionByLocation;
    }

    public DivisionByCurrency getDivisionByCurrency() {
        return divisionByCurrency;
    }
    // endregion
}
