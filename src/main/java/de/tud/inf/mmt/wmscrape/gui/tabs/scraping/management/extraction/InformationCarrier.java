package de.tud.inf.mmt.wmscrape.gui.tabs.scraping.management.extraction;

import de.tud.inf.mmt.wmscrape.dynamicdb.ColumnDatatype;
import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.enums.IdentType;
import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.selection.ElementSelection;

import java.sql.Date;
import java.util.Map;

/**
 * is a uniform data object containing all the necessary information for the complete extraction process.
 * one carrier holds the information for one data element, where to save it in the database and where to find on the website.
 * a carrier may be "reused" by changing key information by {@link TableExtraction#correctCarrierValues(Map, ElementSelection)}
 */
public class InformationCarrier {


    private final ColumnDatatype datatype;
    private final IdentType identType;
    private final String identifier;
    private final String regexFilter;
    private final Date date;
    private String dbTableName;
    private String dbColName;

    // stock/course single+table
    private String isin;


    private String extractedData = null;

    public InformationCarrier(Date date, ColumnDatatype datatype, IdentType identType, String identifier, String regexFilter) {
        this.date = date;
        this.datatype = datatype;
        this.identType = identType;
        this.identifier = identifier;
        this.regexFilter = regexFilter;
    }

    public void setDbTableName(String dbTableName) {
        this.dbTableName = dbTableName;
    }

    public String getDbTableName() {
        return dbTableName;
    }

    public void setDbColName(String dbColName) {
        this.dbColName = dbColName;
    }

    public String getDbColName() {
        return dbColName;
    }

    public Date getDate() {
        return date;
    }

    public ColumnDatatype getDatatype() {
        return datatype;
    }

    public IdentType getIdentType() {
        return identType;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getRegexFilter() {
        return regexFilter;
    }

    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    public String getExtractedData() {
        return extractedData;
    }

    public void setExtractedData(String extractedData) {
        this.extractedData = extractedData;
    }
}
