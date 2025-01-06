package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement;

import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabController;
import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.dialog.InconsistenciesDialog;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Account;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Depot;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Portfolio;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.BreadcrumbElementType;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.interfaces.Openable;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.repository.AccountRepository;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.repository.OwnerRepository;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.repository.PortfolioRepository;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.depots.DepotListController;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.depots.depot.*;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.depots.depot.planung.DepotPlanungOrderController;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.depots.depot.planung.DepotPlanungWertpapiervergleichController;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.kontos.AccountMenuController;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.kontos.AccountService;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.kontos.dialog.FixAccountInconcistenciesDialog;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.kontos.konto.KontoOverviewController;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.kontos.konto.KontoTransactionsController;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners.OwnerController;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners.OwnerService;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners.dialog.FixOwnerInconsistenciesDialog;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners.owner.*;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.portfolios.PortfolioListController;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.portfolios.PortfolioService;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.portfolios.dialog.FixPortfolioInconsistenciesDialog;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.portfolios.portfolio.*;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.BreadCrumbBar;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

import static de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabController.createSubTab;

@Controller
public class PortfolioManagementTabController {

    @Autowired
    private PortfolioManagementTabManager portfolioManagementTabManager;
    @Autowired
    private PrimaryTabController primaryTabController;

    // flag to check if this controller is initialized
    public boolean isInitialized = false;

    // Views of this management
    @FXML
    private TabPane portfolioManagementTabPane;
    @FXML
    private VBox breadCrumbToolbar;
    public final BreadCrumbBar breadCrumbBar = new BreadCrumbBar();

    // Controllers of some dialogs for inconsistencies
    @Autowired
    private InconsistenciesDialog inconsistenciesDialogController;
    @Autowired
    private FixOwnerInconsistenciesDialog fixOwnerInconsistenciesDialog;
    @Autowired
    private FixAccountInconcistenciesDialog fixAccountInconcistenciesDialog;
    @Autowired
    private FixPortfolioInconsistenciesDialog fixPortfolioInconsistenciesDialog;

    // Repositories
    @Autowired
    OwnerRepository ownerRepository;
    @Autowired
    PortfolioRepository portfolioRepository;
    @Autowired
    AccountRepository accountRepository;

    // Services
    @Autowired
    private PortfolioService portfolioService;
    @Autowired
    private OwnerService ownerService;
    @Autowired
    AccountService accountService;

    // Controllers of all main-menus
    @Autowired
    private PortfolioListController portfolioListController;
    @Autowired
    private DepotListController depotListController;
    @Autowired
    private AccountMenuController accountMenuController;
    @Autowired
    private OwnerController ownerController;

    // Controllers of all sub-menus
    @Autowired
    private PortfolioOverviewController portfolioOverviewController;
    @Autowired
    private PortfolioStrukturController portfolioStrukturController;
    @Autowired
    private PortfolioAnalyseController portfolioAnalyseController;
    @Autowired
    private PortfolioBenchmarkController portfolioBenchmarkController;

    @Autowired
    private DepotPlanungController depotPlanungController;
    @Autowired
    private DepotWertpapierController depotWertpapierController;
    @Autowired
    private DepotStrukturController depotStrukturController;
    @Autowired
    private DepotTransaktionenController depotTransaktionenController;
    @Autowired
    private DepotAnlagestrategieController depotAnlagestrategieController;
    @Autowired
    private DepotPlanungWertpapiervergleichController depotPlanungWertpapiervergleichController;
    @Autowired
    private DepotPlanungOrderController depotPlanungOrderController;

    @Autowired
    private KontoOverviewController kontoOverviewController;
    @Autowired
    private KontoTransactionsController kontoTransactionsController;

    @Autowired
    private OwnerOverviewController ownerOverviewController;
    @Autowired
    private OwnerVermögenController ownerVermögenController;
    @Autowired
    private OwnerDepotsController ownerDepotsController;
    @Autowired
    private OwnerKontosController ownerKontosController;
    @Autowired
    private OwnerPortfoliosController ownerPortfoliosController;

    // All tabs
    private Tab portfoliosTab;
    private Tab portfolioOverviewTab;
    private Tab portfolioAnalyseTab;
    private Tab portfolioBenchmarkTab;
    private Tab portfolioStrukturTab;

    private Tab depotTab;
    private Tab depotWertpapierTab;
    private Tab depotStrukturTab;
    private Tab depotPlanungTab;
    private Tab depotPlanungVergleichTab;
    private Tab depotPlanungOrdersTab;
    private Tab depotTransaktionenTab;
    private Tab depotAnlageStrategieTab;

    private Tab kontoTab;
    private Tab kontoÜbersichtTab;
    private Tab kontoTransaktionenTab;

    private Tab inhaberTab;
    private Tab inhaberÜbersichtTab;
    private Tab inhaberVermögenTab;
    private Tab inhaberDepotsTab;
    private Tab inhaberPortfoliosTab;
    private Tab inhaberKontosTab;

    private Set<Tab> depotTabs;
    private List<Tab> portfolioTabs;
    private List<Tab> kontoTabs;
    private List<Tab> ownerTabs;

    // Properties for tabs
    public static final String TAB_PROPERTY_CONTROLLER = "controller";
    public static final String TAB_PROPERTY_ENTITY = "entity";

    /**
     * called when loading the fxml file
     */
    @FXML
    private void initialize() throws IOException {
        portfolioManagementTabManager.setPortfolioController(this);
        breadCrumbToolbar.getChildren().add(breadCrumbBar);

        // Portfolio-Management
        portfoliosTab = createSubTab(
                "Portfolios",
                PrimaryTabManager.loadTabFxml("gui/tabs/portfoliomanagement/tab/portfolios/portfolios.fxml", portfolioListController)
        );
        portfolioOverviewTab = createSubTab(
                "Übersicht",
                PrimaryTabManager.loadTabFxml("gui/tabs/portfoliomanagement/tab/portfolios/portfolio/portfolioOverview.fxml", portfolioOverviewController)
        );
        portfolioAnalyseTab = createSubTab(
                "Analyse",
                PrimaryTabManager.loadTabFxml("gui/tabs/portfoliomanagement/tab/portfolios/portfolio/portfolioAnalyse.fxml", portfolioAnalyseController)
        );
        portfolioBenchmarkTab = createSubTab(
                "Benchmarks",
                PrimaryTabManager.loadTabFxml("gui/tabs/portfoliomanagement/tab/portfolios/portfolio/portfolioBenchmarks.fxml", portfolioBenchmarkController)
        );
        portfolioStrukturTab = createSubTab(
                "Struktur",
                PrimaryTabManager.loadTabFxml("gui/tabs/portfoliomanagement/tab/portfolios/portfolio/portfolioStruktur.fxml", portfolioStrukturController)
        );

        portfoliosTab.getProperties().put(TAB_PROPERTY_CONTROLLER, portfolioListController);
        portfolioOverviewTab.getProperties().put(TAB_PROPERTY_CONTROLLER, portfolioOverviewController);
        portfolioAnalyseTab.getProperties().put(TAB_PROPERTY_CONTROLLER, portfolioAnalyseController);
        portfolioBenchmarkTab.getProperties().put(TAB_PROPERTY_CONTROLLER, portfolioBenchmarkController);
        portfolioStrukturTab.getProperties().put(TAB_PROPERTY_CONTROLLER, portfolioStrukturController);

        //
        Parent parent;

        parent = PrimaryTabManager.loadTabFxml("gui/tabs/portfoliomanagement/tab/depots/depots.fxml", depotListController);
        depotTab = createSubTab("Depots", parent);
        portfolioManagementTabPane.getTabs().add(depotTab);

        parent = PrimaryTabManager.loadTabFxml("gui/tabs/portfoliomanagement/tab/depots/depot/depotWertpapier.fxml", depotWertpapierController);
        depotWertpapierTab = createSubTab("Wertpapiere", parent);
        portfolioManagementTabPane.getTabs().add(depotWertpapierTab);

        parent = PrimaryTabManager.loadTabFxml("gui/tabs/portfoliomanagement/tab/depots/depot/depotStruktur.fxml", depotStrukturController);
        depotStrukturTab = createSubTab("Struktur", parent);
        portfolioManagementTabPane.getTabs().add(depotStrukturTab);

        parent = PrimaryTabManager.loadTabFxml("gui/tabs/portfoliomanagement/tab/depots/depot/depotPlanung.fxml", depotPlanungController);
        depotPlanungTab = createSubTab("Planung", parent);
        portfolioManagementTabPane.getTabs().add(depotPlanungTab);

        parent = PrimaryTabManager.loadTabFxml("gui/tabs/portfoliomanagement/tab/depots/depot/planung/depotPlanungWertpapierVergleich.fxml", depotPlanungWertpapiervergleichController);
        depotPlanungVergleichTab = createSubTab("Wertpapiervergleich", parent);
        portfolioManagementTabPane.getTabs().add(depotPlanungVergleichTab);

        parent = PrimaryTabManager.loadTabFxml("gui/tabs/portfoliomanagement/tab/depots/depot/planung/depotPlanungOrders.fxml", depotPlanungOrderController);
        depotPlanungOrdersTab = createSubTab("Orders", parent);
        portfolioManagementTabPane.getTabs().add(depotPlanungOrdersTab);

        parent = PrimaryTabManager.loadTabFxml("gui/tabs/portfoliomanagement/tab/depots/depot/depotTransaktionen.fxml", depotTransaktionenController);
        depotTransaktionenTab = createSubTab("Transaktionen", parent);
        portfolioManagementTabPane.getTabs().add(depotTransaktionenTab);

        parent = PrimaryTabManager.loadTabFxml("gui/tabs/portfoliomanagement/tab/depots/depot/depotAnlagestrategie.fxml", depotAnlagestrategieController);
        depotAnlageStrategieTab = createSubTab("Anlagestrategie", parent);
        portfolioManagementTabPane.getTabs().add(depotAnlageStrategieTab);

        // Account-Management
        kontoTab = createSubTab(
                "Konten",
                PrimaryTabManager.loadTabFxml("gui/tabs/portfoliomanagement/tab/kontos/kontos.fxml", accountMenuController)
        );
        kontoÜbersichtTab = createSubTab(
                "Übersicht",
                PrimaryTabManager.loadTabFxml("gui/tabs/portfoliomanagement/tab/kontos/konto/kontoOverview.fxml", kontoOverviewController)
        );
        kontoTransaktionenTab = createSubTab(
                "Transaktionen",
                PrimaryTabManager.loadTabFxml("gui/tabs/portfoliomanagement/tab/kontos/konto/kontoTransactions.fxml", kontoTransactionsController)
        );

        kontoTab.getProperties().put(TAB_PROPERTY_CONTROLLER, accountMenuController);
        kontoÜbersichtTab.getProperties().put(TAB_PROPERTY_CONTROLLER, kontoOverviewController);
        kontoTransaktionenTab.getProperties().put(TAB_PROPERTY_CONTROLLER, kontoTransactionsController);

        // Owner-Management
        inhaberTab = createSubTab(
                "Inhaber",
                PrimaryTabManager.loadTabFxml("gui/tabs/portfoliomanagement/tab/owners/owners.fxml", ownerController)
        );
        inhaberÜbersichtTab = createSubTab(
                "Übersicht",
                PrimaryTabManager.loadTabFxml("gui/tabs/portfoliomanagement/tab/owners/owner/ownerOverview.fxml", ownerOverviewController)
        );
        inhaberVermögenTab = createSubTab(
                "Vermögen",
                PrimaryTabManager.loadTabFxml("gui/tabs/portfoliomanagement/tab/owners/owner/ownerVermögen.fxml", ownerVermögenController)
        );
        inhaberPortfoliosTab = createSubTab(
                "Portfolios",
                PrimaryTabManager.loadTabFxml("gui/tabs/portfoliomanagement/tab/owners/owner/ownerPortfolios.fxml", ownerPortfoliosController)
        );
        inhaberKontosTab = createSubTab(
                "Kontos",
                PrimaryTabManager.loadTabFxml("gui/tabs/portfoliomanagement/tab/owners/owner/ownerKontos.fxml", ownerKontosController)
        );
        inhaberDepotsTab = createSubTab(
                "Depots",
                PrimaryTabManager.loadTabFxml("gui/tabs/portfoliomanagement/tab/owners/owner/ownerDepots.fxml", ownerDepotsController)
        );

        inhaberTab.getProperties().put(TAB_PROPERTY_CONTROLLER, ownerController);
        inhaberÜbersichtTab.getProperties().put(TAB_PROPERTY_CONTROLLER, ownerOverviewController);
        inhaberVermögenTab.getProperties().put(TAB_PROPERTY_CONTROLLER, ownerVermögenController);
        inhaberPortfoliosTab.getProperties().put(TAB_PROPERTY_CONTROLLER, ownerPortfoliosController);
        inhaberKontosTab.getProperties().put(TAB_PROPERTY_CONTROLLER, ownerKontosController);
        inhaberDepotsTab.getProperties().put(TAB_PROPERTY_CONTROLLER, ownerDepotsController);

        // Init tab-lists
        portfolioTabs = List.of(portfolioOverviewTab, portfolioStrukturTab, portfolioAnalyseTab, portfolioBenchmarkTab);
        kontoTabs = List.of(kontoÜbersichtTab, kontoTransaktionenTab);
        ownerTabs = List.of(inhaberÜbersichtTab, inhaberVermögenTab, inhaberPortfoliosTab, inhaberDepotsTab, inhaberKontosTab);
        depotTabs = Set.of(depotWertpapierTab, depotStrukturTab, depotPlanungTab, depotTransaktionenTab, depotAnlageStrategieTab);

        // Init and show default tabs
        portfolioManagementTabPane.setStyle(
                "-fx-tab-min-height: 30px;"
                        + "-fx-tab-max-height: 30px;"
                        + "-fx-tab-min-width: 150px;"
                        + "-fx-tab-max-width: 150px;"
                        + "-fx-alignment: CENTER;"
        );
        portfolioManagementTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if (newTab == null) return;

            // for the following part there must exist a controller in tab properties
            if (!newTab.getProperties().containsKey(TAB_PROPERTY_CONTROLLER)) return;
            Openable tabController = (Openable) newTab.getProperties().get(TAB_PROPERTY_CONTROLLER);

            // check on main-menus for database-inconsistencies
            if (isInitialized && isControllerInstanceOf(
                    tabController,
                    ownerController, accountMenuController, portfolioListController
            )) {
                while (ownerRepository.inconsistentOwnerExists()
                        || accountRepository.inconsistentAccountsExists()
                        || portfolioRepository.inconsistentPortfoliosExists()) {
                    // owners
                    Set<Long> inconsistentOwnerIds = ownerRepository.getInconsistentOwnerIds();
                    inconsistentOwnerIds.forEach(ownerId -> {
                        fixOwnerInconsistenciesDialog.setOwner(ownerRepository.reconstructOwner(ownerId));
                        inconsistenciesDialogController.setFxmlFilePath("gui/tabs/portfoliomanagement/tab/owners/dialog/fix_owner_inconsistencies_dialog.fxml");
                        inconsistenciesDialogController.setStageTitle("Inhaber inkonsistent");
                        inconsistenciesDialogController.setController(fixOwnerInconsistenciesDialog);
                        showInconsistenciesDialog();
                    });

                    // portfolios
                    Set<Long> inconsistentPortfolioIds = portfolioRepository.getInconsistentPortfolioIds();
                    inconsistentPortfolioIds.forEach(portfolioId -> {
                        fixPortfolioInconsistenciesDialog.setPortfolio(portfolioRepository.reconstructPortfolio(portfolioId));
                        inconsistenciesDialogController.setFxmlFilePath("gui/tabs/portfoliomanagement/tab/portfolios/dialog/fix_portfolio_inconsistencies_dialog.fxml");
                        inconsistenciesDialogController.setStageTitle("Portfolio inkonsistent");
                        inconsistenciesDialogController.setController(fixPortfolioInconsistenciesDialog);
                        showInconsistenciesDialog();
                    });

                    // accounts
                    Set<Long> inconsistentAccountIds = accountRepository.getInconsistentAccountIds();
                    inconsistentAccountIds.addAll(accountService.findByCurrencyHasNoExchangeCourse());

                    inconsistentAccountIds.forEach(accountId -> {
                        fixAccountInconcistenciesDialog.setAccount(accountRepository.reconstructAccount(accountId));
                        inconsistenciesDialogController.setFxmlFilePath("gui/tabs/portfoliomanagement/tab/kontos/dialog/fix_account_inconsistencies_dialog.fxml");
                        inconsistenciesDialogController.setStageTitle("Konto inkonsistent");
                        inconsistenciesDialogController.setController(fixAccountInconcistenciesDialog);
                        showInconsistenciesDialog();
                    });
                }
            }

            // call open method of controller to refresh data
            tabController.open();
            isInitialized = true;
        });

        portfolioManagementTabPane.getTabs().addAll(portfoliosTab, depotTab, kontoTab, inhaberTab);
        showPortfolioManagementTabs();
    }

    private boolean isControllerInstanceOf(@NonNull Object controller, @NonNull Object... controllers) {
        for (Object c : controllers) {
            if (controller.getClass().isInstance(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Navigates back to the predefined tab after deletion of an entity.
     * If portfolio it navigates back to the portfolio management tabs.
     * If owner it navigates back to the portfolio management tabs and selects the owner tab.
     * If account it navigates back to the portfolio of the account.
     * If depot it navigates back to the portfolio of the depot.
     * @param fromEntity The entity that was deleted.
     */
    public void navigateBackAfterDeletion(@NonNull Object fromEntity) {
        if (fromEntity instanceof Portfolio) {
            showPortfolioManagementTabs();
        } else if (fromEntity instanceof Owner) {
            showPortfolioManagementTabs();
            portfolioManagementTabPane.getSelectionModel().select(inhaberTab);
        } else if (fromEntity instanceof Account) {
            Navigator.navigateToPortfolio(portfolioManagementTabManager, ((Account) fromEntity).getPortfolio(), true);
        } else if (fromEntity instanceof Depot) {
            Navigator.navigateToPortfolio(portfolioManagementTabManager, ((Depot) fromEntity).getPortfolio(), true);
        } else {
            throw new IllegalArgumentException("Unknown entity type: " + fromEntity.getClass().getName());
        }
    }

    public void showPortfolioManagementTabs() {
        breadCrumbBar.clearBreadcrumbs();
        portfolioManagementTabPane.getTabs().setAll(portfoliosTab, depotTab, kontoTab, inhaberTab);
        portfolioManagementTabPane.getSelectionModel().selectFirst();
    }

    // region Show specific entity tabs
    public void showDepotTabs() {
        portfolioManagementTabPane.getTabs().setAll(depotTabs);
        portfolioManagementTabPane.getSelectionModel().select(depotWertpapierTab);
    }

    /**
     * @param portfolio The portfolio to show the tabs for.
     */
    public void showPortfolioTabs(Portfolio portfolio) {
        portfolioTabs.forEach(tab -> {
            if (!tab.getProperties().containsKey(TAB_PROPERTY_ENTITY)) {
                tab.getProperties().put(TAB_PROPERTY_ENTITY, portfolio);
            } else {
                tab.getProperties().replace(TAB_PROPERTY_ENTITY, portfolio);
            }
        });
        portfolioManagementTabPane.getTabs().setAll(portfolioTabs);
        portfolioManagementTabPane.getSelectionModel().selectFirst();
    }

    /**
     * @param account The account to show the tabs for.
     */
    public void showKontoTabs(Account account) {
        kontoTabs.forEach(tab -> {
            if (!tab.getProperties().containsKey(TAB_PROPERTY_ENTITY)) {
                tab.getProperties().put(TAB_PROPERTY_ENTITY, account);
            } else {
                tab.getProperties().replace(TAB_PROPERTY_ENTITY, account);
            }

        });
        portfolioManagementTabPane.getTabs().setAll(kontoTabs);
        portfolioManagementTabPane.getSelectionModel().selectFirst();
    }

    /**
     * @param owner The owner to show the tabs for.
     */
    public void showInhaberTabs(Owner owner) {
        ownerTabs.forEach(tab -> {
            if (!tab.getProperties().containsKey(TAB_PROPERTY_ENTITY)) {
                tab.getProperties().put(TAB_PROPERTY_ENTITY, owner);
            } else {
                tab.getProperties().replace(TAB_PROPERTY_ENTITY, owner);
            }
        });
        portfolioManagementTabPane.getTabs().setAll(ownerTabs);
        portfolioManagementTabPane.getSelectionModel().selectFirst();
    }
    // endregion

    public <T> void createBreadcrumbInstance(@NonNull String label, @NonNull List<T> contextMenuItems,
                                             @NonNull Consumer<T> contextMenuItemAction, @NonNull Runnable onLabelClick,
                                             @NonNull BreadcrumbElementType type) {
        // add root crumb if no root crumb is present
        if (!breadCrumbBar.hasRootCrumble()) {
            switch (type) {
                case OWNER -> breadCrumbBar.addRootCrumb(
                        "Inhaber-Verwaltung",
                        () -> {
                            showPortfolioManagementTabs();
                            portfolioManagementTabPane.getSelectionModel().select(inhaberTab);
                        });
                case PORTFOLIO -> breadCrumbBar.addRootCrumb(
                        "Portfolio-Verwaltung",
                        () -> {
                            showPortfolioManagementTabs();
                            portfolioManagementTabPane.getSelectionModel().select(portfoliosTab);
                        });
                case ACCOUNT -> breadCrumbBar.addRootCrumb(
                        "Konto-Verwaltung",
                        () -> {
                            showPortfolioManagementTabs();
                            portfolioManagementTabPane.getSelectionModel().select(kontoTab);
                        });
                case DEPOT -> breadCrumbBar.addRootCrumb(
                        "Depot-Verwaltung",
                        () -> {
                            showPortfolioManagementTabs();
                            portfolioManagementTabPane.getSelectionModel().select(depotTab);
                        });
            }
        }

        // create and add crumb
        breadCrumbBar.addCrumb(label, type, contextMenuItems, contextMenuItemAction, onLabelClick);
    }

    public void changeBreadcrumbs(List<BreadcrumbElement> elements) {
        breadCrumbBar.clearBreadcrumbs();

        for (BreadcrumbElement element : elements) {
            switch (element.type) {
                case DEPOT -> addDepotBreadcrumbs((Depot) element.element);
                case PORTFOLIO -> addPortfolioBreadcrumbs((Portfolio) element.element);
                case OWNER -> addOwnerBreadcrumbs((Owner) element.element);
                case ACCOUNT -> addKontoBreadcrumbs((Account) element.element);
            }
        }
    }

    // region Add breadcrumbs
    public void addDepotBreadcrumbs(@NonNull Depot chosenDepot) {
        List<Depot> contextMenuItems;

        // ToDo: implement later!
        // check if the breadcrumb already contains a portfolio, so we can filter the accounts by the portfolio
        if (breadCrumbBar.containsType(BreadcrumbElementType.PORTFOLIO)) {
            contextMenuItems = chosenDepot.getPortfolio().getDepots();
        } else {
            contextMenuItems = new ArrayList<>();
        }

        // ToDo: implement later!
        createBreadcrumbInstance(
                chosenDepot.toString(),
                contextMenuItems,
                depot -> showDepotTabs(),
                this::showDepotTabs,
                BreadcrumbElementType.DEPOT
        );
    }

    public void addPortfolioBreadcrumbs(@NonNull Portfolio chosenPortfolio) {
        List<Portfolio> contextMenuItems;

        // check if the breadcrumb already contains an owner, so we can filter the accounts by the owner
        if (breadCrumbBar.containsType(BreadcrumbElementType.OWNER)) {
            // reload the account from the database to get his portfolio
            contextMenuItems = portfolioService.findById(chosenPortfolio.getId())
                    .getOwner()
                    .getPortfolios()
                    .stream().toList();
        } else {
            contextMenuItems = portfolioService.getAll();
        }

        createBreadcrumbInstance(
                chosenPortfolio.toString(),
                contextMenuItems,
                this::showPortfolioTabs,
                () -> showPortfolioTabs(chosenPortfolio),
                BreadcrumbElementType.PORTFOLIO
        );
    }

    public void addOwnerBreadcrumbs(@NonNull Owner chosenOwner) {
        createBreadcrumbInstance(
                chosenOwner.toString(),
                ownerService.getAll(),
                this::showInhaberTabs,
                () -> showInhaberTabs(chosenOwner),
                BreadcrumbElementType.OWNER
        );
    }

    public void addKontoBreadcrumbs(@NonNull Account chosenKonto) {
        List<Account> contextMenuItems;

        // check if the breadcrumb already contains a portfolio, so we can filter the accounts by the portfolio
        if (breadCrumbBar.containsType(BreadcrumbElementType.PORTFOLIO)) {
            // reload the account from the database to get the accounts of the portfolio
            contextMenuItems = accountService.getAccountById(chosenKonto.getId())
                    .getPortfolio()
                    .getAccounts()
                    .stream().toList();
        } else {
            contextMenuItems = accountService.getAll();
        }

        createBreadcrumbInstance(
                chosenKonto.toString(),
                contextMenuItems,
                this::showKontoTabs,
                () -> showKontoTabs(chosenKonto),
                BreadcrumbElementType.ACCOUNT
        );
    }
    // endregion

    // region Getters
    public PrimaryTabController getPrimaryTabController() {
        return primaryTabController;
    }

    public OwnerController getOwnerController() {
        return ownerController;
    }

    public AccountMenuController getAccountMenuController() {
        return accountMenuController;
    }

    public PortfolioListController getPortfolioListController() {
        return portfolioListController;
    }

    public Tab getPortfolioOverviewTab() {
        return portfolioOverviewTab;
    }

    public Tab getKontoTransaktionenTab() {
        return kontoTransaktionenTab;
    }

    public Tab getInhaberÜbersichtTab() {
        return inhaberÜbersichtTab;
    }

    public Tab getInhaberVermögenTab() {
        return inhaberVermögenTab;
    }

    public Tab getInhaberDepotsTab() {
        return inhaberDepotsTab;
    }

    public Tab getInhaberPortfoliosTab() {
        return inhaberPortfoliosTab;
    }

    public Tab getInhaberKontosTab() {
        return inhaberKontosTab;
    }

    public Tab getKontoOverviewTab() {
        return kontoÜbersichtTab;
    }

    public TabPane getPortfolioManagementTabPane() {
        return portfolioManagementTabPane;
    }
    // endregion

    private void showInconsistenciesDialog() {
        PrimaryTabManager.loadFxml(
                "gui/tabs/portfoliomanagement/dialog/inconsistencies_dialog.fxml",
                "",
                portfolioManagementTabPane,
                true,
                inconsistenciesDialogController,
                true
        );
    }
}
