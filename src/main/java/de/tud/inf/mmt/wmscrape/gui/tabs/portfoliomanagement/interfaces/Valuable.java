package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.interfaces;

import java.math.BigDecimal;

/**
 * Represents an entity (f.e. bank account) with a monetary value.
 */
public interface Valuable {

    BigDecimal getValue();
}
