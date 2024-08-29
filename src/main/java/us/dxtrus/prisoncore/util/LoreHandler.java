package us.dxtrus.prisoncore.util;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.logging.log4j.util.Strings;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.dxtrus.commons.utils.StringUtils;
import us.dxtrus.prisoncore.config.Config;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class LoreHandler {
    public ItemStack formatLore(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> lore = Config.getInstance().getPickaxe().getFormat();
        String loreString = Strings.join(lore, '\n');
        loreString = formatTokenEnchants(loreString);
        loreString = formatGemEnchants(loreString);
        loreString = formatStats(loreString, "");

        List<Component> newLore = new ArrayList<>();
        for (String str : loreString.split("\n")) {
            newLore.add(StringUtils.modernMessage(str).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        }
        itemMeta.lore(newLore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private String formatTokenEnchants(String string) {
        string = string.replace("%t-enchant-1%", formatTokenEnchant(""));
        string = string.replace("%t-enchant-2%", formatTokenEnchant(""));
        string = string.replace("%t-enchant-3%", formatTokenEnchant(""));
        string = string.replace("%t-enchant-4%", formatTokenEnchant(""));
        string = string.replace("%t-enchant-5%", formatTokenEnchant(""));
        string = string.replace("%t-enchant-6%", formatTokenEnchant(""));
        string = string.replace("%t-enchant-7%", formatTokenEnchant(""));
        string = string.replace("%t-enchant-8%", formatTokenEnchant(""));
        string = string.replace("%t-enchant-9%", formatTokenEnchant(""));
        string = string.replace("%t-enchant-10%", formatTokenEnchant(""));
        return string;
    }

    private String formatTokenEnchant(String thisWillBeEnchantObject) {
        String enchFormat = Config.getInstance().getPickaxe().getTokenEnchantFormat();
        String nonMax = Config.getInstance().getPickaxe().getNotMaxLevel();
        String maxLevel = Config.getInstance().getPickaxe().getMaxLevel();
        return enchFormat
                .replace("%name%", "Skibidi Enchant")
                .replace("%level%", "700")
                .replace("%max%", nonMax);
    }

    private String formatGemEnchants(String string) {
        string = string.replace("%g-enchant-1%", formatGemEnchant(""));
        string = string.replace("%g-enchant-2%", formatGemEnchant(""));
        string = string.replace("%g-enchant-3%", formatGemEnchant(""));
        string = string.replace("%g-enchant-4%", formatGemEnchant(""));
        string = string.replace("%g-enchant-5%", formatGemEnchant(""));
        return string;
    }

    private String formatGemEnchant(String thisWillBeEnchantObject) {
        String enchFormat = Config.getInstance().getPickaxe().getGemEnchantFormat();
        String nonMax = Config.getInstance().getPickaxe().getNotMaxLevel();
        String maxLevel = Config.getInstance().getPickaxe().getMaxLevel();
        return enchFormat
                .replace("%name%", "Skibidi Enchant")
                .replace("%level%", "700")
                .replace("%max%", nonMax);
    }

    private String formatStats(String string, String thisWilBePickaxeStatsObject) {
        int current = 20;
        int max = 100;
        int progress = (int) ((double) current / max * 100);
        string = string.replace("%level%", "260");
        string = string.replace("%xp-bar%", generateProgressBar(current, max));
        string = string.replace("%xp-percent%", String.valueOf(progress));
        string = string.replace("%skin%", "&7Default");
        return string;
    }

    private String generateProgressBar(int current, int max) {
        String xpBarFormat = Config.getInstance().getPickaxe().getXpBarFormat();
        String xpBarIcon = Config.getInstance().getPickaxe().getXpBarIcon();

        current = Math.max(0, Math.min(current, max));
        int progress = (int) ((double) current / max * 100);
        int numEquals = progress / 10;

        String center = "&a"+xpBarIcon.repeat(Math.max(0, numEquals))+"&c"+xpBarIcon.repeat(Math.max(0, 10 - numEquals));
        return xpBarFormat.replace("%progress%", center);
    }
}
