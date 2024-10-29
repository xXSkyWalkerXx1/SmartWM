package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums;

public enum State {
    ACTIVATED("Aktiviert"),
    DEACTIVATED("Deaktiviert");

    private final String displayText;

    State(String displayText){
        this.displayText = displayText;
    }

    public String getDisplayText() {
        return displayText;
    }
}
