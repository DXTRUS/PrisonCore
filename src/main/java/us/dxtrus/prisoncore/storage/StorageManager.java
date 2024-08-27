package us.dxtrus.prisoncore.storage;

import us.dxtrus.commons.shaded.HikariConfig;
import us.dxtrus.commons.shaded.HikariDataSource;
import us.dxtrus.prisoncore.config.Config;

/**
 * Prevail is probably going to change this to DAOs. smh my head.
 */
public class StorageManager {
    private static StorageManager instance;

    HikariDataSource dataSource;
    HikariConfig config;

    public void connect() {
        Config.Sql sql = Config.getInstance().getSql();
        config = new HikariConfig();

        config.setMaximumPoolSize(250);
        config.setMinimumIdle(10);
        config.setJdbcUrl(String.format("jdbc:mariadb//%s:%d/%s", sql.getHost(), sql.getPort(), sql.getDatabase()));
        config.setUsername(sql.getUsername());
        config.setPassword(sql.getPassword());
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");


    }

    public static StorageManager getInstance() {
        return instance == null ? instance = new StorageManager() : instance;
    }
}
