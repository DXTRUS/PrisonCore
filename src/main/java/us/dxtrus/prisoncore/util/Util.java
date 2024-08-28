package us.dxtrus.prisoncore.util;

import org.bukkit.Material;

public class Util {
    public static boolean anyMaterial(Material haystack, Material... needles) {
        if (needles.length < 1) {
            return false;
        }

        for (Material needle : needles) {
            if (haystack.equals(needle)) {
                return true;
            }
        }

        return false;
    }
}
