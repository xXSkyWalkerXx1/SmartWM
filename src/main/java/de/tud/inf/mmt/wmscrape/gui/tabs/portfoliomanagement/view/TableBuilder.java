package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import org.springframework.lang.NonNull;

import java.util.function.Consumer;

public class TableBuilder<S> {

    private final TableView<S> tableView = new TableView<>();

    public void addColumn(@NonNull String columnName,
                          @NonNull Callback<TableColumn.CellDataFeatures<S, ?>, ObservableValue<?>> cellValueFactory){
        // ToDo: implement
    }

    public void addColumn(@NonNull String columnName,
                          @NonNull Callback<TableColumn.CellDataFeatures<S, ?>, ObservableValue<?>> cellValueFactory,
                          @NonNull Callback<TableColumn<S, ?>, TableCell<S, ?>> cellFactory){
        // ToDo: implement
    }

    public void addContextMenuItem(@NonNull String itemName, @NonNull EventHandler<ActionEvent> onClickAction){
        // ToDo: implement
    }

    public void addActionOnDoubleClickRow(@NonNull Consumer<S> onDoubleClickAction){
        // ToDo: implement
    }

    public void addItems(@NonNull S... tableItems){
        // ToDo: implement
    }

    public @NonNull TableView<S> getResult(){
        return tableView;
    }
}
