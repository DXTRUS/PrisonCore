package us.dxtrus.prisoncore.hooks;

import lombok.Getter;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.dxtrus.commons.utils.StringUtils;
import us.dxtrus.prisoncore.stats.Statistics;
import us.dxtrus.prisoncore.stats.StatsManager;

import java.math.BigDecimal;
import java.text.DecimalFormat;

@Getter
public class PAPIHook extends PlaceholderExpansion {
    private static final String[] SUFFIXES = {
            "", "k", "M", "B", "T", "Q", "Qi", "Sx", "Sp", "Oc", "No"
    };
    private static final BigDecimal THOUSAND = BigDecimal.valueOf(1000);
    private final String identifier = "prisoncore";
    private final String version = "1.0.0";
    private final String author = "DXTRUS";

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

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) {
            return "???";
        }

        return switch (params.toLowerCase()) {
            case "player_name_g" -> applyGradientToName(player.getName(), "9555ff", "ffffff");

            case "tokens" -> {
                Statistics stats = StatsManager.getInstance().getStatistics(player.getUniqueId());
                if (stats == null || stats.getTokens().toString().equals("0")) {
                    yield "0";
                }

                yield formatBigInteger(stats.getTokens(), true);
            }

            case "gems" -> {
                Statistics stats = StatsManager.getInstance().getStatistics(player.getUniqueId());
                if (stats == null || stats.getGems().toString().equals("0")) {
                    yield "0";
                }

                yield formatBigInteger(stats.getGems(), false);
            }

            case "blocks_broken" -> {
                Statistics stats = StatsManager.getInstance().getStatistics(player.getUniqueId());
                if (stats == null || stats.getBlocksBroken().toString().equals("0")) {
                    yield "0";
                }

                yield formatBigInteger(stats.getBlocksBroken(), false);
            }

            case "prestige" -> "0";
            default -> "&cN/A&r";
        };
    }

    static float COLOR1_PORTION = 0.45f;

    public static String applyGradientToName(String name, String color1Hex, String color2Hex) {
        color1Hex = color1Hex.replace("#", "");
        color2Hex = color2Hex.replace("#", "");

        int r1 = Integer.parseInt(color1Hex.substring(0, 2), 16);
        int g1 = Integer.parseInt(color1Hex.substring(2, 4), 16);
        int b1 = Integer.parseInt(color1Hex.substring(4, 6), 16);

        int r2 = Integer.parseInt(color2Hex.substring(0, 2), 16);
        int g2 = Integer.parseInt(color2Hex.substring(2, 4), 16);
        int b2 = Integer.parseInt(color2Hex.substring(4, 6), 16);

        StringBuilder output = new StringBuilder();
        int length = name.length();

        for (int i = 0; i < length; i++) {
            float progress = (float) i / (length - 1);

            int r, g, b;

            if (progress < COLOR1_PORTION) {
                // Use solid color1Hex
                r = r1;
                g = g1;
                b = b1;
            } else {
                // Interpolate from color1 to color2
                float fadeRatio = (progress - COLOR1_PORTION) / (1.0f - COLOR1_PORTION);
                fadeRatio = Math.max(0f, Math.min(1f, fadeRatio)); // Clamp to [0, 1]

                r = (int) (r1 + fadeRatio * (r2 - r1));
                g = (int) (g1 + fadeRatio * (g2 - g1));
                b = (int) (b1 + fadeRatio * (b2 - b1));
            }

            String hex = String.format("%06X", (r << 16) | (g << 8) | b);
            StringBuilder colorCode = new StringBuilder("§x");
            for (char c : hex.toCharArray()) {
                colorCode.append('§').append(c);
            }

            output.append(colorCode).append("§l").append(name.charAt(i));
        }

        return output.toString();
    }

    private static int clamp(int val) {
        return Math.max(0, Math.min(255, val));
    }

}