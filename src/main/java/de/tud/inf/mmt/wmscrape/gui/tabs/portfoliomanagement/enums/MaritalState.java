package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum MaritalState {
    SINGLE("Ledig"),
    MARRIED("Verheiratet"),
    WIDOWED("Verwitwet"),
    DIVORCED("Geschieden"),
    REGISTERED_CIVIL_PARTNERSHIP("Registrierte Lebenspartnerschaft"),
    REGISTERED_LIFE_PARTNER_DECEASED("Eingetragene/r Lebenspartner/in verstorben"),
    REGISTERED_CIVIL_PARTNERSHIP_ANNULLED("Eingetragene Lebenspartnerschaft aufgehoben");

    private final String displayText;

    MaritalState(String displayText){
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
        return Arrays.stream(MaritalState.values()).map(MaritalState::name).collect(Collectors.toList());
    }
}
