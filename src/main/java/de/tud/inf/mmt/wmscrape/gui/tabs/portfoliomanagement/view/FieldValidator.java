package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view;

import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager;
import javafx.scene.control.Control;
import javafx.scene.control.TextInputControl;
import org.springframework.lang.NonNull;

import java.text.ParseException;

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
            if (inputText == null || inputText.isBlank()) {
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
     * Checks if input is in the given range.
     * @param inputs f.e. TextFields or TextAreas to be validated.
     * @return True, if alls inputs are in range.
     */
    public static boolean isInRange(float from, float to, float... inputs) {
        boolean isValid = true;

        for (float input : inputs) {
            if (input < from || input > to) {
                isValid = false;
                break;
            }
        }
        return isValid;
    }
}
