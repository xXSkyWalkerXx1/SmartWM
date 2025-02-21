package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.interfaces;

import javax.persistence.PostLoad;

public interface Changable {

    boolean isChanged();

    void restore();

    void onPrePersistOrUpdateOrRemoveEntity();

    @PostLoad
    void onPostLoadEntity();
}
