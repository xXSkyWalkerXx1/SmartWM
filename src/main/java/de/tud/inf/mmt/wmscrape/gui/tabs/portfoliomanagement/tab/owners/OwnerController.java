package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners;

import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.interfaces.Openable;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners.dialog.CreateOwnerDialog;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.PortfolioTreeView;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.TableFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;

import java.util.*;


@Controller
public class OwnerController implements Openable {

    @Autowired
    private OwnerService ownerService;
    @Autowired
    private CreateOwnerDialog createOwnerDialog; // controller
    @Autowired
    private PortfolioManagementTabManager portfolioManagementTabManager;

    @FXML
    Button createOwnerButton;
    @FXML
    AnchorPane ownerTablePane;
    @FXML
    AnchorPane ownerTreeViewPane;

    public void open() {
        ownerTablePane.getChildren().setAll(TableFactory.createOwnerTable(
                ownerTablePane,
                ownerService.getAll(),
                this,
                ownerService
        ));

        setOwnerTreeView(new PortfolioTreeView(
                ownerTreeViewPane,
                new ArrayList<>(),
                portfolioManagementTabManager
        ));
    }

    @FXML
    public void onClickCreateOwner(){
        PrimaryTabManager.loadFxml(
                "gui/tabs/portfoliomanagement/tab/owners/dialog/create_owner_dialog.fxml",
                "Neuen Inhaber anlegen",
                createOwnerButton,
                true,
                createOwnerDialog,
                false
        );
    }

    public void setOwnerTreeView(@NonNull PortfolioTreeView portfolioTreeView){
        portfolioTreeView.setTooltip(new Tooltip("Auflistung der dem Inhaber zugeordneten Portfolios."));
        ownerTreeViewPane.getChildren().setAll(portfolioTreeView);
    }

    public PortfolioManagementTabManager getPortfolioManagementTabManager() {
        return portfolioManagementTabManager;
    }
}
