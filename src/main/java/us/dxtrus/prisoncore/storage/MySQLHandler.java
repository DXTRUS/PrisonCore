package us.dxtrus.prisoncore.storage;


import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import us.dxtrus.commons.database.DatabaseObject;
import us.dxtrus.commons.database.dao.Dao;
import com.zaxxer.hikari.HikariDataSource;
import us.dxtrus.prisoncore.PrisonCore;
import us.dxtrus.prisoncore.config.Config;
import us.dxtrus.prisoncore.stats.Statistics;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class MySQLHandler {
    private final Map<Class<?>, Dao<?>> daos = new HashMap<>();

    private static MySQLHandler instance;
    @Getter private boolean connected = false;

    private final String driverClass;
    @Getter private HikariDataSource dataSource;

    public MySQLHandler() {
        this.driverClass = "org.mariadb.jdbc.Driver";
    }

    @SuppressWarnings("SameParameterValue")
    @NotNull
    private String[] getSchemaStatements(@NotNull String schemaFileName) throws IOException {
        return new String(Objects.requireNonNull(PrisonCore.getInstance().getResource(schemaFileName))
                .readAllBytes(), StandardCharsets.UTF_8).split(";");
    }

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void connect() {
        Config.Sql sql = Config.getInstance().getSql();

        dataSource = new HikariDataSource();
        dataSource.setDriverClassName(driverClass);
        dataSource.setJdbcUrl(String.format("jdbc:mariadb://%s:%d/%s", sql.getHost(), sql.getPort(), sql.getDatabase()));
        dataSource.setUsername(sql.getUsername());
        dataSource.setPassword(sql.getPassword());

        dataSource.setMaximumPoolSize(10);
        dataSource.setMinimumIdle(10);
        dataSource.setMaxLifetime(1800000);
        dataSource.setKeepaliveTime(0);
        dataSource.setConnectionTimeout(5000);
        dataSource.setPoolName("PrisonCoreHikariPool");

        final Properties properties = new Properties();
        properties.putAll(
                Map.of("cachePrepStmts", "true",
                        "prepStmtCacheSize", "250",
                        "prepStmtCacheSqlLimit", "2048",
                        "useServerPrepStmts", "true",
                        "useLocalSessionState", "true",
                        "useLocalTransactionState", "true"
                ));
        properties.putAll(
                Map.of(
                        "rewriteBatchedStatements", "true",
                        "cacheResultSetMetadata", "true",
                        "cacheServerConfiguration", "true",
                        "elideSetAutoCommits", "true",
                        "maintainTimeStats", "false")
        );
        dataSource.setDataSourceProperties(properties);

        try (Connection connection = dataSource.getConnection()) {
            final String[] databaseSchema = getSchemaStatements(String.format("database/%s_schema.sql", "mariadb"));
            try (Statement statement = connection.createStatement()) {
                for (String tableCreationStatement : databaseSchema) {
                    statement.execute(tableCreationStatement);
                }
                connected = true;
            } catch (SQLException e) {
                destroy();
                throw new IllegalStateException("Failed to create database tables. Please ensure you are running MySQL v8.0+ " +
                        "and that your connecting user account has privileges to create tables.", e);
            }
        } catch (SQLException | IOException e) {
            destroy();
            throw new IllegalStateException("Failed to establish a connection to the MySQL database. " +
                    "Please check the supplied database credentials in the config file", e);
        }
        registerDaos();
    }


    public void destroy() {
        if (dataSource != null) dataSource.close();
    }

    public void registerDaos() {
        daos.put(Statistics.class, new DaoStatictics(dataSource));
    }


    public <T> List<T> getAll(Class<T> clazz) {
        return (List<T>) getDao(clazz).getAll();
    }


    public <T> Optional<T> get(Class<T> clazz, UUID id) {
        return (Optional<T>) getDao(clazz).get(id);
    }


    public <T> void save(Class<T> clazz, T t) {
        getDao(clazz).save((DatabaseObject) t);
    }


    public <T> void update(Class<T> clazz, T t, String[] params) {
        getDao(clazz).update((DatabaseObject) t, params);
    }


    public <T> void delete(Class<T> clazz, T t) {
        getDao(clazz).delete((DatabaseObject) t);
    }

    public <T> void deleteSpecific(Class<T> clazz, T t, Object o) {
        getDao(clazz).deleteSpecific((DatabaseObject) t, o);
    }

    /**
     * Gets the DAO for a specific class.
     *
     * @param clazz The class to get the DAO for.
     * @param <T>   The type of the class.
     * @return The DAO for the specified class.
     */
    private <T extends DatabaseObject> Dao<T> getDao(Class<?> clazz) {
        if (!daos.containsKey(clazz))
            throw new IllegalArgumentException("No DAO registered for class " + clazz.getName());
        return (Dao<T>) daos.get(clazz);
    }

    public static MySQLHandler getInstance() {
        return instance == null ? instance = new MySQLHandler() : instance;
    }
}