package de.tud.inf.mmt.wmscrape.gui.tabs.historic.data;

import org.springframework.lang.NonNull;

public enum SecuritiesType {
    SHARE("Aktien"),
    BOND("Anleihen"),
    CERTIFICATE("Zertifikate"),
    ETF("ETF"),
    FOND("Fonds"),
    RESOURCE("Rohstoffe");


    final String displayText;

    SecuritiesType(@NonNull String displayText){
        this.displayText = displayText;
    }

    public String getDisplayText() {
        return displayText;
    }
}
