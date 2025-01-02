package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.FinancialAsset;

public class BreadcrumbElement{
    public Object element;
    public String type;

    public BreadcrumbElement(Object element, String type) {
        this.element = element;
        this.type = type;
    }
}

