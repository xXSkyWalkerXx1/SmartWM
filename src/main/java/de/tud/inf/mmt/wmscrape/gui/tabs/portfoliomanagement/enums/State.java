package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public String toString() {
        return displayText;
    }

    /**
     * Returns the enum value as a string, using {@code Enum.name()}.
     */
    public static List<String> getValuesAsString() {
        return Arrays.stream(State.values()).map(State::name).collect(Collectors.toList());
    }
}
