package us.dxtrus.prisoncore.util;

import net.md_5.bungee.api.ChatColor;

import java.util.Arrays;

public final class StringUtil {
    public static boolean matchAny(String haystack, boolean ic, String... needles) {
        if (haystack.isEmpty()) {
            return false;
        }

        return Arrays.stream(needles).anyMatch(needle -> ic ? haystack.equalsIgnoreCase(needle) : haystack.equals(needle));
    }

    public static String tl(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static int guiRows(int rows) {
        return Math.max(9, rows * 9);
    }
}
