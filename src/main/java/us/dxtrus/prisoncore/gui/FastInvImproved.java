package us.dxtrus.prisoncore.gui;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import us.dxtrus.commons.gui.FastInv;
import us.dxtrus.commons.gui.ItemBuilder;

import java.util.Objects;
import java.util.function.Function;

@Getter
public abstract class FastInvImproved extends FastInv {
    public FastInvImproved(int size) {
        super(size);
    }
    public FastInvImproved(int size, String title) {
        super(size, title);
    }
    public FastInvImproved(InventoryType type) {
        super(type);
    }
    public FastInvImproved(InventoryType type, String title) {
        super(type, title);
    }
    public FastInvImproved(Function<InventoryHolder, Inventory> inventoryFunction) {
        super(inventoryFunction);
    }

    private Material fillerMaterial = Material.AIR;

    public void fill() {
        ItemStack filler = new ItemBuilder(fillerMaterial).name(ChatColor.translateAlternateColorCodes('&', "&f ")).build();
        ItemStack filler2 = new ItemBuilder(Material.RED_STAINED_GLASS_PANE).name(ChatColor.translateAlternateColorCodes('&', "&f ")).build();
        for (int i = 0; i < this.getInventory().getSize(); i++) {
            if (getInventory().getItem(i) != null) {
                continue;
            }

            setItem(i, i % 2 == 0 ? filler : filler2);
        }
    }

    public FastInvImproved setFillerMaterial(Material fillerMaterial) {
        this.fillerMaterial = fillerMaterial;
        return this;
    }
}
