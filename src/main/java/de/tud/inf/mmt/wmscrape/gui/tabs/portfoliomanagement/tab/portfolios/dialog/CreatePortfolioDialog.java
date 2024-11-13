package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.portfolios.dialog;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Portfolio;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners.OwnerService;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.TableFactory;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class CreatePortfolioDialog {

    Portfolio portfolio = new Portfolio();

    @Autowired
    OwnerService ownerService;

    @FXML
    TextField inputPortfolioName;

    @FXML
    ComboBox<Owner> inputOwner;

    @FXML
    AnchorPane commissionSchemeTablePane;

    @FXML
    AnchorPane commissionSchemeLocationTablePane;

    @FXML
    AnchorPane commissionSchemeCurrencyTablePane;

    @FXML
    private void initialize() {
        inputOwner.getItems().addAll(ownerService.getAllOwners());
        commissionSchemeLocationTablePane.getChildren().add(TableFactory.createPortfolioDivisionByLocationTable(
                commissionSchemeLocationTablePane,
                portfolio.getInvestmentGuideline().getDivisionByLocation()
        ));

    }

    @FXML
    private void onSave() {}

    @FXML
    private void onCancel() {}
}
