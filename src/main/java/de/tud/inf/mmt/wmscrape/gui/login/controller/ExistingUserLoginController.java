package de.tud.inf.mmt.wmscrape.gui.login.controller;

import de.tud.inf.mmt.wmscrape.gui.login.manager.LoginManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager;
import de.tud.inf.mmt.wmscrape.springdata.SpringIndependentData;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ExistingUserLoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ProgressIndicator progress;
    @FXML private Button loginButton;

    /**
     * called when loading the fxml file
     */
    @FXML
    private void initialize()  {
        LoginManager.loadUserProperties();
        usernameField.setText(SpringIndependentData.getUsername());

        usernameField.textProperty().addListener(x -> validUsernameField());
        passwordField.textProperty().addListener(x -> validPasswordField());

        showLoginProgress(false);
    }

    @FXML
    private void handleNewUserButton() {
        PrimaryTabManager.loadFxml("gui/login/controller/newUserLogin.fxml", "Neuen Nutzer anlegen", usernameField, false, null, false);
    }

    @FXML
    private void handleLoginButton() {
        if(!isValidInput()) {
            return;
        }

        boolean success;
        // only checks if the un,pw combination is valid and can be used for spring
        // the actual spring context starts in a sub-task and automatically opens an alert if an exception occurs
        // so this only starts the task creation

        try {
            success = LoginManager.loginAsUser(usernameField.getText(), passwordField.getText(), progress, loginButton);
        } catch (Exception e) {
            // catch everything. there is a lot going on
            LoginManager.programErrorAlert(e, usernameField);
            return;
        }

        if(!success) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Der Verbindungsversuch ist fehlgeschlagen!\n" +
                            "Überprüfen Sie den Pfad sowie Nutzername und Passwort.", ButtonType.CLOSE);
            alert.setHeaderText("Verbindungsfehler");
            var window = usernameField.getScene().getWindow();
            alert.setX(window.getX()+(window.getWidth()/2)-200);
            alert.setY(window.getY()+(window.getHeight()/2)-200);
            alert.showAndWait();
            return;
        }

        showLoginProgress(true);
    }

    @FXML
    private void handleChangeDbPathButton() {
        PrimaryTabManager.loadFxml("gui/login/controller/changeDbPathPopup.fxml",
                "Datenbankpfad ändern", usernameField, true, null, false);
    }


    private boolean isValidInput() {
        // evaluate all to highlight all
        boolean valid = validUsernameField();
        valid &= validPasswordField();
        return valid;
    }

    private boolean validUsernameField() {
        return emptyValidator(usernameField) && usernameValidator(usernameField);
    }

    private boolean validPasswordField() {
        return emptyValidator(passwordField);
    }


    private boolean emptyValidator(TextInputControl input) {
        boolean isValid = input.getText() != null && !input.getText().isBlank();
        PrimaryTabManager.decorateField(input, "Dieses Feld darf nicht leer sein!", isValid, true);
        return isValid;
    }

    private boolean usernameValidator(TextInputControl input) {
        String value = input.getText();
        if(value==null) return true;

        boolean isValid = value.matches("^[a-zA-z0-9\\söäüß]*$");
        PrimaryTabManager.decorateField(input, "Nicht zulässige Zeichen! Nur a-z,0-9,ä,ö,ü,ß sowie Leerzeichen sind erlaubt.",
                                        isValid, true);
        return isValid;
    }

    /**
     * hides the login button and shows the progress while waiting for spring to complete initialization
     * @param show if true progress is displayed
     */
    private void showLoginProgress(boolean show) {
        loginButton.setVisible(!show);
        loginButton.setManaged(!show);
        progress.setVisible(show);
        progress.setManaged(show);
        progress.setProgress(-1);
    }

}
