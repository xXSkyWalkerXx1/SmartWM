package de.tud.inf.mmt.wmscrape.gui.tabs.historic.data;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public enum SecuritiesType {
    SHARE("Aktien"),
    BOND("Anleihen"),
    ETF("ETF"),
    AETF("Aktive ETFs"),
    ETC("ETC"),
    FOND("Fonds");


    final String displayText;

    SecuritiesType(@NonNull String displayText){
        this.displayText = displayText;
    }

    /**
     * Maps the type, given by the excel-sheet, to an instance of {@link SecuritiesType}.
     *
     * @param type can be AKT, ANL, ETF, AETF, ETC, FOD
     */
    @Nullable
    public static SecuritiesType getMapped(@NonNull String type) {
        return switch (type){
            case "AKT" -> SHARE;
            case "ANL" -> BOND;
            case "ETF" -> ETF;
            case "AETF" -> AETF;
            case "ETC" -> ETC;
            case "FOD" -> FOND;
            default -> null;
        };
    }

    public String getDisplayText() {
        return displayText;
    }
}
