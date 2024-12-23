package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.InvestmentType;
import org.checkerframework.common.value.qual.IntRange;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

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
        private BigDecimal assetAllocation = BigDecimal.valueOf(0); // %

        /**
         * Is 0 (means an invalid risk-class), if {@code type} is a child.
         */
        @IntRange(from = 1, to = 12)
        @Column(name = "max_riskclass")
        private int maxRiskclass = 1;

        @Column(name = "max_volatility")
        private BigDecimal maxVolatility = BigDecimal.valueOf(0); // %, within 1 year

        @Column(name = "performance")
        private BigDecimal performance = BigDecimal.valueOf(0); // %, within 1 year

        @Column(name = "rendite")
        private BigDecimal rendite = BigDecimal.valueOf(0); // %, since buy

        @Column(name = "chance_risk_number")
        private BigDecimal chanceRiskNumber = BigDecimal.valueOf(100); // %

        @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
        @JoinColumn(name = "child_entry_id") // ToDo: rename to parent_entry_id
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
            return assetAllocation.floatValue();
        }

        public void setAssetAllocation(float assetAllocation) {
            this.assetAllocation = BigDecimal.valueOf(assetAllocation).setScale(2,RoundingMode.HALF_DOWN);
        }

        public int getMaxRiskclass() {
            return maxRiskclass;
        }

        public void setMaxRiskclass(int maxRiskclass) {
            this.maxRiskclass = maxRiskclass;
        }

        public float getMaxVolatility() {
            return maxVolatility.floatValue();
        }

        public void setMaxVolatility(float maxVolatility) {
            this.maxVolatility = BigDecimal.valueOf(maxVolatility).setScale(2,RoundingMode.HALF_DOWN);
        }

        public float getPerformance() {
            return performance.floatValue();
        }

        public void setPerformance(float performance) {
            this.performance = BigDecimal.valueOf(performance).setScale(2,RoundingMode.HALF_DOWN);
        }

        public float getRendite() {
            return rendite.floatValue();
        }

        public void setRendite(float rendite) {
            this.rendite = BigDecimal.valueOf(rendite).setScale(2,RoundingMode.HALF_DOWN);
        }

        public float getChanceRiskNumber() {
            return chanceRiskNumber.floatValue();
        }

        public void setChanceRiskNumber(float chanceRiskNumber) {
            this.chanceRiskNumber = BigDecimal.valueOf(chanceRiskNumber).setScale(2,RoundingMode.HALF_DOWN);
        }

        public List<Entry> getChildEntries() {
            return childEntries.stream().toList();
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

        private BigDecimal germany = BigDecimal.valueOf(0);
        private BigDecimal europe_without_brd = BigDecimal.valueOf(0);
        private BigDecimal northamerica_with_usa = BigDecimal.valueOf(0);
        private BigDecimal asia_without_china = BigDecimal.valueOf(0);
        private BigDecimal china = BigDecimal.valueOf(0);
        private BigDecimal japan = BigDecimal.valueOf(0);
        private BigDecimal emergine_markets = BigDecimal.valueOf(0);

        public Long getId() {
            return id;
        }

        public float getGermany() {
            return germany.floatValue();
        }

        public void setGermany(float germany) {
            this.germany = BigDecimal.valueOf(germany).setScale(2,RoundingMode.HALF_DOWN);
        }

        public float getEurope_without_brd() {
            return europe_without_brd.floatValue();
        }

        public void setEurope_without_brd(float europe_without_brd) {
            this.europe_without_brd = BigDecimal.valueOf(europe_without_brd).setScale(2,RoundingMode.HALF_DOWN);
        }

        public float getNorthamerica_with_usa() {
            return northamerica_with_usa.floatValue();
        }

        public void setNorthamerica_with_usa(float northamerica_with_usa) {
            this.northamerica_with_usa = BigDecimal.valueOf(northamerica_with_usa).setScale(2,RoundingMode.HALF_DOWN);
        }

        public float getAsia_without_china() {
            return asia_without_china.floatValue();
        }

        public void setAsia_without_china(float asia_without_china) {
            this.asia_without_china = BigDecimal.valueOf(asia_without_china).setScale(2,RoundingMode.HALF_DOWN);
        }

        public float getChina() {
            return china.floatValue();
        }

        public void setChina(float china) {
            this.china = BigDecimal.valueOf(china).setScale(2,RoundingMode.HALF_DOWN);
        }

        public float getJapan() {
            return japan.floatValue();
        }

        public void setJapan(float japan) {
            this.japan = BigDecimal.valueOf(japan).setScale(2,RoundingMode.HALF_DOWN);
        }

        public float getEmergine_markets() {
            return emergine_markets.floatValue();
        }

        public void setEmergine_markets(float emergine_markets) {
            this.emergine_markets = BigDecimal.valueOf(emergine_markets).setScale(2,RoundingMode.HALF_DOWN);
        }

        public float getSum() {
            return germany
                    .add(europe_without_brd)
                    .add(northamerica_with_usa)
                    .add(asia_without_china)
                    .add(china)
                    .add(japan)
                    .add(emergine_markets)
                    .floatValue();
        }
    }

    @Entity
    @Table(name = "anlagen_richtlinie_unterteilung_währung")
    public static class DivisionByCurrency {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private BigDecimal euro = BigDecimal.valueOf(0);
        private BigDecimal usd = BigDecimal.valueOf(0);
        private BigDecimal chf = BigDecimal.valueOf(0); // Schweizer Franken
        private BigDecimal gbp = BigDecimal.valueOf(0); // Britische Pfund
        private BigDecimal yen = BigDecimal.valueOf(0); // Japanischer Yen
        private BigDecimal asia_currencies = BigDecimal.valueOf(0);
        private BigDecimal others = BigDecimal.valueOf(0);

        public Long getId() {
            return id;
        }

        public float getEuro() {
            return euro.floatValue();
        }

        public void setEuro(float euro) {
            this.euro = BigDecimal.valueOf(euro).setScale(2, RoundingMode.HALF_DOWN);
            ;
        }

        public float getUsd() {
            return usd.floatValue();
        }

        public void setUsd(float usd) {
            this.usd = BigDecimal.valueOf(usd).setScale(2, RoundingMode.HALF_DOWN);
            ;
        }

        public float getChf() {
            return chf.floatValue();
        }

        public void setChf(float chf) {
            this.chf = BigDecimal.valueOf(chf).setScale(2, RoundingMode.HALF_DOWN);
            ;
        }

        public float getGbp() {
            return gbp.floatValue();
        }

        public void setGbp(float gbp) {
            this.gbp = BigDecimal.valueOf(gbp).setScale(2, RoundingMode.HALF_DOWN);
            ;
        }

        public float getYen() {
            return yen.floatValue();
        }

        public void setYen(float yen) {
            this.yen = BigDecimal.valueOf(yen).setScale(2, RoundingMode.HALF_DOWN);
            ;
        }

        public float getAsiaCurrencies() {
            return asia_currencies.floatValue();
        }

        public void setAsia_currencies(float asia_currencies) {
            this.asia_currencies = BigDecimal.valueOf(asia_currencies).setScale(2, RoundingMode.HALF_DOWN);
            ;
        }

        public float getOthers() {
            return others.floatValue();
        }

        public void setOthers(float others) {
            this.others = BigDecimal.valueOf(others).setScale(2, RoundingMode.HALF_DOWN);
            ;
        }

        public float getSum() {
            return euro
                    .add(usd)
                    .add(chf)
                    .add(gbp)
                    .add(yen)
                    .add(asia_currencies)
                    .add(others)
                    .floatValue();
        }
    }
    // endregion

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "entry_id") // ToDo: rename to investment_guideline_id
    private Set<Entry> entries = new HashSet<>();

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
                parentEntry.setMaxRiskclass(1);

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
        return entries.stream()
                .sorted(Comparator.comparingInt(entry -> entry.getType().ordinal()))
                .toList();
    }

    public DivisionByLocation getDivisionByLocation() {
        return divisionByLocation;
    }

    public DivisionByCurrency getDivisionByCurrency() {
        return divisionByCurrency;
    }
    // endregion
}
