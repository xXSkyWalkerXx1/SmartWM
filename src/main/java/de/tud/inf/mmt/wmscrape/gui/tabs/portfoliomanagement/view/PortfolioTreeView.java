package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.BreadcrumbElement;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.Navigator;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Account;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Depot;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.FinancialAsset;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Portfolio;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.kontos.AccountService;
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
        private final PortfolioManagementTabManager portfolioManagementManager;

        public Item (@NonNull FinancialAsset asset, @NonNull PortfolioManagementTabManager portfolioManagementManager) {
            this.asset = asset;
            this.portfolioManagementManager = portfolioManagementManager;
        }

        public Item (@NonNull FinancialAsset asset, boolean isRoot, @NonNull PortfolioManagementTabManager portfolioManagementManager) {
            this(asset, portfolioManagementManager);
            this.isRoot = isRoot;
        }

        @Override
        public String toString() {
            if (isRoot) return asset.toString();
            return String.format(
                    "%s\t(%s €)",
                    asset,
                    FormatUtils.formatFloat(asset.getValue(portfolioManagementManager.getAccountService()).floatValue())
            );
        }
    }

    private boolean isOneMainMenu = true;
    private final TreeItem<Item> rootTreeItem;
    private final PortfolioManagementTabManager portfolioManagementManager;

    /**
     * @param parent JavaFX node-based UI Controls and all layout containers (f.e. Pane). Only used for view-sizing.
     * @param isOnMainMenu If true, breadcrumbs will be set, otherwise they will be added.
     */
    public PortfolioTreeView(
            @NonNull Region parent,
            @NonNull List<Portfolio> portfolios,
            @NonNull PortfolioManagementTabManager portfolioManagementManager,
            boolean isOnMainMenu){
        this.isOneMainMenu = isOnMainMenu;
        this.portfolioManagementManager = portfolioManagementManager;

        Portfolio root = new Portfolio();
        root.setName("Portfolio/s");
        rootTreeItem = new TreeItem<>(new Item(root, true, portfolioManagementManager));
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
            TreeItem<Item> portfolioTreeItem = new TreeItem<>(new Item(portfolio, portfolioManagementManager));
            portfolioTreeItem.setExpanded(true);

            // add accounts first
            for (Account account : portfolio.getAccounts()){
                TreeItem<Item> accountTreeItem = new TreeItem<>(new Item(account, portfolioManagementManager));
                portfolioTreeItem.getChildren().add(accountTreeItem);
            }

            // then add depots
            for (Depot depot : portfolio.getDepots()){
                TreeItem<Item> depotTreeItem = new TreeItem<>(new Item(depot, portfolioManagementManager));
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
            String assetType;

            // Handle navigation
            if (selectedAsset instanceof Portfolio) {
                Navigator.navigateToPortfolio(portfolioManagementManager, (Portfolio) selectedAsset);
                assetType = "portfolio";
            } else if (selectedAsset instanceof Account) {
                Navigator.navigateToAccount(portfolioManagementManager, (Account) selectedAsset);
                assetType = "konto";
            } else { // Otherwise, it has to be an instance of 'Depot'
                Navigator.navigateToDepot(portfolioManagementManager, (Depot) selectedAsset);
                assetType = "depot";
            }

            // Handle breadcrumbs
            if (isOneMainMenu) {
                portfolioManagementManager.setCurrentlyDisplayedElement(new BreadcrumbElement(selectedAsset.toString(), assetType));
            } else {
                portfolioManagementManager.addCurrentlyDisplayedElement(new BreadcrumbElement(selectedAsset.toString(), assetType));
            }
        }
    };
}
