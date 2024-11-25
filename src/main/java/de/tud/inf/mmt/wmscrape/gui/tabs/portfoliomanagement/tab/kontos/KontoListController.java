package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.kontos;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.springframework.stereotype.Controller;

import java.awt.*;

@Controller
public class KontoListController {

    @FXML
    AnchorPane accountTablePane;
    @FXML
    AnchorPane accountDepotsTablePane;
    @FXML
    Label sumLabel;

    @FXML
    public void initialize() {

    }

    @FXML
    private void onClickShowAccountHistory() {
        throw new NotImplementedException("Not implemented yet");
    }

    @FXML
    private void onClickCreateAccount() {
        throw new NotImplementedException("Not implemented yet");
    }
}
