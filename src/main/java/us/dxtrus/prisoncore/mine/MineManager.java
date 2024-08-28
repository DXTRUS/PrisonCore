package us.dxtrus.prisoncore.mine;

import us.dxtrus.prisoncore.config.Config;
import us.dxtrus.prisoncore.mine.models.Mine;
import us.dxtrus.prisoncore.mine.models.Server;
import us.dxtrus.prisoncore.mine.network.ServerManager;

import java.util.concurrent.CompletableFuture;

/**
 * Manages mines across the entire network and handles them.
 */
public class MineManager {
    private static MineManager instance;

    /**
     * Loads a mine and gives you the new update mine object.
     * Returns the current mine object if mine was already loaded.
     *
     * @param mine the mine to load
     * @return the new updated mine
     */
    public CompletableFuture<Mine> load(Mine mine) {
        return CompletableFuture.supplyAsync(() -> {
            if (mine.isLoaded()) return mine;

            if (Config.getInstance().getServers().isSingleInstance()) {
                return LocalMineManager.getInstance().load(mine);
            }

            Server server = ServerManager.getInstance().getRandomServer();



            mine.setServer(server);
            mine.setLoaded(true);
            return mine;
        });
    }

    public static MineManager getInstance() {
        if (instance == null) {
            instance = new MineManager();
        }
        return instance;
    }
}
