package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.interfaces;

/**
 * Indicates a controller (or in detail his connected view) that he is used to loading a specific entity.
 * F.e.: {@link de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners.owner.OwnerOverviewController} loads a
 * specific {@link de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner} entity.
 */
public interface Openable {
    void open();
}
