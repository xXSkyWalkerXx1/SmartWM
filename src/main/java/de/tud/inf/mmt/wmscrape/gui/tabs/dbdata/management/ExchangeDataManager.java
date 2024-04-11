package de.tud.inf.mmt.wmscrape.gui.tabs.dbdata.management;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import de.tud.inf.mmt.wmscrape.dynamicdb.ColumnDatatype;
import de.tud.inf.mmt.wmscrape.dynamicdb.DbTableColumn;
import de.tud.inf.mmt.wmscrape.dynamicdb.DbTableColumnRepository;
import de.tud.inf.mmt.wmscrape.dynamicdb.exchange.ExchangeColumnRepository;
import de.tud.inf.mmt.wmscrape.dynamicdb.exchange.ExchangeTableManager;
import de.tud.inf.mmt.wmscrape.gui.tabs.dbdata.data.CustomCell;
import de.tud.inf.mmt.wmscrape.gui.tabs.dbdata.data.CustomRow;
import javafx.scene.control.TableView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExchangeDataManager extends DataManager {

    @Autowired
    ExchangeTableManager exchangeTableManager;
    @Autowired
    ExchangeColumnRepository exchangeColumnRepository;

    @Override
    protected PreparedStatement prepareUpdateStatements(String colName, Connection connection) throws SQLException{
        String sql = "INSERT INTO `"+ dbTableManger.getTableName()+"` (`"+colName+
                "`, datum) VALUES(?,?) ON DUPLICATE KEY UPDATE `"+colName+"`=VALUES(`"+colName+"`);";
        return connection.prepareStatement(sql);
    }

    @Override
    protected void fillDeleteAllStatement(CustomRow row, PreparedStatement statement) throws SQLException {
        fillDeleteSelectionStatement(row, statement);
    }


    @Override
    protected void fillDeleteSelectionStatement(CustomRow row, PreparedStatement statement) throws SQLException {
        var cells = row.getCells();
        if(cells == null || cells.get("datum") == null) return;

        String date = cells.get("datum").getDbData();
        fillByDataType(statement, date, ColumnDatatype.DATE, 1);
    }

    @Override
    protected PreparedStatement prepareDeleteSelectionStatement(Connection connection) throws SQLException {
        return connection.prepareStatement("DELETE FROM `"+ dbTableManger.getTableName()+"` WHERE datum=?");
    }

    @Override
    protected PreparedStatement prepareDeleteAllStatement(Connection connection) throws SQLException {
        return connection.prepareStatement("DELETE FROM `"+ dbTableManger.getTableName()+"`");
    }

    @Override
    protected Map<String, String> getKeyInformation(CustomRow row) {
        Map<String, String> keys = new HashMap<>();
        String date = row.getCells().getOrDefault("datum", new CustomCell(null, null)).getDbData();
        if(date == null) return null;
        keys.put("datum", date);
        return keys;
    }

    @Override
    protected void setStatementKeys(PreparedStatement stmt,
                                    Map<String, String> keys) throws SQLException{

        // 1 = data
        fillByDataType(stmt, keys.get("datum"), ColumnDatatype.DATE, 2);
    }

    @SuppressWarnings("unused")
    @Override
    protected void setColumnRepositoryAndManager() {
        dbTableColumnRepository = exchangeColumnRepository;
        dbTableManger = exchangeTableManager;
    }

    @Override
    public boolean addRowForSelection(Object selection) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO `"+ dbTableManger.getTableName()+
                    "` (datum) VALUES(?) ON DUPLICATE KEY UPDATE datum = datum");
            stmt.setDate(1, new Date(System.currentTimeMillis())); // today
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            AbandonedConnectionCleanupThread.checkedShutdown();
        }
        return true;
    }

    @Override
    protected <T extends DbTableColumn> List<? extends DbTableColumn> getTableColumns(DbTableColumnRepository<T, Integer> repository) {
        return exchangeColumnRepository.findAll();
    }

    @Override
    protected String getSelectionStatement(LocalDate startDate, LocalDate endDate) {
        return "SELECT * FROM "+ExchangeTableManager.TABLE_NAME + getStartAndEndDateQueryPart(startDate, endDate, "datum");
    }

    @Override
    protected String getSelectionStatementOnlyLatestRows() {
        return getSelectionStatement(null, null);
    }

    @Override
    protected void setDataTableInitialSort(TableView<CustomRow> dataTable) {
        // nothing to sort
    }
}
