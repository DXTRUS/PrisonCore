package us.dxtrus.prisoncore.mines;

import us.dxtrus.prisoncore.mines.models.Mine;
import us.dxtrus.prisoncore.mines.models.Server;
import us.dxtrus.prisoncore.mines.network.ServerManager;

/**
 * Manages servers across the entire network and manages them.
 */
public class MineManager {
    private static MineManager instance;

    /**
     * Loads a mine and gives you the new update mine object.
     * @param load the mine to load
     * @return the new update mine
     */
    public Mine load(Mine load) {
        Server server = ServerManager.getInstance().getRandomServer();

    }

    public static MineManager getInstance() {
        if (instance == null) {
            instance = new MineManager();
        }
        return instance;
    }
}
