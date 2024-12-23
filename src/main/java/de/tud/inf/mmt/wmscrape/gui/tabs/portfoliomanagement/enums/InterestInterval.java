package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * Returns the enum value as a string, using {@code Enum.name()}.
     */
    public static List<String> getValuesAsString() {
        return Arrays.stream(InterestInterval.values()).map(InterestInterval::name).collect(Collectors.toList());
    }
}
