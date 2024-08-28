package us.dxtrus.prisoncore.mine.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import us.dxtrus.prisoncore.PrisonCore;

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
}
