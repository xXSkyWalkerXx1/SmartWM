package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums;

import org.springframework.lang.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum InvestmentType {
    LIQUIDITY("Liquidität", true),
    FIXED_DEPOSIT_AND_MONEY_MARKET_FUNDS("Festgeld- & Geldmarktfonds", true),

    BOND("Anleihen", false),
    BOND_ETF("Renten-ETF", false),
    ACTIVE_BONDFONDS("Aktive Rentenfonds", false),
    BOND_AND_BONDFUNDS("Anleihen- & Rentenfonds", true, BOND, BOND_ETF, ACTIVE_BONDFONDS),

    SINGLE_STOCK("Einzelaktien", false),
    EQUITY_ETF("Aktien-ETF", false),
    ACTIVE_EQUITYFUNDS("Aktive Aktienfonds", false),
    EQUITY_AND_EQUITYFUNDS("Aktien- & Aktienfonds", true, SINGLE_STOCK, EQUITY_ETF, ACTIVE_EQUITYFUNDS),

    MIXED_ETF("Misch-ETFs", false),
    ACTIVE_MIXED_FUNDS("Aktive Mischfonds", false),
    MIXED_FUNDS("Mischfonds", true, MIXED_ETF, ACTIVE_MIXED_FUNDS),

    GOLD_ETC("Gold-ETCs", false),
    SILVER_ETC("Silber-ETCs", false),
    PRECIOUS_METALS("Edelmetalle", true, GOLD_ETC, SILVER_ETC),

    COMMODITY_ETF("Rohstoff-ETFs", false),
    COMMODITY_ETC("Rohstoff-ETCs", false),
    COMMODITIES("Rohstoffe", true, COMMODITY_ETF, COMMODITY_ETC),

    OPEN_REAL_ESTATE_FUNDS("Offene Immobilienfonds", false),
    CLOSED_REAL_ESTATE_FUNDS("Geschlossene Immobilienfonds", false),
    PHYSICAL_REAL_ESTATE("Physische Immobilien", false),
    REAL_ESTATE("Immobilien", true, OPEN_REAL_ESTATE_FUNDS, CLOSED_REAL_ESTATE_FUNDS, PHYSICAL_REAL_ESTATE),

    HEDGE_FUNDS("Hedgefonds", false),
    PRIVATE_EQUITY("Private Equity", false),
    ZERTIFIKATE("Zertifikate", false),
    OPTIONSSCHEINE("Optionsscheine", false),
    ALTERNATIVE_INVESTMENTS("Alternative Anlagen", true, HEDGE_FUNDS, PRIVATE_EQUITY, ZERTIFIKATE, OPTIONSSCHEINE);

    private final String displayText;
    private final boolean isParent;
    private final List<InvestmentType> childs;

    InvestmentType(@NonNull String displayText, boolean isParent, @NonNull InvestmentType... child) {
        this.displayText = displayText;
        this.isParent = isParent;
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
        return !isParent;
    }

    /**
     * Returns the enum value as a string, using {@code Enum.name()}.
     */
    public static List<String> getValuesAsString() {
        return Arrays.stream(InvestmentType.values()).map(InvestmentType::name).collect(Collectors.toList());
    }
}
