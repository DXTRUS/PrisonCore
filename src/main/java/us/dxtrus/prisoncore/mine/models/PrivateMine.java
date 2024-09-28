package us.dxtrus.prisoncore.mine.models;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import us.dxtrus.prisoncore.PrisonCore;
import us.dxtrus.prisoncore.config.Config;
import us.dxtrus.prisoncore.config.Lang;
import us.dxtrus.prisoncore.mine.LocalMineManager;
import us.dxtrus.prisoncore.mine.MineManager;
import us.dxtrus.prisoncore.mine.network.TransferManager;
import us.dxtrus.prisoncore.mine.network.broker.Message;
import us.dxtrus.prisoncore.mine.network.broker.Payload;
import us.dxtrus.prisoncore.storage.StorageManager;
import us.dxtrus.prisoncore.util.MessageUtils;

import java.util.UUID;

@Getter
public class PrivateMine implements PrivateWorld {
    private final UUID owner;
    private final String worldName;
    private final LocRef spawnLocation;
    private final LocRef center;
    private boolean loaded;
    private Server server;
    private LocRef npcLocation;
    private int level;

    public PrivateMine(@NotNull UUID owner) {
        this.owner = owner;
        this.worldName = owner.toString();
        this.spawnLocation = new LocRef(40.5, 65, 0.5);
        this.center = new LocRef(0, 64, 0);
        this.level = 1;
    }

    public PrivateMine(@NotNull UUID owner, LocRef spawnLocation, LocRef center, LocRef npcLocation, int level, boolean loaded) {
        this.owner = owner;
        this.worldName = owner.toString();
        this.spawnLocation = spawnLocation;
        this.center = center;
        this.npcLocation = npcLocation;
        this.level = level;
        this.loaded = loaded;
    }

    public Mine getLinkage() {
        Mine ret = LocalMineManager.getInstance().getBreakableMine(owner);
        if (ret == null) {
            ret = new Mine(this, center, Config.getInstance().getRanks().getMineSizes().getOrDefault(level, 16));
            LocalMineManager.getInstance().loadBreakableMine(ret, owner);
        }
        return ret;
    }

    @Override
    public void setNpcLocation(LocRef npcLocation) {
        this.npcLocation = npcLocation;
        update();
    }

    @Override
    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
        update();
    }

    public void setLevel(int level) {
        this.level = level;
        update();
    }

    @Override
    public void setServer(Server server) {
        this.server = server;
        update();
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

    public void update() {
        MineManager.getInstance().cacheMine(this);

        if (!Config.getInstance().getServers().isSingleInstance()) {
            StorageManager.getInstance().save(PrivateMine.class, this);
            Message.builder()
                    .type(Message.Type.UPDATE_CACHE)
                    .payload(Payload.withUUID(this.getOwner()))
                    .build().send(PrisonCore.getInstance().getBroker());
        }
    }
}
