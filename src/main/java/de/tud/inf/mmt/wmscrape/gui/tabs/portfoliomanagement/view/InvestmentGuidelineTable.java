package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view;

import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.InvestmentGuideline;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.InvestmentType;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.util.Callback;
import org.apache.logging.log4j.util.TriConsumer;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.text.ParseException;
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
                    FormatUtils.formatFloat(100 - currSum), FormatUtils.formatFloat(input)
            ),
            this
    );
    private final TriConsumer<InvestmentType, Float, Float> showErrorChildDialog = (type, input, currSum) -> PrimaryTabManager.showDialog(
            Alert.AlertType.ERROR,
            "Fehler",
            String.format(
                    "Die Aufteilung des Gesamtvermögens nach '%s' muss in Summe 100 ergeben.\n" +
                            "Maximal verbleibende Eingabe: %s | Eingegeben: %s",
                    type, FormatUtils.formatFloat(100 - currSum), FormatUtils.formatFloat(input)
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
                null,
                entry -> new SimpleStringProperty(FormatUtils.formatFloat(entry.getAssetAllocation())),
                col -> {
                    var parentRow = col.getRowValue().getParent();
                    float curr = col.getRowValue().getValue().getAssetAllocation();

                    // Try to parse input
                    float input;
                    try {
                        input = FormatUtils.parseFloat(col.getNewValue());
                    } catch (ParseException e) {
                        return;
                    }

                    if (col.getRowValue().getValue().getType().isChild()) { // it's a child
                        // Only allow editing of child-entries if the parent is not the root-item and the asset allocation is set
                        if (parentRow.getValue().getAssetAllocation() == 0) return;
                        // if the sum of the parents children is not greater than 100
                        if (getChildAllocSum(parentRow) - curr + input > 100) {
                            showErrorChildDialog.accept(parentRow.getValue().getType(), input, getChildAllocSum(parentRow) - curr);
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
                        if (getParentAllocSum() - curr + input > 100) {
                            showErrorParentDialog.accept(input, getParentAllocSum() - curr);
                            return;
                        }
                    }
                    col.getRowValue().getValue().setAssetAllocation(input);
                },
                textField -> FieldFormatter.setInputFloatRange(textField, 0f, 100f),
                false
        ));
        getColumns().add(createDynamicColumn(
                "Maximale Risikoklasse (1-12)",
                "Die Risikoklasse gibt an, wie risikoreich die Anlage ist.\n" +
                        "Die Skala reicht von 1 (niedriges Risiko) bis 12 (hohes Risiko).",
                entry -> new SimpleStringProperty(String.valueOf(entry.getMaxRiskclass())),
                col -> col.getRowValue().getValue().setMaxRiskclass(Integer.parseInt(col.getNewValue())),
                textField -> FieldFormatter.setInputIntRange(textField, 1, 12),
                true,
                investmentType -> !InvestmentType.LIQUIDITY.equals(investmentType)
        ));
        getColumns().add(createDynamicColumn(
                "Max. Volatilität innerhalb 1 Jahr (%)",
                "Die maximale Volatilität gibt an, wie stark der Wert der Anlage innerhalb eines Jahres " +
                        "schwanken kann.\nEin höherer Wert bedeutet eine höhere Schwankungsbreite.",
                entry -> new SimpleStringProperty(FormatUtils.formatFloat(entry.getMaxVolatility())),
                col -> {
                    // Try to parse input
                    float input;
                    try {
                        input = FormatUtils.parseFloat(col.getNewValue());
                    } catch (ParseException e) {
                        return;
                    }

                    col.getRowValue().getValue().setMaxVolatility(input);
                },
                textField -> FieldFormatter.setInputFloatRange(textField, 0f, null),
                true,
                investmentType -> !InvestmentType.LIQUIDITY.equals(investmentType)
        ));

        var minSuccess = createStaticColumn("Erwarteter minimaler Anlageerfolg", null);
        getColumns().add(minSuccess);
        minSuccess.getColumns().add(createDynamicColumn(
                "Performance innerhalb 1 Jahr (%)",
                "Gibt den minimal erwarteten Wertzuwachs der Anlage innerhalb eines Jahres an.",
                entry -> new SimpleStringProperty(FormatUtils.formatFloat(entry.getPerformance())),
                col -> {
                    // Try to parse input
                    float input;
                    try {
                        input = FormatUtils.parseFloat(col.getNewValue());
                    } catch (ParseException e) {
                        return;
                    }

                    col.getRowValue().getValue().setPerformance(input);
                },
                textField -> FieldFormatter.setInputFloatRange(textField, 0f, null),
                true,
                investmentType -> !InvestmentType.LIQUIDITY.equals(investmentType)
        ));
        minSuccess.getColumns().add(createDynamicColumn(
                "Nettorendite seit Kauf (%)",
                "Gibt die minimal erwartete Rendite seit Kauf der Anlage an.",
                entry -> new SimpleStringProperty(FormatUtils.formatFloat(entry.getRendite())),
                col -> {
                    // Try to parse input
                    float input;
                    try {
                        input = FormatUtils.parseFloat(col.getNewValue());
                    } catch (ParseException e) {
                        return;
                    }

                    col.getRowValue().getValue().setRendite(input);
                },
                textField -> FieldFormatter.setInputFloatRange(textField, 0f, null),
                true,
                investmentType -> !InvestmentType.LIQUIDITY.equals(investmentType)
        ));

        getColumns().add(createDynamicColumn(
                "Chancen-Risiko-Zahl (%)",
                "Gibt an, um wie viel besser oder schlechter eine Anlage zum Benchmark ist.",
                entry -> new SimpleStringProperty(FormatUtils.formatFloat(entry.getChanceRiskNumber())),
                col -> {
                    // Try to parse input
                    float input;
                    try {
                        input = FormatUtils.parseFloat(col.getNewValue());
                    } catch (ParseException e) {
                        return;
                    }

                    col.getRowValue().getValue().setChanceRiskNumber(input);
                },
                FieldFormatter::setInputOnlyDecimalNumbers,
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
     // @param onInputAction Defines how the input is parsed, f.e. as float ({@link Float#parseFloat(String)}.
     * @param isOnlyParentEditable Defines if only the parent-entries should be editable and show his values.
     */
    private TreeTableColumn<InvestmentGuideline.Entry, String> createDynamicColumn(@NonNull String columnName,
                                                                                   @Nullable String columnDescr,
                                                                                   @Nullable Callback<InvestmentGuideline.Entry, ObservableValue<String>> cellValueFactory,
                                                                                   @Nullable EventHandler<TreeTableColumn.CellEditEvent<InvestmentGuideline.Entry, String>> onCommit,
                                                                                   @NonNull Consumer<TextField> inputFormatter,
                                                                                   //@NonNull Callback<String, String> onInputAction,
                                                                                   boolean isOnlyParentEditable) {
        return createDynamicColumn(columnName, columnDescr, cellValueFactory, onCommit, inputFormatter, isOnlyParentEditable, null);
    }

    /**
     * @param cellValueFactory Defines what is shown in a cell.
     * @param onCommit Defines what will be updated on commit.
     * @param inputFormatter Defines how the input should be formatted, f.e. numbers between 0 and 100.
     // @param onInputAction Defines how the input is parsed, f.e. as float ({@link Float#parseFloat(String)}.
     * @param isOnlyParentEditable Defines if only the parent-entries should be editable and show his values.
     * @param isEditable Defines if the cell is editable. Use to constrain specific cells in a row for a specific column.
     */
    private TreeTableColumn<InvestmentGuideline.Entry, String> createDynamicColumn(@NonNull String columnName,
                                                                                   @Nullable String columnDescr,
                                                                                   @Nullable Callback<InvestmentGuideline.Entry, ObservableValue<String>> cellValueFactory,
                                                                                   @Nullable EventHandler<TreeTableColumn.CellEditEvent<InvestmentGuideline.Entry, String>> onCommit,
                                                                                   @NonNull Consumer<TextField> inputFormatter,
                                                                                   //@NonNull Callback<String, String> onInputAction,
                                                                                   boolean isOnlyParentEditable,
                                                                                   @Nullable Predicate<InvestmentType> isEditable) {
        Callback<TreeTableColumn<InvestmentGuideline.Entry, String>, TreeTableCell<InvestmentGuideline.Entry, String>> cellFactory = new Callback<>() {
            @Override
            public TreeTableCell<InvestmentGuideline.Entry, String> call(TreeTableColumn<InvestmentGuideline.Entry, String> column) {
                return new TreeTableCell<>() {

                    final TextField inputField = new TextField();

                    {
                        inputFormatter.accept(inputField);
                        inputField.setOnAction(commit -> commitEdit(inputField.getText()));
                    }

                    @Override
                    public void startEdit() {
                        super.startEdit();
                        if (isOnlyParentEditable && getTableRow().getItem().getType().isChild()) return;
                        if (isEditable != null && !isEditable.test(getTableRow().getItem().getType())) return;

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
                            setTooltip(null);
                        } else {
                            setText(number);
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

        TreeTableColumn<InvestmentGuideline.Entry, String> newColumn = new TreeTableColumn<>();
        newColumn.setEditable(true);
        newColumn.setCellFactory(cellFactory);

        if (columnDescr == null) {
            newColumn.setText(columnName);
        } else { // Otherwise use the columns graphic as column label, to add a tooltip
            Label columnLabel = new Label(columnName);
            columnLabel.setMinWidth(Region.USE_PREF_SIZE);
            columnLabel.setTooltip(new Tooltip(columnDescr));
            columnLabel.widthProperty().addListener((observableValue, oldWidth, newWidth)
                    -> newColumn.setPrefWidth(newWidth.doubleValue() + 13) // add 13 padding
            );
            newColumn.setGraphic(columnLabel);
        }

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
