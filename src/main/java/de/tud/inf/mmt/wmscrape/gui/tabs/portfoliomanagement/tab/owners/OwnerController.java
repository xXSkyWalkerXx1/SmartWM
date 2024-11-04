package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners;

import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabController;
import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.PortfolioTreeView;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.TableFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;


@Controller
public class OwnerController {

    @Autowired
    private OwnerService ownerService;
    @Autowired
    private CreateOwnerDialog createOwnerDialog; // controller

    @FXML
    Button createOwnerButton;
    @FXML
    AnchorPane ownerTablePane;
    @FXML
    AnchorPane ownerTreeViewPane;

    @FXML
    private void initialize() {
        ownerTablePane.getChildren().add(TableFactory.createOwnerTable(
                ownerTablePane,
                ownerService.getAllOwners(),
                this
        ));
    }

    @FXML
    public void onClickCreateOwner(){
        PrimaryTabManager.loadFxml(
                "tabs/portfoliomanagement/tab/owners/createOwnerDialog.fxml",
                "Neuen Inhaber anlegen",
                null,
                true,
                createOwnerDialog,
                false
        );
    }

    public void setOwnerTreeView(@NonNull PortfolioTreeView portfolioTreeView){
        ownerTreeViewPane.getChildren().clear();
        ownerTreeViewPane.getChildren().add(portfolioTreeView);
    }

}
