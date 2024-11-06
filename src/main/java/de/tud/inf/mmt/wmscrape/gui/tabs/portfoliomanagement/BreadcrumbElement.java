package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BreadcrumbElement{
    public String element;
    public String type;

    public BreadcrumbElement(String element, String type) {
        this.element = element;
        this.type = type;
    }

    /**
     * @return The id as String, if found in {@link BreadcrumbElement#element} (if this element has a format like 'Inhaber / Max Mustermann (ID: id)').
     * @throws IllegalArgumentException if it could not find id.
     */
    public String getId() {
        Pattern regexIdPattern = Pattern.compile("\\\\(ID: (.*?)\\\\)");
        Matcher patternMatcher = regexIdPattern.matcher(element);

        if (patternMatcher.find()) return patternMatcher.group(1);
        throw new IllegalArgumentException("Id konnte nicht gefunden werden!");
    }
}

