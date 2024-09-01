package us.dxtrus.prisoncore.pickaxe.enchants.impl.gem;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import us.dxtrus.prisoncore.pickaxe.enchants.models.EnchantInfo;
import us.dxtrus.prisoncore.pickaxe.enchants.models.GemEnchant;

@EnchantInfo(
        id = "tornado",
        name = "&fTornado",
        description = {
                "Spawns a tornado in your mine",
                "and sucks up blocks to sell!"
        },
        icon = Material.BONE_MEAL,
        maxLevel = 100
)
public class TornadoEnchant extends GemEnchant {
    @Override
    public void trigger(Player player, ItemStack enchantedItem, int level, Event callingEvent) {
        player.sendMessage("tornado triggered on level %s".formatted(level));
    }
}
