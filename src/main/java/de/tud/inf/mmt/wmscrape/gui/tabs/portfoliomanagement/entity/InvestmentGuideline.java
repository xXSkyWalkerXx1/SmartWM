package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.InvestmentType;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.interfaces.Changable;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
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
public class InvestmentGuideline implements Changable {

    // region Entities as inner-classes
    @Entity
    @Table(name = "anlagen_richtlinie_eintrag")
    public static class Entry implements Changable {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        @Enumerated(EnumType.STRING)
        @Column(name = "investment_type", nullable = false)
        private InvestmentType type;
        @Column(name = "asset_allocation")
        private BigDecimal assetAllocation = BigDecimal.valueOf(0); // %
        @IntRange(from = 1, to = 12)
        @Column(name = "max_riskclass")
        private Integer maxRiskclass = 1;
        @Column(name = "max_volatility")
        private BigDecimal maxVolatility = BigDecimal.valueOf(0); // %, within 1 year
        @Column(name = "performance")
        private BigDecimal performance = BigDecimal.valueOf(0); // %, within 1 year
        @Column(name = "rendite") // 'performance_since_buy'
        private BigDecimal performanceSinceBuy = BigDecimal.valueOf(0); // %, since buy
        @Column(name = "chance_risk_number")
        private BigDecimal chanceRiskNumber = BigDecimal.valueOf(100); // %
        @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
        @JoinColumn(name = "child_entry_id") // parent_entry_id
        private List<Entry> childEntries = new ArrayList<>();

        @Transient
        public final SimpleLongProperty idProperty = new SimpleLongProperty();
        @Transient
        public final SimpleObjectProperty<InvestmentType> typeProperty = new SimpleObjectProperty<>();
        @Transient
        public final SimpleObjectProperty<BigDecimal> assetAllocationProperty = new SimpleObjectProperty<>(assetAllocation);
        @Transient
        public final SimpleObjectProperty<Integer> maxRiskclassProperty = new SimpleObjectProperty<>(maxRiskclass);
        @Transient
        public final SimpleObjectProperty<BigDecimal> maxVolatilityProperty = new SimpleObjectProperty<>(maxVolatility);
        @Transient
        public final SimpleObjectProperty<BigDecimal> performanceProperty = new SimpleObjectProperty<>(performance);
        @Transient
        public final SimpleObjectProperty<BigDecimal> performanceSinceBuyProperty = new SimpleObjectProperty<>(performanceSinceBuy);
        @Transient
        public final SimpleObjectProperty<BigDecimal> chanceRiskNumberProperty = new SimpleObjectProperty<>(chanceRiskNumber);

        @Override
        @PostLoad
        public void onPostLoadEntity() {
            childEntries.forEach(Changable::onPostLoadEntity);
            idProperty.set(id);
            typeProperty.set(type);
            assetAllocationProperty.set(assetAllocation);
            maxRiskclassProperty.set(maxRiskclass);
            maxVolatilityProperty.set(maxVolatility);
            performanceProperty.set(performance);
            performanceSinceBuyProperty.set(performanceSinceBuy);
            chanceRiskNumberProperty.set(chanceRiskNumber);
        }

        @Override
        public void onPrePersistOrUpdateOrRemoveEntity() {
            childEntries.forEach(Changable::onPrePersistOrUpdateOrRemoveEntity);
            id = idProperty.get();
            type = typeProperty.get();
            assetAllocation = assetAllocationProperty.get();
            maxRiskclass = maxRiskclassProperty.get();
            maxVolatility = maxVolatilityProperty.get();
            performance = performanceProperty.get();
            performanceSinceBuy = performanceSinceBuyProperty.get();
            chanceRiskNumber = chanceRiskNumberProperty.get();
        }

        @Override
        public String toString(){
            return type.toString();
        }

        @Override
        public boolean isChanged() {
            return childEntries.stream().anyMatch(Changable::isChanged)
                    || !Objects.equals(id, idProperty.get())
                    || !Objects.equals(type, typeProperty.get())
                    || !Objects.equals(assetAllocation, assetAllocationProperty.get())
                    || !Objects.equals(maxRiskclass, maxRiskclassProperty.get())
                    || !Objects.equals(maxVolatility, maxVolatilityProperty.get())
                    || !Objects.equals(performance, performanceProperty.get())
                    || !Objects.equals(performanceSinceBuy, performanceSinceBuyProperty.get())
                    || !Objects.equals(chanceRiskNumber, chanceRiskNumberProperty.get());
        }

        @Override
        public void restore() {
            onPostLoadEntity();
            childEntries.forEach(Changable::restore);
        }

        // region Getters & Setters
        public Long getId() {
            return idProperty.get();
        }

        public void setId(Long id) {
            idProperty.set(id);
        }

        public InvestmentType getType() {
            return typeProperty.get();
        }

        public void setType(InvestmentType type) {
            typeProperty.set(type);
        }

        public float getAssetAllocation() {
            return assetAllocationProperty.get().floatValue();
        }

        public void setAssetAllocation(float assetAllocation) {
            assetAllocationProperty.set(BigDecimal.valueOf(assetAllocation).setScale(2,RoundingMode.HALF_DOWN));
        }

        public Integer getMaxRiskclass() {
            return maxRiskclassProperty.get();
        }

        public void setMaxRiskclass(Integer maxRiskclass) {
            maxRiskclassProperty.set(maxRiskclass);
            childEntries.forEach(entry -> entry.setMaxRiskclass(maxRiskclass));
        }

        public float getMaxVolatility() {
            return maxVolatilityProperty.get().floatValue();
        }

        public void setMaxVolatility(float maxVolatility) {
            maxVolatilityProperty.set(BigDecimal.valueOf(maxVolatility).setScale(2,RoundingMode.HALF_DOWN));
            childEntries.forEach(entry -> entry.setMaxVolatility(maxVolatility));
        }

        public float getPerformance() {
            return performanceProperty.get().floatValue();
        }

        public void setPerformance(float performance) {
            performanceProperty.set(BigDecimal.valueOf(performance).setScale(2,RoundingMode.HALF_DOWN));
            childEntries.forEach(entry -> entry.setPerformance(performance));
        }

        public float getPerformanceSinceBuy() {
            return performanceSinceBuyProperty.get().floatValue();
        }

        public void setPerformanceSinceBuy(float performanceSinceBuy) {
            performanceSinceBuyProperty.set(BigDecimal.valueOf(performanceSinceBuy).setScale(2,RoundingMode.HALF_DOWN));
            childEntries.forEach(entry -> entry.setPerformanceSinceBuy(performanceSinceBuy));
        }

        public float getChanceRiskNumber() {
            return chanceRiskNumberProperty.get().floatValue();
        }

        public void setChanceRiskNumber(float chanceRiskNumber) {
            chanceRiskNumberProperty.set(BigDecimal.valueOf(chanceRiskNumber).setScale(2,RoundingMode.HALF_DOWN));
            childEntries.forEach(entry -> entry.setChanceRiskNumber(chanceRiskNumber));
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
    public static class DivisionByLocation implements Changable {

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

        @Transient
        public final SimpleLongProperty idProperty = new SimpleLongProperty();
        @Transient
        public final SimpleObjectProperty<BigDecimal> germanyProperty = new SimpleObjectProperty<>(germany);
        @Transient
        public final SimpleObjectProperty<BigDecimal> europeWithoutBrdProperty = new SimpleObjectProperty<>(europe_without_brd);
        @Transient
        public final SimpleObjectProperty<BigDecimal> northAmericaWithUsaProperty = new SimpleObjectProperty<>(northamerica_with_usa);
        @Transient
        public final SimpleObjectProperty<BigDecimal> asiaWithoutChinaProperty = new SimpleObjectProperty<>(asia_without_china);
        @Transient
        public final SimpleObjectProperty<BigDecimal> chinaProperty = new SimpleObjectProperty<>(china);
        @Transient
        public final SimpleObjectProperty<BigDecimal> japanProperty = new SimpleObjectProperty<>(japan);
        @Transient
        public final SimpleObjectProperty<BigDecimal> emergineMarketsProperty = new SimpleObjectProperty<>(emergine_markets);

        @Override
        @PostLoad
        public void onPostLoadEntity() {
            idProperty.set(id);
            germanyProperty.set(germany);
            europeWithoutBrdProperty.set(europe_without_brd);
            northAmericaWithUsaProperty.set(northamerica_with_usa);
            asiaWithoutChinaProperty.set(asia_without_china);
            chinaProperty.set(china);
            japanProperty.set(japan);
            emergineMarketsProperty.set(emergine_markets);
        }

        @Override
        public void onPrePersistOrUpdateOrRemoveEntity() {
            id = idProperty.get();
            germany = germanyProperty.get();
            europe_without_brd = europeWithoutBrdProperty.get();
            northamerica_with_usa = northAmericaWithUsaProperty.get();
            asia_without_china = asiaWithoutChinaProperty.get();
            china = chinaProperty.get();
            japan = japanProperty.get();
            emergine_markets = emergineMarketsProperty.get();
        }

        @Override
        public boolean isChanged() {
            return !Objects.equals(id, idProperty.get())
                    || !Objects.equals(europe_without_brd, europeWithoutBrdProperty.get())
                    || !Objects.equals(northamerica_with_usa, northAmericaWithUsaProperty.get())
                    || !Objects.equals(asia_without_china, asiaWithoutChinaProperty.get())
                    || !Objects.equals(china, chinaProperty.get())
                    || !Objects.equals(japan, japanProperty.get())
                    || !Objects.equals(emergine_markets, emergineMarketsProperty.get());
        }

        @Override
        public void restore() {
            onPostLoadEntity();
        }

        public Long getId() {
            return idProperty.get();
        }

        public void setId(Long id) {
            idProperty.set(id);
        }

        public float getGermany() {
            return germanyProperty.get().floatValue();
        }

        public void setGermany(float germany) {
            germanyProperty.set(BigDecimal.valueOf(germany).setScale(2,RoundingMode.HALF_DOWN));
        }

        public float getEurope_without_brd() {
            return europeWithoutBrdProperty.get().floatValue();
        }

        public void setEurope_without_brd(float europe_without_brd) {
            europeWithoutBrdProperty.set(BigDecimal.valueOf(europe_without_brd).setScale(2,RoundingMode.HALF_DOWN));
        }

        public float getNorthamerica_with_usa() {
            return northAmericaWithUsaProperty.get().floatValue();
        }

        public void setNorthamerica_with_usa(float northamerica_with_usa) {
            northAmericaWithUsaProperty.set(BigDecimal.valueOf(northamerica_with_usa).setScale(2,RoundingMode.HALF_DOWN));
        }

        public float getAsia_without_china() {
            return asiaWithoutChinaProperty.get().floatValue();
        }

        public void setAsia_without_china(float asia_without_china) {
            asiaWithoutChinaProperty.set(BigDecimal.valueOf(asia_without_china).setScale(2,RoundingMode.HALF_DOWN));
        }

        public float getChina() {
            return chinaProperty.get().floatValue();
        }

        public void setChina(float china) {
            chinaProperty.set(BigDecimal.valueOf(china).setScale(2,RoundingMode.HALF_DOWN));
        }

        public float getJapan() {
            return japanProperty.get().floatValue();
        }

        public void setJapan(float japan) {
            japanProperty.set(BigDecimal.valueOf(japan).setScale(2,RoundingMode.HALF_DOWN));
        }

        public float getEmergine_markets() {
            return emergineMarketsProperty.get().floatValue();
        }

        public void setEmergine_markets(float emergine_markets) {
            emergineMarketsProperty.set(BigDecimal.valueOf(emergine_markets).setScale(2,RoundingMode.HALF_DOWN));
        }

        public float getSum() {
            return getGermany()
                    + getEurope_without_brd()
                    + getNorthamerica_with_usa()
                    + getAsia_without_china()
                    + getChina()
                    + getJapan()
                    + getEmergine_markets();
        }
    }

    @Entity
    @Table(name = "anlagen_richtlinie_unterteilung_währung")
    public static class DivisionByCurrency implements Changable {

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

        @Transient
        public final SimpleLongProperty idProperty = new SimpleLongProperty();
        @Transient
        public final SimpleObjectProperty<BigDecimal> euroProperty = new SimpleObjectProperty<>(euro);
        @Transient
        public final SimpleObjectProperty<BigDecimal> usdProperty = new SimpleObjectProperty<>(usd);
        @Transient
        public final SimpleObjectProperty<BigDecimal> chfProperty = new SimpleObjectProperty<>(chf);
        @Transient
        public final SimpleObjectProperty<BigDecimal> gbpProperty = new SimpleObjectProperty<>(gbp);
        @Transient
        public final SimpleObjectProperty<BigDecimal> yenProperty = new SimpleObjectProperty<>(yen);
        @Transient
        public final SimpleObjectProperty<BigDecimal> asiaCurrenciesProperty = new SimpleObjectProperty<>(asia_currencies);
        @Transient
        public final SimpleObjectProperty<BigDecimal> othersProperty = new SimpleObjectProperty<>(others);

        @Override
        @PostLoad
        public void onPostLoadEntity() {
            idProperty.set(id);
            euroProperty.set(euro);
            usdProperty.set(usd);
            chfProperty.set(chf);
            gbpProperty.set(gbp);
            yenProperty.set(yen);
            asiaCurrenciesProperty.set(asia_currencies);
            othersProperty.set(others);
        }

        @Override
        public void onPrePersistOrUpdateOrRemoveEntity() {
            id = idProperty.get();
            euro = euroProperty.get();
            usd = usdProperty.get();
            chf = chfProperty.get();
            gbp = gbpProperty.get();
            yen = yenProperty.get();
            asia_currencies = asiaCurrenciesProperty.get();
            others = othersProperty.get();
        }

        @Override
        public boolean isChanged() {
            return !Objects.equals(id, idProperty.get())
                    || !Objects.equals(euro, euroProperty.get())
                    || !Objects.equals(usd, usdProperty.get())
                    || !Objects.equals(chf, chfProperty.get())
                    || !Objects.equals(gbp, gbpProperty.get())
                    || !Objects.equals(yen, yenProperty.get())
                    || !Objects.equals(asia_currencies, asiaCurrenciesProperty.get())
                    || !Objects.equals(others, othersProperty.get());
        }

        @Override
        public void restore() {
            onPostLoadEntity();
        }

        public Long getId() {
            return idProperty.get();
        }

        public void setId(Long id) {
            idProperty.set(id);
        }

        public float getEuro() {
            return euroProperty.get().floatValue();
        }

        public void setEuro(float euro) {
            euroProperty.set(BigDecimal.valueOf(euro).setScale(2, RoundingMode.HALF_DOWN));
        }

        public float getUsd() {
            return usdProperty.get().floatValue();
        }

        public void setUsd(float usd) {
            usdProperty.set(BigDecimal.valueOf(usd).setScale(2, RoundingMode.HALF_DOWN));
        }

        public float getChf() {
            return chfProperty.get().floatValue();
        }

        public void setChf(float chf) {
            chfProperty.set(BigDecimal.valueOf(chf).setScale(2, RoundingMode.HALF_DOWN));
        }

        public float getGbp() {
            return gbpProperty.get().floatValue();
        }

        public void setGbp(float gbp) {
            gbpProperty.set(BigDecimal.valueOf(gbp).setScale(2, RoundingMode.HALF_DOWN));
        }

        public float getYen() {
            return yenProperty.get().floatValue();
        }

        public void setYen(float yen) {
            yenProperty.set(BigDecimal.valueOf(yen).setScale(2, RoundingMode.HALF_DOWN));
        }

        public float getAsiaCurrencies() {
            return asiaCurrenciesProperty.get().floatValue();
        }

        public void setAsia_currencies(float asia_currencies) {
            asiaCurrenciesProperty.set(BigDecimal.valueOf(asia_currencies).setScale(2, RoundingMode.HALF_DOWN));
        }

        public float getOthers() {
            return othersProperty.get().floatValue();
        }

        public void setOthers(float others) {
            othersProperty.set(BigDecimal.valueOf(others).setScale(2, RoundingMode.HALF_DOWN));
        }

        public float getSum() {
            return getEuro() + getUsd() + getChf() + getGbp() + getYen() + getAsiaCurrencies() + getOthers();
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

    @Transient
    private final SimpleLongProperty idProperty = new SimpleLongProperty();

    @Override
    @PostLoad
    public void onPostLoadEntity() {
        divisionByLocation.onPostLoadEntity();
        divisionByCurrency.onPostLoadEntity();
        idProperty.set(id);
    }

    @Override
    public void onPrePersistOrUpdateOrRemoveEntity() {
        id = idProperty.get();
        entries.forEach(Changable::onPrePersistOrUpdateOrRemoveEntity);
        divisionByLocation.onPrePersistOrUpdateOrRemoveEntity();
        divisionByCurrency.onPrePersistOrUpdateOrRemoveEntity();
    }

    @Override
    public boolean isChanged() {
        return entries.stream().anyMatch(Changable::isChanged)
                || divisionByLocation.isChanged()
                || divisionByCurrency.isChanged();
    }

    @Override
    public void restore() {
        onPostLoadEntity();
        entries.forEach(Changable::restore);
        divisionByLocation.restore();
        divisionByCurrency.restore();
    }

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
        return idProperty.get();
    }

    public void setId(Long id) {
        idProperty.set(id);
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
