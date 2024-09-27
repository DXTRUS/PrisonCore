package us.dxtrus.prisoncore.mine.network;

import org.bukkit.Bukkit;
import us.dxtrus.prisoncore.config.ServerSettings;
import us.dxtrus.prisoncore.mine.models.Server;

public class LocalServer extends Server {
    public LocalServer() {
        super(ServerSettings.getInstance().getServerName(), ServerSettings.getInstance().getServerType(),
                0, 20, 0, System.currentTimeMillis(), System.currentTimeMillis());
        lastHeartbeat = System.currentTimeMillis();
    }

    @Override
    public int getPlayerCount() {
        return Bukkit.getOnlinePlayers().size();
    }

    @Override
    public double getTps() {
        return Math.min(20, Bukkit.getTPS()[0]);
    }

    @Override
    public double getMspt() {
        return Bukkit.getAverageTickTime();
    }

    public LocalServer withHeartbeatNow() {
        lastHeartbeat = System.currentTimeMillis();
        tps = getTps();
        mspt = getMspt();
        playerCount = getPlayerCount();
        return this;
    }
}
