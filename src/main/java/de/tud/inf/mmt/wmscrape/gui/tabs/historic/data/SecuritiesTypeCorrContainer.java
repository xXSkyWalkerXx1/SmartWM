package de.tud.inf.mmt.wmscrape.gui.tabs.historic.data;

import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.correlation.identification.ElementIdentCorrelation;
import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.element.WebsiteElement;
import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.enums.IdentType;
import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.enums.MultiplicityType;
import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.management.gui.ElementManagerTable;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.springframework.lang.NonNull;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.enums.IdentTypes.IDENT_TYPE_SIMPLE;

/**
 * Used for each securities-type in the element-configuration to store & handle the correlations.
 */
public class SecuritiesTypeCorrContainer implements Initializable {

    private final SecuritiesType securitiesType;
    private final WebsiteElement websiteElement;
    private final ElementManagerTable scrapingTableManager;

    @FXML
    private ChoiceBox<IdentType> tableIdentType;
    @FXML
    private TextField tableIdent;
    @FXML
    private TableView<ElementIdentCorrelation> tableCorrelations;

    public SecuritiesTypeCorrContainer(@NonNull SecuritiesType securitiesType, @NonNull WebsiteElement websiteElement,
                                       @NonNull ElementManagerTable scrapingTableManager){
        this.securitiesType = securitiesType;
        this.websiteElement = websiteElement;
        this.scrapingTableManager = scrapingTableManager;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // load data
        tableIdentType.getItems().addAll(IDENT_TYPE_SIMPLE);

        var elementIdentifiers = websiteElement.getElementIdentifierByType(securitiesType);
        if (elementIdentifiers != null){
            tableIdentType.setValue(elementIdentifiers.getTableIdenType());
            tableIdent.setText(elementIdentifiers.getTableIdent());
        } else {
            tableIdentType.getSelectionModel().selectFirst();
        }

        // init table with columns and correlations
        scrapingTableManager.initIdentCorrelationTable(websiteElement, tableCorrelations, MultiplicityType.TABELLE, securitiesType);

        ObservableList<ElementIdentCorrelation> elementIdentCorrelations = tableCorrelations.getItems();
        elementIdentCorrelations.forEach(correlation -> correlation.setSecuritiesType(securitiesType));

        // set listeners
        tableIdentType.getSelectionModel().selectedItemProperty()
                .addListener((o,ov,nv) -> elementIdentCorrelations.forEach(c -> c.setTableIdenType(nv)));
        tableIdent.textProperty()
                .addListener((o,ov,nv) -> elementIdentCorrelations.forEach(c -> c.setTableIdent(nv)));
    }

    // region Getters
    public SecuritiesType getSecuritiesType() {
        return securitiesType;
    }

    public List<ElementIdentCorrelation> getCorrelations() {
        return tableCorrelations.getItems();
    }
    // endregion

    /**
     * @return True, if table-ident -type and -content are set.
     */
    public boolean areMandatoryInputsCompleted() {
        return tableIdent.getText() != null && !tableIdent.getText().isBlank();
    }
}
