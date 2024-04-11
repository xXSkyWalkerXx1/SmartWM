package de.tud.inf.mmt.wmscrape.dynamicdb.watchlist;

import de.tud.inf.mmt.wmscrape.dynamicdb.ColumnDatatype;
import de.tud.inf.mmt.wmscrape.dynamicdb.DbTableColumn;
import de.tud.inf.mmt.wmscrape.dynamicdb.VisualDatatype;
import de.tud.inf.mmt.wmscrape.gui.tabs.imports.data.ExcelCorrelation;
import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.correlation.identification.ElementIdentCorrelation;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * column entity for watch list entries (planned transactions)
 */

@Entity
@DiscriminatorValue("W")
public class WatchListColumn extends DbTableColumn {

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "watchListColumn",  orphanRemoval = true, cascade = CascadeType.ALL)
    private final List<ExcelCorrelation> excelCorrelations = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "watchListColumn", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<ElementIdentCorrelation> elementIdentCorrelations;

    /**
     * only used by hibernate. do not save an instance without setting the necessary fields
     */
    public WatchListColumn() {}

    public WatchListColumn(String name, ColumnDatatype columnDatatype) {
        super(name, columnDatatype);
    }

    public WatchListColumn(String name, VisualDatatype visualDatatype) {
        super(name, visualDatatype);
    }

    @Override
    public String getTableName() {
        return WatchListTableManager.TABLE_NAME;
    }
}
