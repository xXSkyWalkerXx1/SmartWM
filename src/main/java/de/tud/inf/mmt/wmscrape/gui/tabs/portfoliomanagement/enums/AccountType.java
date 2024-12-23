package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum AccountType {
    CLEARING_ACCOUNT("Verrechnungskonto"),
    CHECKING_ACCOUNT("Girokonto"),
    FIXED_TERM_DEPOSIT_ACCOUNT("Festgeldkonto");

    private final String displayText;

    AccountType(String displayText){
        this.displayText = displayText;
    }

    @Override
    public String toString() {
        return displayText;
    }

    /**
     * Returns the enum value as a string, using {@code Enum.name()}.
     */
    public static List<String> getValuesAsString() {
        return Arrays.stream(AccountType.values()).map(AccountType::name).collect(Collectors.toList());
    }
}
