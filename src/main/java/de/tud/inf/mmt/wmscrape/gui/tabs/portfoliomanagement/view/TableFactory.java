package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.BreadcrumbElement;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.Navigator;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Account;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Depot;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Portfolio;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.State;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners.OwnerController;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners.owner.OwnerDepotsController;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners.owner.OwnerKontosController;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners.owner.OwnerPortfoliosController;
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
                                                                  @NonNull OwnerPortfoliosController ownerPortfoliosController) {

        TableBuilder<Portfolio> tableBuilder = new TableBuilder<>(parentPortfolioTable, tableItems);
        Consumer<Portfolio> openPortfolioOverviewAction = portfolio -> {
            Navigator.navigateToPortfolio(ownerPortfoliosController.getPortfolioManagementManager(), portfolio);

            ownerPortfoliosController
                    .getPortfolioManagementManager()
                    .addCurrentlyDisplayedElement(new BreadcrumbElement(portfolio.toString(), "portfolio"));
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
                    ownerPortfoliosController
            ));
            ownerPortfoliosController.setDepotTable(TableFactory.createOwnerPortfolioDepotTable(
                    parentDepotsTable,
                    portfolio.getDepots(),
                    ownerPortfoliosController
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
     * @param parent JavaFX node-based UI-Controls and all layout containers (f.e. Pane).
     * @param tableItems Items of table.
     */
    public static TableView<Account> createOwnerPortfolioAccountTable(@NonNull Region parent,
                                                                      @NonNull List<Account> tableItems,
                                                                      @NonNull OwnerPortfoliosController ownerPortfoliosController) {

        TableBuilder<Account> tableBuilder = new TableBuilder<>(parent, tableItems);
        Consumer<Account> openAccountOverviewAction = account -> {
            Navigator.navigateToAccount(ownerPortfoliosController.getPortfolioManagementManager(), account);

            ownerPortfoliosController
                    .getPortfolioManagementManager()
                    .addCurrentlyDisplayedElement(new BreadcrumbElement(account.toString(), "owner"));
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
                                                                  @NonNull OwnerPortfoliosController ownerPortfoliosController) {

        TableBuilder<Depot> tableBuilder = new TableBuilder<>(parent, tableItems);
        Consumer<Depot> openDepotOverviewAction = depot -> {
            Navigator.navigateToDepot(ownerPortfoliosController.getPortfolioManagementManager(), depot);

            ownerPortfoliosController
                    .getPortfolioManagementManager()
                    .addCurrentlyDisplayedElement(new BreadcrumbElement(depot.toString(), "depot"));
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
                ownerDepotsController
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
                                                              @NonNull OwnerDepotsController ownerDepotsController) {

        TableBuilder<Account> tableBuilder = new TableBuilder<>(parent, tableItems);
        Consumer<Account> openAccountOverviewAction = account -> {
            Navigator.navigateToAccount(ownerDepotsController.getPortfolioManagementManager(), account);

            ownerDepotsController
                    .getPortfolioManagementManager()
                    .addCurrentlyDisplayedElement(new BreadcrumbElement(account.toString(), "konto"));
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
                                                              @NonNull OwnerKontosController ownerKontosController) {

        TableBuilder<Account> tableBuilder = new TableBuilder<>(parent, tableItems);
        Consumer<Account> openAccountOverviewAction = account -> {
            Navigator.navigateToAccount(ownerKontosController.getPortfolioManagementManager(), account);

            ownerKontosController
                    .getPortfolioManagementManager()
                    .addCurrentlyDisplayedElement(new BreadcrumbElement(account.toString(), "konto"));
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
}
