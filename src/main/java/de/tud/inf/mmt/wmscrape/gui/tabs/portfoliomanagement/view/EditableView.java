package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view;

import de.tud.inf.mmt.wmscrape.gui.tabs.PrimaryTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.Navigator;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabController;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.PortfolioManagementTabManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Account;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Depot;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Portfolio;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.enums.BreadcrumbElementType;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.interfaces.Changable;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import org.springframework.lang.NonNull;

public abstract class EditableView {

    private Runnable onUnsavedChangesDialog;

    private final ChangeListener<Tab> tabPaneListener = (observableValue, tab, t1) -> {
        if (onUnsavedChangesDialog != null) onUnsavedChangesDialog.run();
    };
    private final EventHandler<MouseEvent> breadCrumbListener = mouseEvent -> {
        if (onUnsavedChangesDialog != null) onUnsavedChangesDialog.run();
    };

    public final void initialize(@NonNull Changable changableEntity,
                                 @NonNull PortfolioManagementTabManager portfolioManagementTabManager,
                                 @NonNull Runnable onButtonYesClickAction,
                                 @NonNull Runnable onButtonNoClickAction) {
        this.onUnsavedChangesDialog = () -> {
            var portfolioManagementController = portfolioManagementTabManager.getPortfolioController();
            // Problem bei dem 2x Dialog ist: vorm 2. Erscheinen wird zum Portfolio (open) navigiert, die Listeners gesetzt, aber dann folgt der 2. Dialog und die
            // Listeners werden wieder gelöscht
            clearListeners(portfolioManagementController);

            if (changableEntity.isChanged()) {
                PrimaryTabManager.showNotificationEntityChanged(
                        () -> {
                            // Navigate back to PortfolioManagement-Tab
                            portfolioManagementController.getPrimaryTabController()
                                    .getPrimaryTabPane()
                                    .getSelectionModel()
                                    .selectLast();

                            // If the user clicked on a root-crumb, we have to recreate it.
                            boolean hasNoRootCrumble = !portfolioManagementController.breadCrumbBar.hasRootCrumble();

                            // Navigate back to the entity
                            if (changableEntity instanceof Owner) {
                                Navigator.navigateToOwner(portfolioManagementTabManager, (Owner) changableEntity, hasNoRootCrumble);
                                portfolioManagementController.breadCrumbBar.removeCrumbsAfterLast(BreadcrumbElementType.OWNER);
                            } else if (changableEntity instanceof Portfolio) {
                                Navigator.navigateToPortfolio(portfolioManagementTabManager, (Portfolio) changableEntity, hasNoRootCrumble);
                                portfolioManagementController.breadCrumbBar.removeCrumbsAfterLast(BreadcrumbElementType.PORTFOLIO);
                            } else if (changableEntity instanceof Account) {
                                Navigator.navigateToAccount(portfolioManagementTabManager, (Account) changableEntity, hasNoRootCrumble);
                                portfolioManagementController.breadCrumbBar.removeCrumbsAfterLast(BreadcrumbElementType.ACCOUNT);
                            } else if (changableEntity instanceof Depot) {
                                Navigator.navigateToDepot(portfolioManagementTabManager, (Depot) changableEntity, hasNoRootCrumble);
                                portfolioManagementController.breadCrumbBar.removeCrumbsAfterLast(BreadcrumbElementType.DEPOT);
                            }
                        },
                        new Runnable() {
                            @Override
                            public void run() {
                                onButtonYesClickAction.run();

                                // Refresh the tab where we wanted to navigate to
                                var tabPaneSelectionModel = portfolioManagementController
                                        .getPortfolioManagementTabPane()
                                        .getSelectionModel();
                                var selectedIndex = tabPaneSelectionModel.getSelectedIndex();
                                tabPaneSelectionModel.clearSelection();
                                tabPaneSelectionModel.select(selectedIndex);
                            }
                        },
                        onButtonNoClickAction
                );
            }
        };
        setListeners(portfolioManagementTabManager.getPortfolioController());
    }

    /**
     * Show dialog if there are unsaved changes
     */
    public final void showUnsavedChangesDialog() {
        if (onUnsavedChangesDialog != null) onUnsavedChangesDialog.run();
    }

    /**
     * Set listeners for tab panes and bread crumb bar
     * @implNote If overriding this method, make sure to call {@code super.setListeners(portfolioManagementTabController)} first!
     */
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

    /**
     * Clear listeners for tab panes and bread crumb bar
     * @implNote If overriding this method, make sure to call {@code super.clearListeners(portfolioManagementTabController)} first!
     */
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
