package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.text.ParseException;
import java.util.function.Predicate;

public class FieldFormatter {

    /**
     * Set text-format of a text field to only allow (float) decimal numbers.
     */
    public static void setInputOnlyDecimalNumbers(@NonNull TextField textField) {
        textField.setTextFormatter(new TextFormatter<String>(change -> {

            // allow empty input
            if (change.getControlNewText().isEmpty()) {
                change.setText(FormatUtils.formatFloat(0));
                return change;
            }
            // otherwise try to parse and check input
            try {
                FormatUtils.parseFloat(change.getControlNewText());
            } catch (Exception e) {
                return null;
            }
            return change;
        }));
    }

    /**
     * Set text-format of a text field to only allow integer numbers in a specific range.
     */
    public static void setInputIntRange(@NonNull TextField textField, int from, int to) {
        textField.setTextFormatter(new TextFormatter<String>(change -> {
            try {
                // allow empty input
                if (change.getControlNewText().isEmpty()) {
                    change.setText(String.valueOf(from));
                    return change;
                }
                // otherwise try to parse and check input
                if (Integer.parseInt(change.getControlNewText()) >= from
                        && Integer.parseInt(change.getControlNewText()) <= to) {
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
        setInputFloatRange(textField, from, to, null);
    }

    /**
     * Set text-format of a text field to only allow float numbers in a specific range.
     * @param changePredicate Extra predicate to check the input.
     */
    public static void setInputFloatRange(@NonNull TextField textField, float from, float to, @Nullable Predicate<TextFormatter.Change> changePredicate) {
        textField.setTextFormatter(new TextFormatter<String>(change -> {

            try {
                // allow empty input
                if (change.getControlNewText().isEmpty()) {
                    change.setText(FormatUtils.formatFloat(from));
                    return change;
                }

                // otherwise try to parse and check input
                if (FormatUtils.parseFloat(change.getControlNewText()) >= from
                        && FormatUtils.parseFloat(change.getControlNewText()) <= to) {
                    if (changePredicate != null && !changePredicate.test(change)) return null;
                    return change;
                }
            } catch (Exception e) {
                return null;
            }
            return null;
        }));
    }
}
