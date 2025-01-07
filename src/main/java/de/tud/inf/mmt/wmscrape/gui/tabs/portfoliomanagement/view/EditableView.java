package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view;

import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabController;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.interfaces.Changable;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public abstract class EditableView {

    private Runnable onUnsavedChangesDialog;
    private final ChangeListener<Tab> tabPaneListener = (observableValue, tab, t1) -> {
        if (onUnsavedChangesDialog != null) onUnsavedChangesDialog.run();
    };
    private final EventHandler<MouseEvent> breadCrumbListener = mouseEvent -> {
        if (onUnsavedChangesDialog != null) onUnsavedChangesDialog.run();
    };

    public final void initialize(@NonNull Changable changableEntity,
                                 @NonNull PortfolioManagementTabController portfolioManagementTabController,
                                 @NonNull Runnable onButtonCancelClickAction,
                                 @NonNull Runnable onButtonYesClickAction,
                                 @NonNull Runnable onButtonNoClickAction) {
        this.onUnsavedChangesDialog = () -> {
            clearListeners(portfolioManagementTabController);
            if (!changableEntity.isChanged()) return;
            PrimaryTabManager.showNotificationEntityChanged(
                    onButtonCancelClickAction,
                    onButtonYesClickAction,
                    onButtonNoClickAction
            );
        };
        setListeners(portfolioManagementTabController);
    }

    @Nullable
    public final Runnable getOnUnsavedChangesAction() {
        return onUnsavedChangesDialog;
    }

    public void setListeners(@NonNull PortfolioManagementTabController portfolioManagementTabController) {
        clearListeners(portfolioManagementTabController);

        portfolioManagementTabController
                .getPortfolioManagementTabPane()
                .getSelectionModel()
                .selectedItemProperty()
                .addListener(tabPaneListener);
        portfolioManagementTabController
                .breadCrumbBar
                .addEventHandler(MouseEvent.MOUSE_CLICKED, breadCrumbListener);
        portfolioManagementTabController.getPrimaryTabController()
                .getPrimaryTabPane()
                .getSelectionModel()
                .selectedItemProperty()
                .addListener(tabPaneListener);
    }

    public void clearListeners(@NonNull PortfolioManagementTabController portfolioManagementTabController) {
        portfolioManagementTabController
                .getPortfolioManagementTabPane()
                .getSelectionModel()
                .selectedItemProperty()
                .removeListener(tabPaneListener);
        portfolioManagementTabController
                .breadCrumbBar
                .removeEventHandler(MouseEvent.MOUSE_CLICKED, breadCrumbListener);
        portfolioManagementTabController.getPrimaryTabController()
                .getPrimaryTabPane()
                .getSelectionModel()
                .selectedItemProperty()
                .removeListener(tabPaneListener);
    }
}
