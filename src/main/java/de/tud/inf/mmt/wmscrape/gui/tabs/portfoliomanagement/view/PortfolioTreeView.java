package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.BreadcrumbElement;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Account;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Depot;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.FinancialAsset;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Portfolio;
import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import org.springframework.lang.NonNull;

import java.util.List;

public class PortfolioTreeView extends TreeView<FinancialAsset> {

    // ToDo: implement converting any currency to €
    // region custom assets to edit toString()
    public static class PortfolioItem extends Portfolio {
        @Override
        public String toString() {
            return String.format("%s\t(%s€)", getName(), getValue());
        }
    }
    public static class AccountItem extends Account {
        @Override
        public String toString() {
            return String.format("%s\t(%s€)", getDescription(), getValue());
        }
    }
    public static class DepotItem extends Depot {
        @Override
        public String toString() {
            return String.format("%s\t(%s€)", getName(), getValue());
        }
    }
    // endregion

    private final TreeItem<FinancialAsset> rootTreeItem;
    private final PortfolioManagementTabManager portfolioManagementManager;

    /**
     * @param parent JavaFX node-based UI Controls and all layout containers (f.e. Pane). Only used for view-sizing.
     */
    public PortfolioTreeView(
            @NonNull Region parent,
            @NonNull List<Portfolio> portfolios,
            @NonNull PortfolioManagementTabManager portfolioManagementManager){
        this.portfolioManagementManager = portfolioManagementManager;

        Portfolio root = new Portfolio();
        root.setName("Portfolio/s");
        rootTreeItem = new TreeItem<>(root);
        rootTreeItem.setExpanded(true);

        setRoot(rootTreeItem);
        setShowRoot(true); // false
        setItems(portfolios);

        // match size to parent size
        prefWidthProperty().bind(parent.widthProperty());
        prefHeightProperty().bind(parent.heightProperty());

        // Add listener to handle clicks
        addEventHandler(MouseEvent.MOUSE_CLICKED, onClickAction);
    }

    private void setItems(@NonNull List<Portfolio> portfolios){
        rootTreeItem.getChildren().clear();

        for (Portfolio portfolio : portfolios){
            TreeItem<FinancialAsset> portfolioTreeItem = new TreeItem<>((PortfolioItem) portfolio);

            // add accounts first
            for (Account account : portfolio.getAccounts()){
                TreeItem<FinancialAsset> accountTreeItem = new TreeItem<>((AccountItem) account);
                portfolioTreeItem.getChildren().add(accountTreeItem);
            }

            // then add depots
            for (Depot depot : portfolio.getDepots()){
                TreeItem<FinancialAsset> depotTreeItem = new TreeItem<>((DepotItem) depot);
                portfolioTreeItem.getChildren().add(depotTreeItem);
            }

            // finally, add it to root-item
            rootTreeItem.getChildren().add(portfolioTreeItem);
        }
    }

    private final EventHandler<MouseEvent> onClickAction = new EventHandler<>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            if (getSelectionModel().getSelectedItem().equals(rootTreeItem)) return;

            FinancialAsset selectedAsset = getSelectionModel().getSelectedItem().getValue();

            if (selectedAsset instanceof Portfolio) {
                portfolioManagementManager.showPortfolioTabs();
                portfolioManagementManager.setCurrentlyDisplayedElement(new BreadcrumbElement(selectedAsset.toString(), "portfolio"));
                // ToDo: portfolioManagementManager.getPortfolioController().getOwnerPortfoliosController().open(selectedAsset);
            } else if (selectedAsset instanceof Account) {
                portfolioManagementManager.showKontoTabs();
                portfolioManagementManager.setCurrentlyDisplayedElement(new BreadcrumbElement(selectedAsset.toString(), "konto"));
                // ToDo: portfolioManagementManager.getPortfolioController().getOwnerPortfoliosController().open(selectedAsset);
            } else { // Otherwise, it has to be an instance of 'Depot'
                portfolioManagementManager.showDepotTabs();
                portfolioManagementManager.setCurrentlyDisplayedElement(new BreadcrumbElement(selectedAsset.toString(), "depot"));
                // ToDo: portfolioManagementManager.getPortfolioController().getOwnerPortfoliosController().open(selectedAsset);
            }
        }
    };
}
