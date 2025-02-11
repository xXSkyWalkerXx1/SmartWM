package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Account;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Depot;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Portfolio;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.BreadcrumbElementType;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class Navigator {

    /**
     * Navigates to overview of specific owner.
     * @param isFirstBreadcrumb if true, the owner will be the first element in the breadcrumb, if false it will be
     *                          added to the end, otherwise if null no action will be performed.
     */
    public static void navigateToOwner(@NonNull PortfolioManagementTabManager portfolioManagementTabManager,
                                       @NonNull Owner owner,
                                       @Nullable Boolean isFirstBreadcrumb) {
        setOrAddBreadcrumb(portfolioManagementTabManager, isFirstBreadcrumb, owner, BreadcrumbElementType.OWNER);
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
     * @param isFirstBreadcrumb  if true, the portfolio will be the first element in the breadcrumb, if false it will be
     *                           added to the end, otherwise if null no action will be performed.
     */
    public static void navigateToPortfolio(@NonNull PortfolioManagementTabManager portfolioManagementTabManager,
                                           @NonNull Portfolio portfolio,
                                           @Nullable Boolean isFirstBreadcrumb) {
        setOrAddBreadcrumb(portfolioManagementTabManager, isFirstBreadcrumb, portfolio, BreadcrumbElementType.PORTFOLIO);
        portfolioManagementTabManager.showPortfolioTabs(portfolio);
    }

    /**
     * Navigates to overview of specific account.
     * @param isFirstBreadcrumb  if true, the account will be the first element in the breadcrumb, if false it will be
     *                           added to the end, otherwise if null no action will be performed.
     */
    public static void navigateToAccount(@NonNull PortfolioManagementTabManager portfolioManagementTabManager,
                                         @NonNull Account account,
                                         @Nullable Boolean isFirstBreadcrumb) {
        setOrAddBreadcrumb(portfolioManagementTabManager, isFirstBreadcrumb, account, BreadcrumbElementType.ACCOUNT);
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
     * @param isFirstBreadcrumb  if true, the depot will be the first element in the breadcrumb, if false it will be
     *      *                    added to the end, otherwise if null no action will be performed.
     */
    public static void navigateToDepot(@NonNull PortfolioManagementTabManager portfolioManagementTabManager,
                                       @NonNull Depot depot,
                                       @Nullable Boolean isFirstBreadcrumb) {
        setOrAddBreadcrumb(portfolioManagementTabManager, isFirstBreadcrumb, depot, BreadcrumbElementType.DEPOT);
        portfolioManagementTabManager.showDepotTabs();
    }

    /**
     * @param isFirstBreadcrumb if true, the breadcrumb-element will be the first element in the breadcrumb, if
     *                          false it will be added to the end and if null no action will be performed
     * @param breadcrumbElement see {@link BreadcrumbElement#element}
     * @param type see {@link BreadcrumbElement#type} (can be {@code konto}, {@code depot}, {@code portfolio} or {@code owner})
     */
    // ToDo: auf 'isFirstBreadcrumb' in BA noch ändern
    private static void setOrAddBreadcrumb(@NonNull PortfolioManagementTabManager portfolioManagementTabManager,
                                           @Nullable Boolean isFirstBreadcrumb,
                                           @NonNull Object breadcrumbElement,
                                           @NonNull BreadcrumbElementType type) {
        if (isFirstBreadcrumb == null) return;
        portfolioManagementTabManager.getPortfolioController().addBreadcrumb(new BreadcrumbElement(breadcrumbElement, type));
    }
}
