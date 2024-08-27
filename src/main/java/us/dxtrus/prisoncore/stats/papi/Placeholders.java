package us.dxtrus.prisoncore.stats.papi;

import lombok.Getter;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.dxtrus.prisoncore.stats.StatsManager;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;

@Getter
public class Placeholders extends PlaceholderExpansion {
    private final String identifier = "prisoncore";
    private final String version = "1.0.0";
    private final String author = "DXTRUS";

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) {
            return "???";
        }

        if (params.equalsIgnoreCase("tokens")) {
            StatsManager.Statistics stats = StatsManager.getInstance().getStatistics(player.getUniqueId());
            if (stats == null) {
                return "0";
            }

            if (stats.getTokens().toString().equals("0")) {
                return "0";
            }

            return formatBigInteger(stats.getTokens(), true);
        }

        if (params.equalsIgnoreCase("gems")) {
            StatsManager.Statistics stats = StatsManager.getInstance().getStatistics(player.getUniqueId());
            if (stats == null) {
                return "0";
            }

            if (stats.getGems().toString().equals("0")) {
                return "0";
            }

            return formatBigInteger(stats.getGems(), false);
        }

        if (params.equalsIgnoreCase("blocks_broken")) {
            StatsManager.Statistics stats = StatsManager.getInstance().getStatistics(player.getUniqueId());
            if (stats == null) {
                return "0";
            }

            if (stats.getBlocksBroken().toString().equals("0")) {
                return "0";
            }

            return formatBigInteger(stats.getBlocksBroken(), false);
        }

        return "&cNA&r";
    }

    private static final String[] SUFFIXES = {
            "", "k", "M", "B", "T", "Q", "Qi", "Sx", "Sp", "Oc", "No"
    };

    private static final BigDecimal THOUSAND = BigDecimal.valueOf(1000);

    public static String formatBigInteger(BigDecimal value, boolean formatDecimals) {
        int suffixIndex = 0;

        while (value.compareTo(THOUSAND) >= 0 && suffixIndex < SUFFIXES.length - 1) {
            value = value.divide(THOUSAND);
            suffixIndex++;
        }

        return formatWithSuffix(value, SUFFIXES[suffixIndex], formatDecimals);
    }

    private static String formatWithSuffix(BigDecimal value, String suffix, boolean formatDecimals) {
        DecimalFormat df = new DecimalFormat(formatDecimals ? "#.00" : "#.##");
        // Format the value directly, no remainder added back.
        return df.format(value) + suffix;
    }
}
