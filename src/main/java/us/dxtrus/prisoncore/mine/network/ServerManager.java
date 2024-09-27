package us.dxtrus.prisoncore.mine.network;

import lombok.Getter;
import us.dxtrus.commons.utils.TaskManager;
import us.dxtrus.prisoncore.PrisonCore;
import us.dxtrus.prisoncore.config.Config;
import us.dxtrus.prisoncore.mine.models.Server;
import us.dxtrus.prisoncore.mine.network.broker.Message;
import us.dxtrus.prisoncore.mine.network.broker.Payload;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerManager {
    private static ServerManager instance;
    @Getter private final LocalServer thisServer;

    private final Map<String, Server> servers = new ConcurrentHashMap<>();

    public ServerManager() {
        this.thisServer = new LocalServer();
        TaskManager.runAsyncRepeat(PrisonCore.getInstance(), this::heartbeat, 20L);
    }

    public static ServerManager getInstance() {
        if (instance == null) {
            instance = new ServerManager();
        }
        return instance;
    }

    public void heartbeat() {
        Message.builder()
                .type(Message.Type.HEARTBEAT)
                .payload(Payload.withServer(thisServer.withHeartbeatNow()))
                .build().send(PrisonCore.getInstance().getBroker());
    }

    public Server getRandomServer() {
        return Config.getInstance().getServers().getDistributionRule().getServer();
    }

    public List<Server> getAllServers() {
        return servers.values().stream().toList();
    }

    public Server getServer(String name) {
        return servers.get(name);
    }

    public void update(Server server) {
        servers.put(server.getName(), server);
    }
}
