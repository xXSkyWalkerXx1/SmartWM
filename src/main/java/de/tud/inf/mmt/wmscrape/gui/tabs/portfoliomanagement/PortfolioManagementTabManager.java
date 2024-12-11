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
    public  String[] depotList = {"Depot 1", "Depot 2", "Depot 3"};
    public  String[] portfolioList = {"Portfolio 1", "Portfolio 2"};
    public  String[] kontoList = {"Konto 1", "Konto 2"};
    public  String[] ownerList = {"Inhaber 1", "Inhaber 2"};
    public  String[] depotsOfPortfolio1List = {"Depot 1", "Depot 2"};
    public  String[] kontosOfPortfolio1List = {"Konto 1", "Konto 2"};

    //
    private List<BreadcrumbElement> currentlyDisplayedElements = new ArrayList<>();

    public void setPortfolioController(PortfolioManagementTabController controller) {
        this.portfolioController = controller;
    }

    public PortfolioManagementTabController getPortfolioController() {
        return portfolioController;
    }

    public List<BreadcrumbElement> getCurrentlyDisplayedElements() {
        return  currentlyDisplayedElements;
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
    public void removeLastCurrentlyDisplayedElement() {
        currentlyDisplayedElements.remove(currentlyDisplayedElements.size() - 1);
        changeBreadcrumbs();
    }

    public void showPortfolioManagementTabs() {
        if (portfolioController != null) {
            portfolioController.showPortfolioManagementTabs();
        }
    }

    public void showDepotTabs() {
        if (portfolioController != null) {
            portfolioController.showDepotTabs();
        }
    }

    public void showDepotPlanungTabs() {
        if (portfolioController != null) {
            portfolioController.showDepotPlanungTabs();
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
    public void removeBreadcrumbs() {
        if (portfolioController != null) {
            portfolioController.removeBreadcrumbs();
        }
    }

    public void changeBreadcrumbs() {
        if (portfolioController != null) {
            portfolioController.changeBreadcrumbs(currentlyDisplayedElements);
        }
    }

    public AccountService getAccountService() {
        return accountService;
    }
}
