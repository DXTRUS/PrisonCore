package us.dxtrus.prisoncore.mine.network;

import us.dxtrus.prisoncore.config.Config;
import us.dxtrus.prisoncore.mine.models.Server;

import java.util.ArrayList;
import java.util.List;

public class ServerManager {
    private static ServerManager instance;
    private final Heartbeat heartbeat;

    private final List<Server> servers = new ArrayList<>();

    public ServerManager() {
        this.heartbeat = new Heartbeat();
    }

    public Server getRandomServer() {
        return Config.getInstance().getServers().getDistributionRule().getServer();
    }

    public List<Server> getAllServers() {
        return servers;
    }

    public static ServerManager getInstance() {
        if (instance == null) {
            instance = new ServerManager();
        }
        return instance;
    }
}
