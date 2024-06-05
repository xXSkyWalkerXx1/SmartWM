package de.tud.inf.mmt.wmscrape.gui.tabs.historic.data;

import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.correlation.identification.ElementIdentCorrelation;
import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.element.WebsiteElement;
import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.enums.IdentType;
import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.enums.MultiplicityType;
import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.management.gui.ElementManagerTable;
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

/***
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

        tableIdentType.setValue(websiteElement.getTableIdenType());
        tableIdent.setText(websiteElement.getTableIdent());

        // set listeners
        tableIdentType.getSelectionModel().selectedItemProperty().addListener((o,ov,nv) -> websiteElement.setTableIdenType(nv));
        tableIdent.textProperty().addListener((o,ov,nv) -> websiteElement.setTableIdent(nv));

        // init table with columns and correlations
        scrapingTableManager.initIdentCorrelationTable(websiteElement, tableCorrelations, MultiplicityType.TABELLE, securitiesType);
        tableCorrelations.getItems().forEach(correlation -> correlation.setSecuritiesType(securitiesType));
    }

    // region Getters
    public SecuritiesType getSecuritiesType() {
        return securitiesType;
    }

    public IdentType getTableIdentType(){
        return tableIdentType.getValue();
    }

    public String getTableIdent(){
        return tableIdent.getText();
    }

    public List<ElementIdentCorrelation> getCorrelations() {
        return tableCorrelations.getItems();
    }
    // endregion
}
