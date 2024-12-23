package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view;

import javafx.scene.control.ComboBox;

import java.util.List;

public class ComboBoxValidator {

    /**
     * Checks if all ComboBoxes have a selected item. If not, the ComboBox will be highlighted as error.
     * @return True, if all ComboBoxes have a selected item.
     */
    public static boolean areComboboxInputsValid(List<ComboBox<?>> comboBoxes) {
        for (ComboBox<?> comboBox : comboBoxes) {
            if (comboBox.getSelectionModel().getSelectedItem() == null) {
                comboBox.getStyleClass().add("bad-input");
                return false;
            } else {
                comboBox.getStyleClass().remove("bad-input");
            }
        }
        return true;
    }
}
