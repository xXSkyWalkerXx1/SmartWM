package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.interfaces;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.kontos.AccountService;
import org.springframework.dao.DataAccessException;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Represents an entity (f.e. bank account) with a monetary value.
 */
public interface Valuable {

    /**
     * @param accountService the account service to use for converting in EUR.
     * @return the value of this entity in EUR.
     * @throws DataAccessException if the value could not be converted to EUR (f.e. no exchange-entry in table for the given currency).
     */
    BigDecimal getValue(@NonNull AccountService accountService) throws DataAccessException;

    Currency getValueCurrency();
}
