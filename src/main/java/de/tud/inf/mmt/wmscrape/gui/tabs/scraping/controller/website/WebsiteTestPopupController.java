package de.tud.inf.mmt.wmscrape.gui.tabs.scraping.controller.website;

import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.controller.ScrapingWebsiteTabController;
import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.Website;
import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.management.website.WebsiteTester;
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
public class WebsiteTestPopupController {
    @FXML private Label nextStep;
    @FXML private TextArea logTextArea;
    private WebsiteTester websiteTester;

    @Autowired
    private ScrapingWebsiteTabController scrapingWebsiteTabController;

    /**
     * called when loading the fxml file
     */
    @FXML
    private void initialize() {
        SimpleStringProperty logText = new SimpleStringProperty("");
        logTextArea.textProperty().bind(logText);

        Website website = scrapingWebsiteTabController.getWebsiteUnpersistedData();
        websiteTester = new WebsiteTester(website, logText);

        nextStep.setText("Browser starten");
    }

    /**
     * does the next step in {@link de.tud.inf.mmt.wmscrape.gui.tabs.scraping.management.website.WebsiteTester}
     */
    @FXML
    private void handleNextStepButton() {

        boolean done = websiteTester.doNextStep();
        if(done) {
            handleCancelButton();
            return;
        }

        int step = websiteTester.getStep();

        // next step
        switch (step) {
            case 1 -> nextStep.setText("Webseite laden");
            case 2 -> nextStep.setText("Cookies akzeptieren");
            case 3 -> nextStep.setText("Login Informationen ausfüllen");
            case 4 -> nextStep.setText("Einloggen");
            case 5 -> nextStep.setText("Ausloggen");
            case 6 -> nextStep.setText("Browser schließen");
            case 7 -> nextStep.setText("Test beenden");
            default -> handleCancelButton();
        }

    }

    @FXML
    private void handleCancelButton() {
        websiteTester.cancel();
        logTextArea.getScene().getWindow().hide();
    }

}
