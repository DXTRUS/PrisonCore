package us.dxtrus.prisoncore.stats;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import us.dxtrus.prisoncore.storage.StorageManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("FieldMayBeFinal")
public class StatsManager {

    private static StatsManager instance;
    private Map<UUID, Statistics> stats = new HashMap<>();

    public static StatsManager getInstance() {
        return instance == null ? instance = new StatsManager() : instance;
    }

    public Statistics getStatistics(UUID uuid) {
        if (stats == null) {
            stats = new HashMap<>();
        }

        return stats.containsKey(uuid) ? stats.get(uuid) : stats.put(uuid, new Statistics(uuid));
    }

    public void load(UUID uuid) {
        if (stats.containsKey(uuid)) return;

        try {
            StorageManager.getInstance().get(Statistics.class, uuid).thenAccept((stat) -> {
                stats.put(uuid, stat.get());
            }).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveAllOnline() {
        Bukkit.getOnlinePlayers().forEach(player -> instance.save(player.getUniqueId()));
    }

    public void save(UUID who) {
        try {
            if (StorageManager.getInstance().get(Statistics.class, who).get().isEmpty()) {
                StorageManager.getInstance().save(Statistics.class, getStatistics(who));
            } else {
                StorageManager.getInstance().update(Statistics.class, getStatistics(who));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
