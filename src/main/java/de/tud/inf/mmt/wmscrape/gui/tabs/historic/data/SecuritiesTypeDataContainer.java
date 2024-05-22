package de.tud.inf.mmt.wmscrape.gui.tabs.historic.data;

import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.enums.IdentType;
import org.springframework.lang.NonNull;

import java.util.List;

/***
 * Represents the data-container for each securities-type in the website-configuration.
 */
public class SecuritiesTypeDataContainer {

    private final SecuritiesType type;

    private IdentType
            idTypeHistoryCourse,
            idTypeDateFromDay,
            idTypeDateFromMonth,
            idTypeDateFromYear,
            idTypeDateToDay,
            idTypeDateToMonth,
            idTypeDateToYear,
            idTypeButtonLoad,
            idTypeButtonNextPage,
            idTypeCountPages;

    private String
            idContentHistoryCourse,
            idContentDateFromDay,
            idContentDateFromMonth,
            idContentDateFromYear,
            idContentDateToDay,
            idContentDateToMonth,
            idContentDateToYear,
            idContentButtonLoad,
            idContentButtonNextPage,
            idContentCountPages;

    /***
     * Default constructor
     * @param type The securities-type which this container represents.
     */
    public SecuritiesTypeDataContainer(SecuritiesType type){
        this.type = type;
    }

    /***
     * Maps the type, given by the excel-sheet, to an instance of {@link SecuritiesType}.
     * @param type A = Aktie, BZR = Bezugsrechte; FA = Aktienfond; FAD = deutsche Aktienfonds, FAA = ausländischer FA;
     *             FAE = europäische FA, FV = Gemischte Fond; FVE = europäische FV; FVA = internationale FV;
     *             FG = Geldmarktfond; FGD = deutsche FG, FGE = europäische FG, FGA = ausländische FG,
     *             FIM = Immobilienfond; FR = Rentenfond / O = Obligationen; FRD = deutesche FR, FRA = Internat. FR,
     *             HF = Hedg, S = Spezialitäten, R = festferzinsliche Wertpapiere; RD = deutsche R, RA = ausländische R,
     *             Rst = Rohstoffe, Z = Zertifikate
     * @// TODO: 22.05.2024 Implement mapping of the other types!
     */
    public SecuritiesTypeDataContainer(@NonNull String type){
        switch (type){
            case "Z" -> this.type = SecuritiesType.CERTIFICATE;
            case "Rst" -> this.type = SecuritiesType.RESOURCE;
            case "A" -> this.type = SecuritiesType.SHARE;
            default -> throw new IllegalArgumentException(String.format(
                    "Der Wertpapier-Typ %s kann nicht gemappt werden.",
                    type
            ));
        }
    }

    // region Getters
    public SecuritiesType getType() {
        return type;
    }

    // region Identification-Types
    public IdentType getIdTypeHistoryCourse() {
        return idTypeHistoryCourse;
    }

    public IdentType getIdTypeDateFromDay() {
        return idTypeDateFromDay;
    }

    public IdentType getIdTypeDateFromMonth() {
        return idTypeDateFromMonth;
    }

    public IdentType getIdTypeDateFromYear() {
        return idTypeDateFromYear;
    }

    public IdentType getIdTypeDateToDay() {
        return idTypeDateToDay;
    }

    public IdentType getIdTypeDateToMonth() {
        return idTypeDateToMonth;
    }

    public IdentType getIdTypeDateToYear() {
        return idTypeDateToYear;
    }

    public IdentType getIdTypeButtonLoad() {
        return idTypeButtonLoad;
    }

    public IdentType getIdTypeButtonNextPage() {
        return idTypeButtonNextPage;
    }

    public IdentType getIdTypeCountPages() {
        return idTypeCountPages;
    }
    // endregion

    // region Identification-Contents
    public String getIdContentHistoryCourse() {
        return idContentHistoryCourse;
    }

    public String getIdContentDateFromDay() {
        return idContentDateFromDay;
    }

    public String getIdContentDateFromMonth() {
        return idContentDateFromMonth;
    }

    public String getIdContentDateFromYear() {
        return idContentDateFromYear;
    }

    public String getIdContentDateToDay() {
        return idContentDateToDay;
    }

    public String getIdContentDateToMonth() {
        return idContentDateToMonth;
    }

    public String getIdContentDateToYear() {
        return idContentDateToYear;
    }

    public String getIdContentButtonLoad() {
        return idContentButtonLoad;
    }

    public String getIdContentButtonNextPage() {
        return idContentButtonNextPage;
    }

    public String getIdContentCountPages() {
        return idContentCountPages;
    }
    // endregion
    // endregion

    // region Setters
    // region Identification-Types
    public void setIdTypeHistoryCourse(IdentType idTypeHistoryCourse) {
        this.idTypeHistoryCourse = idTypeHistoryCourse;
    }

    public void setIdTypeDateFromDay(IdentType idTypeDateFromDay) {
        this.idTypeDateFromDay = idTypeDateFromDay;
    }

    public void setIdTypeDateFromMonth(IdentType idTypeDateFromMonth) {
        this.idTypeDateFromMonth = idTypeDateFromMonth;
    }

    public void setIdTypeDateFromYear(IdentType idTypeDateFromYear) {
        this.idTypeDateFromYear = idTypeDateFromYear;
    }

    public void setIdTypeDateToDay(IdentType idTypeDateToDay) {
        this.idTypeDateToDay = idTypeDateToDay;
    }

    public void setIdTypeDateToMonth(IdentType idTypeDateToMonth) {
        this.idTypeDateToMonth = idTypeDateToMonth;
    }

    public void setIdTypeDateToYear(IdentType idTypeDateToYear) {
        this.idTypeDateToYear = idTypeDateToYear;
    }

    public void setIdTypeButtonLoad(IdentType idTypeButtonLoad) {
        this.idTypeButtonLoad = idTypeButtonLoad;
    }

    public void setIdTypeButtonNextPage(IdentType idTypeButtonNextPage) {
        this.idTypeButtonNextPage = idTypeButtonNextPage;
    }

    public void setIdTypeCountPages(IdentType idTypeCountPages) {
        this.idTypeCountPages = idTypeCountPages;
    }
    // endregion
    // region Identification-Contents
    public void setIdContentHistoryCourse(String idContentHistoryCourse) {
        this.idContentHistoryCourse = idContentHistoryCourse;
    }

    public void setIdContentDateFromDay(String idContentDateFromDay) {
        this.idContentDateFromDay = idContentDateFromDay;
    }

    public void setIdContentDateFromMonth(String idContentDateFromMonth) {
        this.idContentDateFromMonth = idContentDateFromMonth;
    }

    public void setIdContentDateFromYear(String idContentDateFromYear) {
        this.idContentDateFromYear = idContentDateFromYear;
    }

    public void setIdContentDateToDay(String idContentDateToDay) {
        this.idContentDateToDay = idContentDateToDay;
    }

    public void setIdContentDateToMonth(String idContentDateToMonth) {
        this.idContentDateToMonth = idContentDateToMonth;
    }

    public void setIdContentDateToYear(String idContentDateToYear) {
        this.idContentDateToYear = idContentDateToYear;
    }

    public void setIdContentButtonLoad(String idContentButtonLoad) {
        this.idContentButtonLoad = idContentButtonLoad;
    }

    public void setIdContentButtonNextPage(String idContentButtonNextPage) {
        this.idContentButtonNextPage = idContentButtonNextPage;
    }

    public void setIdContentCountPages(String idContentCountPages) {
        this.idContentCountPages = idContentCountPages;
    }
    // endregion
    // endregion

    /***
     * @return True, if all contents are complete or no content is completed.
     */
    public boolean isInputValid(){
        boolean isAllCompleted = !isStringEmpty(idContentHistoryCourse)
                && !isStringEmpty(idContentDateFromDay)
                && !isStringEmpty(idContentDateFromMonth)
                && !isStringEmpty(idContentDateFromYear)
                && !isStringEmpty(idContentDateToDay)
                && !isStringEmpty(idContentDateToMonth)
                && !isStringEmpty(idContentDateToYear)
                && !isStringEmpty(idContentButtonLoad)
                && !isStringEmpty(idContentButtonNextPage)
                && !isStringEmpty(idContentCountPages);
        boolean isNoneCompleted = isStringEmpty(idContentHistoryCourse)
                && isStringEmpty(idContentDateFromDay)
                && isStringEmpty(idContentDateFromMonth)
                && isStringEmpty(idContentDateFromYear)
                && isStringEmpty(idContentDateToDay)
                && isStringEmpty(idContentDateToMonth)
                && isStringEmpty(idContentDateToYear)
                && isStringEmpty(idContentButtonLoad)
                && isStringEmpty(idContentButtonNextPage)
                && isStringEmpty(idContentCountPages);
        return isAllCompleted || isNoneCompleted;
    }

    /***
     * @return True, if the string is null, empty or blank.
     */
    private boolean isStringEmpty(String string){
        return string == null || string.isEmpty() || string.isBlank();
    }
}
