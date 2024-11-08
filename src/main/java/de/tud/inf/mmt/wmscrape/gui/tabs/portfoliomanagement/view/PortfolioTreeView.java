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

public class PortfolioTreeView extends TreeView<PortfolioTreeView.Item> {

    public static class Item {

        private boolean isRoot = false;
        private final FinancialAsset asset;

        public Item (@NonNull FinancialAsset asset) {
            this.asset = asset;
        }

        public Item (@NonNull FinancialAsset asset, boolean isRoot) {
            this(asset);
            this.isRoot = isRoot;
        }

        @Override
        public String toString() {
            if (isRoot) return asset.toString();
            return String.format("%s\t(%s €)", asset, asset.getValue().toString());
        }
    }

    // ToDo: implement converting any currency to €

    private final TreeItem<Item> rootTreeItem;
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
        rootTreeItem = new TreeItem<>(new Item(root, true));
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
            TreeItem<Item> portfolioTreeItem = new TreeItem<>(new Item(portfolio));
            portfolioTreeItem.setExpanded(true);

            // add accounts first
            for (Account account : portfolio.getAccounts()){
                TreeItem<Item> accountTreeItem = new TreeItem<>(new Item(account));
                portfolioTreeItem.getChildren().add(accountTreeItem);
            }

            // then add depots
            for (Depot depot : portfolio.getDepots()){
                TreeItem<Item> depotTreeItem = new TreeItem<>(new Item(depot));
                portfolioTreeItem.getChildren().add(depotTreeItem);
            }

            // finally, add it to root-item
            rootTreeItem.getChildren().add(portfolioTreeItem);
        }
    }

    private final EventHandler<MouseEvent> onClickAction = new EventHandler<>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            TreeItem<Item> selectedItem = getSelectionModel().getSelectedItem();
            if (selectedItem == null || selectedItem.equals(rootTreeItem)) return;

            FinancialAsset selectedAsset = selectedItem.getValue().asset;

            if (selectedAsset instanceof Portfolio) {
                portfolioManagementManager.showPortfolioTabs();
                portfolioManagementManager.setCurrentlyDisplayedElement(new BreadcrumbElement(selectedAsset.toString(), "portfolio"));
                // ToDo: portfolioManagementManager.getPortfolioController().getOwnerPortfoliosController().open(selectedAsset);
            } else if (selectedAsset instanceof Account) {
                portfolioManagementManager.showKontoTabs((Account) selectedAsset);
                portfolioManagementManager.setCurrentlyDisplayedElement(new BreadcrumbElement(selectedAsset.toString(), "konto"));
                portfolioManagementManager.getPortfolioController().getOwnerPortfoliosController().open();
            } else { // Otherwise, it has to be an instance of 'Depot'
                portfolioManagementManager.showDepotTabs();
                portfolioManagementManager.setCurrentlyDisplayedElement(new BreadcrumbElement(selectedAsset.toString(), "depot"));
                // ToDo: portfolioManagementManager.getPortfolioController().getOwnerPortfoliosController().open(selectedAsset);
            }
        }
    };
}
