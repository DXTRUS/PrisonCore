package us.dxtrus.prisoncore.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import us.dxtrus.prisoncore.PickaxeManager;

public class PickaxeListeners implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (PickaxeManager.isPickaxe(e.getInventory().getItem(e.getSlot()))) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        if (PickaxeManager.isPickaxe(e.getItemDrop().getItemStack())) {
            e.setCancelled(true);
        }
    }
}
