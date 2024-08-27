package us.dxtrus.prisoncore.mines.loader;

import com.infernalsuite.aswm.api.loaders.SlimeLoader;
import lombok.Getter;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.dxtrus.prisoncore.config.Config;
import us.dxtrus.prisoncore.persist.DatabaseType;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Getter
public class LoaderManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoaderManager.class);
    private static LoaderManager instance;
    private SlimeLoader loader;

    private LoaderManager() {
        Config.Storage storageConfig = Config.getInstance().getStorage();
        String host = storageConfig.getHost();
        int port = storageConfig.getPort();
        String username = storageConfig.getUsername();
        String password = storageConfig.getPassword();
        String database = storageConfig.getDatabase();
        boolean useSSL = storageConfig.isUseSSL();
        switch (storageConfig.getType()) {
            case MYSQL -> {
                try {
                    registerLoader(DatabaseType.MYSQL, new MySQLWorldLoader(host, port, database, useSSL, username, password));
                } catch (final SQLException ex) {
                    LOGGER.error("Failed to establish connection to the MySQL server:", ex);
                }
            }
            default -> throw new NotImplementedException("Unsupported database: " + database);
        }
    }

    public void registerLoader(DatabaseType dataSource, SlimeLoader loader) {
        if (loader instanceof UpdatableLoader) {
            try {
                ((UpdatableLoader) loader).update();
            } catch (final UpdatableLoader.NewerDatabaseException e) {
                LOGGER.error("Data source {} version is {}, while this SWM version only supports up to version {}.",
                        dataSource, e.getDatabaseVersion(), e.getCurrentVersion(), e);
                return;
            } catch (final IOException ex) {
                LOGGER.error("Failed to update data source {}", dataSource, ex);
                return;
            }
        }

        this.loader = loader;
    }

    public SlimeLoader getLoader() {
        return loader;
    }

    public static LoaderManager getInstance() {
        if (instance == null) {
            instance = new LoaderManager();
        }
        return instance;
    }
}