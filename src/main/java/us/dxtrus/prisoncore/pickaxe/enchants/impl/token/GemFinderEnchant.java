package us.dxtrus.prisoncore.pickaxe.enchants.impl.token;

import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import us.dxtrus.prisoncore.PrisonCore;
import us.dxtrus.prisoncore.pickaxe.enchants.models.EnchantInfo;
import us.dxtrus.prisoncore.pickaxe.enchants.models.EnchantTriggerData;
import us.dxtrus.prisoncore.pickaxe.enchants.models.TokenEnchant;

@EnchantInfo(
        id = "gem_finder",
        name = "&aGem Finder",
        maxLevel = 5000,
        description = {
                "&fFind &aGems &fas you mine."
        },
        icon = Material.EMERALD
)
public class GemFinderEnchant extends TokenEnchant {
    @Override
    public void trigger(EnchantTriggerData data) {
        if (!(data.getCallingEvent() instanceof BlockBreakEvent event)) return;
        if (!shouldProc(data.getLevel())) return;
        data.getPlayer().sendMessage("Gem finder triggered");
    }

    private boolean shouldProc(int level) {
        return PrisonCore.getInstance().getRandomBoolean(level * 0.0004);
    }
}
