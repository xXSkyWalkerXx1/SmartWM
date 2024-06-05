package de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data;

import de.tud.inf.mmt.wmscrape.gui.tabs.historic.data.SecuritiesType;
import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.enums.IdentType;

import javax.persistence.*;

/***
 * Represents all identifiers for a securities-type.
 */
@Entity
@Table(name = "wertpapier_identifikatoren")
public class HistoricWebsiteIdentifiers {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private int id;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "securities_type")
    private SecuritiesType securitiesType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "website_id")
    private Website website;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "historic_link_ident_type", nullable = false)
    private IdentType historicLinkIdentType = IdentType.ID;

    @Column(columnDefinition = "TEXT", name = "historic_link_ident")
    private String historicLinkIdent;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "date_from_day_ident_type", nullable = false)
    private IdentType dateFromDayIdentType = IdentType.ID;

    @Column(columnDefinition = "TEXT", name = "date_from_day_ident")
    private String dateFromDayIdent;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "date_from_month_ident_type", nullable = false)
    private IdentType dateFromMonthIdentType = IdentType.ID;

    @Column(columnDefinition = "TEXT", name = "date_from_month_ident")
    private String dateFromMonthIdent;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "date_from_year_ident_type", nullable = false)
    private IdentType dateFromYearIdentType = IdentType.ID;

    @Column(columnDefinition = "TEXT", name = "date_from_year_ident")
    private String dateFromYearIdent;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "date_until_day_ident_type", nullable = false)
    private IdentType dateUntilDayIdentType = IdentType.ID;

    @Column(columnDefinition = "TEXT", name = "date_until_day_ident")
    private String dateUntilDayIdent;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "date_until_month_ident_type", nullable = false)
    private IdentType dateUntilMonthIdentType = IdentType.ID;

    @Column(columnDefinition = "TEXT", name = "date_until_month_ident")
    private String dateUntilMonthIdent;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "date_until_year_ident_type", nullable = false)
    private IdentType dateUntilYearIdentType = IdentType.ID;

    @Column(columnDefinition = "TEXT", name = "date_until_year_ident")
    private String dateUntilYearIdent;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "load_button_ident_type", nullable = false)
    private IdentType loadButtonIdentType = IdentType.ID;

    @Column(columnDefinition = "TEXT", name = "load_button_ident")
    private String loadButtonIdent;

    @Column(columnDefinition = "TEXT", name = "next_page_button_ident")
    private String nextPageButtonIdent;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "next_page_button_ident_type", nullable = false)
    private IdentType nextPageButtonIdentType = IdentType.ID;

    @Column(columnDefinition = "TEXT", name = "page_count_ident")
    private String pageCountIdent;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "page_count_ident_type", nullable = false)
    private IdentType pageCountIdentType = IdentType.ID;

    // region Getters
    public int getId() {
        return id;
    }

    public SecuritiesType getSecuritiesType() {
        return securitiesType;
    }

    public Website getWebsite() {
        return website;
    }

    public IdentType getHistoricLinkIdentType() {
        return historicLinkIdentType;
    }

    public String getHistoricLinkIdent() {
        return historicLinkIdent;
    }

    public IdentType getDateFromDayIdentType() {
        return dateFromDayIdentType;
    }

    public String getDateFromDayIdent() {
        return dateFromDayIdent;
    }

    public IdentType getDateFromMonthIdentType() {
        return dateFromMonthIdentType;
    }

    public String getDateFromMonthIdent() {
        return dateFromMonthIdent;
    }

    public IdentType getDateFromYearIdentType() {
        return dateFromYearIdentType;
    }

    public String getDateFromYearIdent() {
        return dateFromYearIdent;
    }

    public IdentType getDateUntilDayIdentType() {
        return dateUntilDayIdentType;
    }

    public String getDateUntilDayIdent() {
        return dateUntilDayIdent;
    }

    public IdentType getDateUntilMonthIdentType() {
        return dateUntilMonthIdentType;
    }

    public String getDateUntilMonthIdent() {
        return dateUntilMonthIdent;
    }

    public IdentType getDateUntilYearIdentType() {
        return dateUntilYearIdentType;
    }

    public String getDateUntilYearIdent() {
        return dateUntilYearIdent;
    }

    public IdentType getLoadButtonIdentType() {
        return loadButtonIdentType;
    }

    public String getLoadButtonIdent() {
        return loadButtonIdent;
    }

    public String getNextPageButtonIdent() {
        return nextPageButtonIdent;
    }

    public IdentType getNextPageButtonIdentType() {
        return nextPageButtonIdentType;
    }

    public String getPageCountIdent() {
        return pageCountIdent;
    }

    public IdentType getPageCountIdentType() {
        return pageCountIdentType;
    }
    // endregion

    // region Setters
    public void setSecuritiesType(SecuritiesType securitiesType) {
        this.securitiesType = securitiesType;
    }

    public void setWebsite(Website website) {
        this.website = website;
    }

    public void setHistoricLinkIdentType(IdentType historicLinkIdentType) {
        this.historicLinkIdentType = historicLinkIdentType;
    }

    public void setHistoricLinkIdent(String historicLinkIdent) {
        this.historicLinkIdent = historicLinkIdent;
    }

    public void setDateFromDayIdentType(IdentType dateFromDayIdentType) {
        this.dateFromDayIdentType = dateFromDayIdentType;
    }

    public void setDateFromDayIdent(String dateFromDayIdent) {
        this.dateFromDayIdent = dateFromDayIdent;
    }

    public void setDateFromMonthIdentType(IdentType dateFromMonthIdentType) {
        this.dateFromMonthIdentType = dateFromMonthIdentType;
    }

    public void setDateFromMonthIdent(String dateFromMonthIdent) {
        this.dateFromMonthIdent = dateFromMonthIdent;
    }

    public void setDateFromYearIdentType(IdentType dateFromYearIdentType) {
        this.dateFromYearIdentType = dateFromYearIdentType;
    }

    public void setDateFromYearIdent(String dateFromYearIdent) {
        this.dateFromYearIdent = dateFromYearIdent;
    }

    public void setDateUntilDayIdentType(IdentType dateUntilDayIdentType) {
        this.dateUntilDayIdentType = dateUntilDayIdentType;
    }

    public void setDateUntilDayIdent(String dateUntilDayIdent) {
        this.dateUntilDayIdent = dateUntilDayIdent;
    }

    public void setDateUntilMonthIdentType(IdentType dateUntilMonthIdentType) {
        this.dateUntilMonthIdentType = dateUntilMonthIdentType;
    }

    public void setDateUntilMonthIdent(String dateUntilMonthIdent) {
        this.dateUntilMonthIdent = dateUntilMonthIdent;
    }

    public void setDateUntilYearIdentType(IdentType dateUntilYearIdentType) {
        this.dateUntilYearIdentType = dateUntilYearIdentType;
    }

    public void setDateUntilYearIdent(String dateUntilYearIdent) {
        this.dateUntilYearIdent = dateUntilYearIdent;
    }

    public void setLoadButtonIdentType(IdentType loadButtonIdentType) {
        this.loadButtonIdentType = loadButtonIdentType;
    }

    public void setLoadButtonIdent(String loadButtonIdent) {
        this.loadButtonIdent = loadButtonIdent;
    }

    public void setNextPageButtonIdent(String nextPageButtonIdent) {
        this.nextPageButtonIdent = nextPageButtonIdent;
    }

    public void setNextPageButtonIdentType(IdentType nextPageButtonIdentType) {
        this.nextPageButtonIdentType = nextPageButtonIdentType;
    }

    public void setPageCountIdent(String pageCountIdent) {
        this.pageCountIdent = pageCountIdent;
    }

    public void setPageCountIdentType(IdentType pageCountIdentType) {
        this.pageCountIdentType = pageCountIdentType;
    }

    // endregion
}
