package us.dxtrus.prisoncore.pickaxe.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import us.dxtrus.prisoncore.mine.MineManager;
import us.dxtrus.prisoncore.mine.models.PrivateMine;
import us.dxtrus.prisoncore.pickaxe.PickaxeManager;
import us.dxtrus.prisoncore.pickaxe.enchants.EnchantManager;
import us.dxtrus.prisoncore.pickaxe.enchants.models.EnchantReference;
import us.dxtrus.prisoncore.pickaxe.enchants.models.EnchantTriggerData;

/**
 * Triggers enchants & handles stats
 */
public class ToolListeners implements Listener {
    private final EnchantManager enchantManager = EnchantManager.getInstance();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        ItemStack tool = e.getPlayer().getInventory().getItemInMainHand();
        PrivateMine mine = MineManager.getInstance().getMine(e.getPlayer().getUniqueId());
        for (EnchantReference ref : enchantManager.getEnchantRefs(tool)) {
            ref.enchant().trigger(new EnchantTriggerData(e.getPlayer(), tool, ref.level(), mine, e));
        }

        PickaxeManager.incrementExp(e.getPlayer());
    }
}