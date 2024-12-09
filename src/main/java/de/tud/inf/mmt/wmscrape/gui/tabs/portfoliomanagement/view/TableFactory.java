package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view;

import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.BreadcrumbElement;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.Navigator;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.*;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.AccountType;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.State;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.kontos.AccountMenuController;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners.OwnerController;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners.OwnerService;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners.owner.OwnerDepotsController;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners.owner.OwnerPortfoliosController;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.portfolios.PortfolioListController;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.util.Callback;
import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.springframework.lang.NonNull;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
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
            @NonNull OwnerController ownerController,
            @NonNull OwnerService ownerService){

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
                        ownerService.delete(owner, parent, ownerController);
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
                        -> new SimpleStringProperty(FormatUtils.formatFloat(portfolioCellDataFeatures.getValue().getValue().floatValue()))
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
                portfolio -> {
                    Navigator.navigateToOwnerAssets(portfolioManagementTabManager, portfolio.getOwner());
                    portfolioManagementTabManager.setCurrentlyDisplayedElement(new BreadcrumbElement(portfolio.getOwner().toString(), "owner"));
                    // ^ just a little workaround, not nice, but reduces code
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
                        -> new SimpleStringProperty(FormatUtils.formatFloat(accountCellDataFeatures.getValue().getValue().floatValue()))
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
                        -> new SimpleStringProperty(FormatUtils.formatFloat(depotCellDataFeatures.getValue().getValue().floatValue()))
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
                        -> new SimpleStringProperty(FormatUtils.formatFloat(depotCellDataFeatures.getValue().getValue().floatValue()))
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
                        -> new SimpleStringProperty(FormatUtils.formatFloat(accountCellDataFeatures.getValue().getValue().floatValue()))
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
                        -> new SimpleStringProperty(FormatUtils.formatFloat(accountCellDataFeatures.getValue().getValue().floatValue()))
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
                        -> new SimpleStringProperty(FormatUtils.formatFloat(portfolioCellDataFeatures.getValue().getValue().floatValue()))
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
        BiConsumer<Float, Float> showErrorDialog = (input, currSum) -> PrimaryTabManager.showDialog(
                Alert.AlertType.ERROR,
                "Fehler",
                String.format(
                        "Die Aufteilung des Gesamtvermögens nach Ländern bzw. Regionen muss in Summe 100 ergeben.\n" +
                                "Maximal verbleibende Eingabe: %s | Eingegeben: %s",
                        FormatUtils.formatFloat(100 - currSum), FormatUtils.formatFloat(input)
                ),
                tableBuilder.getResult()
        );

        tableBuilder.addColumn(
                "",
                0.1f,
                (Callback<TableColumn.CellDataFeatures<InvestmentGuideline.DivisionByLocation, String>, ObservableValue<String>>) cellDataFeatures
                        -> new SimpleStringProperty("%")
        );
        tableBuilder.addEditableColumn(
                "Deutschland",
                0.12f,
                cellDataFeatures ->
                        new SimpleStringProperty(FormatUtils.formatFloat(cellDataFeatures.getValue().getGermany())),
                col -> {
                    var division = col.getRowValue();
                    float input;

                    try {
                        input = FormatUtils.parseFloat(col.getNewValue());
                    } catch (ParseException e) {
                        return;
                    }

                    if (division.getSum() - division.getGermany() + input > 100) {
                        showErrorDialog.accept(input, division.getSum());
                        return;
                    }
                    col.getRowValue().setGermany(input);
                });
        tableBuilder.addEditableColumn(
                "Europa ohne BRD",
                0.12f,
                cellDataFeatures ->
                        new SimpleStringProperty(FormatUtils.formatFloat(cellDataFeatures.getValue().getEurope_without_brd())),
                col -> {
                    var division = col.getRowValue();
                    float input;

                    try {
                        input = FormatUtils.parseFloat(col.getNewValue());
                    } catch (ParseException e) {
                        return;
                    }

                    if (division.getSum() - division.getEurope_without_brd() + input > 100) {
                        showErrorDialog.accept(input, division.getSum());
                        return;
                    }
                    col.getRowValue().setEurope_without_brd(input);
                });
        tableBuilder.addEditableColumn(
                "Nordamerika, USA",
                0.12f,
                cellDataFeatures ->
                        new SimpleStringProperty(FormatUtils.formatFloat(cellDataFeatures.getValue().getNorthamerica_with_usa())),
                col -> {
                    var division = col.getRowValue();
                    float input;

                    try {
                        input = FormatUtils.parseFloat(col.getNewValue());
                    } catch (ParseException e) {
                        return;
                    }

                    if (division.getSum() - division.getNorthamerica_with_usa() + input > 100) {
                        showErrorDialog.accept(input, division.getSum());
                        return;
                    }
                    col.getRowValue().setNorthamerica_with_usa(input);
                });
        tableBuilder.addEditableColumn(
                "Asien (ohne China)",
                0.12f,
                cellDataFeatures ->
                        new SimpleStringProperty(FormatUtils.formatFloat(cellDataFeatures.getValue().getAsia_without_china())),
                col -> {
                    var division = col.getRowValue();
                    float input;

                    try {
                        input = FormatUtils.parseFloat(col.getNewValue());
                    } catch (ParseException e) {
                        return;
                    }

                    if (division.getSum() - division.getAsia_without_china() + input > 100) {
                        showErrorDialog.accept(input, division.getSum());
                        return;
                    }
                    col.getRowValue().setAsia_without_china(input);
                });
        tableBuilder.addEditableColumn(
                "China",
                0.12f,
                cellDataFeatures ->
                        new SimpleStringProperty(FormatUtils.formatFloat(cellDataFeatures.getValue().getChina())),
                col -> {
                    var division = col.getRowValue();
                    float input;

                    try {
                        input = FormatUtils.parseFloat(col.getNewValue());
                    } catch (ParseException e) {
                        return;
                    }

                    if (division.getSum() - division.getChina() + input > 100) {
                        showErrorDialog.accept(input, division.getSum());
                        return;
                    }
                    col.getRowValue().setChina(input);
                }
        );
        tableBuilder.addEditableColumn(
                "Japan",
                0.12f,
                cellDataFeatures ->
                        new SimpleStringProperty(FormatUtils.formatFloat(cellDataFeatures.getValue().getJapan())),
                col -> {
                    var division = col.getRowValue();
                    float input;

                    try {
                        input = FormatUtils.parseFloat(col.getNewValue());
                    } catch (ParseException e) {
                        return;
                    }

                    if (division.getSum() - division.getJapan() + input > 100) {
                        showErrorDialog.accept(input, division.getSum());
                        return;
                    }
                    col.getRowValue().setJapan(input);
                }
        );
        tableBuilder.addEditableColumn(
                "Emergine Markets",
                0.12f,
                cellDataFeatures ->
                        new SimpleStringProperty(FormatUtils.formatFloat(cellDataFeatures.getValue().getEmergine_markets())),
                col -> {
                    var division = col.getRowValue();
                    float input;

                    try {
                        input = FormatUtils.parseFloat(col.getNewValue());
                    } catch (ParseException e) {
                        return;
                    }

                    if (division.getSum() - division.getEmergine_markets() + input > 100) {
                        showErrorDialog.accept(input, division.getSum());
                        return;
                    }
                    col.getRowValue().setEmergine_markets(input);
                }
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
        BiConsumer<Float, Float> showErrorDialog = (input, currSum) -> PrimaryTabManager.showDialog(
                Alert.AlertType.ERROR,
                "Fehler",
                String.format(
                        "Die Aufteilung des Gesamtvermögens nach Währung muss in Summe 100 ergeben.\n" +
                                "Maximal verbleibende Eingabe: %s | Eingegeben: %s",
                        100 - currSum, input
                ),
                tableBuilder.getResult()
        );

        tableBuilder.addColumn(
                "",
                0.1f,
                (Callback<TableColumn.CellDataFeatures<InvestmentGuideline.DivisionByCurrency, String>, ObservableValue<String>>) cellDataFeatures
                        -> new SimpleStringProperty("%")
        );
        tableBuilder.addEditableColumn(
                "Euro",
                0.12f,
                cellDataFeatures ->
                        new SimpleStringProperty(FormatUtils.formatFloat(cellDataFeatures.getValue().getEuro())),
                col -> {
                    var division = col.getRowValue();
                    float input;

                    try {
                        input = FormatUtils.parseFloat(col.getNewValue());
                    } catch (ParseException e) {
                        return;
                    }

                    if (division.getSum() - division.getEuro() + input > 100) {
                        showErrorDialog.accept(input, division.getSum());
                        return;
                    }
                    col.getRowValue().setEuro(input);
                }
        );
        tableBuilder.addEditableColumn(
                "US-Dollar",
                0.12f,
                cellDataFeatures ->
                        new SimpleStringProperty(FormatUtils.formatFloat(cellDataFeatures.getValue().getUsd())),
                col -> {
                    var division = col.getRowValue();
                    float input;

                    try {
                        input = FormatUtils.parseFloat(col.getNewValue());
                    } catch (ParseException e) {
                        return;
                    }

                    if (division.getSum() - division.getUsd() + input > 100) {
                        showErrorDialog.accept(input, division.getSum());
                        return;
                    }
                    col.getRowValue().setUsd(input);
                }
        );
        tableBuilder.addEditableColumn(
                "Schweizer Franken",
                0.12f,
                cellDataFeatures ->
                        new SimpleStringProperty(FormatUtils.formatFloat(cellDataFeatures.getValue().getChf())),
                col -> {
                    var division = col.getRowValue();
                    float input;

                    try {
                        input = FormatUtils.parseFloat(col.getNewValue());
                    } catch (ParseException e) {
                        return;
                    }

                    if (division.getSum() - division.getChf() + input > 100) {
                        showErrorDialog.accept(input, division.getSum());
                        return;
                    }
                    col.getRowValue().setChf(input);
                }
        );
        tableBuilder.addEditableColumn(
                "Britische Pfunds",
                0.12f,
                cellDataFeatures ->
                        new SimpleStringProperty(FormatUtils.formatFloat(cellDataFeatures.getValue().getGbp())),
                col -> {
                    var division = col.getRowValue();
                    float input;

                    try {
                        input = FormatUtils.parseFloat(col.getNewValue());
                    } catch (ParseException e) {
                        return;
                    }

                    if (division.getSum() - division.getGbp() + input > 100) {
                        showErrorDialog.accept(input, division.getSum());
                        return;
                    }
                    col.getRowValue().setGbp(input);
                }
        );
        tableBuilder.addEditableColumn(
                "Japanischer Yen",
                0.12f,
                cellDataFeatures ->
                        new SimpleStringProperty(FormatUtils.formatFloat(cellDataFeatures.getValue().getYen())),
                col -> {
                    var division = col.getRowValue();
                    float input;

                    try {
                        input = FormatUtils.parseFloat(col.getNewValue());
                    } catch (ParseException e) {
                        return;
                    }

                    if (division.getSum() - division.getYen() + input > 100) {
                        showErrorDialog.accept(input, division.getSum());
                        return;
                    }
                    col.getRowValue().setYen(input);
                }
        );
        tableBuilder.addEditableColumn(
                "Asiatische Währungen",
                0.12f,
                cellDataFeatures ->
                        new SimpleStringProperty(FormatUtils.formatFloat(cellDataFeatures.getValue().getAsiaCurrencies())),
                col -> {
                    var division = col.getRowValue();
                    float input;

                    try {
                        input = FormatUtils.parseFloat(col.getNewValue());
                    } catch (ParseException e) {
                        return;
                    }

                    if (division.getSum() - division.getAsiaCurrencies() + input > 100) {
                        showErrorDialog.accept(input, division.getSum());
                        return;
                    }
                    col.getRowValue().setAsia_currencies(input);
                }
        );
        tableBuilder.addEditableColumn(
                "Alle andere",
                0.12f,
                cellDataFeatures ->
                        new SimpleStringProperty(FormatUtils.formatFloat(cellDataFeatures.getValue().getOthers())),
                col -> {
                    var division = col.getRowValue();
                    float input;

                    try {
                        input = FormatUtils.parseFloat(col.getNewValue());
                    } catch (ParseException e) {
                        return;
                    }

                    if (division.getSum() - division.getOthers() + input > 100) {
                        showErrorDialog.accept(input, division.getSum());
                        return;
                    }
                    col.getRowValue().setOthers(input);
                }
        );

        return tableBuilder.getResult();
    }

    /**
     * @param parentAccountTable JavaFX node-based UI-Controls and all layout containers (f.e. Pane).
     */
    public static TableView<Account> createAccountsTable(@NonNull Region parentAccountTable,
                                                         @NonNull Region parentDepotTable,
                                                         @NonNull List<Account> tableItems,
                                                         @NonNull AccountMenuController accountMenuController,
                                                         @NonNull PortfolioManagementTabManager portfolioManagementTabManager) {

        TableBuilder<Account> tableBuilder = new TableBuilder<>(parentAccountTable, tableItems);
        Consumer<Account> openAccountOverviewAction = account -> {
            Navigator.navigateToAccount(portfolioManagementTabManager, account);
            portfolioManagementTabManager.setCurrentlyDisplayedElement(new BreadcrumbElement(account.toString(), "konto"));
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
        tableBuilder.addColumn(
                "Inhaber",
                0.1f,
                (Callback<TableColumn.CellDataFeatures<Account, String>, ObservableValue<String>>) accountCellDataFeatures
                        -> new SimpleStringProperty(accountCellDataFeatures.getValue().getOwner().toString())
        );
        tableBuilder.addNestedColumn(
                "Bank",
                0.2f,
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

        tableBuilder.setActionOnSingleClickRow(account -> {
            TableView<Depot> table;

            // Only clearing accounts are used in depots
            if (AccountType.CLEARING_ACCOUNT.equals(account.getType())) {
                table = TableFactory.createAccountDepotsTable(
                        parentDepotTable,
                        account.getMappedDepots(),
                        portfolioManagementTabManager
                );
                if (account.getMappedDepots().isEmpty()) table.setPlaceholder(new Label("Keine Depots zugeordnet"));
            } else {
                table = TableFactory.createAccountDepotsTable(
                        parentDepotTable,
                        List.of(),
                        portfolioManagementTabManager
                );
                table.setPlaceholder(new Label("Kein Verrechnungskonto ausgewählt"));
            }

            accountMenuController.setDepotTable(table);
        });
        tableBuilder.setActionOnDoubleClickRow(openAccountOverviewAction);

        tableBuilder.addRowContextMenuItem("Details anzeigen", openAccountOverviewAction);
        tableBuilder.addRowContextMenuItem("Transaktionen anzeigen", account -> {
            throw new NotImplementedException("Not implemented yet");
        });
        tableBuilder.addRowContextMenuItem("Löschen", account -> {
            throw new NotImplementedException("Not implemented yet");
        });

        return tableBuilder.getResult();
    }

    /**
     * @param parent JavaFX node-based UI-Controls and all layout containers (f.e. Pane).
     */
    public static TableView<Depot> createAccountDepotsTable(@NonNull Region parent,
                                                            @NonNull List<Depot> tableItems,
                                                            @NonNull PortfolioManagementTabManager portfolioManagementTabManager) {
        TableBuilder<Depot> tableBuilder = new TableBuilder<>(parent, tableItems);
        Consumer<Depot> openDepotOverviewAction = depot -> {
            Navigator.navigateToDepot(portfolioManagementTabManager, depot);
            portfolioManagementTabManager.setCurrentlyDisplayedElement(new BreadcrumbElement(depot.toString(), "depot"));
        };

        tableBuilder.addColumn(
                "Depot-Name",
                0.5f,
                (Callback<TableColumn.CellDataFeatures<Depot, String>, ObservableValue<String>>) depotCellDataFeatures
                        -> new SimpleStringProperty(depotCellDataFeatures.getValue().getName())
        );
        tableBuilder.addColumn(
                "Inhaber",
                0.3f,
                (Callback<TableColumn.CellDataFeatures<Depot, String>, ObservableValue<String>>) depotCellDataFeatures
                        -> new SimpleStringProperty(depotCellDataFeatures.getValue().getOwner().toString())
        );
        tableBuilder.addColumn(
                "Bemerkung",
                0.2f,
                (Callback<TableColumn.CellDataFeatures<Depot, String>, ObservableValue<String>>) depotCellDataFeatures
                        -> new SimpleStringProperty(depotCellDataFeatures.getValue().getNotice())
        );

        tableBuilder.setActionOnDoubleClickRow(openDepotOverviewAction);
        tableBuilder.addRowContextMenuItem("Details anzeigen", openDepotOverviewAction);

        return tableBuilder.getResult();
    }
}
