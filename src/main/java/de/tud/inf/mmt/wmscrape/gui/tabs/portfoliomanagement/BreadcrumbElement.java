package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.BreadcrumbElementType;
import org.springframework.lang.NonNull;

public class BreadcrumbElement {

    public Object element;
    public final BreadcrumbElementType type;

    public BreadcrumbElement(@NonNull Object element, @NonNull BreadcrumbElementType type) {
        this.element = element;
        this.type = type;
    }

    @Override
    public String toString() {
        return element.toString();
    }
}

