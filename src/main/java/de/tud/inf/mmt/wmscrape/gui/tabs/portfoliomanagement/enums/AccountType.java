package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum AccountType {
    CHECKING_ACCOUNT("Girokonto"),
    SAVINGS_ACCOUNT("Sparkonto"),
    FIXED_TERM_DEPOSIT_ACCOUNT("Festgeldkonto"),
    DAILY_MONEY_ACCOUNT("Tagesgeldkonto"),
    CREDIT_ACCOUNT("Darlehenskonto"),
    CLEARING_ACCOUNT("Verrechnungskonto");

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
