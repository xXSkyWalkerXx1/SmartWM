package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.util.Callback;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.function.Consumer;

/**
 * Builder to simplify building custom table-views.
 * @param <S> Table-type
 */
public class TableBuilder<S> {

    private final TableView<S> tableView = new TableView<>();
    private final ContextMenu rowContextMenu = new ContextMenu();
    private Consumer<S> onRowSingleClickAction, onRowDoubleClickAction;

    /**
     * @param parent JavaFX node-based UI Controls and all layout containers (f.e. Pane). Only used for table-sizing.
     */
    public TableBuilder(@NonNull Region parent, @NonNull List<S> tableItems){
        tableView.getItems().addAll(tableItems);
        tableView.prefWidthProperty().bind(parent.widthProperty());
        tableView.prefHeightProperty().bind(parent.heightProperty());
    }

    /**
     * @param columnWidth (%), defines column width depending on table width.
     * @param cellValueFactory Defines what is shown in a cell.
     */
    public <T> void addColumn(@NonNull String columnName,
                              float columnWidth,
                              @NonNull Callback<TableColumn.CellDataFeatures<S, T>, ObservableValue<T>> cellValueFactory){
        addColumn(columnName, columnWidth, cellValueFactory, null);
    }

    /**
     * @param columnWidth (%), defines column width depending on table width.
     * @param cellValueFactory Defines what is shown in a cell.
     * @param cellFactory Defines how the cell is build, f.e.: with a button, checkbox, etc.
     * @implNote Do not use this method, if passing {@code cellFactory = null}
     */
    public <T> void addColumn(@NonNull String columnName,
                              float columnWidth,
                              @NonNull Callback<TableColumn.CellDataFeatures<S, T>, ObservableValue<T>> cellValueFactory,
                              @Nullable Callback<TableColumn<S, T>, TableCell<S, T>> cellFactory){
        TableColumn<S, T> newColumn = new TableColumn<>(columnName);
        newColumn.setCellValueFactory(cellValueFactory);
        if (cellFactory != null) newColumn.setCellFactory(cellFactory);
        newColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(columnWidth));
        tableView.getColumns().add(newColumn);
    }

    /**
     * Add item to context-menu of table
     * @param itemName Text which is display in the context-menu for this item.
     * @param onClickAction Action to be performed on item-click.
     */
    public void addRowContextMenuItem(@NonNull String itemName, @NonNull EventHandler<ActionEvent> onClickAction){
        MenuItem newContextMenuItem = new MenuItem(itemName);
        newContextMenuItem.setOnAction(onClickAction);
        rowContextMenu.getItems().add(newContextMenuItem);
        initializeRowFactory();
    }

    public void setActionOnSingleClickRow(@NonNull Consumer<S> onSingleClickAction){
        this.onRowSingleClickAction = onSingleClickAction;
        initializeRowFactory();
    }

    public void setActionOnDoubleClickRow(@NonNull Consumer<S> onDoubleClickAction){
        this.onRowDoubleClickAction = onDoubleClickAction;
        initializeRowFactory();
    }

    private void initializeRowFactory(){
        tableView.setRowFactory(tableView -> {
            TableRow<S> tableRow = new TableRow<>();

            // Handling context-menu
            tableRow.itemProperty().addListener((observableValue, oldItem, newItem) -> {
                if (newItem == null || rowContextMenu.getItems().isEmpty()) {
                    tableRow.setContextMenu(null);
                } else {
                    tableRow.setContextMenu(rowContextMenu);
                }
            });

            // Handling mouse-clicks
            tableRow.setOnMouseClicked(mouseEvent -> {
                if (tableRow.isEmpty()) return;

                // single-click
                if (mouseEvent.getClickCount() == 1 && onRowSingleClickAction != null) {
                    onRowSingleClickAction.accept(tableRow.getItem());
                }

                // double-click
                if (mouseEvent.getClickCount() == 2 && onRowDoubleClickAction != null) {
                    onRowDoubleClickAction.accept(tableRow.getItem());
                }
            });

            return tableRow;
        });
    }

    /**
     * @return Table with all added columns, items and so on.
     */
    public @NonNull TableView<S> getResult(){
        return tableView;
    }
}
