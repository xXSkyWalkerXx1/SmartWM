package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners;

import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Account;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Depot;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Portfolio;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.AccountType;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.InterestInterval;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.MaritalState;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.PortfolioTreeView;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.TableFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;

import java.util.*;


@Controller
public class OwnerController {

    @Autowired
    private OwnerService ownerService;
    @Autowired
    private CreateOwnerDialog createOwnerDialog; // controller
    @Autowired
    private PortfolioManagementTabManager portfolioManagementTabManager;

    @FXML
    Button createOwnerButton;
    @FXML
    AnchorPane ownerTablePane;
    @FXML
    AnchorPane ownerTreeViewPane;

    @FXML
    public void initialize() {
        Owner defaultOwner = new Owner();
        defaultOwner.setForename("Initial");
        defaultOwner.setAftername("Owner");
        defaultOwner.setCreatedAt(Calendar.getInstance().getTime());

        Owner.Address defaultOwnerAddress = new Owner.Address();
        defaultOwnerAddress.setCountry("Deutschland");
        defaultOwnerAddress.setPlz("01609");
        defaultOwnerAddress.setLocation("Frauenhain");
        defaultOwnerAddress.setStreet("Hauptstraße");
        defaultOwnerAddress.setStreetNumber("123");

        Owner.TaxInformation defaultOwnerTaxInformation = new Owner.TaxInformation();
        defaultOwnerTaxInformation.setTaxNumber("10314141");
        defaultOwnerTaxInformation.setMaritalState(MaritalState.SINGLE);

        Portfolio portfolio = new Portfolio();
        portfolio.setName("Default");
        portfolio.setCreatedAt(Calendar.getInstance().getTime());
        portfolio.setOwner(defaultOwner);

        Account account = new Account();
        account.setBalance(410);
        account.setCreatedAt(Calendar.getInstance().getTime());
        account.setOwner(defaultOwner);
        account.setBankName("Junges Konto");
        account.setCurrencyCode(Currency.getInstance("EUR"));
        account.setDescription("Mein standard Konto");
        account.setIban("DE01234566789");
        account.setInterestDays("Montags");
        account.setInterestInterval(InterestInterval.YEARLY);
        account.setInterestRate(1);
        account.setKontoNumber("1313141442142");
        account.setType(AccountType.CHECKING_ACCOUNT);

        Depot depot = new Depot();
        depot.setOwner(defaultOwner);
        depot.setCreatedAt(Calendar.getInstance().getTime());
        depot.setName("Krypto-Depot");
        Depot.DepotBank depotBank = new Depot.DepotBank();
        depotBank.setName("Sparkasse Meißen");
        depot.setBank(depotBank);
        depot.setPortfolio(portfolio);

        defaultOwner.setAddress(defaultOwnerAddress);
        defaultOwner.setTaxInformation(defaultOwnerTaxInformation);
        defaultOwner.setPortfolios(Collections.singleton(portfolio));
        defaultOwner.setAccounts(Collections.singleton(account));
        defaultOwner.setDepots(Collections.singleton(depot));
        portfolio.setAccounts(List.of(account));
        portfolio.setDepots(List.of(depot));

        //
        ownerTablePane.getChildren().add(TableFactory.createOwnerTable(
                ownerTablePane,
                List.of(defaultOwner), //ownerService.getAllOwners()
                this
        ));

        setOwnerTreeView(new PortfolioTreeView(ownerTreeViewPane, new ArrayList<>(), portfolioManagementTabManager, true));
    }

    @FXML
    public void onClickCreateOwner(){
        PrimaryTabManager.loadFxml(
                "gui/tabs/portfoliomanagement/tab/owners/createOwnerDialog.fxml",
                "Neuen Inhaber anlegen",
                createOwnerButton,
                true,
                createOwnerDialog,
                false
        );
    }

    public void setOwnerTreeView(@NonNull PortfolioTreeView portfolioTreeView){
        ownerTreeViewPane.getChildren().clear();
        ownerTreeViewPane.getChildren().add(portfolioTreeView);
    }

    public PortfolioManagementTabManager getPortfolioManagementTabManager() {
        return portfolioManagementTabManager;
    }
}
