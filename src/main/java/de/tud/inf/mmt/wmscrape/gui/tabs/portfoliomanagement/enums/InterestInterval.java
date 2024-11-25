package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums;

public enum InterestInterval {
    MONTHLY("Monatlich"),
    QUARTERLY("Quartalsweise"),
    YEARLY("Jährlich");

    private final String displayText;

    InterestInterval(String displayText){
        this.displayText = displayText;
    }

    @Override
    public String toString() {
        return displayText;
    }
}
