package us.dxtrus.prisoncore.pickaxe;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.logging.log4j.util.Strings;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.dxtrus.commons.utils.StringUtils;
import us.dxtrus.prisoncore.config.Config;
import us.dxtrus.prisoncore.pickaxe.enchants.EnchantManager;
import us.dxtrus.prisoncore.pickaxe.enchants.models.EnchantReference;
import us.dxtrus.prisoncore.pickaxe.enchants.models.EnchantType;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@UtilityClass
public class LoreHandler {
    private final Pattern UNUSED_LORE_PATTERN = Pattern.compile("%[tg]-enchant-\\d+%");

    public ItemStack formatLore(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> lore = Config.getInstance().getPickaxe().getFormat();
        String loreString = Strings.join(lore, '\n');
        loreString = formatTokenEnchants(itemStack, loreString);
        loreString = formatGemEnchants(itemStack, loreString);
        loreString = formatStats(loreString, PickaxeManager.getStats(itemStack));

        List<Component> newLore = new ArrayList<>();
        for (String str : loreString.split("\n")) {
            if (UNUSED_LORE_PATTERN.matcher(str).find()) continue;
            newLore.add(StringUtils.modernMessage(str).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        }
        itemMeta.lore(newLore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private String formatTokenEnchants(ItemStack itemStack, String string) {
        List<EnchantReference> refs = EnchantManager.getInstance().getEnchantRefs(itemStack);
        refs.removeIf(ref -> ref.enchant().getType() == EnchantType.GEM);
        for (int i = 1; i <= refs.size(); i++) {
            string = string.replace("%t-enchant-{}%".replace("{}", String.valueOf(i)), formatEnchantRef(refs.get(i - 1)));
        }
        return string;
    }

    private String formatGemEnchants(ItemStack itemStack, String string) {
        List<EnchantReference> refs = EnchantManager.getInstance().getEnchantRefs(itemStack);
        refs.removeIf(ref -> ref.enchant().getType() == EnchantType.TOKEN);
        for (int i = 1; i <= refs.size(); i++) {
            string = string.replace("%g-enchant-{}%".replace("{}", String.valueOf(i)), formatEnchantRef(refs.get(i - 1)));
        }
        return string;
    }

    private String formatEnchantRef(EnchantReference ref) {
        String enchFormat = Config.getInstance().getPickaxe().getTokenEnchantFormat();
        String nonMax = Config.getInstance().getPickaxe().getNotMaxLevel();
        String maxLevel = Config.getInstance().getPickaxe().getMaxLevel();
        return enchFormat
                .replace("%name%", ref.enchant().getName())
                .replace("%level%", "" + ref.level())
                .replace("%max%", ref.level() == ref.enchant().getMaxLevel() ? maxLevel : nonMax);
    }

    private String formatStats(String string, PickaxeStats stats) {
        int current = stats.getExperience();
        int max = PickaxeManager.expRequirement(stats.getLevel());
        int progress = (int) ((double) current / max * 100);
        string = string.replace("%level%", stats.getLevel().toString());
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
