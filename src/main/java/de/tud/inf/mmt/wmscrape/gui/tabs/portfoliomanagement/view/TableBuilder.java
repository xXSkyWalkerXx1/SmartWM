package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view;

import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.util.Callback;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Map;
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
     * @implNote Height of table will be bind to parent height.
     */
    public TableBuilder(@NonNull Region parent, @NonNull List<S> tableItems){
        this(parent, tableItems, true);
    }

    /**
     * @param parent JavaFX node-based UI Controls and all layout containers (f.e. Pane). Only used for table-sizing.
     */
    public TableBuilder(@NonNull Region parent, @NonNull List<S> tableItems, boolean bindHeightToParent){
        tableView.getItems().addAll(tableItems);
        tableView.setEditable(true);
        tableView.prefWidthProperty().bind(parent.widthProperty());
        if (bindHeightToParent) tableView.prefHeightProperty().bind(parent.heightProperty());
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
     * @implNote Use {@link TableBuilder#addColumn(String, float, Callback)}, if passing {@code cellFactory = null}
     */
    public <T> void addColumn(@NonNull String columnName,
                              float columnWidth,
                              @NonNull Callback<TableColumn.CellDataFeatures<S, T>, ObservableValue<T>> cellValueFactory,
                              @Nullable Callback<TableColumn<S, T>, TableCell<S, T>> cellFactory){
        TableColumn<S, T> newColumn = createDefaultColumn(columnName, cellValueFactory, cellFactory);
        newColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(columnWidth));
        tableView.getColumns().add(newColumn);
    }

    /**
     * Creates and adds a new editable (with text-field) column in decimal-format from 0f to 100f.
     * @param columnWidth (%), defines column width depending on table width.
     * @param cellValueFactory Defines what is shown in a cell.
     * @param onCommit Defines what will be updated on commit.
     * @param textFieldFormatter Defines how the text-field is formatted. Use f.e. {@link FieldFormatter}
     */
    public <T> void addEditableColumn(@NonNull String columnName,
                                      float columnWidth,
                                      @NonNull Consumer<TextField> textFieldFormatter,
                                      @NonNull Callback<TableColumn.CellDataFeatures<S, String>, ObservableValue<String>> cellValueFactory,
                                      @NonNull EventHandler<TableColumn.CellEditEvent<S, String>> onCommit) {
        Callback<TableColumn<S, String>, TableCell<S, String>> cellFactory = new Callback<>() {
            @Override
            public TableCell<S, String> call(TableColumn<S, String> column) {
                return new TableCell<>() {

                    final TextField inputField = new TextField();

                    {
                        textFieldFormatter.accept(inputField);
                        inputField.setOnAction(commit -> commitEdit(inputField.getText()));
                    }

                    @Override
                    public void startEdit() {
                        super.startEdit();
                        graphicProperty().setValue(inputField);
                        setText(null);
                        inputField.setText(getItem() != null ? getItem() : "");
                        inputField.requestFocus();
                    }

                    @Override
                    public void cancelEdit() {
                        super.cancelEdit();
                        setText(getItem() != null ? getItem() : "");
                        graphicProperty().setValue(null);
                    }

                    @Override
                    protected void updateItem(String number, boolean empty) {
                        super.updateItem(number, empty);
                        if (empty || number == null) {
                            setText(null);
                        } else {
                            setText(number);
                        }
                        setGraphic(null);
                    }
                };
            }
        };

        TableColumn<S, String> newColumn = createDefaultColumn(columnName, cellValueFactory, cellFactory);
        newColumn.setEditable(true);
        newColumn.setOnEditCommit(onCommit);
        newColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(columnWidth));
        tableView.getColumns().add(newColumn);
    }

    /**
     * @param subCols Map entries containing of sub-column-name and his cell-value-factory.
     */
    public <T> void addNestedColumn(@NonNull String parentColName,
                                    float parentColumnWidth,
                                    @NonNull Map.Entry<String, Callback<TableColumn.CellDataFeatures<S, T>, ObservableValue<T>>>... subCols) {
        TableColumn<S, T> newColumn = createDefaultColumn(parentColName, null, null);

        for (var subCol : subCols) {
            TableColumn<S, T> newSubCol = createDefaultColumn(subCol.getKey(), subCol.getValue(), null);
            newSubCol.prefWidthProperty().bind(newColumn.widthProperty().divide(subCols.length));
            newColumn.getColumns().add(newSubCol);
        }
        newColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(parentColumnWidth));
        tableView.getColumns().add(newColumn);
    }

    private <T> TableColumn<S, T> createDefaultColumn (@NonNull String columnName,
                                                       @Nullable Callback<TableColumn.CellDataFeatures<S, T>, ObservableValue<T>> cellValueFactory,
                                                       @Nullable Callback<TableColumn<S, T>, TableCell<S, T>> cellFactory) {
        TableColumn<S, T> newColumn = new TableColumn<>(columnName);
        newColumn.setEditable(false);
        if (cellValueFactory != null) newColumn.setCellValueFactory(cellValueFactory);
        if (cellFactory != null) newColumn.setCellFactory(cellFactory);
        return newColumn;
    }

    /**
     * Add item to context-menu of table
     * @param itemName Text which is display in the context-menu for this item.
     * @param onClickAction Action to be performed on item-click.
     */
    public void addRowContextMenuItem(@NonNull String itemName, @NonNull Consumer<S> onClickAction){
        MenuItem newContextMenuItem = new MenuItem(itemName);
        newContextMenuItem.setOnAction(actionEvent -> onClickAction.accept(tableView.getSelectionModel().getSelectedItem()));
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
