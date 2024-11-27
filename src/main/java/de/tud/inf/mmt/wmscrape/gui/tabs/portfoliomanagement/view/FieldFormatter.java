package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import org.springframework.lang.NonNull;

public class FieldFormatter {

    /**
     * Set text-format of a text field to only allow (all) decimal numbers.
     */
    public static void setInputOnlyDecimalNumbers(@NonNull TextField textField) {
        textField.setTextFormatter(new TextFormatter<String>(change -> {
            String textChange = change.getText();

            if (textChange.isEmpty() // removed anything
                    || textChange.matches("^\\d$") // allow numerics
                    || textChange.matches("^[.]$") && !textField.getText().contains(".") // allow '.' and only once
                    || textChange.matches("^\\d*\\.?\\d+$")) { // allow full decimal numbers instead of single numbers
                return change;
            }
            return null;
        }));
    }

    /**
     * Set text-format of a text field to only allow integer numbers in a specific range.
     */
    public static void setInputIntRange(@NonNull TextField textField, int from, int to) {
        textField.setTextFormatter(new TextFormatter<String>(change -> {
            String textChange = change.getText();

            try {
                if (change.isReplaced()) {
                    if (Integer.parseInt(textChange) >= from && Integer.parseInt(textChange) <= to) return change;
                }
                if (Integer.parseInt(textField.getText() + textChange) >= from
                        && Integer.parseInt(textField.getText() + textChange) <= to) {
                    return change;
                }
            } catch (NumberFormatException e) {
                return null;
            }
            return null;
        }));
    }

    /**
     * Set text-format of a text field to only allow float numbers in a specific range.
     */
    public static void setInputFloatRange(@NonNull TextField textField, float from, float to) {
        textField.setTextFormatter(new TextFormatter<String>(change -> {
            String textChange = change.getText();

            try {
                if (change.isReplaced()) {
                    if (Float.parseFloat(textChange) >= from && Float.parseFloat(textChange) <= to) return change;
                }
                if (Float.parseFloat(textField.getText() + textChange) >= from
                        && Float.parseFloat(textField.getText() + textChange) <= to) {
                    return change;
                }
            } catch (NumberFormatException e) {
                return null;
            }
            return null;
        }));
    }
}
