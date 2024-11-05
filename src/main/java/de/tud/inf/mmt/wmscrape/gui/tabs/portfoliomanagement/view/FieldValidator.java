package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view;

import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager;
import javafx.scene.control.Control;
import javafx.scene.control.TextInputControl;
import org.springframework.lang.NonNull;

public class FieldValidator {

    /**
     * Checks if input is empty. If it is, the input-field will be highlighted as error.
     * @param inputs f.e. TextFields or TextAreas to be validated.
     * @return True, if input is empty or blank.
     * @see de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager#decorateField(Control, String, boolean, boolean)
     */
    public static boolean isInputEmpty(@NonNull TextInputControl... inputs) {
        boolean isValid = false;

        for (TextInputControl input : inputs) {
            String inputText = input.getText();
            if (inputText.isEmpty() || inputText.isBlank()) {
                PrimaryTabManager.decorateField(
                        input,
                        "Dieses Feld darf nicht leer sein!",
                        false,
                        true
                );
                isValid = true;
            } else {
                // Removes bad-input style, if already set, because the input is now valid
                input.getStyleClass().remove("bad-input");
                input.setTooltip(null);
            }
        }
        return isValid;
    }

    /**
     * Checks if input is in the given range. If it isn't, the input-field will be highlighted as error.
     * @param inputs f.e. TextFields or TextAreas to be validated.
     * @return True, if input is in range.
     * @see de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager#decorateField(Control, String, boolean, boolean)
     * @implNote On no input, it will throw a {@link NullPointerException}, so you should call {@link FieldValidator#isInputEmpty(TextInputControl...)} before.
     */
    public static boolean isInRange(int from, int to, @NonNull TextInputControl... inputs) {
        boolean isValid = true;

        for (TextInputControl input : inputs) {
            try {
                int inputValue = (int) Float.parseFloat(input.getText());

                if (inputValue < from || inputValue > to) {
                    PrimaryTabManager.decorateField(
                            input,
                            String.format("Die Eingabe darf nur zwischen %s und %s sein!", from, to),
                            false,
                            true
                    );
                    isValid = false;
                } else {
                    // Removes bad-input style, if already set, because the input is now valid
                    input.getStyleClass().remove("bad-input");
                    input.setTooltip(null);
                }
            } catch (NumberFormatException numberFormatException) {
                PrimaryTabManager.decorateField(
                        input,
                        "Dieses Feld hat eine ungültige Eingabe!",
                        false,
                        true
                );
                isValid = false;
            }
        }
        return isValid;
    }
}
