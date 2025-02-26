package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.BreadcrumbElementType;
import javafx.scene.Node;
import javafx.scene.control.ToolBar;
import javafx.scene.paint.Paint;
import org.springframework.lang.NonNull;

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
     * @param element the element to be displayed in the crumb, f.e. an account, depot, owner or portfolio
     * @param onClick the action to be executed when the crumb is clicked
     */
    public void addCrumb(@NonNull BreadCrumbElement element, @NonNull Runnable onClick) {
        if (!hasRootCrumble) throw new IllegalStateException("Root crumb not added yet");

        // add prefix to text
        String label = getLabelWithPrefix(element.type, element.toString());

        // create crumb
        org.controlsfx.control.BreadCrumbBar.BreadCrumbButton crumb = createDefaultCrumb(label, onClick);
        crumb.setUserData(element);

        // add new crumb
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

    @NonNull
    private String getLabelWithPrefix(@NonNull BreadcrumbElementType type, @NonNull String label) {
        return switch (type) {
            case PORTFOLIO -> "Portfolio: " + label;
            case ACCOUNT -> "Konto: " + label;
            case DEPOT -> "Depot: " + label;
            case OWNER -> "Inhaber: " + label;
        };
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
            BreadCrumbElement userData = (BreadCrumbElement) crumb.getUserData();
            if (userData == null) continue;
            if (lastType.equals(userData.type)) lastIndex = getItems().indexOf(crumb);
        }
        if (lastIndex != -1) getItems().remove(lastIndex + 1, getItems().size());
    }

    /**
     * Updates the label of the current crumb
     * @param crumbItem The item to be displayed in the current crumb
     */
    public <T> void updateCurrentCrumbLabel(@NonNull T crumbItem) {
        if (getItems().size() < 2) return; // that means there is no crumb or only a root-crumb
        org.controlsfx.control.BreadCrumbBar.BreadCrumbButton currentCrumb = (org.controlsfx.control.BreadCrumbBar.BreadCrumbButton) getItems().get(getItems().size() - 1);

        BreadCrumbElement userData = (BreadCrumbElement) currentCrumb.getUserData();
        if (userData == null) return;

        String labelWithPrefix = getLabelWithPrefix(userData.type, crumbItem.toString());
        currentCrumb.setText(trimText(labelWithPrefix));
    }

    /**
     * Updates the crumb with the given element.
     * @param crumb The crumb to be updated. It has to be a crumb that is contained in the bread crumb bar. See {@link #getItems()} for all crumb elements.
     * @param crumbElement The new element to be displayed in the crumb, f.e. an owner.
     */
    public void updateCrumb(@NonNull org.controlsfx.control.BreadCrumbBar.BreadCrumbButton crumb, @NonNull Object crumbElement) {
        BreadCrumbElement userData = (BreadCrumbElement) crumb.getUserData();
        if (userData == null) return;

        userData.element = crumbElement;
        String labelWithPrefix = getLabelWithPrefix(userData.type, crumbElement.toString());
        crumb.setText(trimText(labelWithPrefix));
        crumb.setUserData(userData);
    }

    /**
     * Trims the text to a maximum of 50 characters
     */
    @NonNull
    private String trimText(@NonNull String text) {
        return text.length() > 50 ? text.substring(0, 48) + "..." : text;
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
