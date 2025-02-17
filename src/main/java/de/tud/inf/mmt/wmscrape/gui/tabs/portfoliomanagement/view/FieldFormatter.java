package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view;

import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

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
            // allow only one comma
            if (change.getText().equals(",") && change.getControlText().contains(",")) return null;

            // allow only two decimal places
            if (change.getControlNewText().contains(",")) {
                String[] parts = change.getControlNewText().split(",");
                if (parts.length == 2 && parts[1].length() > 2) return null;
            }

            // don't allow dots
            if (change.getText().equals(".")) return null;

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
                } else {
                    // otherwise set to 'to' as default value
                    change.setText(String.valueOf(to));
                    change.setRange(0, change.getControlText().length());
                    return change;
                }
            } catch (NumberFormatException e) {
                return null;
            }
        }));
    }

    /**
     * Set text-format of a text field to only allow float numbers in a specific range.
     */
    public static void setInputFloatRange(@NonNull TextField textField, @NonNull Float from, @Nullable Float to) {
        setInputFloatRange(textField, from, to, null);
    }

    /**
     * Set text-format of a text field to only allow float numbers in a specific range.
     * @param changePredicate Extra predicate to check the input.
     */
    public static void setInputFloatRange(@NonNull TextField textField, @NonNull Float from, @Nullable Float to,
                                          @Nullable Predicate<TextFormatter.Change> changePredicate) {
        textField.setTextFormatter(new TextFormatter<String>(change -> {

            // allow empty input and set to 0 or 'from' as default value
            if (change.getControlNewText().isEmpty()) {
                if (from <= 0 && (to == null || to >= 0)) {
                    // if 0 is in range, set to 0 as default/neutral value
                    change.setText(FormatUtils.formatFloat(0));
                } else {
                    change.setText(FormatUtils.formatFloat(from));
                }
                return change;
            }

            // allow only one comma
            if (change.getControlText().contains(",") && change.getText().equals(",")) return null;

            // allow only two decimal places
            if (change.getControlNewText().contains(",")) {
                String[] parts = change.getControlNewText().split(",");
                if (parts.length == 2 && parts[1].length() > 2) return null;
            }

            // don't allow dots
            if (change.getText().equals(".")) return null;

            // otherwise try to parse and check input
            try {
                if (FormatUtils.parseFloat(change.getControlNewText()) >= from
                        && (to == null || FormatUtils.parseFloat(change.getControlNewText()) <= to)) {
                    if (changePredicate != null && !changePredicate.test(change)) return null;
                    return change;
                } else if (to != null) {
                    // otherwise set to 'to' as default value
                    change.setText(FormatUtils.formatFloat(to));
                    change.setRange(0, change.getControlText().length());
                    return change;
                }
            } catch (Exception e) {
                return null;
            }
            return null;
        }));
    }

    public static void setDeactivatedAtFormatter(@NonNull DatePicker inputCreatedAt, @NonNull DatePicker inputDeactivatedAt) {
        inputDeactivatedAt.getEditor().setTextFormatter(new TextFormatter<String>(change -> {
            if (change.getText().isEmpty()) return change;

            LocalDate parsedDate = LocalDate.parse(
                    change.getText(),
                    DateTimeFormatter.ofPattern("dd.MM.yyyy")
            );

            if (inputCreatedAt.getValue() != null && (parsedDate.isBefore(inputCreatedAt.getValue()) || parsedDate.isAfter(LocalDate.now()))) {
                inputDeactivatedAt.getEditor().setStyle("-fx-border-color: red;");
                inputDeactivatedAt.getEditor().tooltipProperty().unbind();
                inputDeactivatedAt.getEditor().setTooltip(new Tooltip("Das Datum kann nicht vor dem Erstell-Datum bzw. in der Zukunft liegen!"));
                change.setText("");
                change.setRange(0, change.getControlText().length());
                return change;
            } else {
                inputDeactivatedAt.getEditor().setStyle(null);
                inputDeactivatedAt.getEditor().tooltipProperty().unbind();
                inputDeactivatedAt.getEditor().setTooltip(null);
            }
            return change;
        }));
    }
}
