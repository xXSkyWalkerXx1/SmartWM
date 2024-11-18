package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.interfaces;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;

/**
 * Indicates a controller (or in detail his connected view) that he is used to load data even
 * after initialization via {@code @FXML private void initialize() {}}.
 *
 * ToDo: rename to 'Loadable' with method 'load()'
 * Indicates a tab-controller that his view uses dynamic data (f.e. {@link Owner}, so his view has to be updatable.
 * Calling {@code load()} should be done, if a click on this tab took place.
 */
public interface Openable {
    void open();
}
