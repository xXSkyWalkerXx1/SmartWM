package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.Navigator;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Account;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Depot;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.FinancialAsset;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Portfolio;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Region;
import org.springframework.lang.NonNull;

import java.util.List;

public class PortfolioTreeView extends TreeView<PortfolioTreeView.Item> {

    public class Item {

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
            if (asset instanceof Portfolio) return String.format(
                        disableShowPortfolioSum ? "%s" : "%s (%s €)",
                        asset,
                        FormatUtils.formatFloat(asset.getValue(portfolioManagementManager.getAccountService()).floatValue())
                );
            if (asset instanceof Account) return String.format(
                    "Konto: %s (%s €)",
                    asset,
                    FormatUtils.formatFloat(asset.getValue(portfolioManagementManager.getAccountService()).floatValue())
            );
            if (asset instanceof Depot) return String.format(
                    "Depot: %s (%s €)",
                    asset,
                    FormatUtils.formatFloat(asset.getValue(portfolioManagementManager.getAccountService()).floatValue())
            );
            throw new IllegalArgumentException("Unknown asset type: " + asset.getClass());
        }
    }

    private final TreeItem<Item> rootTreeItem;
    private final PortfolioManagementTabManager portfolioManagementManager;
    private boolean disableShowPortfolioSum = false;

    /**
     * @param parent JavaFX node-based UI Controls and all layout containers (f.e. Pane). Only used for view-sizing.
     */
    public PortfolioTreeView(
            @NonNull Region parent,
            @NonNull List<Portfolio> portfolios,
            @NonNull PortfolioManagementTabManager portfolioManagementManager){
        this(parent, portfolios, portfolioManagementManager, false);
    }

    /**
     * With this constructor, the sum of the portfolios will not be shown.
     * @param parent JavaFX node-based UI Controls and all layout containers (f.e. Pane). Only used for view-sizing.
     */
    public PortfolioTreeView(
            @NonNull Region parent,
            @NonNull List<Portfolio> portfolios,
            @NonNull PortfolioManagementTabManager portfolioManagementManager,
            boolean disableShowPortfolioSum){
        this.portfolioManagementManager = portfolioManagementManager;
        this.disableShowPortfolioSum = disableShowPortfolioSum;

        Portfolio root = new Portfolio();
        if (portfolios.isEmpty()) {
            root.setName("Keine Portfolios vorhanden");
        } else if (portfolios.size() == 1) {
            root.setName("Portfolio");
        } else {
            root.setName("Portfolios");
        }
        rootTreeItem = new TreeItem<>(new Item(root, true, portfolioManagementManager));
        rootTreeItem.setExpanded(true);

        setRoot(rootTreeItem);
        setShowRoot(true); // false
        setItems(portfolios);

        // match size to parent size
        prefWidthProperty().bind(parent.widthProperty());
        prefHeightProperty().bind(parent.heightProperty());

        // Add listener to handle clicks
        getSelectionModel().selectedItemProperty().addListener(onClickListener);
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

            // if there are no accounts or depots, add a dummy item
            if (portfolio.getAccounts().isEmpty() && portfolio.getDepots().isEmpty()){
                Portfolio emptyDepotsAndAccountsItem = new Portfolio();
                emptyDepotsAndAccountsItem.setName("Keine Konten oder Depots vorhanden");
                TreeItem<Item> emptyDepotsAndAccountsTreeItem = new TreeItem<>(new Item(emptyDepotsAndAccountsItem, true, portfolioManagementManager));
                portfolioTreeItem.getChildren().add(emptyDepotsAndAccountsTreeItem);
            }

            // finally, add it to root-item
            rootTreeItem.getChildren().add(portfolioTreeItem);
        }
    }

    private final ChangeListener<TreeItem<Item>> onClickListener = new ChangeListener<>() {
        @Override
        public void changed(ObservableValue<? extends TreeItem<Item>> observableValue, TreeItem<Item> itemTreeItem, TreeItem<Item> t1) {
            if (t1 == null) return;

            // Do not navigate to root, because it is not a real asset
            Item selectedItem = t1.getValue();
            if (selectedItem.isRoot) return;

            // Handle navigation
            FinancialAsset selectedAsset = selectedItem.asset;

            if (selectedAsset instanceof Portfolio) {
                // If the selected portfolio is already open, do nothing.
                // Note: this case is used to pretend adding a crumb for the same portfolio.
                if (portfolioManagementManager.getPortfolioController().getPortfolioOverviewTab().equals(
                        portfolioManagementManager.getPortfolioController().getPortfolioManagementTabPane().getSelectionModel().getSelectedItem())
                ) {
                    return;
                }
                Navigator.navigateToPortfolio(portfolioManagementManager, (Portfolio) selectedAsset, true);
            } else if (selectedAsset instanceof Account) {
                Navigator.navigateToAccount(portfolioManagementManager, (Account) selectedAsset, true);
            } else { // Otherwise, it has to be an instance of 'Depot'
                Navigator.navigateToDepot(portfolioManagementManager, (Depot) selectedAsset, true);
            }
        }
    };
}
