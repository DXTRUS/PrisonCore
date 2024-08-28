package us.dxtrus.prisoncore.mine.network;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.util.Pool;
import us.dxtrus.prisoncore.config.Config;
import us.dxtrus.prisoncore.config.Lang;
import us.dxtrus.prisoncore.mine.models.PrivateMine;
import us.dxtrus.prisoncore.mine.models.Server;
import us.dxtrus.prisoncore.util.MessageUtils;

public class TransferManager {
    private static TransferManager instance;

    private final Pool<Jedis> pool;

    private TransferManager() {
        this.pool = startJedisPool();
    }

    public void addPlayerForTransfer(Player player, Server target) {
        try (Jedis jedis = pool.getResource()) {
            jedis.hset("prisoncore:transfers", player.getUniqueId().toString(), target.getName());
        }
    }

    public void checkout(Player player) {
        try (Jedis jedis = pool.getResource()) {
            if (!jedis.hexists("prisoncore:transfers", player.getUniqueId().toString())) {
                return;
            }
            String serverName = jedis.hget("prisoncore:transfers", player.getUniqueId().toString());
            if (!serverName.equals(ServerManager.getInstance().getThisServer().getName())) {
                ServerManager.getInstance().getServer(serverName).transferPlayer(player);
                return;
            }

            jedis.hdel("prisoncore:transfers", player.getUniqueId().toString());
            PrivateMine mine = new PrivateMine(player.getUniqueId());
            player.teleportAsync(mine.getSpawnLocation().toBukkit(Bukkit.getWorld(mine.getWorldName()))).thenAccept(success -> {
                player.setAllowFlight(true);
                player.setFlying(true);
                MessageUtils.send(player, Lang.getInstance().getCommand().getMine().getTeleportComplete());
            });
        }
    }

    // CALL ONCE!
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
}
