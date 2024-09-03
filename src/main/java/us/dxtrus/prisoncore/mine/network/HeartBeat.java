package us.dxtrus.prisoncore.mine.network;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import us.dxtrus.commons.utils.TaskManager;
import us.dxtrus.prisoncore.PrisonCore;
import us.dxtrus.prisoncore.util.LogUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public final class HeartBeat {
    private static HeartBeat instance;
    private final JavaPlugin plugin;
    private final JedisPool jedisPool;
    private Map<String, String> servers = new ConcurrentHashMap<>();

    private HeartBeat(JavaPlugin plugin) {
        this.plugin = plugin;

        final JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(1);
        config.setMaxTotal(3);
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);

        this.jedisPool = new JedisPool(config, "127.0.0.1", 6379, 0, "redis password");
    }

    public static HeartBeat getInstance() {
        if (instance == null) {
            instance = new HeartBeat(PrisonCore.getInstance());
        }
        return instance;
    }

    public void connect() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.ping();
        } catch (Exception e) {
            LogUtil.severe("Failed to ping Redis server! (%s)".formatted(e.getMessage()), e);
        }

        startGet();
        startPost();
    }

    private void startGet() {
        TaskManager.runAsyncRepeat(plugin, () -> {
            try (Jedis jedis = jedisPool.getResource()) {
                servers = jedis.hgetAll("prisoncore:heartbeat");
                servers.forEach((serverName, serverData) -> {
                    if (isOnline(serverName)) {
                        jedis.hdel("prisoncore:heartbeat", serverName);
                        servers.remove(serverName);
                    }
                });
            } catch (Exception e) {
                LogUtil.severe("Could not get server data from Redis! (%s)".formatted(e.getMessage()), e);
            }
        }, 20L);
    }

    private void startPost() {
        long startTime = System.currentTimeMillis();

        TaskManager.runAsyncRepeat(plugin, () -> {
            String currentServer = "config thing";
            double tps = Math.min(plugin.getServer().getTPS()[0], 20.0);
            double mspt = Math.round(plugin.getServer().getAverageTickTime() * 100.0) / 100.0;
            long lastHeartBeat = System.currentTimeMillis();
            int onlinePlayers = Bukkit.getOnlinePlayers().size();

            JsonObject serverData = new JsonObject();
            serverData.addProperty("server", currentServer);
            serverData.addProperty("onlinePlayers", onlinePlayers);
            serverData.addProperty("tps", tps);
            serverData.addProperty("mspt", mspt);
            serverData.addProperty("startTime", startTime);
            serverData.addProperty("lastHeartBeat", lastHeartBeat);

            String serverDataJson = serverData.toString();

            try (Jedis jedis = jedisPool.getResource()) {
                jedis.hset("prisoncore:heartbeat", currentServer, serverDataJson);
            } catch (Exception e) {
                LogUtil.severe("Could not post server data to Redis! (%s)".formatted(e.getMessage()), e);
            }
        }, 20L);
    }

    public JsonObject getServerObject(String server) {
        try {
            String jsonString = servers.get(server);
            if (jsonString != null) {
                return (JsonObject) JsonParser.parseString(jsonString);
            }
            return null;
        } catch (JsonSyntaxException e) {
            LogUtil.severe("Error while parsing JSON! (%s)".formatted(e.getMessage()), e);
        }
        return null;
    }

    public int getPlayerCount(String server) {
        JsonObject serverObj = getServerObject(server);
        if (serverObj != null) {
            return Integer.parseInt(serverObj.get("onlinePlayers").toString());
        }
        return 0;
    }

    public double getTPS(String server) {
        JsonObject serverObj = getServerObject(server);
        if (serverObj != null) {
            return Double.parseDouble(serverObj.get("tps").toString());
        }
        return 00.0;
    }

    public double getMSPT(String server) {
        JsonObject serverObj = getServerObject(server);
        if (serverObj != null) {
            return Double.parseDouble(serverObj.get("mspt").toString());
        }
        return 00.0;
    }

//    public long[] getOnlineTime(String server) {
//        JsonObject serverObj = getServerObject(server);
//        if (serverObj != null) {
//            long milliOnline = Long.parseLong(serverObj.get("lastHeartBeat").toString()) - Long.parseLong(serverObj.get("startTime").toString());
//            return TimeUtil.splitTime(milliOnline);
//        }
//        return new long[]{0, 0, 0, 0};
//    }
//
//    public String getFormattedOnlineTime(String server) {
//        JsonObject serverObj = getServerObject(server);
//        if (serverObj != null) {
//            long milliOnline = Long.parseLong(serverObj.get("lastHeartBeat").toString()) - Long.parseLong(serverObj.get("startTime").toString());
//            return TimeUtil.formatTimeSince(milliOnline);
//        }
//        return "0s";
//    }

    public boolean isOnline(String server) {
        JsonObject serverObj = getServerObject(server);
        if (serverObj != null) {
            return getLastHeartbeat(server) >= System.currentTimeMillis() - 4000;
        }
        return false;
    }

    public long getLastHeartbeat(String server) {
        JsonObject serverObj = getServerObject(server);
        if (serverObj != null) {
            return Long.parseLong(serverObj.get("lastHeartBeat").toString());
        }
        return 0;
    }

    public long getStartTime(String server) {
        JsonObject serverObj = getServerObject(server);
        if (serverObj != null) {
            return Long.parseLong(serverObj.get("startTime").toString());
        }
        return 0;
    }
}
