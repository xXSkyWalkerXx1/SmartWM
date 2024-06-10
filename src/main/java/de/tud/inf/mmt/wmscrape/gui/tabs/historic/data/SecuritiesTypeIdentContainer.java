package de.tud.inf.mmt.wmscrape.gui.tabs.historic.data;

import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.HistoricWebsiteIdentifiers;
import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.enums.IdentType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import org.springframework.lang.NonNull;

import java.net.URL;
import java.util.ResourceBundle;

import static de.tud.inf.mmt.wmscrape.gui.tabs.historic.controller.HistoricWebsiteTabController.addTypeToChoiceBox;
import static de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.enums.IdentTypes.*;
import static de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.enums.IdentTypes.IDENT_TYPE_DEACTIVATED;


/***
 * Used for each securities-type in the website-configuration to store & handle the data.
 */
public class SecuritiesTypeIdentContainer implements Initializable {

    private SecuritiesType type;

    @FXML
    private ChoiceBox<IdentType>
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

    @FXML
    private TextField
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
    public SecuritiesTypeIdentContainer(SecuritiesType type){
        this.type = type;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addTypeToChoiceBox(idTypeHistoryCourse, IDENT_TYPE_DEACTIVATED);
        addTypeToChoiceBox(idTypeDateFromDay, IDENT_TYPE_SIMPLE);
        addTypeToChoiceBox(idTypeDateFromMonth, IDENT_TYPE_SIMPLE);
        addTypeToChoiceBox(idTypeDateFromYear, IDENT_TYPE_SIMPLE);
        addTypeToChoiceBox(idTypeDateToDay, IDENT_TYPE_SIMPLE);
        addTypeToChoiceBox(idTypeDateToMonth, IDENT_TYPE_SIMPLE);
        addTypeToChoiceBox(idTypeDateToYear, IDENT_TYPE_SIMPLE);
        addTypeToChoiceBox(idTypeButtonLoad, IDENT_TYPE_DEACTIVATED);
        addTypeToChoiceBox(idTypeButtonNextPage, IDENT_TYPE_DEACTIVATED);
        addTypeToChoiceBox(idTypeCountPages, IDENT_TYPE_DEACTIVATED);
    }

    // region Getters
    public SecuritiesType getType() {
        return type;
    }

    public IdentType getIdTypeHistoryCourse() {
        return idTypeHistoryCourse.getValue();
    }

    public IdentType getIdTypeDateFromDay() {
        return idTypeDateFromDay.getValue();
    }

    public IdentType getIdTypeDateFromMonth() {
        return idTypeDateFromMonth.getValue();
    }

    public IdentType getIdTypeDateFromYear() {
        return idTypeDateFromYear.getValue();
    }

    public IdentType getIdTypeDateToDay() {
        return idTypeDateToDay.getValue();
    }

    public IdentType getIdTypeDateToMonth() {
        return idTypeDateToMonth.getValue();
    }

    public IdentType getIdTypeDateToYear() {
        return idTypeDateToYear.getValue();
    }

    public IdentType getIdTypeButtonLoad() {
        return idTypeButtonLoad.getValue();
    }

    public IdentType getIdTypeButtonNextPage() {
        return idTypeButtonNextPage.getValue();
    }

    public IdentType getIdTypeCountPages() {
        return idTypeCountPages.getValue();
    }

    public String getIdContentHistoryCourse() {
        return idContentHistoryCourse.getText();
    }

    public String getIdContentDateFromDay() {
        return idContentDateFromDay.getText();
    }

    public String getIdContentDateFromMonth() {
        return idContentDateFromMonth.getText();
    }

    public String getIdContentDateFromYear() {
        return idContentDateFromYear.getText();
    }

    public String getIdContentDateToDay() {
        return idContentDateToDay.getText();
    }

    public String getIdContentDateToMonth() {
        return idContentDateToMonth.getText();
    }

    public String getIdContentDateToYear() {
        return idContentDateToYear.getText();
    }

    public String getIdContentButtonLoad() {
        return idContentButtonLoad.getText();
    }

    public String getIdContentButtonNextPage() {
        return idContentButtonNextPage.getText();
    }

    public String getIdContentCountPages() {
        return idContentCountPages.getText();
    }

    public TextField getFieldHistoryCourse() {
        return idContentHistoryCourse;
    }

    public TextField getFieldDateFromDay() {
        return idContentDateFromDay;
    }

    public TextField getFieldDateFromMonth() {
        return idContentDateFromMonth;
    }

    public TextField getFieldDateFromYear() {
        return idContentDateFromYear;
    }

    public TextField getFieldDateToDay() {
        return idContentDateToDay;
    }

    public TextField getFieldDateToMonth() {
        return idContentDateToMonth;
    }

    public TextField getFieldDateToYear() {
        return idContentDateToYear;
    }

    public TextField getFieldButtonLoad() {
        return idContentButtonLoad;
    }

    public TextField getFieldButtonNextPage() {
        return idContentButtonNextPage;
    }

    public TextField getFieldCountPages() {
        return idContentCountPages;
    }
    // endregion

    // region Setters
    public void setType(SecuritiesType type) {
        this.type = type;
    }

    public void setIdTypeHistoryCourse(IdentType idTypeHistoryCourse) {
        this.idTypeHistoryCourse.setValue(idTypeHistoryCourse);
    }

    public void setIdTypeDateFromDay(IdentType idTypeDateFromDay) {
        this.idTypeDateFromDay.setValue(idTypeDateFromDay);
    }

    public void setIdTypeDateFromMonth(IdentType idTypeDateFromMonth) {
        this.idTypeDateFromMonth.setValue(idTypeDateFromMonth);
    }

    public void setIdTypeDateFromYear(IdentType idTypeDateFromYear) {
        this.idTypeDateFromYear.setValue(idTypeDateFromYear);
    }

    public void setIdTypeDateToDay(IdentType idTypeDateToDay) {
        this.idTypeDateToDay.setValue(idTypeDateToDay);
    }

    public void setIdTypeDateToMonth(IdentType idTypeDateToMonth) {
        this.idTypeDateToMonth.setValue(idTypeDateToMonth);
    }

    public void setIdTypeDateToYear(IdentType idTypeDateToYear) {
        this.idTypeDateToYear.setValue(idTypeDateToYear);
    }

    public void setIdTypeButtonLoad(IdentType idTypeButtonLoad) {
        this.idTypeButtonLoad.setValue(idTypeButtonLoad);
    }

    public void setIdTypeButtonNextPage(IdentType idTypeButtonNextPage) {
        this.idTypeButtonNextPage.setValue(idTypeButtonNextPage);
    }

    public void setIdTypeCountPages(IdentType idTypeCountPages) {
        this.idTypeCountPages.setValue(idTypeCountPages);
    }

    public void setIdContentHistoryCourse(String idContentHistoryCourse) {
        this.idContentHistoryCourse.setText(idContentHistoryCourse);
    }

    public void setIdContentDateFromDay(String idContentDateFromDay) {
        this.idContentDateFromDay.setText(idContentDateFromDay);
    }

    public void setIdContentDateFromMonth(String idContentDateFromMonth) {
        this.idContentDateFromMonth.setText(idContentDateFromMonth);
    }

    public void setIdContentDateFromYear(String idContentDateFromYear) {
        this.idContentDateFromYear.setText(idContentDateFromYear);
    }

    public void setIdContentDateToDay(String idContentDateToDay) {
        this.idContentDateToDay.setText(idContentDateToDay);
    }

    public void setIdContentDateToMonth(String idContentDateToMonth) {
        this.idContentDateToMonth.setText(idContentDateToMonth);
    }

    public void setIdContentDateToYear(String idContentDateToYear) {
        this.idContentDateToYear.setText(idContentDateToYear);
    }

    public void setIdContentButtonLoad(String idContentButtonLoad) {
        this.idContentButtonLoad.setText(idContentButtonLoad);
    }

    public void setIdContentButtonNextPage(String idContentButtonNextPage) {
        this.idContentButtonNextPage.setText(idContentButtonNextPage);
    }

    public void setIdContentCountPages(String idContentCountPages) {
        this.idContentCountPages.setText(idContentCountPages);
    }
    // endregion

    /***
     * Not mandatory are the contents for dateFromMonth, dateFromMonthYear as well for all dateTo fields.
     * @return True, if all mandatory contents (choiceboxes and textfields) are completed.
     */
    public boolean areInputsCompleted(){
        return !isStringEmpty(idContentHistoryCourse)
                && !isStringEmpty(idContentDateFromDay)
                //&& !isStringEmpty(idContentDateFromMonth)
                //&& !isStringEmpty(idContentDateFromYear)
                //&& !isStringEmpty(idContentDateToDay)
                //&& !isStringEmpty(idContentDateToMonth)
                //&& !isStringEmpty(idContentDateToYear)
                && !isStringEmpty(idContentButtonLoad)
                && !isStringEmpty(idContentButtonNextPage)
                && !isStringEmpty(idContentCountPages);
    }

    /***
     * @return True, if the string is null, empty or blank.
     */
    private boolean isStringEmpty(@NonNull TextField inputField){
        String input = inputField.getText();
        return input == null || input.isEmpty() || input.isBlank();
    }

    /***
     * Clear content of all choice-boxes and text-fields.
     */
    public void clearAll(){
        // clear choice-boxes
        idTypeHistoryCourse.setValue(null);
        idTypeDateFromDay.setValue(null);
        idTypeDateFromMonth.setValue(null);
        idTypeDateFromYear.setValue(null);
        idTypeDateToDay.setValue(null);
        idTypeDateToMonth.setValue(null);
        idTypeDateToYear.setValue(null);
        idTypeButtonLoad.setValue(null);
        idTypeButtonNextPage.setValue(null);
        idTypeCountPages.setValue(null);

        // clear text-fields
        idContentHistoryCourse.clear();
        idContentDateFromDay.clear();
        idContentDateFromMonth.clear();
        idContentDateFromYear.clear();
        idContentDateToDay.clear();
        idContentDateToMonth.clear();
        idContentDateToYear.clear();
        idContentButtonLoad.clear();
        idContentButtonNextPage.clear();
        idContentCountPages.clear();
    }

    /***
     * Write every data from {@link SecuritiesTypeIdentContainer} to {@link HistoricWebsiteIdentifiers}.
     */
    public void writeTo(@NonNull HistoricWebsiteIdentifiers identsEntity){
        identsEntity.setSecuritiesType(getType());
        identsEntity.setHistoricLinkIdentType(getIdTypeHistoryCourse());
        identsEntity.setHistoricLinkIdent(getIdContentHistoryCourse());
        identsEntity.setDateFromDayIdentType(getIdTypeDateFromDay());
        identsEntity.setDateFromDayIdent(getIdContentDateFromDay());
        identsEntity.setDateFromMonthIdentType(getIdTypeDateFromMonth());
        identsEntity.setDateFromMonthIdent(getIdContentDateFromMonth());
        identsEntity.setDateFromYearIdentType(getIdTypeDateFromYear());
        identsEntity.setDateFromYearIdent(getIdContentDateFromYear());
        identsEntity.setDateUntilDayIdentType(getIdTypeDateToDay());
        identsEntity.setDateUntilDayIdent(getIdContentDateToDay());
        identsEntity.setDateUntilMonthIdentType(getIdTypeDateToMonth());
        identsEntity.setDateUntilMonthIdent(getIdContentDateToMonth());
        identsEntity.setDateUntilYearIdentType(getIdTypeDateToYear());
        identsEntity.setDateUntilYearIdent(getIdContentDateToYear());
        identsEntity.setLoadButtonIdentType(getIdTypeButtonLoad());
        identsEntity.setLoadButtonIdent(getIdContentButtonLoad());
        identsEntity.setNextPageButtonIdentType(getIdTypeButtonNextPage());
        identsEntity.setNextPageButtonIdent(getIdContentButtonNextPage());
        identsEntity.setPageCountIdentType(getIdTypeCountPages());
        identsEntity.setPageCountIdent(getIdContentCountPages());
    }

    /***
     * Write every data from {@link HistoricWebsiteIdentifiers} to {@link SecuritiesTypeIdentContainer}.
     */
    public void writeFrom(@NonNull HistoricWebsiteIdentifiers identsEntity){
        setType(identsEntity.getSecuritiesType());
        setIdTypeHistoryCourse(identsEntity.getHistoricLinkIdentType());
        setIdTypeDateFromDay(identsEntity.getDateFromDayIdentType());
        setIdTypeDateFromMonth(identsEntity.getDateFromMonthIdentType());
        setIdTypeDateFromYear(identsEntity.getDateFromYearIdentType());
        setIdTypeDateToDay(identsEntity.getDateUntilDayIdentType());
        setIdTypeDateToMonth(identsEntity.getDateUntilMonthIdentType());
        setIdTypeDateToYear(identsEntity.getDateUntilYearIdentType());
        setIdTypeButtonLoad(identsEntity.getLoadButtonIdentType());
        setIdTypeButtonNextPage(identsEntity.getNextPageButtonIdentType());
        setIdTypeCountPages(identsEntity.getPageCountIdentType());
        setIdContentHistoryCourse(identsEntity.getHistoricLinkIdent());
        setIdContentDateFromDay(identsEntity.getDateFromDayIdent());
        setIdContentDateFromMonth(identsEntity.getDateFromMonthIdent());
        setIdContentDateFromYear(identsEntity.getDateFromYearIdent());
        setIdContentDateToDay(identsEntity.getDateUntilDayIdent());
        setIdContentDateToMonth(identsEntity.getDateUntilMonthIdent());
        setIdContentDateToYear(identsEntity.getDateUntilYearIdent());
        setIdContentButtonLoad(identsEntity.getLoadButtonIdent());
        setIdContentButtonNextPage(identsEntity.getNextPageButtonIdent());
        setIdContentCountPages(identsEntity.getPageCountIdent());
    }
}
