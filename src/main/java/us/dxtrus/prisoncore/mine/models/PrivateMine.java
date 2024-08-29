package us.dxtrus.prisoncore.mine.models;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import us.dxtrus.commons.utils.StringUtils;
import us.dxtrus.commons.utils.TaskManager;
import us.dxtrus.prisoncore.config.Config;
import us.dxtrus.prisoncore.config.Lang;
import us.dxtrus.prisoncore.mine.LocalMineManager;
import us.dxtrus.prisoncore.mine.network.TransferManager;
import us.dxtrus.prisoncore.util.MessageUtils;

import java.util.UUID;

@Getter
@Setter
public class PrivateMine implements PrivateWorld {
    private boolean loaded;
    private Server server;
    private final UUID owner;
    private final String worldName;

    private final LocRef spawnLocation;
    private LocRef npcLocation;

    private final LocRef center;
    private int level;

    public PrivateMine(@NotNull UUID owner) {
        this.owner = owner;
        this.worldName = owner.toString();
        this.spawnLocation = new LocRef(40.5, 65, 0.5);
        this.center = new LocRef(0, 64, 0);
        this.level = 1;
    }

    public PrivateMine(@NotNull UUID owner, int level) {
        this.owner = owner;
        this.worldName = owner.toString();
        this.spawnLocation = new LocRef(50, 70, 0);
        this.center = new LocRef(0, 64, 0);
        this.level = level;
    }

    public Mine getLinkage() {
        Mine ret = LocalMineManager.getInstance().getBreakableMine(owner);
        if (ret == null) {
            ret = new Mine(this, center, Config.getInstance().getRanks().getMineSizes().getOrDefault(level, 16));
            LocalMineManager.getInstance().loadBreakableMine(ret, owner);
        }
        return ret;
    }

    public void connectLocal(Player player) {
        Location location = this.getSpawnLocation().toBukkit(Bukkit.getWorld(this.getWorldName()));
        location.setYaw(90);
        location.setPitch(0);
        player.teleportAsync(location).thenAccept(success -> {
            player.setAllowFlight(true);
            player.setFlying(true);
            MessageUtils.send(player, Lang.getInstance().getCommand().getMine().getTeleportComplete());
        });
    }

    public void connect(Player player) {
        TransferManager.getInstance().addPlayerForTransfer(player, server);
        getServer().transferPlayer(player);
    }
}
