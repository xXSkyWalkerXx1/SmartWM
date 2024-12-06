package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view;

import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.InvestmentGuideline;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.InvestmentType;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.util.Callback;
import org.apache.logging.log4j.util.TriConsumer;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.xml.stream.events.EntityReference;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class InvestmentGuidelineTable extends TreeTableView<InvestmentGuideline.Entry> {

    private final TreeItem<InvestmentGuideline.Entry> rootItem = new TreeItem<>(new InvestmentGuideline.Entry());
    private final BiConsumer<Float, Float> showErrorParentDialog = (input, currSum) -> PrimaryTabManager.showDialog(
            Alert.AlertType.ERROR,
            "Fehler",
            String.format(
                    "Die Aufteilung des Gesamtvermögens muss in Summe 100 ergeben.\n" +
                            "Maximal verbleibende Eingabe: %s | Eingegeben: %s",
                    100 - currSum, input
            ),
            this
    );
    private final TriConsumer<InvestmentType, Float, Float> showErrorChildDialog = (type, input, currSum) -> PrimaryTabManager.showDialog(
            Alert.AlertType.ERROR,
            "Fehler",
            String.format(
                    "Die Aufteilung des Gesamtvermögens nach '%s' muss in Summe 100 ergeben.\n" +
                            "Maximal verbleibende Eingabe: %s | Eingegeben: %s",
                    type, 100 - currSum, input
            ),
            this
    );

    /**
     * @param parent JavaFX node-based UI Controls and all layout containers (f.e. Pane). Only used for view-sizing.
     */
    public InvestmentGuidelineTable(@NonNull Region parent, @NonNull List<InvestmentGuideline.Entry> entries) {
        // Define columns
        getColumns().add(createStaticColumn(
                "Anlagetyp",
                col -> new SimpleStringProperty(col.getValue().getValue().getType().toString())
        ));
        getColumns().add(createDynamicColumn(
                "Aufteilung Gesamtvermögen (%)",
                entry -> new SimpleFloatProperty(entry.getAssetAllocation()),
                col -> {
                    var parentRow = col.getRowValue().getParent();
                    float input = (float) col.getNewValue();
                    float currAlloc = col.getRowValue().getValue().getAssetAllocation();

                    if (col.getRowValue().getValue().getType().isChild()) { // it's a child
                        // Only allow editing of child-entries if the parent is not the root-item and the asset allocation is set
                        if (parentRow.getValue().getAssetAllocation() == 0) return;
                        // if the sum of the parents children is not greater than 100
                        if (getChildAllocSum(parentRow) - currAlloc + input > 100) {
                            showErrorChildDialog.accept(
                                    parentRow.getValue().getType(),
                                    input,
                                    getChildAllocSum(parentRow) - currAlloc
                            );
                            return;
                        }
                    } else {
                        if (parentRow == null) return; // it's the root-item
                        // it's a parent

                        // Set asset allocation of all children to 0 if the parent is set to 0
                        if (input == 0) {
                            col.getRowValue().getChildren().forEach(child -> {
                                child.getValue().setAssetAllocation(0);
                                col.getTreeTableView().refresh();
                            });
                        }
                        // Return if sum is greater than 100
                        if (getParentAllocSum() - currAlloc + input > 100) {
                            showErrorParentDialog.accept(input, getParentAllocSum() - currAlloc);
                            return;
                        }
                    }
                    col.getRowValue().getValue().setAssetAllocation(input);
                },
                textField -> FieldFormatter.setInputFloatRange(textField, 0, 100),
                Float::parseFloat,
                false
        ));
        getColumns().add(createDynamicColumn(
                "Maximale Risikoklasse (1-12)",
                entry -> new SimpleIntegerProperty(entry.getMaxRiskclass()),
                col -> col.getRowValue().getValue().setMaxRiskclass((int) col.getNewValue()),
                textField -> FieldFormatter.setInputIntRange(textField, 1, 12),
                Integer::parseInt,
                true,
                investmentType -> !InvestmentType.LIQUIDITY.equals(investmentType)
        ));
        getColumns().add(createDynamicColumn(
                "Max. Volatilität innerhalb 1 Jahr (%)",
                entry -> new SimpleFloatProperty(entry.getMaxVolatility()),
                col -> col.getRowValue().getValue().setMaxVolatility((float) col.getNewValue()),
                textField -> FieldFormatter.setInputFloatRange(textField, 0, 100),
                Float::parseFloat,
                true,
                investmentType -> !InvestmentType.LIQUIDITY.equals(investmentType)
        ));

        var minSuccess = createStaticColumn("Erwarteter minimaler Anlageerfolg", null);
        getColumns().add(minSuccess);
        minSuccess.getColumns().add(createDynamicColumn(
                "Performance innerhalb 12 Monate (%)",
                entry -> new SimpleFloatProperty(entry.getPerformance()),
                col -> col.getRowValue().getValue().setPerformance((float) col.getNewValue()),
                FieldFormatter::setInputOnlyDecimalNumbers,
                Float::parseFloat,
                true,
                investmentType -> !InvestmentType.LIQUIDITY.equals(investmentType)
        ));
        minSuccess.getColumns().add(createDynamicColumn(
                "Rendite seit Kauf (%)",
                entry -> new SimpleFloatProperty(entry.getRendite()),
                col -> col.getRowValue().getValue().setRendite((float) col.getNewValue()),
                FieldFormatter::setInputOnlyDecimalNumbers,
                Float::parseFloat,
                true,
                investmentType -> !InvestmentType.LIQUIDITY.equals(investmentType)
        ));

        getColumns().add(createDynamicColumn(
                "Chancen-Risiko-Zahl (%)",
                entry -> new SimpleFloatProperty(entry.getChanceRiskNumber()),
                col -> col.getRowValue().getValue().setChanceRiskNumber((float) col.getNewValue()),
                textField -> FieldFormatter.setInputFloatRange(textField, 0, 100),
                Float::parseFloat,
                true,
                investmentType -> !InvestmentType.LIQUIDITY.equals(investmentType)
        ));

        // Initialize table
        rootItem.setExpanded(true);
        setRoot(rootItem);
        setShowRoot(false);
        setEditable(true);
        initializeItems(entries);
        prefWidthProperty().bind(parent.widthProperty());
        //prefHeightProperty().bind(parent.heightProperty());
    }

    /**
     * @param cellValueFactory Defines what is shown in a cell.
     * @return Static (not-editable) column with the given name and cell value factory.
     */
    private TreeTableColumn<InvestmentGuideline.Entry, String> createStaticColumn(@NonNull String columnName,
                                                                                  @Nullable Callback<TreeTableColumn.CellDataFeatures<InvestmentGuideline.Entry, String>, ObservableValue<String>> cellValueFactory) {
        TreeTableColumn<InvestmentGuideline.Entry, String> newColumn = new TreeTableColumn<>(columnName);
        newColumn.setEditable(false);
        if (cellValueFactory != null) newColumn.setCellValueFactory(cellValueFactory);
        return newColumn;
    }

    /**
     * @param cellValueFactory Defines what is shown in a cell.
     * @param onCommit Defines what will be updated on commit.
     * @param inputFormatter Defines how the input should be formatted, f.e. numbers between 0 and 100.
     * @param onInputAction Defines how the input is parsed, f.e. as float ({@link Float#parseFloat(String)}.
     * @param isOnlyParentEditable Defines if only the parent-entries should be editable and show his values.
     * @return Static (not-editable) column with the given name and cell value factory.
     */
    private TreeTableColumn<InvestmentGuideline.Entry, Number> createDynamicColumn(@NonNull String columnName,
                                                                                   @Nullable Callback<InvestmentGuideline.Entry, ObservableValue<Number>> cellValueFactory,
                                                                                   @Nullable EventHandler<TreeTableColumn.CellEditEvent<InvestmentGuideline.Entry, Number>> onCommit,
                                                                                   @NonNull Consumer<TextField> inputFormatter,
                                                                                   @NonNull Callback<String, Number> onInputAction,
                                                                                   boolean isOnlyParentEditable) {
        return createDynamicColumn(columnName, cellValueFactory, onCommit, inputFormatter, onInputAction, isOnlyParentEditable, null);
    }

    /**
     * @param cellValueFactory Defines what is shown in a cell.
     * @param onCommit Defines what will be updated on commit.
     * @param inputFormatter Defines how the input should be formatted, f.e. numbers between 0 and 100.
     * @param onInputAction Defines how the input is parsed, f.e. as float ({@link Float#parseFloat(String)}.
     * @param isOnlyParentEditable Defines if only the parent-entries should be editable and show his values.
     * @param isEditable Defines if the cell is editable. Use to constrain specific cells in a row for a specific column.
     * @return Static (not-editable) column with the given name and cell value factory.
     */
    private TreeTableColumn<InvestmentGuideline.Entry, Number> createDynamicColumn(@NonNull String columnName,
                                                                                   @Nullable Callback<InvestmentGuideline.Entry, ObservableValue<Number>> cellValueFactory,
                                                                                   @Nullable EventHandler<TreeTableColumn.CellEditEvent<InvestmentGuideline.Entry, Number>> onCommit,
                                                                                   @NonNull Consumer<TextField> inputFormatter,
                                                                                   @NonNull Callback<String, Number> onInputAction,
                                                                                   boolean isOnlyParentEditable,
                                                                                   @Nullable Predicate<InvestmentType> isEditable) {
        Callback<TreeTableColumn<InvestmentGuideline.Entry, Number>, TreeTableCell<InvestmentGuideline.Entry, Number>> cellFactory = new Callback<>() {
            @Override
            public TreeTableCell<InvestmentGuideline.Entry, Number> call(TreeTableColumn<InvestmentGuideline.Entry, Number> column) {
                return new TreeTableCell<>() {

                    final TextField inputField = new TextField();

                    {
                        inputFormatter.accept(inputField);
                        inputField.setOnAction(commit -> commitEdit(onInputAction.call(inputField.getText())));
                    }

                    @Override
                    public void startEdit() {
                        super.startEdit();
                        if (isOnlyParentEditable && getTableRow().getItem().getType().isChild()) return;
                        if (isEditable != null && !isEditable.test(getTableRow().getItem().getType())) return;

                        graphicProperty().setValue(inputField);
                        setText(null);
                        inputField.setText(getItem() != null ? getItem().toString() : "");
                        inputField.requestFocus();
                    }

                    @Override
                    public void cancelEdit() {
                        super.cancelEdit();
                        setText(getItem() != null ? getItem().toString() : "");
                        graphicProperty().setValue(null);
                    }

                    @Override
                    protected void updateItem(Number number, boolean empty) {
                        super.updateItem(number, empty);
                        if (empty || number == null) {
                            setText(null);
                            setTooltip(null);
                        } else {
                            setText(number.toString());
                            if (getTableRow().getItem().getType().isChild()) {
                                setTooltip(new Tooltip(String.format(
                                        "Anteil der %s in Relation zum Anteil der %s",
                                        getTableRow().getItem().getType(),
                                        getTableRow().getTreeItem().getParent().getValue().getType()
                                )));
                            }
                        }
                        setGraphic(null);
                    }
                };
            }
        };

        TreeTableColumn<InvestmentGuideline.Entry, Number> newColumn = new TreeTableColumn<>(columnName);
        newColumn.setEditable(true);
        newColumn.setCellFactory(cellFactory);

        if (cellValueFactory != null) newColumn.setCellValueFactory(cell -> {
            InvestmentGuideline.Entry entry = cell.getValue().getValue();
            if (isOnlyParentEditable && entry.getType().isChild()) return null;
            if (isEditable != null && !isEditable.test(entry.getType())) return null;
            return cellValueFactory.call(cell.getValue().getValue());
        });
        if (onCommit != null) newColumn.setOnEditCommit(onCommit);

        return newColumn;
    }

    private void initializeItems(List<InvestmentGuideline.Entry> entries) {
        for (InvestmentGuideline.Entry entry : entries) {
            TreeItem<InvestmentGuideline.Entry> item = new TreeItem<>(entry);
            rootItem.getChildren().add(item);

            if (!entry.getChildEntries().isEmpty()) {
                for (InvestmentGuideline.Entry child : entry.getChildEntries()) {
                    item.getChildren().add(new TreeItem<>(child));
                }
            }
        }
    }

    private float getParentAllocSum() {
        return rootItem.getChildren().stream()
                .filter(item -> !item.getValue().getType().isChild())
                .map(item -> item.getValue().getAssetAllocation())
                .reduce(0f, Float::sum);
    }

    private float getChildAllocSum(@NonNull TreeItem<InvestmentGuideline.Entry> parent) {
        return parent.getChildren().stream()
                .filter(entry -> entry.getValue().getType().isChild())
                .map(item -> item.getValue().getAssetAllocation())
                .reduce(0f, Float::sum);
    }
}
