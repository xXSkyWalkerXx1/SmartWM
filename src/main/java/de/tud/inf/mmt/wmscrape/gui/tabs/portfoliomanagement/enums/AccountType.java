package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums;

public enum AccountType {
    CLEARING_ACCOUNT("Verrechnungskonto"),
    CHECKING_ACCOUNT("Girokonto"),
    FIXED_TERM_DEPOSIT_ACCOUNT("Festgeldkonto");

    private final String displayText;

    AccountType(String displayText){
        this.displayText = displayText;
    }

    public String getDisplayText() {
        return displayText;
    }
}
