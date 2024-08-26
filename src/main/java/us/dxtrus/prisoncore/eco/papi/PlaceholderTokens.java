package us.dxtrus.prisoncore.eco.papi;

import lombok.Getter;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.dxtrus.prisoncore.eco.EconomyManager;

import java.math.BigInteger;
import java.text.DecimalFormat;

@Getter
public class PlaceholderTokens extends PlaceholderExpansion {
    private final String identifier = "prisoncore";
    private final String version = "1.0.0";
    private final String author = "DXTRUS";

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equalsIgnoreCase("tokens")) {
            EconomyManager.Tokens tokens = EconomyManager.getTokens(player.getUniqueId());
            return formatBigInteger(tokens.getCount());
        }

        return null;
    }

    private static final String[] SUFFIXES = {
            "", "K", "M", "B", "T", "Q", "Qi", "Sx", "Sp", "Oc", "No"
    };

    private static final BigInteger THOUSAND = BigInteger.valueOf(1000);

    public static String formatBigInteger(BigInteger value) {
        int suffixIndex = 0;
        BigInteger remainder = BigInteger.ZERO;

        while (value.compareTo(THOUSAND) >= 0 && suffixIndex < SUFFIXES.length - 1) {
            remainder = value.remainder(THOUSAND);
            value = value.divide(THOUSAND);
            suffixIndex++;
        }

        return formatWithSuffix(value, remainder, SUFFIXES[suffixIndex]);
    }

    private static String formatWithSuffix(BigInteger value, BigInteger remainder, String suffix) {
        DecimalFormat df = new DecimalFormat("#.00");

        // To get a more precise decimal value
        double formattedValue = value.doubleValue() + remainder.doubleValue() / 1000.0;
        return df.format(formattedValue) + suffix;
    }
}
