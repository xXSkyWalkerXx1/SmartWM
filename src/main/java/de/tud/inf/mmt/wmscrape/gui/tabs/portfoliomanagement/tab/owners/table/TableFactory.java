package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners.table;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.BreadcrumbElement;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
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

        tableBuilder.setActionOnSingleClickRow(owner -> {
            ownerController.setOwnerTreeView(new PortfolioTreeView(parent, owner.getPortfolios()));
        });
        tableBuilder.setActionOnDoubleClickRow(owner -> { // ToDo: implement open specific user
            var manager = ownerController.getPortfolioManagementTabManager();
            manager.showInhaberTabs();
            manager.setCurrentlyDisplayedElement(new BreadcrumbElement(owner.toString(), "inhaber"));
        });

        tableBuilder.addRowContextMenuItem(
                "Details anzeigen",
                owner -> { // ToDo: implement open specific user
                    System.out.println("Öffne Inhaber " + owner);
                    var manager = ownerController.getPortfolioManagementTabManager();
                    manager.showInhaberTabs();
                    manager.setCurrentlyDisplayedElement(new BreadcrumbElement(owner.toString(), "inhaber"));
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
