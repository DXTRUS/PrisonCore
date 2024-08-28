package us.dxtrus.prisoncore.mine.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import us.dxtrus.prisoncore.PrisonCore;

import java.util.Objects;

@Getter
@AllArgsConstructor
public class Server {
    private final String name;
    private final int playerCount;
    private final double tps;
    private final double mspt;

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
}
