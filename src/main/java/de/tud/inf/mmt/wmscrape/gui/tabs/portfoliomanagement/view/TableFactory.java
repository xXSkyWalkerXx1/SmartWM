package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.BreadcrumbElement;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.Navigator;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.*;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.State;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners.OwnerController;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners.owner.OwnerDepotsController;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners.owner.OwnerPortfoliosController;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.portfolios.PortfolioListController;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.util.Callback;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Factory which produces predefined tables.<br>
 * <br>
 * The factory-methods should have a structure like the following example:
 * <pre>
 * {@code
 * TableBuilder<T> tableBuilder = new TableBuilder<>();
 *
 * tableBuilder.addColumn()
 * // add some more columns here...
 *
 * tableBuilder.setActionOnSingleClickRow()
 * tableBuilder.setActionOnDoubleClickRow()
 *
 * tableBuilder.addRowContextMenuItem()
 * // add some more items here...
 *
 * return tableBuilder.getResult();
 * }
 * </pre>
 */
public class TableFactory {

    /**
     * @param parent JavaFX node-based UI-Controls and all layout containers (f.e. Pane).
     * @param tableItems Items of table.
     */
    public static TableView<Owner> createOwnerTable(
            @NonNull Region parent,
            @NonNull List<Owner> tableItems,
            @NonNull OwnerController ownerController){

        TableBuilder<Owner> tableBuilder = new TableBuilder<>(parent, tableItems);
        Consumer<Owner> openOwnerOverviewAction = owner -> {
            Navigator.navigateToOwner(ownerController.getPortfolioManagementTabManager(), owner);

            ownerController
                    .getPortfolioManagementTabManager()
                    .setCurrentlyDisplayedElement(new BreadcrumbElement(owner.toString(), "owner"));
        };

        tableBuilder.addColumn(
                "Vorname",
                0.25f,
                (Callback<TableColumn.CellDataFeatures<Owner, String>, ObservableValue<String>>) ownerCellDataFeatures
                        -> new SimpleStringProperty(ownerCellDataFeatures.getValue().getForename())
        );
        tableBuilder.addColumn(
                "Nachname",
                0.25f,
                (Callback<TableColumn.CellDataFeatures<Owner, String>, ObservableValue<String>>) ownerCellDataFeatures
                        -> new SimpleStringProperty(ownerCellDataFeatures.getValue().getAftername())
        );
        tableBuilder.addColumn(
                "Status",
                0.15f,
                (Callback<TableColumn.CellDataFeatures<Owner, String>, ObservableValue<String>>) ownerCellDataFeatures
                        -> new SimpleStringProperty(ownerCellDataFeatures.getValue().getState().getDisplayText())
        );
        tableBuilder.addColumn(
                "Bemerkung",
                0.35f,
                (Callback<TableColumn.CellDataFeatures<Owner, String>, ObservableValue<String>>) ownerCellDataFeatures
                        -> new SimpleStringProperty(ownerCellDataFeatures.getValue().getNotice())
        );

        tableBuilder.setActionOnSingleClickRow(owner -> ownerController.setOwnerTreeView(new PortfolioTreeView(
                parent,
                owner.getPortfolios().stream().toList(),
                ownerController.getPortfolioManagementTabManager(),
                true
        )));
        tableBuilder.setActionOnDoubleClickRow(openOwnerOverviewAction);

        tableBuilder.addRowContextMenuItem("Details anzeigen", openOwnerOverviewAction);
        tableBuilder.addRowContextMenuItem(
                "Status umschalten",
                new Consumer<Owner>() {
                    @Override
                    public void accept(Owner owner) {
                        // ToDo: implement in future work
                        if (State.ACTIVATED.equals(owner.getState())) {
                            System.out.println("Deaktiviere Inhaber " + owner);
                        } else {
                            System.out.println("Aktiviere Inhaber " + owner);
                        }
                    }
                }
        );
        tableBuilder.addRowContextMenuItem(
                "Löschen",
                new Consumer<Owner>() {
                    @Override
                    public void accept(Owner owner) {
                        // ToDo: implement in future work
                        System.out.println("Lösche Inhaber " + owner);
                    }
                }
        );

        return tableBuilder.getResult();
    }

    /**
     * @param parentPortfolioTable JavaFX node-based UI-Controls and all layout containers (f.e. Pane).
     * @param tableItems Items of table.
     */
    public static TableView<Portfolio> createOwnerPortfoliosTable(@NonNull Region parentPortfolioTable,
                                                                  @NonNull Region parentAccountsTable,
                                                                  @NonNull Region parentDepotsTable,
                                                                  @NonNull List<Portfolio> tableItems,
                                                                  @NonNull OwnerPortfoliosController ownerPortfoliosController,
                                                                  @NonNull PortfolioManagementTabManager portfolioManagementTabManager) {

        TableBuilder<Portfolio> tableBuilder = new TableBuilder<>(parentPortfolioTable, tableItems);
        Consumer<Portfolio> openPortfolioOverviewAction = portfolio -> {
            Navigator.navigateToPortfolio(portfolioManagementTabManager, portfolio);
            portfolioManagementTabManager.addCurrentlyDisplayedElement(new BreadcrumbElement(portfolio.toString(), "portfolio"));
        };

        tableBuilder.addColumn(
                "Portfolio-Name",
                0.5f,
                (Callback<TableColumn.CellDataFeatures<Portfolio, String>, ObservableValue<String>>) portfolioCellDataFeatures
                -> new SimpleStringProperty(portfolioCellDataFeatures.getValue().getName())
        );
        tableBuilder.addColumn(
                "Status",
                0.25f,
                (Callback<TableColumn.CellDataFeatures<Portfolio, String>, ObservableValue<String>>) portfolioCellDataFeatures
                        -> new SimpleStringProperty(portfolioCellDataFeatures.getValue().getState().getDisplayText())
        );
        tableBuilder.addColumn(
                "Wert",
                0.25f,
                (Callback<TableColumn.CellDataFeatures<Portfolio, String>, ObservableValue<String>>) portfolioCellDataFeatures
                        -> new SimpleStringProperty(portfolioCellDataFeatures.getValue().getValue().toString())
        );

        tableBuilder.setActionOnSingleClickRow(portfolio -> {
            ownerPortfoliosController.setAccountTable(TableFactory.createOwnerPortfolioAccountTable(
                    parentAccountsTable,
                    portfolio.getAccounts(),
                    portfolioManagementTabManager
            ));
            ownerPortfoliosController.setDepotTable(TableFactory.createOwnerPortfolioDepotTable(
                    parentDepotsTable,
                    portfolio.getDepots(),
                    portfolioManagementTabManager
            ));
        });
        tableBuilder.setActionOnDoubleClickRow(openPortfolioOverviewAction);

        tableBuilder.addRowContextMenuItem("Details anzeigen", openPortfolioOverviewAction);
        tableBuilder.addRowContextMenuItem(
                "Vermögen anzeigen",
                portfolio -> Navigator.navigateToOwnerAssets(portfolioManagementTabManager, portfolio.getOwner())
                // ^ just a little workaround, not nice, but reduces code
        );

        return tableBuilder.getResult();
    }

    /**
     * @param parent JavaFX node-based UI-Controls and all layout containers (f.e. Pane).
     * @param tableItems Items of table.
     */
    public static TableView<Account> createOwnerPortfolioAccountTable(@NonNull Region parent,
                                                                      @NonNull List<Account> tableItems,
                                                                      @NonNull PortfolioManagementTabManager portfolioManagementTabManager) {

        TableBuilder<Account> tableBuilder = new TableBuilder<>(parent, tableItems);
        Consumer<Account> openAccountOverviewAction = account -> {
            Navigator.navigateToAccount(portfolioManagementTabManager, account);
            portfolioManagementTabManager.addCurrentlyDisplayedElement(new BreadcrumbElement(account.toString(), "owner"));
        };

        tableBuilder.addColumn(
                "Konto-Bezeichnung",
                0.3f,
                (Callback<TableColumn.CellDataFeatures<Account, String>, ObservableValue<String>>) accountCellDataFeatures
                        -> new SimpleStringProperty(accountCellDataFeatures.getValue().getDescription())
        );
        tableBuilder.addColumn(
                "Typ",
                0.1f,
                (Callback<TableColumn.CellDataFeatures<Account, String>, ObservableValue<String>>) accountCellDataFeatures
                        -> new SimpleStringProperty(accountCellDataFeatures.getValue().getType().toString())
        );
        tableBuilder.addColumn(
                "Status",
                0.1f,
                (Callback<TableColumn.CellDataFeatures<Account, String>, ObservableValue<String>>) accountCellDataFeatures
                        -> new SimpleStringProperty(accountCellDataFeatures.getValue().getState().getDisplayText())
        );
        tableBuilder.addColumn(
                "Inhaber",
                0.2f,
                (Callback<TableColumn.CellDataFeatures<Account, String>, ObservableValue<String>>) accountCellDataFeatures
                        -> new SimpleStringProperty(accountCellDataFeatures.getValue().getOwner().toString())
        );
        tableBuilder.addColumn(
                "Bemerkung",
                0.2f,
                (Callback<TableColumn.CellDataFeatures<Account, String>, ObservableValue<String>>) accountCellDataFeatures
                        -> new SimpleStringProperty(accountCellDataFeatures.getValue().getNotice())
        );
        tableBuilder.addColumn(
                "Betrag",
                0.1f,
                (Callback<TableColumn.CellDataFeatures<Account, String>, ObservableValue<String>>) accountCellDataFeatures
                        -> new SimpleStringProperty(accountCellDataFeatures.getValue().getValue().toString())
        );

        tableBuilder.setActionOnDoubleClickRow(openAccountOverviewAction);

        tableBuilder.addRowContextMenuItem("Details anzeigen", openAccountOverviewAction);

        return tableBuilder.getResult();
    }

    /**
     * @param parent JavaFX node-based UI-Controls and all layout containers (f.e. Pane).
     * @param tableItems Items of table.
     */
    public static TableView<Depot> createOwnerPortfolioDepotTable(@NonNull Region parent,
                                                                  @NonNull List<Depot> tableItems,
                                                                  @NonNull PortfolioManagementTabManager portfolioManagementTabManager) {

        TableBuilder<Depot> tableBuilder = new TableBuilder<>(parent, tableItems);
        Consumer<Depot> openDepotOverviewAction = depot -> {
            Navigator.navigateToDepot(portfolioManagementTabManager, depot);
            portfolioManagementTabManager.addCurrentlyDisplayedElement(new BreadcrumbElement(depot.toString(), "depot"));
        };

        tableBuilder.addColumn(
                "Depot-Name",
                0.4f,
                (Callback<TableColumn.CellDataFeatures<Depot, String>, ObservableValue<String>>) depotCellDataFeatures
                        -> new SimpleStringProperty(depotCellDataFeatures.getValue().getName())
        );
        tableBuilder.addColumn(
                "Status",
                0.1f,
                (Callback<TableColumn.CellDataFeatures<Depot, String>, ObservableValue<String>>) depotCellDataFeatures
                        -> new SimpleStringProperty(depotCellDataFeatures.getValue().getState().getDisplayText())
        );
        tableBuilder.addColumn(
                "Inhaber",
                0.1f,
                (Callback<TableColumn.CellDataFeatures<Depot, String>, ObservableValue<String>>) depotCellDataFeatures
                        -> new SimpleStringProperty(depotCellDataFeatures.getValue().getOwner().toString())
        );
        tableBuilder.addColumn(
                "Bemerkung",
                0.3f,
                (Callback<TableColumn.CellDataFeatures<Depot, String>, ObservableValue<String>>) depotCellDataFeatures
                        -> new SimpleStringProperty(depotCellDataFeatures.getValue().getNotice())
        );
        tableBuilder.addColumn(
                "Wert",
                0.1f,
                (Callback<TableColumn.CellDataFeatures<Depot, String>, ObservableValue<String>>) depotCellDataFeatures
                        -> new SimpleStringProperty(depotCellDataFeatures.getValue().getValue().toString())
        );

        tableBuilder.setActionOnDoubleClickRow(openDepotOverviewAction);

        tableBuilder.addRowContextMenuItem("Details anzeigen", openDepotOverviewAction);

        return tableBuilder.getResult();
    }

    /**
     * @param parentDepotsTable JavaFX node-based UI-Controls and all layout containers (f.e. Pane).
     * @param tableItems Items of table.
     */
    public static TableView<Depot> createOwnerDepotsTable(@NonNull Region parentDepotsTable,
                                                          @NonNull Region parentDepotAccountsTable,
                                                          @NonNull List<Depot> tableItems,
                                                          @NonNull OwnerDepotsController ownerDepotsController) {

        TableBuilder<Depot> tableBuilder = new TableBuilder<>(parentDepotsTable, tableItems);
        Consumer<Depot> openDepotOverviewAction = depot -> {
            Navigator.navigateToDepot(ownerDepotsController.getPortfolioManagementManager(), depot);

            ownerDepotsController
                    .getPortfolioManagementManager()
                    .addCurrentlyDisplayedElement(new BreadcrumbElement(depot.toString(), "depot"));
        };

        tableBuilder.addColumn(
                "Depot-Name",
                0.3f,
                (Callback<TableColumn.CellDataFeatures<Depot, String>, ObservableValue<String>>) depotCellDataFeatures
                        -> new SimpleStringProperty(depotCellDataFeatures.getValue().getName())
        );
        tableBuilder.addColumn(
                "In Portfolio",
                0.3f,
                (Callback<TableColumn.CellDataFeatures<Depot, String>, ObservableValue<String>>) depotCellDataFeatures
                        -> new SimpleStringProperty(depotCellDataFeatures.getValue().getPortfolio().toString())
        );
        tableBuilder.addColumn(
                "Status",
                0.1f,
                (Callback<TableColumn.CellDataFeatures<Depot, String>, ObservableValue<String>>) depotCellDataFeatures
                        -> new SimpleStringProperty(depotCellDataFeatures.getValue().getState().getDisplayText())
        );
        tableBuilder.addColumn(
                "Bemerkung",
                0.2f,
                (Callback<TableColumn.CellDataFeatures<Depot, String>, ObservableValue<String>>) depotCellDataFeatures
                        -> new SimpleStringProperty(depotCellDataFeatures.getValue().getNotice())
        );
        tableBuilder.addColumn(
                "Wert",
                0.1f,
                (Callback<TableColumn.CellDataFeatures<Depot, String>, ObservableValue<String>>) depotCellDataFeatures
                        -> new SimpleStringProperty(depotCellDataFeatures.getValue().getValue().toString())
        );

        tableBuilder.setActionOnSingleClickRow(depot -> ownerDepotsController.setDepotAccountsTable(TableFactory.createOwnerDepotAccountsTable(
                parentDepotAccountsTable,
                depot.getBillingAccounts(),
                ownerDepotsController.getPortfolioManagementManager()
        )));
        tableBuilder.setActionOnDoubleClickRow(openDepotOverviewAction);

        tableBuilder.addRowContextMenuItem("Details anzeigen", openDepotOverviewAction);
        tableBuilder.addRowContextMenuItem("Portfolio anzeigen", depot -> {
            Navigator.navigateToPortfolio(ownerDepotsController.getPortfolioManagementManager(), depot.getPortfolio());

            ownerDepotsController
                    .getPortfolioManagementManager()
                    .addCurrentlyDisplayedElement(new BreadcrumbElement(depot.getPortfolio().toString(), "portfolio"));
        });

        return tableBuilder.getResult();
    }

    /**
     * @param parent JavaFX node-based UI-Controls and all layout containers (f.e. Pane).
     * @param tableItems Items of table.
     */
    public static TableView<Account> createOwnerDepotAccountsTable(@NonNull Region parent,
                                                                   @NonNull List<Account> tableItems,
                                                                   @NonNull PortfolioManagementTabManager portfolioManagementTabManager) {

        TableBuilder<Account> tableBuilder = new TableBuilder<>(parent, tableItems);
        Consumer<Account> openAccountOverviewAction = account -> {
            Navigator.navigateToAccount(portfolioManagementTabManager, account);
            portfolioManagementTabManager.addCurrentlyDisplayedElement(new BreadcrumbElement(account.toString(), "konto"));
        };

        tableBuilder.addColumn(
                "Konto-Bezeichnung",
                0.35f,
                (Callback<TableColumn.CellDataFeatures<Account, String>, ObservableValue<String>>) accountCellDataFeatures
                        -> new SimpleStringProperty(accountCellDataFeatures.getValue().getDescription())
        );
        tableBuilder.addColumn(
                "Bank",
                0.25f,
                (Callback<TableColumn.CellDataFeatures<Account, String>, ObservableValue<String>>) accountCellDataFeatures
                        -> new SimpleStringProperty(accountCellDataFeatures.getValue().getBankName())
        );
        tableBuilder.addColumn(
                "Typ",
                0.1f,
                (Callback<TableColumn.CellDataFeatures<Account, String>, ObservableValue<String>>) accountCellDataFeatures
                        -> new SimpleStringProperty(accountCellDataFeatures.getValue().getType().toString())
        );
        tableBuilder.addColumn(
                "Bemerkung",
                0.2f,
                (Callback<TableColumn.CellDataFeatures<Account, String>, ObservableValue<String>>) accountCellDataFeatures
                        -> new SimpleStringProperty(accountCellDataFeatures.getValue().getNotice())
        );
        tableBuilder.addColumn(
                "Betrag",
                0.1f,
                (Callback<TableColumn.CellDataFeatures<Account, String>, ObservableValue<String>>) accountCellDataFeatures
                        -> new SimpleStringProperty(accountCellDataFeatures.getValue().getValue().toString())
        );

        tableBuilder.setActionOnDoubleClickRow(openAccountOverviewAction);

        tableBuilder.addRowContextMenuItem("Details anzeigen", openAccountOverviewAction);

        return tableBuilder.getResult();
    }

    /**
     * @param parent JavaFX node-based UI-Controls and all layout containers (f.e. Pane).
     * @param tableItems Items of table.
     */
    public static TableView<Account> createOwnerAccountsTable(@NonNull Region parent,
                                                              @NonNull List<Account> tableItems,
                                                              @NonNull PortfolioManagementTabManager portfolioManagementTabManager) {

        TableBuilder<Account> tableBuilder = new TableBuilder<>(parent, tableItems);
        Consumer<Account> openAccountOverviewAction = account -> {
            Navigator.navigateToAccount(portfolioManagementTabManager, account);
            portfolioManagementTabManager.addCurrentlyDisplayedElement(new BreadcrumbElement(account.toString(), "konto"));
        };

        tableBuilder.addColumn(
                "Konto-Bezeichnung",
                0.2f,
                (Callback<TableColumn.CellDataFeatures<Account, String>, ObservableValue<String>>) accountCellDataFeatures
                        -> new SimpleStringProperty(accountCellDataFeatures.getValue().getDescription())
        );
        tableBuilder.addColumn(
                "Typ",
                0.1f,
                (Callback<TableColumn.CellDataFeatures<Account, String>, ObservableValue<String>>) accountCellDataFeatures
                        -> new SimpleStringProperty(accountCellDataFeatures.getValue().getType().toString())
        );
        tableBuilder.addColumn(
                "Status",
                0.1f,
                (Callback<TableColumn.CellDataFeatures<Account, String>, ObservableValue<String>>) accountCellDataFeatures
                        -> new SimpleStringProperty(accountCellDataFeatures.getValue().getState().getDisplayText())
        );
        tableBuilder.addNestedColumn(
                "Bank",
                0.3f,
                Map.entry(
                        "Name",
                        accountCellDataFeatures -> new SimpleStringProperty(accountCellDataFeatures.getValue().getBankName())
                ),
                Map.entry(
                        "IBAN",
                        accountCellDataFeatures -> new SimpleStringProperty(accountCellDataFeatures.getValue().getIban())
                ),
                Map.entry(
                        "Konto-Nr.",
                        (Callback<TableColumn.CellDataFeatures<Account, String>, ObservableValue<String>>) accountCellDataFeatures
                                -> new SimpleStringProperty(accountCellDataFeatures.getValue().getKontoNumber())
                )
        );
        tableBuilder.addColumn(
                "Bemerkung",
                0.2f,
                (Callback<TableColumn.CellDataFeatures<Account, String>, ObservableValue<String>>) accountCellDataFeatures
                        -> new SimpleStringProperty(accountCellDataFeatures.getValue().getNotice())
        );
        tableBuilder.addColumn(
                "Betrag",
                0.1f,
                (Callback<TableColumn.CellDataFeatures<Account, String>, ObservableValue<String>>) accountCellDataFeatures
                        -> new SimpleStringProperty(accountCellDataFeatures.getValue().getValue().toString())
        );

        tableBuilder.setActionOnDoubleClickRow(openAccountOverviewAction);

        tableBuilder.addRowContextMenuItem("Details anzeigen", openAccountOverviewAction);

        return tableBuilder.getResult();
    }

    /**
     * @param parentPortfolioTable JavaFX node-based UI-Controls and all layout containers (f.e. Pane).
     * @param tableItems Items of table.
     */
    public static TableView<Portfolio> createPortfolioTable(@NonNull Region parentPortfolioTable,
                                                            @NonNull Region parentAccountsTable,
                                                            @NonNull Region parentDepotsTable,
                                                            @NonNull List<Portfolio> tableItems,
                                                            @NonNull PortfolioListController portfolioListController) {

        TableBuilder<Portfolio> tableBuilder = new TableBuilder<>(parentPortfolioTable, tableItems);
        Consumer<Portfolio> openPortfolioOverviewAction = portfolio -> {
            Navigator.navigateToPortfolio(portfolioListController.getPortfolioManagementManager(), portfolio);

            portfolioListController
                    .getPortfolioManagementManager()
                    .setCurrentlyDisplayedElement(new BreadcrumbElement(portfolio.toString(), "portfolio"));
        };

        tableBuilder.addColumn(
                "Portfolio-Name",
                0.5f,
                (Callback<TableColumn.CellDataFeatures<Portfolio, String>, ObservableValue<String>>) portfolioCellDataFeatures
                        -> new SimpleStringProperty(portfolioCellDataFeatures.getValue().getName())
        );
        tableBuilder.addColumn(
                "Status",
                0.1f,
                (Callback<TableColumn.CellDataFeatures<Portfolio, String>, ObservableValue<String>>) portfolioCellDataFeatures
                        -> new SimpleStringProperty(portfolioCellDataFeatures.getValue().getState().getDisplayText())
        );
        tableBuilder.addColumn(
                "Inhaber",
                0.20f,
                (Callback<TableColumn.CellDataFeatures<Portfolio, String>, ObservableValue<String>>) portfolioCellDataFeatures
                        -> new SimpleStringProperty(portfolioCellDataFeatures.getValue().getOwner().toString())
        );
        tableBuilder.addColumn(
                "Wert",
                0.20f,
                (Callback<TableColumn.CellDataFeatures<Portfolio, String>, ObservableValue<String>>) portfolioCellDataFeatures
                        -> new SimpleStringProperty(portfolioCellDataFeatures.getValue().getValue().toString())
        );

        tableBuilder.setActionOnSingleClickRow(portfolio -> {
            portfolioListController.setAccountTable(TableFactory.createOwnerPortfolioAccountTable(
                    parentAccountsTable,
                    portfolio.getAccounts(),
                    portfolioListController.getPortfolioManagementManager()
            ));
            portfolioListController.setDepotTable(TableFactory.createOwnerPortfolioDepotTable(
                    parentDepotsTable,
                    portfolio.getDepots(),
                    portfolioListController.getPortfolioManagementManager()
            ));
        });
        tableBuilder.setActionOnDoubleClickRow(openPortfolioOverviewAction);

        tableBuilder.addRowContextMenuItem("Details anzeigen", openPortfolioOverviewAction);
        tableBuilder.addRowContextMenuItem(
                "Vermögen anzeigen",
                new Consumer<Portfolio>() {
                    @Override
                    public void accept(Portfolio portfolio) {
                        // ToDo: implement
                    }
                }
        );

        return tableBuilder.getResult();
    }

    /**
     * @param tableParent JavaFX node-based UI-Controls and all layout containers (f.e. Pane).
     * @param divisionByLocation Instance to be edited.
     */
    public static TableView<InvestmentGuideline.DivisionByLocation> createPortfolioDivisionByLocationTable(@NonNull Region tableParent,
                                                                                                           @NonNull InvestmentGuideline.DivisionByLocation divisionByLocation) {
        TableBuilder<InvestmentGuideline.DivisionByLocation> tableBuilder = new TableBuilder<>(tableParent, List.of(divisionByLocation));

        tableBuilder.addColumn(
                "",
                0.1f,
                (Callback<TableColumn.CellDataFeatures<InvestmentGuideline.DivisionByLocation, String>, ObservableValue<String>>) cellDataFeatures
                        -> new SimpleStringProperty("%")
        );
        tableBuilder.addEditableColumn(
                "Deutschland",
                0.12f,
                cellDataFeatures -> new SimpleFloatProperty(cellDataFeatures.getValue().getGermany()),
                col -> col.getRowValue().setGermany((float) col.getNewValue())
        );
        tableBuilder.addEditableColumn(
                "Europa ohne BRD",
                0.12f,
                cellDataFeatures -> new SimpleFloatProperty(cellDataFeatures.getValue().getEurope_without_brd()),
                col -> col.getRowValue().setEurope_without_brd((float) col.getNewValue())
        );
        tableBuilder.addEditableColumn(
                "Nordamerika, USA",
                0.12f,
                cellDataFeatures -> new SimpleFloatProperty(cellDataFeatures.getValue().getNorthamerica_with_usa()),
                col -> col.getRowValue().setNorthamerica_with_usa((float) col.getNewValue())
        );
        tableBuilder.addEditableColumn(
                "Asien (ohne China)",
                0.12f,
                cellDataFeatures -> new SimpleFloatProperty(cellDataFeatures.getValue().getAsia_without_china()),
                col -> col.getRowValue().setAsia_without_china((float) col.getNewValue())
        );
        tableBuilder.addEditableColumn(
                "China",
                0.12f,
                cellDataFeatures -> new SimpleFloatProperty(cellDataFeatures.getValue().getChina()),
                col -> col.getRowValue().setChina((float) col.getNewValue())
        );
        tableBuilder.addEditableColumn(
                "Japan",
                0.12f,
                cellDataFeatures -> new SimpleFloatProperty(cellDataFeatures.getValue().getJapan()),
                col -> col.getRowValue().setJapan((float) col.getNewValue())
        );
        tableBuilder.addEditableColumn(
                "Emergine Markets",
                0.12f,
                cellDataFeatures -> new SimpleFloatProperty(cellDataFeatures.getValue().getEmergine_markets()),
                col -> col.getRowValue().setEmergine_markets((float) col.getNewValue())
        );

        return tableBuilder.getResult();
    }

    /**
     * @param tableParent JavaFX node-based UI-Controls and all layout containers (f.e. Pane).
     * @param divisionByLocation Instance to be edited.
     */
    public static TableView<InvestmentGuideline.DivisionByCurrency> createPortfolioDivisionByCurrencyTable(@NonNull Region tableParent,
                                                                                                           @NonNull InvestmentGuideline.DivisionByCurrency divisionByLocation) {
        TableBuilder<InvestmentGuideline.DivisionByCurrency> tableBuilder = new TableBuilder<>(tableParent, List.of(divisionByLocation));

        tableBuilder.addColumn(
                "",
                0.1f,
                (Callback<TableColumn.CellDataFeatures<InvestmentGuideline.DivisionByCurrency, String>, ObservableValue<String>>) cellDataFeatures
                        -> new SimpleStringProperty("%")
        );
        tableBuilder.addEditableColumn(
                "Euro",
                0.12f,
                cellDataFeatures -> new SimpleFloatProperty(cellDataFeatures.getValue().getEuro()),
                col -> col.getRowValue().setEuro((float) col.getNewValue())
        );
        tableBuilder.addEditableColumn(
                "US-Dollar",
                0.12f,
                cellDataFeatures -> new SimpleFloatProperty(cellDataFeatures.getValue().getUsd()),
                col -> col.getRowValue().setUsd((float) col.getNewValue())
        );
        tableBuilder.addEditableColumn(
                "Schweizer Franken",
                0.12f,
                cellDataFeatures -> new SimpleFloatProperty(cellDataFeatures.getValue().getChf()),
                col -> col.getRowValue().setChf((float) col.getNewValue())
        );
        tableBuilder.addEditableColumn(
                "Britische Pfunds",
                0.12f,
                cellDataFeatures -> new SimpleFloatProperty(cellDataFeatures.getValue().getGbp()),
                col -> col.getRowValue().setGbp((float) col.getNewValue())
        );
        tableBuilder.addEditableColumn(
                "Japanischer Yen",
                0.12f,
                cellDataFeatures -> new SimpleFloatProperty(cellDataFeatures.getValue().getYen()),
                col -> col.getRowValue().setYen((float) col.getNewValue())
        );
        tableBuilder.addEditableColumn(
                "Asiatische Währungen",
                0.12f,
                cellDataFeatures -> new SimpleFloatProperty(cellDataFeatures.getValue().getAsiaCurrencies()),
                col -> col.getRowValue().setAsia_currencies((float) col.getNewValue())
        );
        tableBuilder.addEditableColumn(
                "Alle andere",
                0.12f,
                cellDataFeatures -> new SimpleFloatProperty(cellDataFeatures.getValue().getOthers()),
                col -> col.getRowValue().setOthers((float) col.getNewValue())
        );

        return tableBuilder.getResult();
    }
}
