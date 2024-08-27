package us.dxtrus.prisoncore.storage;

import lombok.Getter;
import us.dxtrus.commons.shaded.HikariConfig;
import us.dxtrus.commons.shaded.HikariDataSource;
import us.dxtrus.prisoncore.PrisonCore;
import us.dxtrus.prisoncore.config.Config;
import us.dxtrus.prisoncore.stats.Statistics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Prevail is probably going to change this to DAOs
 */
public class StorageManager {
    private static StorageManager instance;

    private boolean error = false;

    @Getter private HikariDataSource dataSource;
    @Getter private HikariConfig config;

    public void connect() {
        Config.Sql sql = Config.getInstance().getSql();
        config = new HikariConfig();

        config.setMaximumPoolSize(250);
        config.setMinimumIdle(10);
        config.setJdbcUrl(String.format("jdbc:mariadb://%s:%d/%s", sql.getHost(), sql.getPort(), sql.getDatabase()));
        config.setUsername(sql.getUsername());
        config.setPassword(sql.getPassword());
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        try {
            dataSource = new HikariDataSource(config);
        } catch (Exception ex) {
            PrisonCore.getInstance().getLogger().log(Level.SEVERE, "Database connection failed", ex);
            error = true;
        }
    }

    public boolean hasStatistics(UUID uuid) {
        if (error) {
            throw new UnsupportedOperationException("Database not connected.");
        }

        String sql = "SELECT * FROM statistics WHERE uuid = ?;";
        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid.toString());
            ResultSet results = statement.executeQuery();

            return results.next();
        } catch (SQLException ex) {
            PrisonCore.getInstance().getLogger().log(Level.SEVERE, "Failed to query statistics for " + uuid, ex);
            return false;
        }
    }

    public void saveStatistics(Statistics statistics) {
        if (error) {
            throw new UnsupportedOperationException("Database not connected.");
        }

        if (!hasStatistics(statistics.getUuid())) {
            createStatistics(statistics);
            return;
        }

        String sql = "";
        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement("")) {

        } catch (SQLException ex) {
            PrisonCore.getInstance().getLogger().log(Level.SEVERE, "Failed to save statistics for " + statistics.getUuid(), ex);
        }
    }

    public void createStatistics(Statistics statistics) {
        if (error) {
            throw new UnsupportedOperationException("Database not connected.");
        }

        String sql = "INSERT INTO statistics (uuid, tokens, gems, blocks_broken) VALUES (?, ?, ?, ?);";
        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, statistics.getUuid().toString());
            statement.setString(2, statistics.getTokens().toString());
            statement.setString(3, statistics.getGems().toString());
            statement.setString(4, statistics.getBlocksBroken().toString());

            statement.executeUpdate();
        } catch (SQLException ex) {
            PrisonCore.getInstance().getLogger().log(Level.SEVERE, "Failed to create statistics for " + statistics.getUuid(), ex);
        }
    }

    public static StorageManager getInstance() {
        return instance == null ? instance = new StorageManager() : instance;
    }
}
