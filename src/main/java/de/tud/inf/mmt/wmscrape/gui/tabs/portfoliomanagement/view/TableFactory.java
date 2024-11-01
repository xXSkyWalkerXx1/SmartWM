package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
    public static TableView<Owner> createOwnerTable(@NonNull Region parent, @NonNull List<Owner> tableItems){
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
        tableBuilder.addActionOnDoubleClickRow(
                new Consumer<Owner>() {
                    @Override
                    public void accept(Owner owner) {
                        // ToDo: implement
                    }
                }
        );
        tableBuilder.addRowContextMenuItem(
                "Details anzeigen",
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        // ToDo: implement
                    }
                }
        );
        tableBuilder.addRowContextMenuItem(
                "Löschen",
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        // ToDo: implement
                    }
                }
        );
        return tableBuilder.getResult();
    }
}
