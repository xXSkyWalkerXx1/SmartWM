package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.BreadcrumbElementType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToolBar;
import javafx.scene.paint.Paint;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.function.Consumer;

public class BreadCrumbBar extends ToolBar {

    private boolean hasRootCrumble = false;

    public BreadCrumbBar() {
        super();

        setMinHeight(25);
        setStyle("-fx-spacing: 0px;"); // reduces the spacing between the buttons
    }

    public void addRootCrumb(@NonNull String label, @NonNull Runnable onClick) {
        if (hasRootCrumble) throw new IllegalStateException("Root crumb already added");

        org.controlsfx.control.BreadCrumbBar.BreadCrumbButton crumb = createDefaultCrumb(label, onClick);
        crumb.setTextFill(Paint.valueOf("blue"));
        crumb.getStyleClass().add("first");

        getItems().add(crumb);
        hasRootCrumble = true;
    }

    public <T> void addCrumb(@NonNull String label, @NonNull BreadcrumbElementType type, @NonNull List<T> contextMenuItems,
                             @NonNull Consumer<T> contextMenuItemAction, @NonNull Runnable onClick) {
        if (!hasRootCrumble) throw new IllegalStateException("Root crumb not added yet");

        // add prefix to text
        switch (type) {
            case OWNER -> label = "Inhaber: " + label;
            case PORTFOLIO -> label = "Portfolio: " + label;
            case ACCOUNT -> label = "Konto: " + label;
            case DEPOT -> label = "Depot: " + label;
        }

        // create crumb
        org.controlsfx.control.BreadCrumbBar.BreadCrumbButton crumb = createDefaultCrumb(label, onClick);

        // create context-menu
        ContextMenu contextMenu = new ContextMenu();
        for (T contextMenuItem : contextMenuItems) {
            MenuItem menuItem = new MenuItem(contextMenuItem.toString());
            menuItem.setOnAction(event -> {
                contextMenuItemAction.accept(contextMenuItem);
                removeCrumbsAfter(crumb);
                getItems().remove(crumb);
                addCrumb(contextMenuItem.toString(), type, contextMenuItems, contextMenuItemAction, onClick);
            });
            contextMenu.getItems().add(menuItem);
        }

        crumb.setContextMenu(contextMenu);
        crumb.setUserData(type);
        getItems().add(crumb);
    }

    @NonNull
    private org.controlsfx.control.BreadCrumbBar.BreadCrumbButton createDefaultCrumb(@NonNull String label, @NonNull Runnable onClick) {
        org.controlsfx.control.BreadCrumbBar.BreadCrumbButton crumb = new org.controlsfx.control.BreadCrumbBar.BreadCrumbButton(trimText(label));
        crumb.setOnAction(event -> {
            onClick.run();
            removeCrumbsAfter(crumb);
        });
        return crumb;
    }

    public void clearBreadcrumbs() {
        getItems().clear();
        hasRootCrumble = false;
    }

    private void removeCrumbsAfter(@NonNull org.controlsfx.control.BreadCrumbBar.BreadCrumbButton crumb) {
        int index = getItems().indexOf(crumb);
        if (index != -1) getItems().remove(index + 1, getItems().size());
    }

    @NonNull
    private String trimText(@NonNull String text) {
        return text.length() > 50 ? text.substring(0, 50) + "..." : text;
    }

    public boolean hasRootCrumble() {
        return hasRootCrumble;
    }

    public boolean containsType(@NonNull BreadcrumbElementType type) {
        return getItems().stream()
                .map(org.controlsfx.control.BreadCrumbBar.BreadCrumbButton.class::cast)
                .anyMatch(crumb -> type.equals(crumb.getUserData()));
    }
}
