package us.dxtrus.prisoncore.mines.network.broker;

import info.preva1l.fadsb.Fadsb;
import org.jetbrains.annotations.Nullable;

public class ServerManager {

    public @Nullable String serverName() {
        return Fadsb.getInstance().getServerSettings() != null ? Fadsb.getInstance().getServerSettings().getServerName() : "server";
    }
}
