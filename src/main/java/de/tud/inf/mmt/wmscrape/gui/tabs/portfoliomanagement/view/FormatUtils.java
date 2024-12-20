package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view;

import org.springframework.lang.Nullable;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.ParsePosition;
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

    /**
     * @param text any integer or decimal number like "123", "1.445" (means 1445) or "1.023,45" (means 1023,45). If null or empty, "0" is assumed.
     */
    public static float parseFloat(@Nullable String text) throws ParseException {
        ParsePosition parsePosition = new ParsePosition(0);
        if (text == null || text.isEmpty()) text = "0";
        float parsedValue = DECIMAL_FORMAT.parse(text, parsePosition).floatValue();

        if (parsePosition.getIndex() != text.length()) throw new ParseException("Invalid input", parsePosition.getIndex());
        return parsedValue;
    }
}

