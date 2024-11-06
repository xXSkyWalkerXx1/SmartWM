package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.view;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Account;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Depot;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Portfolio;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Region;
import org.springframework.lang.NonNull;

import java.util.List;


public class PortfolioTreeView extends TreeView<String> {

    private final TreeItem<String> rootTreeItem = new TreeItem<>("Portfolio/s");

    /**
     * @param parent JavaFX node-based UI Controls and all layout containers (f.e. Pane). Only used for view-sizing.
     */
    public PortfolioTreeView(@NonNull Region parent, @NonNull List<Portfolio> portfolios){
        setRoot(rootTreeItem);
        setShowRoot(true); // false

        setItems(portfolios);

        prefWidthProperty().bind(parent.widthProperty());
        prefHeightProperty().bind(parent.heightProperty());
    }

    // ToDo: implement onClick on portfolio, konto, depot
    private void setItems(@NonNull List<Portfolio> portfolios){
        rootTreeItem.getChildren().clear();

        for (Portfolio portfolio : portfolios){
            TreeItem<String> portfolioTreeItem = new TreeItem<>(createItemDisplayText(
                    portfolio.getName(),
                    portfolio.getValue().doubleValue()
            ));

            // add accounts first
            for (Account account : portfolio.getAccounts()){
                TreeItem<String> accountTreeItem = new TreeItem<>(createItemDisplayText(
                   account.getDescription(),
                   account.getValue().doubleValue()
                ));
                portfolioTreeItem.getChildren().add(accountTreeItem);
            }

            // then add depots
            for (Depot depot : portfolio.getDepots()){
                TreeItem<String> depotTreeItem = new TreeItem<>(createItemDisplayText(
                        depot.getName(),
                        depot.getValue().doubleValue()
                ));
                portfolioTreeItem.getChildren().add(depotTreeItem);
            }

            // finally, add it to root-item
            rootTreeItem.getChildren().add(portfolioTreeItem);
        }

    }

    // ToDo: add javadoc, implement converting any currency to €
    private String createItemDisplayText(String objName, double balance){
        return String.format("%s\t(%s€)", objName, balance);
    }
}
