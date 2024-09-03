package us.dxtrus.prisoncore.mine.network;

import lombok.Getter;
import us.dxtrus.prisoncore.config.Config;
import us.dxtrus.prisoncore.mine.models.Server;

import java.util.ArrayList;
import java.util.List;

public class ServerManager {
    private static ServerManager instance;
    @Getter private final Server thisServer;

    private final List<Server> servers = new ArrayList<>();

    public ServerManager() {
        this.thisServer = new LocalServer();
    }

    public static ServerManager getInstance() {
        if (instance == null) {
            instance = new ServerManager();
        }
        return instance;
    }

    public Server getRandomServer() {
        return Config.getInstance().getServers().getDistributionRule().getServer();
    }

    public List<Server> getAllServers() {
        return servers;
    }

    public Server getServer(String name) {
        servers.removeIf(s -> s.getName().equalsIgnoreCase(name));
        return servers.getFirst();
    }
}
