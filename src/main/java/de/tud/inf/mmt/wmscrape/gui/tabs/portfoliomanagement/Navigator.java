package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Account;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Depot;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Portfolio;
import org.springframework.lang.NonNull;

public class Navigator {

    /**
     * Navigates to overview of specific owner.
     * @param isFirstBreadcrumb if true, the owner will be the first element in the breadcrumb otherwise it will be added to the end.
     */
    public static void navigateToOwner(@NonNull PortfolioManagementTabManager portfolioManagementTabManager,
                                       @NonNull Owner owner,
                                       boolean isFirstBreadcrumb) {
        portfolioManagementTabManager.showInhaberTabs(owner);
        setOrAddBreadcrumb(portfolioManagementTabManager, isFirstBreadcrumb, owner, "owner");
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
     * @param isFirstBreadcrumb if true, the portfolio will be the first element in the breadcrumb otherwise it will be added to the end.
     */
    public static void navigateToPortfolio(@NonNull PortfolioManagementTabManager portfolioManagementTabManager,
                                           @NonNull Portfolio portfolio,
                                           @NonNull boolean isFirstBreadcrumb) {
        portfolioManagementTabManager.showPortfolioTabs(portfolio);
        setOrAddBreadcrumb(portfolioManagementTabManager, isFirstBreadcrumb, portfolio, "portfolio");
    }

    /**
     * Navigates to overview of specific account.
     * @param isFirstBreadcrumb if true, the account will be the first element in the breadcrumb otherwise it will be added to the end.
     */
    public static void navigateToAccount(@NonNull PortfolioManagementTabManager portfolioManagementTabManager,
                                         @NonNull Account account,
                                         boolean isFirstBreadcrumb) {
        portfolioManagementTabManager.showKontoTabs(account);
        setOrAddBreadcrumb(portfolioManagementTabManager, isFirstBreadcrumb, account, "konto");
    }

    /**
     * Navigates to transactions of specific account.
     */
    public static void navigateToAccountTransactions(@NonNull PortfolioManagementTabManager portfolioManagementTabManager,
                                                     @NonNull Account account) {
        portfolioManagementTabManager.showKontoTabs(account);

        var portfolioManagementController = portfolioManagementTabManager.getPortfolioController();
        portfolioManagementController
                .getPortfolioManagementTabPane()
                .getSelectionModel()
                .select(portfolioManagementController.getKontoTransaktionenTab());
    }

    /**
     * Navigates to overview of specific depot.
     * @param isFirstBreadcrumb if true, the depot will be the first element in the breadcrumb otherwise it will be added to the end.
     */
    public static void navigateToDepot(@NonNull PortfolioManagementTabManager portfolioManagementTabManager,
                                       @NonNull Depot depot,
                                       boolean isFirstBreadcrumb) {
        portfolioManagementTabManager.showDepotTabs();
        setOrAddBreadcrumb(portfolioManagementTabManager, isFirstBreadcrumb, depot, "depot");
    }

    /**
     * @param isFirstBreadcrumb if true, the breadcrumb-element will be the first element in the breadcrumb otherwise it will be added to the end.
     * @param breadcrumbElement see {@link BreadcrumbElement#element}
     * @param type see {@link BreadcrumbElement#type} (can be {@code konto}, {@code depot}, {@code portfolio} or {@code owner})
     */
    private static void setOrAddBreadcrumb(@NonNull PortfolioManagementTabManager portfolioManagementTabManager,
                                    boolean isFirstBreadcrumb,
                                    @NonNull Object breadcrumbElement,
                                    @NonNull String type) {
        if (isFirstBreadcrumb) {
            portfolioManagementTabManager.setCurrentlyDisplayedElement(new BreadcrumbElement(breadcrumbElement, type));
        } else {
            portfolioManagementTabManager.addCurrentlyDisplayedElement(new BreadcrumbElement(breadcrumbElement, type));
        }
    }
}
