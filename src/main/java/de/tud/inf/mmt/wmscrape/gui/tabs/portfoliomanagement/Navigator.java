package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Account;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Depot;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Portfolio;
import org.springframework.lang.NonNull;

public class Navigator {

    /**
     * Navigates to overview of specific owner.
     */
    public static void navigateToOwner(@NonNull PortfolioManagementTabManager portfolioManagementTabManager,
                                       @NonNull Owner owner) {
        portfolioManagementTabManager.showInhaberTabs(owner);
    }

    /**
     * Navigates to assets of specific owner.
     */
    public static void navigateToOwnerAssets(@NonNull PortfolioManagementTabManager portfolioManagementTabManager,
                                             @NonNull Owner owner) {
        portfolioManagementTabManager.showInhaberTabs(owner);

        var portfolioManagementController = portfolioManagementTabManager.getPortfolioController();
        portfolioManagementController
                .getPortfolioManagementTabPane()
                .getSelectionModel()
                .select(portfolioManagementController.getInhaberVermögenTab());
    }

    /**
     * Navigates to overview of specific portfolio.
     */
    public static void navigateToPortfolio(@NonNull PortfolioManagementTabManager portfolioManagementTabManager,
                                           @NonNull Portfolio portfolio) {
        portfolioManagementTabManager.showPortfolioTabs();
    }

    /**
     * Navigates to overview of specific account.
     */
    public static void navigateToAccount(@NonNull PortfolioManagementTabManager portfolioManagementTabManager,
                                         @NonNull Account account) {
        portfolioManagementTabManager.showKontoTabs(account);
    }

    /**
     * Navigates to overview of specific depot.
     */
    public static void navigateToDepot(@NonNull PortfolioManagementTabManager portfolioManagementTabManager,
                                       @NonNull Depot depot) {
        portfolioManagementTabManager.showDepotTabs();
    }
}
