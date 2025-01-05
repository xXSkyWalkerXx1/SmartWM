package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.FinancialAsset;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.BreadcrumbElementType;

public class BreadcrumbElement{
    public Object element;
    public BreadcrumbElementType type;

    public BreadcrumbElement(Object element, BreadcrumbElementType type) {
        this.element = element;
        this.type = type;
    }
}

