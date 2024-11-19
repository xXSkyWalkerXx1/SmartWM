package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.InvestmentType;
import org.checkerframework.common.value.qual.IntRange;

import javax.persistence.*;
import java.util.ArrayList;
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

        @Enumerated(EnumType.STRING)
        @Column(name = "investment_type", nullable = false)
        private InvestmentType type;

        @Column(name = "asset_allocation")
        private float assetAllocation; // %

        @IntRange(from = 1, to = 12)
        @Column(name = "max_riskclass")
        private int maxRiskclass;

        @Column(name = "max_volatility")
        private float maxVolatility; // %, within 1 year

        @Column(name = "performance")
        private float performance; // %, within 1 year

        @Column(name = "rendite")
        private float rendite; // %, since buy

        @Column(name = "chance_risk_number")
        private float chanceRiskNumber; // %

        @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
        @JoinColumn(name = "child_entry_id")
        private List<Entry> childEntries = new ArrayList<>();

        @Override
        public String toString(){
            return type.toString();
        }

        // region Getters & Setters
        public Long getId() {
            return id;
        }

        public InvestmentType getType() {
            return type;
        }

        public void setType(InvestmentType type) {
            this.type = type;
        }

        public float getAssetAllocation() {
            return assetAllocation;
        }

        public void setAssetAllocation(float assetAllocation) {
            this.assetAllocation = assetAllocation;
        }

        public int getMaxRiskclass() {
            return maxRiskclass;
        }

        public void setMaxRiskclass(int maxRiskclass) {
            this.maxRiskclass = maxRiskclass;
        }

        public float getMaxVolatility() {
            return maxVolatility;
        }

        public void setMaxVolatility(float maxVolatility) {
            this.maxVolatility = maxVolatility;
        }

        public float getPerformance() {
            return performance;
        }

        public void setPerformance(float performance) {
            this.performance = performance;
        }

        public float getRendite() {
            return rendite;
        }

        public void setRendite(float rendite) {
            this.rendite = rendite;
        }

        public float getChanceRiskNumber() {
            return chanceRiskNumber;
        }

        public void setChanceRiskNumber(float chanceRiskNumber) {
            this.chanceRiskNumber = chanceRiskNumber;
        }

        public List<Entry> getChildEntries() {
            return childEntries;
        }

        public void addChildEntry(Entry childEntry) {
            this.childEntries.add(childEntry);
        }
        // endregion
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

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "entry_id")
    private List<Entry> entries = new ArrayList<>();

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "division_by_location_id")
    private DivisionByLocation divisionByLocation = new DivisionByLocation();

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "division_by_currency_id")
    private DivisionByCurrency divisionByCurrency = new DivisionByCurrency();

    /**
     * Creates initial entries for the investment guideline.
     */
    public void initializeEntries() {
        if (!entries.isEmpty()) throw new IllegalStateException("Entries are already initialized!");

        for (InvestmentType type : InvestmentType.values()) {
            Entry parentEntry;

            if (!type.isChild()) { // is parent
                parentEntry = new Entry();
                parentEntry.setType(type);

                for (InvestmentType childType : type.getChilds()) {
                    var childEntry = new Entry();
                    childEntry.setType(childType);
                    parentEntry.addChildEntry(childEntry);
                }
                entries.add(parentEntry);
            }
        }
    }

    // region Getters & Setters
    public Long getId() {
        return id;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public DivisionByLocation getDivisionByLocation() {
        return divisionByLocation;
    }

    public DivisionByCurrency getDivisionByCurrency() {
        return divisionByCurrency;
    }
    // endregion
}
