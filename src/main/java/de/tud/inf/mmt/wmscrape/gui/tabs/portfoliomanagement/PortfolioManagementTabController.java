package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement;

import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.dialog.InconsistenciesDialog;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Account;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Portfolio;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.AccountType;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.InterestInterval;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.MaritalState;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.State;
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
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners.dialog.FixOwnerInconsistenciesDialog;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners.owner.*;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.portfolios.PortfolioListController;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.portfolios.portfolio.*;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabController.createSubTab;

@Controller
public class PortfolioManagementTabController {

    public ToolBar breadcrumbContainer;

    @FXML
    private Button switchSceneButton;
    @FXML
    private TabPane portfolioManagementTabPane;
    @FXML
    private Label currentUserLabel;

    @Autowired
    private InconsistenciesDialog inconsistenciesDialogController;
    @Autowired
    private FixOwnerInconsistenciesDialog fixOwnerInconsistenciesDialog;
    @Autowired
    private FixAccountInconcistenciesDialog fixAccountInconcistenciesDialog;

    @Autowired
    OwnerRepository ownerRepository;
    @Autowired
    PortfolioRepository portfolioRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    AccountService accountService;

    @Autowired
    private PortfolioManagementTabManager portfolioManagementTabManager;
    @Autowired
    private PortfolioListController portfolioListController;
    @Autowired
    private DepotListController depotListController;
    @Autowired
    private AccountMenuController accountMenuController;
    @Autowired
    private OwnerController ownerController;

    @Autowired
    private PortfolioOverviewController portfolioOverviewController;
    @Autowired
    private PortfolioStrukturController portfolioStrukturController;
    @Autowired
    private PortfolioAnalyseController portfolioAnalyseController;
    @Autowired
    private PortfolioBenchmarkController portfolioBenchmarkController;
    @Autowired
    private PortfolioDepotsController portfolioDepotsController;
    @Autowired
    private PortfolioKontosController portfolioKontosController;

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


    private Tab portfoliosTab;
    private Tab portfolioOverviewTab;
    private Tab portfolioAnalyseTab;
    private Tab portfolioBenchmarkTab;
    private Tab portfolioStrukturTab;
    private Tab portfolioDepotsTab;
    private Tab portfolioKontosTab;

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

    public class ContexMenuItem {
        private String label;
        private Runnable action;

        public ContexMenuItem(String label, Runnable action) {
            this.label = label;
            this.action = action;
        }

        public String getLabel() {
            return label;
        }

        public Runnable getAction() {
            return action;
        }

        public void performAction() {
            if (action != null) {
                action.run();
            }
        }
    }

    public ContexMenuItem emptyContexMenuItem = new ContexMenuItem("", null);
    public List<ContexMenuItem> emptyContextMenuItemList = new ArrayList<>();

    public static final String TAB_PROPERTY_CONTROLLER = "controller";
    public static final String TAB_PROPERTY_ENTITY = "entity";
    public boolean isInitialized = false;

    /**
     * called when loading the fxml file
     */
    @FXML
    private void initialize() throws IOException {
        portfolioManagementTabManager.setPortfolioController(this);

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
        portfolioDepotsTab = createSubTab(
                "Depots",
                PrimaryTabManager.loadTabFxml("gui/tabs/portfoliomanagement/tab/portfolios/portfolio/portfolioDepots.fxml", portfolioDepotsController)
        );
        portfolioKontosTab = createSubTab(
                "Kontos",
                PrimaryTabManager.loadTabFxml("gui/tabs/portfoliomanagement/tab/portfolios/portfolio/portfolioKontos.fxml", portfolioKontosController)
        );

        portfoliosTab.getProperties().put(TAB_PROPERTY_CONTROLLER, portfolioListController);
        portfolioOverviewTab.getProperties().put(TAB_PROPERTY_CONTROLLER, portfolioOverviewController);
        portfolioAnalyseTab.getProperties().put(TAB_PROPERTY_CONTROLLER, portfolioAnalyseController);
        portfolioBenchmarkTab.getProperties().put(TAB_PROPERTY_CONTROLLER, portfolioBenchmarkController);
        portfolioStrukturTab.getProperties().put(TAB_PROPERTY_CONTROLLER, portfolioStrukturController);
        portfolioDepotsTab.getProperties().put(TAB_PROPERTY_CONTROLLER, portfolioDepotsController);
        portfolioKontosTab.getProperties().put(TAB_PROPERTY_CONTROLLER, portfolioKontosController);

        //
        Parent parent;

        depotListController = new DepotListController(portfolioManagementTabManager);
        parent = PrimaryTabManager.loadTabFxml("gui/tabs/portfoliomanagement/tab/depots/depots.fxml", depotListController);
        depotTab = createSubTab("Depots", parent);
        portfolioManagementTabPane.getTabs().add(depotTab);

        depotWertpapierController = new DepotWertpapierController(portfolioManagementTabManager);
        parent = PrimaryTabManager.loadTabFxml("gui/tabs/portfoliomanagement/tab/depots/depot/depotWertpapier.fxml", depotWertpapierController);
        depotWertpapierTab = createSubTab("Wertpapiere", parent);
        portfolioManagementTabPane.getTabs().add(depotWertpapierTab);

        depotStrukturController = new DepotStrukturController(portfolioManagementTabManager);
        parent = PrimaryTabManager.loadTabFxml("gui/tabs/portfoliomanagement/tab/depots/depot/depotStruktur.fxml", depotStrukturController);
        depotStrukturTab = createSubTab("Struktur", parent);
        portfolioManagementTabPane.getTabs().add(depotStrukturTab);

        depotPlanungController = new DepotPlanungController(portfolioManagementTabManager);
        parent = PrimaryTabManager.loadTabFxml("gui/tabs/portfoliomanagement/tab/depots/depot/depotPlanung.fxml", depotPlanungController);
        depotPlanungTab = createSubTab("Planung", parent);
        portfolioManagementTabPane.getTabs().add(depotPlanungTab);

        depotPlanungWertpapiervergleichController = new DepotPlanungWertpapiervergleichController(portfolioManagementTabManager);
        parent = PrimaryTabManager.loadTabFxml("gui/tabs/portfoliomanagement/tab/depots/depot/planung/depotPlanungWertpapierVergleich.fxml", depotPlanungWertpapiervergleichController);
        depotPlanungVergleichTab = createSubTab("Wertpapiervergleich", parent);
        portfolioManagementTabPane.getTabs().add(depotPlanungVergleichTab);

        depotPlanungOrderController = new DepotPlanungOrderController(portfolioManagementTabManager);
        parent = PrimaryTabManager.loadTabFxml("gui/tabs/portfoliomanagement/tab/depots/depot/planung/depotPlanungOrders.fxml", depotPlanungOrderController);
        depotPlanungOrdersTab = createSubTab("Orders", parent);
        portfolioManagementTabPane.getTabs().add(depotPlanungOrdersTab);

        depotTransaktionenController = new DepotTransaktionenController(portfolioManagementTabManager);
        parent = PrimaryTabManager.loadTabFxml("gui/tabs/portfoliomanagement/tab/depots/depot/depotTransaktionen.fxml", depotTransaktionenController);
        depotTransaktionenTab = createSubTab("Transaktionen", parent);
        portfolioManagementTabPane.getTabs().add(depotTransaktionenTab);

        depotAnlagestrategieController = new DepotAnlagestrategieController(portfolioManagementTabManager);
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
        portfolioTabs = List.of(portfolioOverviewTab, portfolioDepotsTab, portfolioStrukturTab, portfolioKontosTab, portfolioAnalyseTab, portfolioBenchmarkTab);
        kontoTabs = List.of();
        kontoTabs = List.of(kontoÜbersichtTab, kontoTransaktionenTab);
        ownerTabs = List.of(inhaberÜbersichtTab, inhaberVermögenTab, inhaberPortfoliosTab, inhaberDepotsTab, inhaberKontosTab);

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

            // idk why?
            if (newTab == depotPlanungTab) {
                showDepotPlanungTabs();
                changeBreadcrumbs(portfolioManagementTabManager.getCurrentlyDisplayedElements());
                addDepotPlanungBreadcrumbs();
            }

            // for the following part there must exist a controller in tab properties
            if (!newTab.getProperties().containsKey(TAB_PROPERTY_CONTROLLER)) return;
            Openable tabController = (Openable) newTab.getProperties().get(TAB_PROPERTY_CONTROLLER);

            // check on main-menus for database-inconsistencies
            if (isInitialized && isControllerInstanceOf(
                    tabController,
                    ownerController, accountMenuController, portfolioListController
            )) {
                while (ownerRepository.inconsistentOwnerExists() || accountRepository.inconsistentAccountsExists()) {
                    // owners
                    List<Long> inconsistentOwnerIds = new ArrayList<>();
                    inconsistentOwnerIds.addAll(ownerRepository.findAllByAddressOrTaxInformationIsInvalid());
                    inconsistentOwnerIds.addAll(ownerRepository.findAllByForenameIsNullOrAfternameIsNullOrCreatedAtIsNull());
                    inconsistentOwnerIds.addAll(ownerRepository.findAllByAddressContainingNullValues());
                    inconsistentOwnerIds.addAll(ownerRepository.findAllByTaxInformationContainingNullOrInvalidValues(MaritalState.getValuesAsString()));
                    inconsistentOwnerIds.addAll(ownerRepository.findAllByStateIsNotIn(State.getValuesAsString()));
                    inconsistentOwnerIds = new ArrayList<>(new HashSet<>(inconsistentOwnerIds)); // to remove duplicates

                    inconsistentOwnerIds.forEach(ownerId -> {
                        fixOwnerInconsistenciesDialog.setOwner(ownerRepository.reconstructOwner(ownerId));
                        inconsistenciesDialogController.setFxmlFilePath("gui/tabs/portfoliomanagement/tab/owners/dialog/fix_owner_inconsistencies_dialog.fxml");
                        inconsistenciesDialogController.setStageTitle("Inhaber inkonsistent");
                        inconsistenciesDialogController.setController(fixOwnerInconsistenciesDialog);
                        showInconsistenciesDialog();
                    });

                    // ToDo: Portfolios here!
                    // portfolios
                    /*
                    List<Long> inconsistentPortfolioIds = portfolioRepository.findAllByOwnerOrInvestmentguidelineIsInvalid();
                    inconsistentPortfolioIds.addAll(portfolioRepository.findAllByNullEntry());
                    inconsistentPortfolioIds.addAll(portfolioRepository.findAllByInvalidLiteral());
                    inconsistentPortfolioIds.addAll(portfolioRepository.findAllInvalidInvestmentguidelines());
                    inconsistentPortfolioIds = new ArrayList<>(new HashSet<>(inconsistentPortfolioIds)); // to remove duplicates

                    inconsistentPortfolioIds.forEach(portfolioId -> PrimaryTabManager.showDialogWithAction(
                            Alert.AlertType.WARNING,
                            "Portfolio inkonsistent",
                            String.format(
                                    "Auf Grund von Inkonsistenzen im Portfolio '%s' muss dieses sowie die enthaltenen Konten und Depots nun gelöscht werden.",
                                    portfolioRepository.findNameBy(portfolioId).get()
                            ),
                            null,
                            o -> portfolioRepository.deleteById(portfolioId)
                    ));
                     */

                    // accounts
                    List<Long> inconsistentAccountIds = new ArrayList<>();
                    inconsistentAccountIds.addAll(accountRepository.findAllByOwnerAndPortfolioIsInvalid());
                    inconsistentAccountIds.addAll(accountRepository.findByCurrencyCodeIsNullOrBalanceIsNullOrBankNameIsNullOrKontoNumberIsNullOrInterestRateIsNullOrIbanIsNullOrCreatedAtIsNull());
                    inconsistentAccountIds.addAll(accountRepository.findByStateNotInOrTypeNotInOrInterestIntervalNotIn(
                            State.getValuesAsString(), AccountType.getValuesAsString(), InterestInterval.getValuesAsString())
                    );
                    inconsistentAccountIds.addAll(accountRepository.findByInterestRateIsNotBetween0And100());
                    inconsistentAccountIds.addAll(accountRepository.findByInterestDaysIsNotBetween0And365());
                    inconsistentAccountIds.addAll(accountRepository.findByCurrencyIsNotIn(
                            Currency.getAvailableCurrencies().stream().map(Currency::getCurrencyCode).collect(Collectors.toList()))
                    );
                    inconsistentAccountIds.addAll(accountService.findByCurrencyHasNoExchangeCourse());
                    inconsistentAccountIds = new ArrayList<>(new HashSet<>(inconsistentAccountIds)); // to remove duplicates

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

    private void hideAllTabs() {
        portfolioManagementTabPane.getTabs().clear();
    }

    private void addTab(Tab tab) {
        portfolioManagementTabPane.getTabs().add(tab);
    }

    public void showDepotTabs() {
        removeBreadcrumbs();
        hideAllTabs();
        addTab(depotWertpapierTab);
        addTab(depotStrukturTab);
        addTab(depotPlanungTab);
        addTab(depotTransaktionenTab);
        addTab(depotAnlageStrategieTab);
        portfolioManagementTabPane.getSelectionModel().select(depotWertpapierTab);
    }

    public void showPortfolioManagementTabs() {
        removeBreadcrumbs();
        hideAllTabs();
        addTab(portfoliosTab);
        addTab(depotTab);
        addTab(kontoTab);
        addTab(inhaberTab);
        portfolioManagementTabPane.getSelectionModel().selectFirst();
    }

    public void showPortfolioTabs(Portfolio portfolio) {
        removeBreadcrumbs();
        hideAllTabs();

        portfolioTabs.forEach(tab -> {
            if (!tab.getProperties().containsKey(TAB_PROPERTY_ENTITY)) {
                tab.getProperties().put(TAB_PROPERTY_ENTITY, portfolio);
            } else {
                tab.getProperties().replace(TAB_PROPERTY_ENTITY, portfolio);
            }
            addTab(tab);
        });
        portfolioManagementTabPane.getSelectionModel().selectFirst();
    }

    public void showDepotPlanungTabs() {
        removeBreadcrumbs();
        hideAllTabs();
        addTab(depotPlanungVergleichTab);
        addTab(depotPlanungOrdersTab);
        portfolioManagementTabPane.getSelectionModel().select(depotPlanungVergleichTab);
    }

    public void showKontoTabs(Account account) {
        removeBreadcrumbs();
        hideAllTabs();

        kontoTabs.forEach(tab -> {
            if (!tab.getProperties().containsKey(TAB_PROPERTY_ENTITY)) {
                tab.getProperties().put(TAB_PROPERTY_ENTITY, account);
            } else {
                tab.getProperties().replace(TAB_PROPERTY_ENTITY, account);
            }
        });

        addTab(kontoÜbersichtTab);
        addTab(kontoTransaktionenTab);

        portfolioManagementTabPane.getSelectionModel().selectFirst();
    }

    public void showInhaberTabs(Owner owner) {
        removeBreadcrumbs();
        hideAllTabs();

        ownerTabs.forEach(tab -> {
            if (!tab.getProperties().containsKey(TAB_PROPERTY_ENTITY)) {
                tab.getProperties().put(TAB_PROPERTY_ENTITY, owner);
            } else {
                tab.getProperties().replace(TAB_PROPERTY_ENTITY, owner);
            }
            addTab(tab);
        });
        portfolioManagementTabPane.getSelectionModel().selectFirst();
    }

    public void createBreadcrumbInstance(String label, List<ContexMenuItem> captions, Runnable onLabelClick, String type) {
        Label label1 = new Label(label);

        ContextMenu contextMenu = new ContextMenu();

        for (ContexMenuItem caption : captions) {
            MenuItem menuItem = new MenuItem(caption.label);
            contextMenu.getItems().add(menuItem);
            contextMenu.setOnAction(event -> {
                removeBreadcrumbs();
                caption.performAction();
            });
        }

        // setContextMenu to label
        label1.setContextMenu(contextMenu);
        label1.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                onLabelClick.run();
            }
        });
        breadcrumbContainer.getItems().add(label1);
    }

    public void removeBreadcrumbs() {
        breadcrumbContainer.getItems().removeIf(item -> item instanceof Label);
    }

    public void changeBreadcrumbs(List<BreadcrumbElement> elements) {
        removeBreadcrumbs();
        for (BreadcrumbElement element : elements) {
            switch (element.type) {
                case "depot" -> addDepotBreadcrumbs(element.element, portfolioManagementTabManager.depotList);
                case "portfolio" ->
                        addPortfolioBreadcrumbs(element.element, portfolioManagementTabManager.portfolioList);
                case "owner" -> addOwnerBreadcrumbs(element.element, portfolioManagementTabManager.ownerList);
                case "konto" -> addKontoBreadcrumbs(element.element, portfolioManagementTabManager.kontoList);
            }
        }
    }

    //checks if breadcrumbs contain certain type
    public boolean breadcrumbsContainType(String type) {
        boolean containsType = false;
        for (BreadcrumbElement element : portfolioManagementTabManager.getCurrentlyDisplayedElements()) {
            if (element.type.equals(type)) {
                containsType = true;
                break; // Sobald ein passendes Objekt gefunden wurde, die Schleife beenden
            }
        }
        return containsType;
    }

    public void addDepotBreadcrumbs(String chosenDepot, String[] otherDepots) {
        List<ContexMenuItem> contextMenuList = new ArrayList<>();

        //if user is in portfolio view
        if (breadcrumbsContainType("portfolio")) {
            //set up context menu items
            for (String depotName : portfolioManagementTabManager.depotsOfPortfolio1List) {
                ContexMenuItem newItem = new ContexMenuItem(depotName, () -> {
                    portfolioManagementTabManager.removeLastCurrentlyDisplayedElement();
                    portfolioManagementTabManager.addCurrentlyDisplayedElement(new BreadcrumbElement(depotName, "depot"));
                });
                contextMenuList.add(newItem);
            }
            //create Breadcrumb for "/ Depots"
            createBreadcrumbInstance("/ Depots / ", emptyContextMenuItemList, () -> {
                showPortfolioTabs(null);
                portfolioManagementTabManager.removeLastCurrentlyDisplayedElement();
                changeBreadcrumbs(portfolioManagementTabManager.getCurrentlyDisplayedElements());
            }, "depot");
            //create Breadcrumb for specific depot
            createBreadcrumbInstance(chosenDepot, contextMenuList, () -> {
                showDepotTabs();
                changeBreadcrumbs(portfolioManagementTabManager.getCurrentlyDisplayedElements());
            }, "depot");
        } else {
            for (String depotName : otherDepots) {
                ContexMenuItem newItem = new ContexMenuItem(depotName, () -> portfolioManagementTabManager.setCurrentlyDisplayedElement(new BreadcrumbElement(depotName, "depot")));
                contextMenuList.add(newItem);
            }
            createBreadcrumbInstance("Depots / ", emptyContextMenuItemList, this::showPortfolioManagementTabs, "depot");
            createBreadcrumbInstance(chosenDepot, contextMenuList, () -> {
                showDepotTabs();
                addDepotBreadcrumbs(chosenDepot, otherDepots);
            }, "depot");
        }


    }

    public void addPortfolioBreadcrumbs(String chosenPortfolio, String[] otherPortfolios) {
        List<ContexMenuItem> contextMenuList = new ArrayList<>();
        for (String portfolioName : otherPortfolios) {
            ContexMenuItem newItem = new ContexMenuItem(portfolioName, () -> portfolioManagementTabManager.setCurrentlyDisplayedElement(new BreadcrumbElement(portfolioName, "portfolio")));
            contextMenuList.add(newItem);
        }
        createBreadcrumbInstance("Portfolios / ", emptyContextMenuItemList, this::showPortfolioManagementTabs, "portfolio");
        createBreadcrumbInstance(chosenPortfolio, contextMenuList, () -> {
            showPortfolioTabs(null);
            addPortfolioBreadcrumbs(chosenPortfolio, otherPortfolios);
        }, "portfolio");
    }

    public void addOwnerBreadcrumbs(String chosenOwner, String[] otherOwners) {
        List<ContexMenuItem> contextMenuList = new ArrayList<>();
        for (String ownerName : otherOwners) {
            ContexMenuItem newItem = new ContexMenuItem(ownerName, () -> portfolioManagementTabManager.setCurrentlyDisplayedElement(new BreadcrumbElement(ownerName, "portfolio")));
            contextMenuList.add(newItem);
        }
        createBreadcrumbInstance("Inhaber / ", emptyContextMenuItemList, this::showPortfolioManagementTabs, "owner");
        createBreadcrumbInstance(chosenOwner, contextMenuList, () -> {
            showInhaberTabs(null);
            addOwnerBreadcrumbs(chosenOwner, otherOwners);
        }, "owner");
    }

    public void addKontoBreadcrumbs(String chosenKonto, String[] otherKontos) {
        List<ContexMenuItem> contextMenuList = new ArrayList<>();

        if (breadcrumbsContainType("portfolio")) {
            for (String kontoName : portfolioManagementTabManager.kontosOfPortfolio1List) {
                ContexMenuItem newItem = new ContexMenuItem(kontoName, () -> {
                    portfolioManagementTabManager.removeLastCurrentlyDisplayedElement();
                    portfolioManagementTabManager.addCurrentlyDisplayedElement(new BreadcrumbElement(kontoName, "konto"));
                });
                contextMenuList.add(newItem);
            }
            createBreadcrumbInstance("/ Konten / ", emptyContextMenuItemList, () -> {
                showPortfolioTabs(null);
                portfolioManagementTabManager.removeLastCurrentlyDisplayedElement();
                changeBreadcrumbs(portfolioManagementTabManager.getCurrentlyDisplayedElements());
            }, "konto");
            createBreadcrumbInstance(chosenKonto, contextMenuList, () -> {
                showPortfolioTabs(null);
                changeBreadcrumbs(portfolioManagementTabManager.getCurrentlyDisplayedElements());
            }, "depot");
        } else {
            for (String kontoName : otherKontos) {
                ContexMenuItem newItem = new ContexMenuItem(kontoName, () -> portfolioManagementTabManager.setCurrentlyDisplayedElement(new BreadcrumbElement(kontoName, "portfolio")));
                contextMenuList.add(newItem);
            }
            createBreadcrumbInstance("Konten / ", emptyContextMenuItemList, this::showPortfolioManagementTabs, "konto");
            createBreadcrumbInstance(chosenKonto, contextMenuList, () -> {
                showKontoTabs(null);
                addKontoBreadcrumbs(chosenKonto, otherKontos);
            }, "konto");
        }

    }

    public void addDepotPlanungBreadcrumbs() {
        createBreadcrumbInstance(" / Planung", emptyContextMenuItemList, () -> {
        }, "");
    }

    // region Getters
    public OwnerController getOwnerController() {
        return ownerController;
    }

    public AccountMenuController getAccountMenuController() {
        return accountMenuController;
    }

    public PortfolioListController getPortfolioListController() {
        return portfolioListController;
    }

    public PortfolioStrukturController getPortfolioStrukturController() {
        return portfolioStrukturController;
    }

    public PortfolioAnalyseController getPortfolioAnalyseController() {
        return portfolioAnalyseController;
    }

    public PortfolioBenchmarkController getPortfolioBenchmarkController() {
        return portfolioBenchmarkController;
    }

    public PortfolioDepotsController getPortfolioDepotsController() {
        return portfolioDepotsController;
    }

    public PortfolioKontosController getPortfolioKontosController() {
        return portfolioKontosController;
    }

    public DepotPlanungController getDepotPlanungController() {
        return depotPlanungController;
    }

    public DepotWertpapierController getDepotWertpapierController() {
        return depotWertpapierController;
    }

    public DepotStrukturController getDepotStrukturController() {
        return depotStrukturController;
    }

    public DepotTransaktionenController getDepotTransaktionenController() {
        return depotTransaktionenController;
    }

    public DepotAnlagestrategieController getDepotAnlagestrategieController() {
        return depotAnlagestrategieController;
    }

    public DepotPlanungWertpapiervergleichController getDepotPlanungWertpapiervergleichController() {
        return depotPlanungWertpapiervergleichController;
    }

    public DepotPlanungOrderController getDepotPlanungOrderController() {
        return depotPlanungOrderController;
    }

    public KontoOverviewController getKontoOverviewController() {
        return kontoOverviewController;
    }

    public KontoTransactionsController getKontoTransactionsController() {
        return kontoTransactionsController;
    }

    public OwnerOverviewController getOwnerOverviewController() {
        return ownerOverviewController;
    }

    public OwnerVermögenController getOwnerVermögenController() {
        return ownerVermögenController;
    }

    public OwnerDepotsController getOwnerDepotsController() {
        return ownerDepotsController;
    }

    public OwnerKontosController getOwnerKontosController() {
        return ownerKontosController;
    }

    public OwnerPortfoliosController getOwnerPortfoliosController() {
        return ownerPortfoliosController;
    }

    public Tab getPortfolioOverviewTab() {
        return portfolioOverviewTab;
    }

    public Tab getPortfolioAnalyseTab() {
        return portfolioAnalyseTab;
    }

    public Tab getPortfolioBenchmarkTab() {
        return portfolioBenchmarkTab;
    }

    public Tab getPortfolioStrukturTab() {
        return portfolioStrukturTab;
    }

    public Tab getPortfolioDepotsTab() {
        return portfolioDepotsTab;
    }

    public Tab getPortfolioKontosTab() {
        return portfolioKontosTab;
    }

    public Tab getDepotWertpapierTab() {
        return depotWertpapierTab;
    }

    public Tab getDepotStrukturTab() {
        return depotStrukturTab;
    }

    public Tab getDepotPlanungTab() {
        return depotPlanungTab;
    }

    public Tab getDepotPlanungVergleichTab() {
        return depotPlanungVergleichTab;
    }

    public Tab getDepotPlanungOrdersTab() {
        return depotPlanungOrdersTab;
    }

    public Tab getDepotTransaktionenTab() {
        return depotTransaktionenTab;
    }

    public Tab getDepotAnlageStrategieTab() {
        return depotAnlageStrategieTab;
    }

    public Tab getKontoÜbersichtTab() {
        return kontoÜbersichtTab;
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
