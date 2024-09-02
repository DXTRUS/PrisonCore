package us.dxtrus.prisoncore.pickaxe.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import us.dxtrus.prisoncore.pickaxe.PickaxeManager;
import us.dxtrus.prisoncore.pickaxe.PickaxeStats;
import us.dxtrus.prisoncore.pickaxe.enchants.EnchantManager;
import us.dxtrus.prisoncore.pickaxe.enchants.models.EnchantReference;

/**
 * Triggers enchants & handles stats
 */
public class ToolListeners implements Listener {
    private final EnchantManager enchantManager = EnchantManager.getInstance();

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        ItemStack tool = e.getPlayer().getInventory().getItemInMainHand();
        for (EnchantReference ref : enchantManager.getEnchantRefs(tool)) {
            ref.enchant().trigger(e.getPlayer(), tool, ref.level(), e);
        }

        PickaxeStats stats = PickaxeManager.getStats(tool);
        if (stats.getExperience() == 999) {
            PickaxeManager.incrementLevel(e.getPlayer(), tool);
        } else {
            PickaxeManager.incrementExp(e.getPlayer(), tool);
        }
    }
}