package us.dxtrus.prisoncore.pickaxe.enchants.impl.gem;

import org.bukkit.Material;
import us.dxtrus.prisoncore.pickaxe.enchants.models.EnchantInfo;
import us.dxtrus.prisoncore.pickaxe.enchants.models.EnchantTriggerData;
import us.dxtrus.prisoncore.pickaxe.enchants.models.GemEnchant;

@EnchantInfo(
        id = "tornado",
        name = "&fTornado",
        description = {
                "&fSpawns a tornado in your mine",
                "&fand sucks up blocks to sell!"
        },
        icon = Material.BONE_MEAL,
        maxLevel = 100
)
public class TornadoEnchant extends GemEnchant {
    @Override
    public void trigger(EnchantTriggerData data) {
        // not impl
    }
}
