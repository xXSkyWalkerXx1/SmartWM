package de.tud.inf.mmt.wmscrape.gui.tabs.scraping.management.website;

import de.tud.inf.mmt.wmscrape.gui.tabs.historic.data.SecuritiesType;
import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.HistoricWebsiteIdentifiers;
import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.Website;
import de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.enums.IdentType;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Service;
import javafx.scene.control.TextArea;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.*;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.lang.NonNull;

import java.time.Duration;
import java.util.*;
import java.util.List;
import java.util.NoSuchElementException;

public abstract class WebsiteHandler extends Service<Void> {

    public static final int IFRAME_SEARCH_DEPTH = 3;

    protected final boolean headless;
    private final SimpleStringProperty logText;

    protected Website website;
    protected FirefoxDriver driver;
    private WebDriverWait wait;
    private long waitForWsElementSec = 3;
    protected boolean waitForWsElementSecActive = true;
    protected boolean waitForWsElementSecWasActive = true;

    private int uniqueElementId = 0;

    private final String identDelimiter = ";";

    protected WebsiteHandler(SimpleStringProperty logText, Boolean headless) {
        this.headless = headless;
        this.logText = logText;
    }

    /**
     * only used by the website tester where the website configuration is known
     *
     * @param website the configuration to test
     * @param logText the text property of the tester log area
     * @param headless in this case always false. starts the browser with a window open
     */
    public WebsiteHandler(Website website, SimpleStringProperty logText, Boolean headless) {
        this(logText, headless);
        this.website = website;
    }

    public boolean isHeadless() {
        return headless;
    }

    /**
     * converts double to long
     * @param waitForWsElementSec the waiting time website elements until declared as not found
     */
    public void setWaitForWsElementSec(double waitForWsElementSec) {
        this.waitForWsElementSec = (long) waitForWsElementSec;
    }

    /**
     * enables or disables waiting for website elements.
     * usefull if the existance of elements is implicitly given (like rows inside a table)
     *
     * @param doWait if true waiting is enabled
     */
    public void waitForWsElements(boolean doWait) {
        if(driver == null) return;
        waitForWsElementSecActive = doWait;


        Duration d;
        if(doWait) { d = Duration.ofMillis((waitForWsElementSec*1000));
        } else { d = Duration.ofMillis(0); }

        wait = new WebDriverWait(driver, d);
        driver.manage().timeouts().implicitlyWait(d);
    }


    /**
     * used to temporarily deactivate implicit waiting by the {@link WebDriver}.
     * useful for the recursive search where it is known that a wait time already was done before.
     *
     * @param disable if true try to disable the waiting
     */
    private void tempDisableWaiting(boolean disable) {
        if (disable && waitForWsElementSecActive) {
            // deactivate and was active before -> deactivate tmp
            waitForWsElementSecWasActive = true;
            waitForWsElements(false);
        } else if(disable) {
            // deactivate and wasn't active before -> nothing to do
            waitForWsElementSecWasActive = false;
        } else if(waitForWsElementSecWasActive) {
            // activate and was active -> activate again
            waitForWsElements(true);
        }  // activate and wasn't active -> do nothing

    }

    protected WebDriver getDriver() {
        return driver;
    }

    /**
     * creates a new {@link WebDriver} session
     *
     * @return true if successful
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    protected boolean startBrowser() {
        try {
            if(driver != null && browserIsOpen()) {
                addToLog("INFO:\tBrowser noch geöffnet. Setze fort.");
                return true;
            }

            FirefoxOptions options = new FirefoxOptions();
            options.setBinary(new FirefoxBinary());
            options.setLogLevel(FirefoxDriverLogLevel.FATAL);
            if (headless) options.addArguments("--headless");

            driver = new FirefoxDriver(options);
            waitForWsElements(true);

            return true;
        } catch (SessionNotCreatedException e) {
            addToLog("ERR:\t\t Selenium konnte nicht gestartet werden.\n\n"+e.getMessage()+"\n\n"+e.getCause()+"\n\n");
            return false;
        }
    }

    /**
     * gives information if a session is still active. there is no direct option
     *
     * @return true if a session exists
     */
    protected boolean browserIsOpen() {
        try{
            driver.getTitle();
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    protected boolean loadLoginPage() {
        return loadPage(website.getLoginUrl());
    }

    /**
     * tries to click a html dom element to hide a cookie banner
     *
     * @return true if successful
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    protected boolean acceptCookies() {
        IdentType type = website.getCookieAcceptIdentType();
        if (type == IdentType.DEAKTIVIERT) return true;

        var idents = website.getCookieAcceptIdent().split(identDelimiter);

        for(String ident : idents) {
            WebElement element = extractElementFromRoot(type, ident);

            if (element == null) return false;

            if (!clickElement(element, type, ident)) return false;
            waitLoadEvent();
        }

        addToLog("INFO:\tCookie-Einstellungen gespeichert");
        return true;
    }

    /**
     * sets the user information into html text fields
     *
     * @return true if successful
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    protected boolean fillLoginInformation() {

        WebElement username = extractElementFromRoot(website.getUsernameIdentType(), website.getUsernameIdent());
        if (username == null) return false;
        setText(username, website.getUsername());

        WebElement password = extractElementFromRoot(website.getPasswordIdentType(), website.getPasswordIdent());
        if (password == null) return false;
        setText(password, website.getPassword());

        addToLog("INFO:\tLogin Informationen ausgefüllt");
        return true;
    }

    /**
     * clicks the login button
     *
     * @return true if successful
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    protected boolean login() {
        if (website.getLoginButtonIdentType() == IdentType.ENTER) {
            WebElement password = extractElementFromRoot(website.getPasswordIdentType(), website.getPasswordIdent());
            if(password != null) {
                // submit like pressing enter
                submit(password);
                addToLog("INFO:\tLogin erfolgreich");
                return true;
            }
            return false;
        }

        WebElement loginButton = extractElementFromRoot(website.getLoginButtonIdentType(), website.getLoginButtonIdent());
        if (loginButton == null) return false;
        if (!clickElement(loginButton, website.getLoginButtonIdentType(), website.getLoginButtonIdent())) return false;
        addToLog("INFO:\tLogin erfolgreich");
        return true;
    }

    /**
     * clicks a logout button or loads a logout url
     *
     * @return true if successful
     */
    @SuppressWarnings("UnusedReturnValue")
    protected boolean logout() {
        if(website == null) return false;

        IdentType type = website.getLogoutIdentType();
        if (type == IdentType.DEAKTIVIERT) return true;

        if (type == IdentType.URL) {
            driver.get(website.getLogoutIdent());
            waitLoadEvent();
            addToLog("INFO:\tLogout erfolgreich");
            return true;
        }

        WebElement logoutButton = extractElementFromRoot(type, website.getLogoutIdent());
        if (logoutButton == null) {
            addToLog("ERR:\t\tLogout fehlgeschlagen");
            return false;
        }

        clickElement(logoutButton, type, website.getLogoutIdent());
        waitLoadEvent();

        addToLog("INFO:\tLogout erfolgreich");
        return true;
    }

    /**
     * waits for the dom ready state event
     * ToDo: improve performance - waiting until the state is complete can result in a long waiting time, even if the
     *      page is already loaded (but only some ressources like google-scripts are still loading)
     */
    protected void waitLoadEvent() {
        try {
            FluentWait<FirefoxDriver> wait = new FluentWait<>(driver)
                    .withTimeout(Duration.ofSeconds(10))
                    .pollingEvery(Duration.ofMillis(300))
                    .ignoring(ElementNotInteractableException.class)
                    .ignoring(NoSuchElementException.class);

            wait.until(webDriver
                    -> driver.executeScript("return document.readyState").equals("complete")
            );
        } catch (Exception e) {
            System.out.println(e.getMessage()+" <-> "+e.getCause());
        }
    }

    /**
     * tries to load an url and resets the identifiers
     *
     * @param url the website url
     * @return true if successful
     */
    protected boolean loadPage(String url) {
        // reset ids for a new page
        uniqueElementId = 0;

        try {
            driver.get(url);
        } catch (WebDriverException e) {
            System.out.println(e.getMessage()+" <-> "+e.getCause());
            return false;
        }

        addToLog("INFO:\t" + url + " geladen");
        return true;
    }

    public WebElement extractElementFromRoot(IdentType type, String identifier) {
        return extractElementFromRoot(type, identifier, true);
    }

    /**
     * tries to find an element straight from the website root
     *
     * @param type the {@link IdentType} used to find the element
     * @param identifier the identifier of the specified type
     * @return the {@link WebElement} if found otherwise null
     */
    public WebElement extractElementFromRoot(IdentType type, String identifier, boolean printErrorIfNotFound) {
        // solely the selenium element without iframe or relation context
        var elements = extractAllFramesFromContext(type, identifier, null, printErrorIfNotFound);
        if (elements != null && elements.size() > 0) return elements.get(0).get();
        return null;
    }

    /**
     * extracts and returns just the {@link WebElement} with a parent as a search starting point
     *
     * @param type the {@link IdentType} used to find the element
     * @param identifier the identifier of the specified type
     * @param context the reference point used for the search
     * @return the {@link WebElement} if found otherwise null
     */
    public WebElement extractElementFromContext(IdentType type, String identifier, WebElementInContext context) {
        var elements = extractAllFramesFromContext(type, identifier, context);
        if (elements != null) return elements.get(0).get();
        return null;
    }

    /**
     * extracts the {@link WebElement} and puts saves there context so that the element can be used as a reference point.
     * this allows composing multiple elements
     *
     * @param type the {@link IdentType} used to find the element
     * @param identifier the identifier of the specified type
     * @param context the reference point used for the search
     * @return the {@link WebElementInContext} if found otherwise null
     */
    public WebElementInContext extractFrameElementFromContext(IdentType type, String identifier, WebElementInContext context) {
        // a frame element containing the selenium element and iframe/context information
        var elements = extractAllFramesFromContext(type, identifier,  context);
        if (elements != null && elements.size() > 0) return elements.get(0);
        return null;
    }

    private void switchToFrame(WebElement frame) {
        if(frame != null) driver.switchTo().frame(frame);
        else driver.switchTo().defaultContent();
    }

    public List<WebElementInContext> extractAllFramesFromContext(IdentType type, String ident, WebElementInContext parent) {
        return extractAllFramesFromContext(type, ident, parent, true);
    }

    /**
     * extracts all elements matching for an identifier (useful for rows from a table).
     * used by all other search options.
     * it tries to find
     *
     * @param type the {@link IdentType} used to find the element
     * @param ident the identifier of the specified type
     * @param parent null uses the driver context otherwise it's the reference point where the search begins
     * @return all elements found for the identifier
     */
    public List<WebElementInContext> extractAllFramesFromContext(IdentType type, String ident, WebElementInContext parent, boolean printErrorIfNotFound) {

        int parentId = 0;
        WebElement frame = null;
        SearchContext searchContext = driver;
        String identifier = ident;

        // switches to the parents frame and changes the xpath if used
        if(parent != null) {
            frame = parent.getFrame();
            searchContext = parent.get();
            parentId = parent.getId();
            identifier = enhancedIdentifier(type, identifier, parentId);
        }

        switchToFrame(frame);

        try {
            // search elements in root
            var webElements = findElementsRelative(searchContext, type, identifier);
            if (webElements != null && webElements.size() > 0) return toContextList(webElements, frame, parentId);


            // disable implicit waiting while searching recursively if active
            tempDisableWaiting(true);

            // search in sub iframes recursively
            List<WebElementInContext> webElementInContexts = recursiveSearch(searchContext, type, identifier,
                    searchContext.findElements(By.tagName("iframe")), 0, parentId);


            if (webElementInContexts != null) return webElementInContexts;

            if(printErrorIfNotFound) {
                addToLog("ERR:\t\tKeine Elemente unter '" + ident + "' gefunden. Suche mit: " + type);
            }
        } catch (InvalidSelectorException e) {
            e.printStackTrace();
            addToLog("ERR:\t\tInvalider Identifizierer vom Typ "+type+": '"+ident+"'");
        }

        tempDisableWaiting(false);
        return null;
    }

    /**
     * embeds the {@link WebElement}s inside their context
     *
     * @param webElements all elements to embed
     * @param frame the iframe the elements were found or null if it's the normal website frame
     * @param parentId the id of the parent used as a reference or 0
     * @return the embedded {@link WebElement}s
     */
    private List<WebElementInContext> toContextList(List<WebElement> webElements, WebElement frame, int parentId) {
        List<WebElementInContext> webElementInContexts = new ArrayList<>();

        for (var element : webElements) {
            uniqueElementId++;
            webElementInContexts.add(new WebElementInContext(element, frame, uniqueElementId, parentId));
            setUniqueId(element, uniqueElementId); // sets the id inside the html code

        }
        return webElementInContexts;
    }

    /**
     * searches elements given a {@link WebElement} as reference point.
     * note: has no effect with xpath due to the definition of the xpath standard a single xpath is relative to
     * the website root, the {@link WebElement} does not change that.
     *
     * @param context some {@link WebElement} as reference point
     * @param type the {@link IdentType} used to find the element
     * @param identifier the identifier of the specified type
     * @return all found {@link WebElement}
     */
    private List<WebElement> findElementsRelative(SearchContext context, IdentType type, String identifier) throws InvalidSelectorException {
        List<WebElement> elements;
        switch (type) {
            case ID -> elements = context.findElements(By.id(identifier));
            case XPATH -> elements = context.findElements(By.xpath(identifier));
            case CSS -> elements = context.findElements(By.cssSelector(identifier));
            case TAG -> elements = context.findElements(By.tagName(identifier));
            default -> elements = null;
        }
        return elements;
    }

    /**
     * uses javascript to add a site wide unique id attribute to a dom elemente (which the {@link WebElement} is the
     * representation for
     * @param element the element the id is set
     * @param id the id to set
     */
    private void setUniqueId(WebElement element, int id) {
        if(element == null) return;
        try {
            driver.executeScript("arguments[0].setAttribute('wms', "+id+")", element);
        } catch (Exception e) {
            System.out.println(e.getMessage()+" "+ e.getCause());
        }

    }

    /**
     * creates an absolute xpath based on the id priorly set
     *
     * @param type the {@link IdentType} used to find the element
     * @param identifier the identifier of the specified type
     * @param parenId the id of the parent
     * @return an "absolute" xpath using the parent as a reference point
     */
    private String enhancedIdentifier(IdentType type, String identifier, int parenId) {
        if(type == IdentType.XPATH) {
            checkRelativeXPath(identifier);
            return "//*[@wms=" + parenId + "]" + identifier;
        }
        return identifier;
    }

    private void checkRelativeXPath(String identifier) {
        if(!identifier.startsWith("/")) {
            addToLog("WARN:\tPotentiell fehlgeformter XPath '"+identifier+"'. Ein Unterpfad sollt mit / oder // beginnen.");
        }
    }

    /**
     * searches for elements inside multiple html iframes
     *
     * todo: using the parent id attribute makes no sense when switching frames as the element with the id does
     *       not exist inside the other frame
     *
     * @param context some {@link WebElement} as reference point
     * @param type the {@link IdentType} used to find the element
     * @param identifier the identifier of the specified type
     * @param iframes all iframes inside the current frame
     * @param depth the current search depth
     * @param parentId the parent id
     * @return all found elements
     */
    private List<WebElementInContext> recursiveSearch(SearchContext context, IdentType type, String identifier,
                                                      List<WebElement> iframes, int depth, int parentId) throws InvalidSelectorException {
        List<WebElementInContext> webElementInContexts;

        // nothing found in max depth. return
        if (depth >= IFRAME_SEARCH_DEPTH) return null;

        // search every iframe
        for (WebElement frame : iframes) {
            // switch to the frame
            try {
                driver.switchTo().frame(frame);

                /*
                Zum Beispiel unter WallStreetOnline ist es so, dass durch Skripte nach dem Laden (also readyState == complete)
                auch iFrames wieder gelöscht werden, wodurch der Stale-Fehler zustande kommen kann.

                driver.executeScript("arguments[0].contentWindow.focus();", frame);
                 */
            } catch (StaleElementReferenceException e) {
                addToLog("WARN:\tEin iFrame konnte nicht geöffnet werden. Dadurch können Elemente in diesem Frame nicht gefunden werden.");
                continue;
            }

            // look inside the frame
            var webElements = findElementsRelative(context, type, identifier);

            // found something
            if(webElements != null && webElements.size() > 0) {
                return toContextList(webElements, frame, parentId);
            }

            // found nothing, search again in the sub-frames inside this frame
            webElementInContexts = recursiveSearch(context, type, identifier, driver.findElements(By.tagName("iframe")), depth + 1, parentId);
            if (webElementInContexts != null) return webElementInContexts;

            driver.switchTo().parentFrame();
        }
        return null;
    }

    // only call in respective frame
    private void setText(WebElement element, String text) {
        if(element == null) return;
        element.sendKeys(text);
    }

    // only call in respective frame
    private void submit(WebElement element) {
        if(element == null) return;
        element.submit();
    }

    // only call in respective frame
    /**
     * clicks an element if it can. if the button is behind some other html element a button can't be clicked normally
     * but javascript can to it.
     *
     * @param element the element/button to click
     */

    // only call in respective frame
    private boolean clickElement(WebElement element, IdentType elIdentType, String elIdent) {
        if (element == null) return false;
        int tryCount = 0;

        waitLoadEvent();
        scrollIntoView(element);

        while (tryCount < 3) {
            try {
                element.click();
                return true;
            } catch (Exception e) {
                try {
                    driver.executeScript("arguments[0].click();", element);
                    return true;
                } catch (Exception e2) {
                    element = extractElementFromRoot(elIdentType, elIdent, false);
                }
            }
            tryCount++;
        }

        if (tryCount == 3) {
            addToLog("WARN:\tElement (" + element + ") konnte nicht geklickt werden.");
            return false;
        }
        return true;
    }

    private void scrollIntoView(WebElement element) {
        int attempts = 0;
        int maxAttempts = 5;

        while (attempts < maxAttempts) {
            try {
                boolean isVisible = (Boolean) ((JavascriptExecutor) driver).executeScript(
                        "var elem = arguments[0],                 " +
                                "  box = elem.getBoundingClientRect(),    " +
                                "  cx = box.left + box.width / 2,         " +
                                "  cy = box.top + box.height / 2,         " +
                                "  e = document.elementFromPoint(cx, cy); " +
                                "for (; e; e = e.parentElement) {         " +
                                "  if (e === elem)                        " +
                                "    return true;                         " +
                                "}                                        " +
                                "return false;                            "
                        , element);

                if (!isVisible) {
                    driver.executeScript("arguments[0].scrollIntoView({behavior: 'instant', block: 'center', inline: 'center'});", element);
                }
                break;
            } catch (Exception e) {
                addToLog("WARN:\tFehler beim Scrollen. Versuch, um 500 Pixel zu scrollen.");
                ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 500);");
                attempts++;
            }
        }
        if (attempts == maxAttempts) {
            addToLog("ERROR:\tMaximale Anzahl von Versuchen erreicht. Element konnte nicht gescrollt werden.");
        }
    }

    protected void quit() {
        try {
            if (driver != null) {
                driver.quit();
                driver = null;
                addToLog("INFO:\tBrowser wurde beendet");
            }
        } catch (Exception e) {
            addToLog("ERR:\t\tFehler beim Schließen des Browsers.\n\n"+e.getMessage()+"\n\n"+e.getCause()+"\n\n");
        }
    }

    protected void addToLog(@NonNull String message) {
        // not doing this would we be a problem due to the multithreaded execution
        Platform.runLater(() -> ((TextArea) logText.getBean()).appendText("\n" + message));
    }

    protected boolean loadSearchPage() {
        addToLog("INFO:\tZu Such-Unterseite navigiert");
        return loadPage(website.getSearchUrl());
    }

    protected boolean searchForStock(@NonNull String isin, @NonNull SecuritiesType securitiesType) {
        WebElement searchInput = extractElementFromRoot(website.getSearchFieldIdentType(), website.getSearchFieldIdent());
        if (searchInput == null) return false;

        if (!clickElement(searchInput, website.getSearchFieldIdentType(), website.getSearchFieldIdent())) return false;
        setText(searchInput, isin);
        waitLoadEvent();

        if(website.getSearchButtonIdentType() == IdentType.ENTER) {
            submit(searchInput);
        } else {
            WebElement searchButton = extractElementFromRoot(website.getSearchButtonIdentType(), website.getSearchButtonIdent());
            if (searchButton == null) return false;
            if (!clickElement(searchButton, website.getSearchButtonIdentType(), website.getSearchButtonIdent())) return false;
        }

        addToLog(String.format("INFO:\tZu Wertpapier-Unterseite von %s (%s) navigiert", isin, securitiesType.getDisplayText()));
        return true;
    }

    protected boolean loadHistoricPage(@NonNull HistoricWebsiteIdentifiers identifiers) {
        WebElement element;

        for (String ident : identifiers.getHistoricLinkIdent().split(identDelimiter)) {
             element = extractElementFromRoot(identifiers.getHistoricLinkIdentType(), ident);
            if (element == null) return false;
            if (!clickElement(element, identifiers.getHistoricLinkIdentType(), ident)) return false;
            waitLoadEvent();
        }

        addToLog("INFO:\tZu historischen Daten navigiert");
        return true;
    }

    protected boolean setDate(@NonNull HistoricWebsiteIdentifiers identifiers) {
        WebElement dateFromMonthElement = null;
        WebElement dateFromYearElement = null;
        WebElement dateUntilMonthElement = null;
        WebElement dateUntilYearElement = null;

        var dateFromDayElement = extractElementFromRoot(
                identifiers.getDateFromDayIdentType(),
                identifiers.getDateFromDayIdent()
        );
        var dateUntilDayElement = extractElementFromRoot(
                identifiers.getDateUntilDayIdentType(),
                identifiers.getDateUntilDayIdent()
        );

        if(identifiers.getDateFromMonthIdent() != null && !identifiers.getDateFromMonthIdent().equals("")) {
            dateFromMonthElement = extractElementFromRoot(
                    identifiers.getDateFromMonthIdentType(),
                    identifiers.getDateFromMonthIdent()
            );
            dateFromYearElement = extractElementFromRoot(
                    identifiers.getDateFromYearIdentType(),
                    identifiers.getDateFromYearIdent()
            );
            dateUntilMonthElement = extractElementFromRoot(
                    identifiers.getDateUntilMonthIdentType(),
                    identifiers.getDateUntilMonthIdent()
            );
            dateUntilYearElement = extractElementFromRoot(
                    identifiers.getDateUntilYearIdentType(),
                    identifiers.getDateUntilYearIdent()
            );
        }

        if(dateFromDayElement != null
                && dateFromMonthElement != null
                && dateFromYearElement != null
                && dateUntilDayElement != null
                && dateUntilMonthElement != null
                && dateUntilYearElement != null) {

            if (website.getDateFrom() != null && !website.getDateFrom().equals("")) {
                var dateFrom = website.getDateFrom().split("#");

                if (dateFrom.length != 3) return false;

                new Select(dateFromDayElement).selectByVisibleText(dateFrom[0]);
                new Select(dateFromMonthElement).selectByVisibleText(dateFrom[1]);
                new Select(dateFromYearElement).selectByVisibleText(dateFrom[2]);
            } else {
                new Select(dateFromDayElement).selectByIndex(0);
                new Select(dateFromMonthElement).selectByIndex(0);
                new Select(dateFromYearElement).selectByIndex(0);
            }

            var untilDaySelect = new Select(dateUntilDayElement);
            var untilMonthSelect = new Select(dateUntilMonthElement);
            var untilYearSelect = new Select(dateUntilYearElement);

            if (website.getDateUntil() != null && !website.getDateUntil().equals("")) {
                var dateUntil = website.getDateUntil().split("#");

                if (dateUntil.length != 3) return false;

                untilDaySelect.selectByVisibleText(dateUntil[0]);
                untilMonthSelect.selectByVisibleText(dateUntil[1]);
                untilYearSelect.selectByVisibleText(dateUntil[2]);
            } else {
                untilDaySelect.selectByIndex(untilDaySelect.getOptions().size() - 1);
                untilMonthSelect.selectByIndex(untilMonthSelect.getOptions().size() - 1);
                untilYearSelect.selectByIndex(untilYearSelect.getOptions().size() - 1);
            }

            addToLog("INFO:\tDatum gesetzt");
            return true;
        } else if(dateFromDayElement != null
                && dateFromMonthElement == null
                && dateFromYearElement == null
                && dateUntilDayElement != null
                && dateUntilMonthElement == null
                && dateUntilYearElement == null) {

            var dateFrom = website.getDateFrom();
            var dateUntil = website.getDateUntil();

            // set date for start on browser
            scrollIntoView(dateFromDayElement);
            dateFromDayElement.clear();

            if (!clickElement(
                    dateFromDayElement,
                    identifiers.getDateFromDayIdentType(),
                    identifiers.getDateFromDayIdent())
            ) return false;

            setText(dateFromDayElement, dateFrom);
            driver.executeScript("arguments[0].blur()", dateFromDayElement);

            // set date for end on browser; don't set if there is nothing set on the config to use the current date
            scrollIntoView(dateUntilDayElement);

            if (!clickElement(
                    dateUntilDayElement,
                    identifiers.getDateUntilDayIdentType(),
                    identifiers.getDateUntilDayIdent())
            ) return false;
            if (dateUntil != null && !dateUntil.isBlank()) {
                dateUntilDayElement.clear();
                setText(dateUntilDayElement, dateUntil);
            }
            driver.executeScript("arguments[0].blur()", dateUntilDayElement);

            addToLog("INFO:\tDatum gesetzt");
            return true;
        } else {
            addToLog("INFO:\tDatum setzen fehlgeschlagen");
            return false;
        }
    }

    protected boolean declineNotifications() {
        if(website.getNotificationDeclineIdent() == null) return true;

        WebElement element = extractElementFromRoot(website.getDeclineNotificationIdentType(), website.getNotificationDeclineIdent(), false);
        if (element == null) return true;
        if (!clickElement(element, website.getDeclineNotificationIdentType(), website.getNotificationDeclineIdent())) return false;

        addToLog("INFO:\tBenachrichtigungen abgelehnt");
        return true;
    }

    protected boolean loadHistoricData(@NonNull HistoricWebsiteIdentifiers identifiers) {
        WebElement element = extractElementFromRoot(identifiers.getLoadButtonIdentType(), identifiers.getLoadButtonIdent());
        if (element == null) return false;
        if (!clickElement(element, identifiers.getLoadButtonIdentType(), identifiers.getLoadButtonIdent())) return false;

        addToLog("INFO:\tHistorische Daten geladen");
        return true;
    }

    protected Integer readPageCount(@NonNull HistoricWebsiteIdentifiers identifiers) {
        IdentType type = identifiers.getPageCountIdentType();
        if (type == IdentType.DEAKTIVIERT) return 1;

        WebElement element = extractElementFromRoot(type, identifiers.getPageCountIdent());
        if (element == null) {
            addToLog("INFO:\tSeitenzahl konnte nicht gelesen werden");
            return 1;
        }

        try {
            return Integer.parseInt(element.getText());
        } catch (NumberFormatException nfe) {
            return 1;
        }
    }

    protected boolean readPageCountTest(@NonNull HistoricWebsiteIdentifiers identifiers) {
        if (identifiers.getPageCountIdentType() == IdentType.DEAKTIVIERT) return true;

        WebElement element = extractElementFromRoot(
                identifiers.getPageCountIdentType(),
                identifiers.getPageCountIdent()
        );
        if (element == null) return false;

        addToLog("INFO:\tPage Count: " + element.getText());
        return true;
    }

    protected boolean nextTablePage(@NonNull HistoricWebsiteIdentifiers identifiers) {
        if (identifiers.getNextPageButtonIdentType() == IdentType.DEAKTIVIERT) return true;

        WebElement element = extractElementFromRoot(identifiers.getNextPageButtonIdentType(), identifiers.getNextPageButtonIdent());
        if (element == null) return false;
        if (!clickElement(element, identifiers.getNextPageButtonIdentType(), identifiers.getNextPageButtonIdent())) return false;

        addToLog("INFO:\tNächste Tabellenseite geladen");
        return true;
    }
}
