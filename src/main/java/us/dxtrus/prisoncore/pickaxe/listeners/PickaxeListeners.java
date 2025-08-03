package us.dxtrus.prisoncore.pickaxe.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import us.dxtrus.prisoncore.pickaxe.PickaxeManager;

/**
 * Handles the pickaxe being moved from the first slot.
 */
public class PickaxeListeners implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (PickaxeManager.isPickaxe(e.getCurrentItem()) || e.getHotbarButton() == 0) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        if (PickaxeManager.isPickaxe(e.getItemDrop().getItemStack())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onOffhand(PlayerSwapHandItemsEvent e) {
        if (PickaxeManager.isPickaxe(e.getOffHandItem())) {
            e.setCancelled(true);
        }
    }
}

