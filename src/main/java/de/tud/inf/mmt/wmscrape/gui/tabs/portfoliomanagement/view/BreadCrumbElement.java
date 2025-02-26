package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.BreadcrumbElementType;
import org.springframework.lang.NonNull;

public class BreadCrumbElement {

    public Object element;
    public final BreadcrumbElementType type;

    public BreadCrumbElement(@NonNull Object element, @NonNull BreadcrumbElementType type) {
        this.element = element;
        this.type = type;
    }

    @Override
    public String toString() {
        return element.toString();
    }
}

