package us.dxtrus.prisoncore.mine.network.loadbalancer;

import us.dxtrus.prisoncore.PrisonCore;
import us.dxtrus.prisoncore.mine.models.Server;
import us.dxtrus.prisoncore.mine.models.ServerType;
import us.dxtrus.prisoncore.mine.network.ServerManager;

import java.util.Comparator;
import java.util.List;

public enum Distributor {
    ROUND_ROBBIN {
        @Override
        public Server getServer() {
            List<Server> servers = ServerManager.getInstance().getAllServers();
            servers.removeIf(server -> server.getType() != ServerType.MINE);
            if (servers.isEmpty()) return null;
            if (DistributorConstants.currentServerIndex >= servers.size() - 1) {
                DistributorConstants.currentServerIndex = 0;
                return servers.get(DistributorConstants.currentServerIndex);
            }
            Server server = servers.get(DistributorConstants.currentServerIndex);
            DistributorConstants.currentServerIndex++;
            return server;
        }
    },
    LOWEST_PLAYER {
        @Override
        public Server getServer() {
            List<Server> servers = ServerManager.getInstance().getAllServers();
            servers.removeIf(server -> server.getType() != ServerType.MINE);
            servers.sort(Comparator.comparingInt(Server::getPlayerCount));
            return servers.getFirst();
        }
    },
    LOWEST_USAGE {
        @Override
        public Server getServer() {
            List<Server> servers = ServerManager.getInstance().getAllServers();
            servers.removeIf(server -> server.getType() != ServerType.MINE);
            servers.sort(Comparator.comparingDouble(Server::getMspt));
            return servers.getFirst();
        }
    },
    RANDOM {
        @Override
        public Server getServer() {
            List<Server> servers = ServerManager.getInstance().getAllServers();
            servers.removeIf(server -> server.getType() != ServerType.MINE);
            int index = PrisonCore.getInstance().getRandom().nextInt(servers.size() - 1);
            return servers.get(index);
        }
    };

    public abstract Server getServer();
}
