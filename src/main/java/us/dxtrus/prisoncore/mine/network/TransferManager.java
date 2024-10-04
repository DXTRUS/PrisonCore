package us.dxtrus.prisoncore.mine.network;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.util.Pool;
import us.dxtrus.prisoncore.config.Config;
import us.dxtrus.prisoncore.config.Lang;
import us.dxtrus.prisoncore.locations.Loc;
import us.dxtrus.prisoncore.locations.Locations;
import us.dxtrus.prisoncore.mine.models.PrivateMine;
import us.dxtrus.prisoncore.mine.models.Server;
import us.dxtrus.prisoncore.util.MessageUtils;

public class TransferManager {
    private static TransferManager instance;

    private final Pool<Jedis> pool;

    private TransferManager() {
        this.pool = startJedisPool();
    }

    @NotNull
    private static Pool<Jedis> startJedisPool() {
        Config.Redis redisConfig = Config.getInstance().getRedis();
        final String password = redisConfig.getPassword();
        final String host = redisConfig.getHost();
        final int port = redisConfig.getPort();

        final JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(0);
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);

        return password.isEmpty()
                ? new JedisPool(config, host, port, 0, false)
                : new JedisPool(config, host, port, 0, password, false);
    }

    public static TransferManager getInstance() {
        if (instance == null) {
            instance = new TransferManager();
        }
        return instance;
    }

    public void addPlayerForTransfer(Player player, Server target, TransferReason reason,
                                     @Nullable us.dxtrus.prisoncore.locations.Location loc) {
        try (Jedis jedis = pool.getResource()) {
            if (reason == TransferReason.LOCATION_TELEPORT) {
                assert loc != null;
                jedis.hset("prisoncore:transfers", player.getUniqueId().toString(), reason.name() + "::" + target.getName() + "::" + loc.name());
                return;
            }
            jedis.hset("prisoncore:transfers", player.getUniqueId().toString(), reason.name() + "::" + target.getName());
        }
    }

    public void checkout(Player player) {
        try (Jedis jedis = pool.getResource()) {
            if (!jedis.hexists("prisoncore:transfers", player.getUniqueId().toString())) {
                return;
            }
            String[] parts = jedis.hget("prisoncore:transfers", player.getUniqueId().toString()).split("::");
            TransferReason reason = TransferReason.valueOf(parts[0]);

            switch (reason) {
                case GOTO_ISLAND -> handleGotoIsland(player, parts[1], jedis);
                case LOCATION_TELEPORT -> handleLocationTeleport(player, parts[1], parts[2], jedis);
            }
        }
    }

    private void handleGotoIsland(Player player, String serverName, Jedis jedisConnection) {
        if (!serverName.equals(ServerManager.getInstance().getThisServer().getName())) {
            ServerManager.getInstance().getServer(serverName).transferPlayer(player);
            return;
        }

        jedisConnection.hdel("prisoncore:transfers", player.getUniqueId().toString());
        PrivateMine mine = new PrivateMine(player.getUniqueId());
        Location location = mine.getSpawnLocation().toBukkit(Bukkit.getWorld(mine.getWorldName()));
        location.setYaw(90);
        location.setPitch(0);
        player.teleportAsync(location).thenAccept(success -> {
            player.setAllowFlight(true);
            player.setFlying(true);
            MessageUtils.send(player, Lang.getInstance().getCommand().getMine().getTeleportComplete());
        });
    }

    private void handleLocationTeleport(Player player, String serverName, String locationName, Jedis jedisConnection) {
        if (!serverName.equals(ServerManager.getInstance().getThisServer().getName())) {
            ServerManager.getInstance().getServer(serverName).transferPlayer(player);
            return;
        }

        jedisConnection.hdel("prisoncore:transfers", player.getUniqueId().toString());
        Loc loc = Locations.getLocation(us.dxtrus.prisoncore.locations.Location.valueOf(locationName));
        Location location = loc.toBukkit();
        location.setYaw(90);
        location.setPitch(0);
        player.teleportAsync(location).thenAccept(success -> {
            MessageUtils.send(player, Lang.getInstance().getCommand().getSpawn().getTeleportComplete());
        });
    }

    public enum TransferReason {
        GOTO_ISLAND,
        LOCATION_TELEPORT
    }
}
