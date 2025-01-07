package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.BreadcrumbElementType;
import javafx.scene.Node;
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

    /**
     * Adds a root crumb (looks different to the normal one) to the bread crumb bar
     * @param label the label of the crumb
     * @param onClick the action to be executed when the crumb is clicked
     */
    public void addRootCrumb(@NonNull String label, @NonNull Runnable onClick) {
        if (hasRootCrumble) throw new IllegalStateException("Root crumb already added");

        org.controlsfx.control.BreadCrumbBar.BreadCrumbButton crumb = createDefaultCrumb(label, onClick);
        crumb.setTextFill(Paint.valueOf("blue"));
        crumb.getStyleClass().add("first");

        getItems().add(crumb);
        hasRootCrumble = true;
    }

    /**
     * Adds a crumb to the bread crumb bar
     * @param label the label of the crumb
     * @param type the type of the crumb
     * @param contextMenuItems the items to be displayed in the context menu
     * @param contextMenuItemAction the action to be executed when a context menu item is clicked
     * @param onClick the action to be executed when the crumb is clicked
     * @param <T> the type of the context menu items
     */
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

    /**
     * Clears all breadcrumbs
     */
    public void clearBreadcrumbs() {
        getItems().clear();
        hasRootCrumble = false;
    }

    /**
     * Removes all breadcrumbs after the given crumb
     */
    private void removeCrumbsAfter(@NonNull org.controlsfx.control.BreadCrumbBar.BreadCrumbButton crumb) {
        int index = getItems().indexOf(crumb);
        if (index != -1) getItems().remove(index + 1, getItems().size());
    }

    /**
     * Removes all breadcrumbs after the last occurrence of the given type
     */
    public void removeCrumbsAfterLast(@NonNull BreadcrumbElementType lastType) {
        int lastIndex = -1;

        for (Node crumb : getItems()) {
            if (lastType.equals(crumb.getUserData())) lastIndex = getItems().indexOf(crumb);
        }

        if (lastIndex != -1) getItems().remove(lastIndex + 1, getItems().size());
    }

    /**
     * Trims the text to a maximum of 50 characters
     */
    @NonNull
    private String trimText(@NonNull String text) {
        return text.length() > 50 ? text.substring(0, 50) + "..." : text;
    }

    /**
     * @return true if the bread crumb bar has a root crumb
     */
    public boolean hasRootCrumble() {
        return hasRootCrumble;
    }

    public boolean containsType(@NonNull BreadcrumbElementType type) {
        return getItems().stream()
                .map(org.controlsfx.control.BreadCrumbBar.BreadCrumbButton.class::cast)
                .anyMatch(crumb -> type.equals(crumb.getUserData()));
    }
}
