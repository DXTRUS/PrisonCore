package us.dxtrus.prisoncore.stats;

import lombok.*;

import java.math.BigDecimal;
import java.math.BigInteger;
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

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public class Statistics {
        private final UUID uuid;
        private BigDecimal tokens = new BigDecimal("0");
        private BigDecimal gems = new BigDecimal("0");
        private BigDecimal blocksBroken = new BigDecimal("0");

        public void giveTokens(BigDecimal amount) {
            tokens = tokens.add(amount);
        }

        public void giveTokens(long amount) {
            giveTokens(new BigDecimal(String.valueOf(amount)));
        }

        public void giveGems(BigDecimal amount) {
            gems = gems.add(amount);
        }

        public void giveGems(long amount) {
            giveGems(new BigDecimal(String.valueOf(amount)));
        }

        public void incrementBrokenBlocks() {
            blocksBroken = blocksBroken.add(new BigDecimal("1"));
        }
    }
}
