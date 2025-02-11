package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Account;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Portfolio;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.kontos.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
public class PortfolioManagementTabManager {

    @Autowired
    AccountService accountService;

    private PortfolioManagementTabController portfolioController;


    public PortfolioManagementTabController getPortfolioController() {
        return portfolioController;
    }

    public AccountService getAccountService() {
        return accountService;
    }

    public void setPortfolioController(PortfolioManagementTabController controller) {
        this.portfolioController = controller;
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
}
