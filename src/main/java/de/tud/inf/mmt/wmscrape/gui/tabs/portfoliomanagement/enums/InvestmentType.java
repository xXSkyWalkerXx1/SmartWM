package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums;

import org.springframework.lang.NonNull;

import java.util.List;

public enum InvestmentType {
    LIQUIDITY("Liquidität"),
    FIXED_DEPOSIT_AND_MONEY_MARKET_FUNDS("Festgeld- & Geldmarktfonds"),

    BOND("Anleihen"),
    BOND_ETF("Renten-ETF"),
    ACTIVE_BONDFONDS("Aktive Rentenfonds"),
    BOND_AND_BONDFUNDS("Anleihen- & Rentenfonds", BOND, BOND_ETF, ACTIVE_BONDFONDS),

    SINGLE_STOCK("Einzelaktien"),
    EQUITY_ETF("Aktien-ETF"),
    ACTIVE_EQUITYFUNDS("Aktive Aktienfonds"),
    EQUITY_AND_EQUITYFUNDS("Aktien- & Aktienfonds", SINGLE_STOCK, EQUITY_ETF, ACTIVE_EQUITYFUNDS),

    MIXED_ETF("Misch-ETFs"),
    ACTIVE_MIXED_FUNDS("Aktive Mischfonds"),
    MIXED_FUNDS("Mischfonds", MIXED_ETF, ACTIVE_MIXED_FUNDS),

    GOLD_ETC("Gold-ETCs"),
    SILVER_ETC("Silber-ETCs"),
    PRECIOUS_METALS("Edelmetalle", GOLD_ETC, SILVER_ETC),

    COMMODITY_ETF("Rohstoff-ETFs"),
    COMMODITY_ETC("Rohstoff-ETCs"),
    COMMODITIES("Rohstoffe", COMMODITY_ETF, COMMODITY_ETC),

    OPEN_REAL_ESTATE_FUNDS("Offene Immobilienfonds"),
    CLOSED_REAL_ESTATE_FUNDS("Geschlossene Immobilienfonds"),
    PHYSICAL_REAL_ESTATE("Physische Immobilien"),
    REAL_ESTATE("Immobilien", OPEN_REAL_ESTATE_FUNDS, CLOSED_REAL_ESTATE_FUNDS, PHYSICAL_REAL_ESTATE),

    HEDGE_FUNDS("Hedgefonds"),
    PRIVATE_EQUITY("Private Equity"),
    ZERTIFIKATE("Zertifikate"),
    OPTIONSSCHEINE("Optionsscheine"),
    ALTERNATIVE_INVESTMENTS("Alternative Anlagen", HEDGE_FUNDS, PRIVATE_EQUITY, ZERTIFIKATE, OPTIONSSCHEINE);

    private final String displayText;
    private final List<InvestmentType> childs;

    InvestmentType(@NonNull String displayText, @NonNull InvestmentType... child) {
        this.displayText = displayText;
        this.childs = List.of(child);
    }

    @Override
    public String toString() {
        return displayText;
    }

    public List<InvestmentType> getChilds() {
        return childs;
    }

    public boolean isChild() {
        return childs.isEmpty();
    }
}
