package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Account;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Portfolio;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.kontos.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PortfolioManagementTabManager {

    @Autowired
    AccountService accountService;

    private PortfolioManagementTabController portfolioController;

    // static data to simulate Portfolios, etc.
    public  String[] depotsOfPortfolio1List = {"Depot 1", "Depot 2"};
    public  String[] kontosOfPortfolio1List = {"Konto 1", "Konto 2"};

    //
    private final List<BreadcrumbElement> currentlyDisplayedElements = new ArrayList<>();

    public PortfolioManagementTabController getPortfolioController() {
        return portfolioController;
    }

    public AccountService getAccountService() {
        return accountService;
    }

    public void setPortfolioController(PortfolioManagementTabController controller) {
        this.portfolioController = controller;
    }

    public void setCurrentlyDisplayedElement(BreadcrumbElement newElement) {
        currentlyDisplayedElements.clear();
        currentlyDisplayedElements.add(newElement);
        changeBreadcrumbs();
    }
    public void addCurrentlyDisplayedElement(BreadcrumbElement newElement) {
        currentlyDisplayedElements.add(newElement);
        changeBreadcrumbs();
    }

    public void showDepotTabs() {
        if (portfolioController != null) {
            portfolioController.showDepotTabs();
        }
    }

    public void showPortfolioTabs(@Nullable Portfolio portfolio) {
        if (portfolioController != null) {
            portfolioController.showPortfolioTabs(portfolio);
        }
    }

    public void showKontoTabs(@Nullable Account account) {
        if (portfolioController != null) {
            portfolioController.showKontoTabs(account);
        }
    }

    public void showInhaberTabs(@Nullable Owner owner) {
        if (portfolioController != null) {
            portfolioController.showInhaberTabs(owner);
        }
    }

    public void changeBreadcrumbs() {
        if (portfolioController != null) {
            portfolioController.changeBreadcrumbs(currentlyDisplayedElements);
        }
    }
}
