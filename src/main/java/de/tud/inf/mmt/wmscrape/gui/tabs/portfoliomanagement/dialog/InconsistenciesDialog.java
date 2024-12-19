package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.dialog;

import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.springframework.stereotype.Controller;

/**
 * Dialog to fix inconsistencies.
 * Pass the data/entity with inconsistencies to controller-class.
 * It behaves like a consumer -> it reset the {@code fxmlFilePath}, {@code stageTitle} and {@code controller} after the
 * dialog is shown.
 */
@Controller
public class InconsistenciesDialog {

    private String fxmlFilePath;
    private String stageTitle;
    private Object controller;

    @FXML
    Button showInconsistenciesButton;

    @FXML
    private void onShowInconsistencies() {
        if (fxmlFilePath == null || fxmlFilePath.isEmpty() || stageTitle == null || controller == null) {
           throw new IllegalStateException("fxmlFilePath, stageTitle or controller is not set.");
        }

        PrimaryTabManager.loadFxml(
                fxmlFilePath,
                stageTitle,
                showInconsistenciesButton,
                true,
                controller,
                true
        );
        showInconsistenciesButton.getScene().getWindow().hide();

        fxmlFilePath = null;
        stageTitle = null;
        controller = null;
    }

    // region Setters
    public void setFxmlFilePath(String fxmlFilePath) {
        this.fxmlFilePath = fxmlFilePath;
    }

    public void setStageTitle(String stageTitle) {
        this.stageTitle = stageTitle;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }
    // endregion
}
