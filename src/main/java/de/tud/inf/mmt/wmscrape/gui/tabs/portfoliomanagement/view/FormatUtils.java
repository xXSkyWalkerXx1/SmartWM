package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;

public class FormatUtils {

    private static final DecimalFormat DECIMAL_FORMAT;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        DECIMAL_FORMAT = new DecimalFormat("#,##0.00", symbols);
    }

    public static String formatFloat(float value) {
        return DECIMAL_FORMAT.format(value);
    }

    public static float parseFloat(String text) throws ParseException {
        return DECIMAL_FORMAT.parse(text).floatValue();
    }
}

