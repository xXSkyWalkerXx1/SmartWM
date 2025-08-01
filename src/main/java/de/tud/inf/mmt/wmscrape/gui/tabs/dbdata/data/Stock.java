package de.tud.inf.mmt.wmscrape.gui.tabs.dbdata.data;

import de.tud.inf.mmt.wmscrape.gui.tabs.depots.data.DepotTransaction;
import de.tud.inf.mmt.wmscrape.gui.tabs.historic.data.SecuritiesType;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view.FieldFormatter;
import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.selection.ElementSelection;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TextField;
import org.checkerframework.common.value.qual.IntRange;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "wertpapier")
public class Stock {

    @Id
    @Column(length = 50)
    private String isin;

    @OneToMany(fetch=FetchType.LAZY, mappedBy ="stock", orphanRemoval = true, cascade = CascadeType.ALL)
    private final List<DepotTransaction> depotTransactions = new ArrayList<>();

    @OneToMany(fetch=FetchType.LAZY, mappedBy ="stock", orphanRemoval = true, cascade = CascadeType.ALL)
    private final List<ElementSelection> elementSelections = new ArrayList<>();

    @Column(name = "wkn",columnDefinition = "TEXT")
    private String _wkn;

    @Column(name = "name",columnDefinition = "TEXT")
    private String _name;

    @Column(name = "typ",columnDefinition = "TEXT")
    private String _stockType;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "scrape_type", columnDefinition = "TEXT")
    private SecuritiesType _scrapeType;

    @IntRange(from = 1, to = 12)
    @Column(name = "risc_class")
    private Integer _riscClass;

    // don't know what its exactly for butt he wants it for sorting purposes
    @Column(name = "r_par")
    private Integer _sortOrder;

    @Transient
    private final SimpleStringProperty wkn = new SimpleStringProperty();
    @Transient
    private final SimpleStringProperty name = new SimpleStringProperty();
    @Transient
    private final SimpleStringProperty stockType = new SimpleStringProperty();
    @Transient
    private final SimpleStringProperty sortOrder = new SimpleStringProperty();
    @Transient
    private final SimpleStringProperty riscClass = new SimpleStringProperty();


    /**
     * only used by hibernate. do not save an instance without setting the necessary fields
     */
    public Stock() {}

    /**
     * Important: sortOrder is not a primitive and can be set to null!
     *
     * @param isin the unique stock isin
     * @param sortOrder optional sortOrder aka "r_par"
     * @param scrapeType abstracted stock-type, used to match stocks to their identifiers (f.e. website-configuration)
     */
    public Stock(String isin, String wkn, String name, String stockType, Integer sortOrder, SecuritiesType scrapeType, Integer riscClass) {
        this.isin = isin;
        this._wkn = wkn;
        this._name = name;
        this._stockType = stockType;
        this._scrapeType = scrapeType;
        this._riscClass = riscClass;
        this._sortOrder = sortOrder;
        initListener();
    }

    public String getIsin() {
        return isin;
    }

    public String getWkn() {
        return _wkn;
    }

    public SimpleStringProperty wknProperty() {
        return wkn;
    }

    public String getName() {
        return _name;
    }

    public SecuritiesType getScrapeType() {
        return _scrapeType;
    }

    public Integer getRiscClass() {
        return _riscClass;
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public SimpleStringProperty stockTypeProperty() {
        return stockType;
    }

    public SimpleStringProperty sortOrderProperty() {
        return sortOrder;
    }

    public SimpleStringProperty riscClassProperty() {
        return riscClass;
    }

    public void set_wkn(String _wkn) {
        this._wkn = _wkn;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public void set_stockType(String _stockType) {
        this._stockType = _stockType;
    }

    public void set_sortOrder(Integer _sortOrder) {
        this._sortOrder = _sortOrder;
    }

    public void set_riscClass(Integer _riscClass) {
        this._riscClass = _riscClass;
    }

    public void set_scrapeType(SecuritiesType scrapeType) { this._scrapeType = scrapeType; }

    /**
     * due to the fact that hibernate creates proxies (subclasses of the actual entities) one has to use "instanceof" to compare
     * objects. normally checking of equality can cause unexpected results.
     * lazy loaded fields are omitted because one can not know if a session is still attached.
     *
     * @param o the object to compare to
     * @return true if equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stock stock)) return false;
        return Objects.equals(isin, stock.isin);
    }

    /**
     * called after entity creation by hibernate (loading from the database)
     * updates the property values to those from the database
     */
    @PostLoad
    private void setPropertiesFromPersistence() {
        name.set(_name);
        wkn.set(_wkn);
        stockType.set(_stockType);
        sortOrder.set((_sortOrder == null) ? (null):(String.valueOf(_sortOrder)));
        riscClass.set(_riscClass == null ? null : String.valueOf(_riscClass));
        initListener();
    }

    /**
     * allows using properties which can't be stored by hibernate.
     * when a property changes the filed inside the entity changes which can be stored as usual
     */
    private void initListener() {
        wkn.addListener((o,ov,nv) -> _wkn = nv.trim());
        name.addListener((o,ov,nv) -> _name = nv.trim());
        stockType.addListener((o,ov,nv) -> _stockType = nv.trim());
        sortOrder.addListener((o,ov,nv) -> cleanOrder(ov, nv));
        riscClass.addListener((o,ov,nv) -> {
            if(nv == null || nv.isBlank()) {
                _riscClass = null;
                return;
            }
            if(ov.equals(nv)) return;

            String clean = nv.replaceAll("[^0-9]","");
            if(clean.matches("^[0-9]+$")) {
                riscClass.set(clean);
                _riscClass = Integer.valueOf(clean);
            } else {
                riscClass.set(ov);
            }
        });
    }

    /**
     * had to be added to allow modifying the "r_par" value which is numerical and therefore needs validation
     *
     * @param ov the previously set cell value
     * @param nv the value after committing the value (enter)
     */
    private void cleanOrder(String ov, String nv) {
        // a null value is explicitly allowed as it is not a primitive
        if(nv == null || nv.isBlank()) {
            sortOrder.set(null);
            _sortOrder = null;
            return;
        }
        
        if(ov.equals(nv)) return;

        String clean = nv.replaceAll("[^+\\-0-9]","");

        if(clean.matches("^[+-]?[0-9]+$")) {
            sortOrder.set(clean);
            _sortOrder = Integer.valueOf(clean);
        } else {
            sortOrder.set(ov);
        }
    }
}
