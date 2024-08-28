package us.dxtrus.prisoncore.statistics;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("FieldMayBeFinal")
public class StatsManager {
    private static StatsManager instance;
    private Map<UUID, Statistics> stats = new HashMap<>();

    public Statistics getStatistics(UUID uuid) {
        if (stats == null) {
            stats = new HashMap<>();
        }

        return stats.containsKey(uuid) ? stats.get(uuid) : stats.put(uuid, new Statistics(uuid));
    }

    public static StatsManager getInstance() {
        return instance == null ? instance = new StatsManager() : instance;
    }
}