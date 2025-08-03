package us.dxtrus.prisoncore.storage;

import us.dxtrus.commons.database.DatabaseHandler;
import us.dxtrus.commons.database.DatabaseObject;
import us.dxtrus.prisoncore.storage.handlers.MySQLHandler;
import us.dxtrus.prisoncore.util.LogUtil;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class StorageManager {
    private static StorageManager instance;

    private final Map<DatabaseType, Class<? extends DatabaseHandler>> databaseHandlers = new HashMap<>();
    private final DatabaseHandler handler;

    private StorageManager() {
        LogUtil.info("Connecting to Database and populating caches...");
        databaseHandlers.put(DatabaseType.MYSQL, MySQLHandler.class);
        databaseHandlers.put(DatabaseType.MARIADB, MySQLHandler.class);

        this.handler = initHandler();
        LogUtil.debug("Connected to Database and populated caches!");
    }

    public static StorageManager getInstance() {
        if (instance == null) {
            instance = new StorageManager();
            instance.handler.connect();
        }
        return instance;
    }

    public <T extends DatabaseObject> CompletableFuture<List<T>> getAll(Class<T> clazz) {
        if (!isConnected()) {
            LogUtil.severe("Tried to perform database action when the database is not connected!");
            return CompletableFuture.completedFuture(List.of());
        }
        return CompletableFuture.supplyAsync(() -> handler.getAll(clazz));
    }

    public <T extends DatabaseObject> CompletableFuture<Optional<T>> get(Class<T> clazz, UUID id) {
        if (!isConnected()) {
            LogUtil.severe("Tried to perform database action when the database is not connected!");
            return CompletableFuture.completedFuture(Optional.empty());
        }
        return CompletableFuture.supplyAsync(() -> handler.get(clazz, id));
    }

    /**
     * Search the database for something matching name.
     *
     * @param clazz the class to search for.
     * @param name  the name, either a username or a servername.
     * @return a completable future of the optional object or an empty optional
     */
    public <T extends DatabaseObject> CompletableFuture<Optional<T>> search(Class<T> clazz, String name) {
        if (!isConnected()) {
            LogUtil.severe("Tried to perform database action when the database is not connected!");
            return CompletableFuture.completedFuture(Optional.empty());
        }
        return CompletableFuture.supplyAsync(() -> handler.search(clazz, name));
    }

    public <T extends DatabaseObject> CompletableFuture<Void> save(Class<T> clazz, T t) {
        if (!isConnected()) {
            LogUtil.severe("Tried to perform database action when the database is not connected!");
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.supplyAsync(() -> {
            handler.save(clazz, t);
            return null;
        });
    }

    public <T extends DatabaseObject> CompletableFuture<Void> delete(Class<T> clazz, T t) {
        if (!isConnected()) {
            LogUtil.severe("Tried to perform database action when the database is not connected!");
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.supplyAsync(() -> {
            handler.delete(clazz, t);
            return null;
        });
    }

    public <T extends DatabaseObject> CompletableFuture<Void> update(Class<T> clazz, T t, String... params) {
        if (!isConnected()) {
            LogUtil.severe("Tried to perform database action when the database is not connected!");
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.supplyAsync(() -> {
            handler.update(clazz, t, params);
            return null;
        });
    }

    public boolean isConnected() {
        return handler.isConnected();
    }

    public void shutdown() {
        handler.destroy();
    }

    private DatabaseHandler initHandler() {
        DatabaseType type = DatabaseType.MARIADB;
        LogUtil.debug("DB Type: %s".formatted(type.getFriendlyName()));
        try {
            Class<? extends DatabaseHandler> handlerClass = databaseHandlers.get(type);
            if (handlerClass == null) {
                throw new IllegalStateException("No handler for database type %s registered!".formatted(type.getFriendlyName()));
            }
            return handlerClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}