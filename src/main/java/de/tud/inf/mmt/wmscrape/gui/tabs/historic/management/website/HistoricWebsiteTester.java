package de.tud.inf.mmt.wmscrape.gui.tabs.historic.management.website;

import de.tud.inf.mmt.wmscrape.gui.tabs.historic.data.SecuritiesType;
import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.HistoricWebsiteIdentifiers;
import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.Website;
import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.enums.IdentType;
import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.management.website.WebsiteHandler;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import org.apache.poi.ss.formula.eval.NotImplementedFunctionException;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HistoricWebsiteTester extends WebsiteHandler {

    private int step = 0;
    private int dummiesPointer = 0;
    private boolean hasAcceptedCookies = false;
    private HistoricWebsiteIdentifiers currentIdentifiers;
    private String currentIsin;

    // this contains securities-types with an example (ISIN) to be tested
    private final List<Map.Entry<SecuritiesType, String>> dummies = new ArrayList<>(List.of(
            Map.entry(SecuritiesType.SHARE, "DE0005140008"),
            Map.entry(SecuritiesType.BOND, "DE000BU0E113"),
            Map.entry(SecuritiesType.ETF, "IE00B4L5Y983"),
            Map.entry(SecuritiesType.AETF, "IE00BF4G6Y48"),
            Map.entry(SecuritiesType.ETC, "DE000A0S9GB0"),
            Map.entry(SecuritiesType.FOND, "DE0009807008")
    ));

    public HistoricWebsiteTester(Website website, SimpleStringProperty logText) {
        super(website, logText, false);
    }

    // region Getters
    /**
     * @return Message about what will be done on next step.
     * @implNote Should be called mainly after {@link HistoricWebsiteTester#doNextStep()}.
     */
    @Nullable
    public String getNextStepAction() {
        return switch (step) {
            case 0 -> "Browser starten";
            case 1 -> "Webseite laden";
            case 2 -> "Cookies akzeptieren/ablehnen";
            case 3 -> "Login Informationen ausfüllen";
            case 4 -> "Einloggen";
            case 5 -> "Navigiere auf Such-Seite";
            case 6 -> String.format("Suche nach %s %s", currentIdentifiers.getSecuritiesType().getDisplayText(), currentIsin);
            case 7 -> "Navigiere zu historischen Daten";
            case 8 -> "Stelle Datum ein";
            case 9 -> "Lade historische Daten";
            case 10 -> "Ausloggen";
            case 11 -> "Browser schließen";
            case 12 -> "Test beenden";
            default -> null;
        };
    }

    public Map.Entry<SecuritiesType, String> getCurrentDummy() {
        return dummies.get(dummiesPointer);
    }
    // endregion

    @Override
    public boolean cancel() {
        // super.cancel(); use when called as service
        quit();
        return true;
    }

    @Override
    protected Task<Void> createTask() {
        // maybe create a service if necessary
        throw new NotImplementedFunctionException("WebsiteTester can't be run as a service.");
    }

    public boolean doNextStep() {
        // get current dummy data and stop if there could no container with identifiers be found
        loadCurrentDummyData();

        if (currentIdentifiers == null) {
            // only if there is no more dummy to test and the last step was 9 it will go through the if-statement
            if (step < 10) return true;
        }

        // if closed stop test
        if(step>0 && getDriver() == null) return true;

        // do step
        switch (step) {
            case 0 -> {
                if(!startBrowser()) {
                    step = 11;
                    return false;
                }
                if(isNotUsingLogin()) {
                    step = 5;
                    return false;
                }
            }
            case 1 -> {
                if (!loadLoginPage()) {
                    step = 11;
                    return false;
                }
                if(IdentType.DEAKTIVIERT.equals(website.getCookieAcceptIdentType())) {
                    step=3;
                    return false;
                }
            }
            case 2 ->  {
                declineNotifications();

                if (!acceptCookies()) {
                    step = 11;
                    return false;
                }
                else {
                    hasAcceptedCookies = true;
                }
            }
            case 3 -> {
                declineNotifications();

                if(isNotUsingLogin()) {
                    step = 5;
                    return false;
                }
                else if(!fillLoginInformation()) {
                    step = 11;
                    return false;
                }
            }
            case 4 -> {
                declineNotifications();

                if(!login()) {
                    step = 11;
                    return false;
                }
            }
            case 5 -> {
                declineNotifications();

                if(!loadSearchPage()) {
                    step = 11;
                    return false;
                }
                if(!hasAcceptedCookies && !acceptCookies()) {
                    step = 11;
                    return false;
                }
            }
            case 6 -> {
                declineNotifications();

                if (!searchForStock(currentIsin, currentIdentifiers.getSecuritiesType())) {
                    setCountersForNextSecuritiesType();
                    return false;
                }
            }
            case 7 -> {
                declineNotifications();

                if(!loadHistoricPage(currentIdentifiers)) {
                    setCountersForNextSecuritiesType();
                    return false;
                }
            }
            case 8 -> {
                declineNotifications();

                if(!setDate(currentIdentifiers)) {
                    setCountersForNextSecuritiesType();
                    return false;
                }

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ignore) {}

                if(!readPageCountTest(currentIdentifiers)) {
                    addToLog("INFO:\tSeitenzahl konnte nicht gelesen werden");
                }
            }
            case 9 -> {
                declineNotifications();

                if(!loadHistoricData(currentIdentifiers)) {
                    setCountersForNextSecuritiesType();
                    return false;
                }
                if(!nextTablePage(currentIdentifiers)) {
                    setCountersForNextSecuritiesType();
                    return false;
                }
                if (hasNextDummy()){
                    setCountersForNextSecuritiesType();
                    return false;
                }
                if(isNotUsingLogin()) {
                    step = 11;
                    return false;
                }
            }
            case 10 -> {
                declineNotifications();

                if(!logout()) {
                    step = 11;
                    return false;
                }
            }
            case 11 -> quit();
            default -> {
                return true;
            }
        }

        step++;
        return false;
    }

    private boolean hasNextDummy() {
        int startInd = dummiesPointer + 1;
        if (startInd >= dummies.size()) return false;

        var identifiers = dummies.subList(startInd, dummies.size());
        identifiers.removeIf(dummy -> website.getHistoricIdentifiersByType(dummy.getKey()) == null);

        return identifiers.size() > 0;
    }

    /**
     * @return True, if identifier-type for username is deactivated.
     */
    private boolean isNotUsingLogin() {
        return IdentType.DEAKTIVIERT.equals(website.getUsernameIdentType());
    }

    /**
     * Loads current dummy, identifiers and isin until the container of the identifiers is not null.
     * @implNote Uses recursive call.
     */
    private void loadCurrentDummyData() {
        Map.Entry<SecuritiesType, String> currentDummy = getCurrentDummy();
        currentIdentifiers = website.getHistoricIdentifiersByType(currentDummy.getKey());
        currentIsin = currentDummy.getValue();

        if (currentIdentifiers == null && hasNextDummy()) {
            dummiesPointer++;
            loadCurrentDummyData();
        }
    }

    /**
     * If there is still a dummy to test, it sets {@code step=5} and increases {@code dummiesPointer}, to test the next one
     * on next step, otherwise it sets {@code step=11}.
     */
    private void setCountersForNextSecuritiesType() {
        if (hasNextDummy()) {
            dummiesPointer++;
            step = 5;
        } else {
            step = 11;
        }
    }
}