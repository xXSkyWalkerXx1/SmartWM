package de.tud.inf.mmt.wmscrape.gui.tabs.historic.controller.website;

import de.tud.inf.mmt.wmscrape.gui.tabs.historic.controller.HistoricWebsiteTabController;
import de.tud.inf.mmt.wmscrape.gui.tabs.historic.management.website.HistoricWebsiteTester;
import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.Website;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * used for skipping through all login and logout steps usually done by the scraper
 */
@Controller
public class HistoricWebsiteTestPopupController {

    private HistoricWebsiteTester websiteTester;

    @FXML
    private Label nextStepLabel;
    @FXML
    private TextArea logTextArea;

    @Autowired
    private HistoricWebsiteTabController historicWebsiteTabController;

    /**
     * called when loading the fxml file
     */
    @FXML
    private void initialize() {
        SimpleStringProperty logText = new SimpleStringProperty("");
        logTextArea.textProperty().bind(logText);

        Website website = historicWebsiteTabController.getWebsiteUnpersistedData();
        websiteTester = new HistoricWebsiteTester(website, logText);

        nextStepLabel.setText(websiteTester.getNextStepAction());
    }

    /**
     * does the next step in {@link de.tud.inf.mmt.wmscrape.gui.tabs.scraping.management.website.WebsiteTester}
     */
    @FXML
    private void handleNextStepButton() {
        // do next step and handle on finished
        if(websiteTester.doNextStep()) {
            handleCancelButton();
            return;
        }

        // if there is a next step print message, otherwise quit
        String nextStepAction = websiteTester.getNextStepAction();

        if (nextStepAction != null) {
            nextStepLabel.setText(nextStepAction);

            String warningMessage = websiteTester.getWarningMessage();
            if (warningMessage != null) nextStepLabel.setText(warningMessage);
        } else {
            handleCancelButton();
        }
    }

    @FXML
    private void handleCancelButton() {
        websiteTester.cancel();
        logTextArea.getScene().getWindow().hide(); // use ((Stage) logTextArea.getScene().getWindow()).close(); ?
    }

}
