package us.dxtrus.prisoncore.mine.models;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import us.dxtrus.prisoncore.PrisonCore;

import java.util.Objects;

@Getter
@AllArgsConstructor
public class Server {
    @Expose
    protected final String name;
    @Expose
    protected final ServerType type;
    @Expose
    protected int playerCount;
    @Expose
    protected double tps;
    @Expose
    protected double mspt;
    @Expose
    protected final long timeStarted;
    @Expose
    protected long lastHeartbeat;

    public void transferPlayer(Player player) {
        PrisonCore.getInstance().getMessenger().connect(player, name);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Server server = (Server) object;
        return name.equals(server.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, playerCount, tps, mspt);
    }

    @Override
    public String toString() {
        return "Server{name='%s', type='%s', playerCount=%d, tps=%f, mspt=%f, timeStarted=%d, lastHeartbeat=%d}"
                .formatted(name, type.getName(), playerCount, tps, mspt, timeStarted, lastHeartbeat);
    }
}
