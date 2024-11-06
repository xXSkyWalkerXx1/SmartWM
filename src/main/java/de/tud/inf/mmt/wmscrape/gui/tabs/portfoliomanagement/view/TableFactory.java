package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.BreadcrumbElement;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.State;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners.OwnerController;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.PortfolioTreeView;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.TableBuilder;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.util.Callback;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.function.Consumer;

public class TableFactory {

    /**
     * @param parent JavaFX node-based UI Controls and all layout containers (f.e. Pane).
     * @param tableItems Items of table.
     */
    public static TableView<Owner> createOwnerTable(
            @NonNull Region parent,
            @NonNull List<Owner> tableItems,
            @NonNull OwnerController ownerController){

        TableBuilder<Owner> tableBuilder = new TableBuilder<>(parent, tableItems);
        Consumer<Owner> openOwnerOverviewAction = owner -> {
            var manager = ownerController.getPortfolioManagementTabManager();
            manager.showInhaberTabs();
            manager.setCurrentlyDisplayedElement(new BreadcrumbElement(owner.toString(), "owner"));
            manager.getPortfolioController().getOwnerOverviewController().open(owner);
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
                owner.getPortfolios(),
                ownerController.getPortfolioManagementTabManager()
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
}
