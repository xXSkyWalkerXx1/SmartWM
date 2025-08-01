package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Account;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Depot;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Portfolio;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.BreadcrumbElementType;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.BreadCrumbElement;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class Navigator {

    /**
     * Navigates to overview of specific owner.
     * @param addBreadCrumb only if true, a breadcrumb will be added.
     */
    public static void navigateToOwner(@NonNull PortfolioManagementTabManager portfolioManagementTabManager,
                                       @NonNull Owner owner,
                                       @Nullable boolean addBreadCrumb) {
        handleBreadcrumb(portfolioManagementTabManager, addBreadCrumb, owner, BreadcrumbElementType.OWNER);
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
     * @param addBreadCrumb  only if true, a breadcrumb will be added.
     */
    public static void navigateToPortfolio(@NonNull PortfolioManagementTabManager portfolioManagementTabManager,
                                           @NonNull Portfolio portfolio,
                                           @Nullable boolean addBreadCrumb) {
        handleBreadcrumb(portfolioManagementTabManager, addBreadCrumb, portfolio, BreadcrumbElementType.PORTFOLIO);
        portfolioManagementTabManager.showPortfolioTabs(portfolio);
    }

    /**
     * Navigates to overview of specific account.
     * @param addBreadCrumb  only if true, a breadcrumb will be added.
     */
    public static void navigateToAccount(@NonNull PortfolioManagementTabManager portfolioManagementTabManager,
                                         @NonNull Account account,
                                         @Nullable boolean addBreadCrumb) {
        handleBreadcrumb(portfolioManagementTabManager, addBreadCrumb, account, BreadcrumbElementType.ACCOUNT);
        portfolioManagementTabManager.showKontoTabs(account);
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
     * @param addBreadCrumb  only if true, a breadcrumb will be added.
     */
    public static void navigateToDepot(@NonNull PortfolioManagementTabManager portfolioManagementTabManager,
                                       @NonNull Depot depot,
                                       @Nullable boolean addBreadCrumb) {
        handleBreadcrumb(portfolioManagementTabManager, addBreadCrumb, depot, BreadcrumbElementType.DEPOT);
        portfolioManagementTabManager.showDepotTabs();
    }

    /**
     * @param addBreadCrumb only if true, a breadcrumb will be added.
     * @param breadcrumbElement see {@link BreadCrumbElement#element}
     * @param type see {@link BreadCrumbElement#type} (can be {@code konto}, {@code depot}, {@code portfolio} or {@code owner})
     */
    private static void handleBreadcrumb(@NonNull PortfolioManagementTabManager portfolioManagementTabManager,
                                         @Nullable boolean addBreadCrumb,
                                         @NonNull Object breadcrumbElement,
                                         @NonNull BreadcrumbElementType type) {
        if (!addBreadCrumb) return;
        portfolioManagementTabManager.getPortfolioController().addBreadcrumb(new BreadCrumbElement(breadcrumbElement, type));
    }
}
