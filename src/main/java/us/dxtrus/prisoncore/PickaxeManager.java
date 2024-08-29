package us.dxtrus.prisoncore;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import us.dxtrus.commons.utils.StringUtils;
import us.dxtrus.commons.utils.TaskManager;
import us.dxtrus.prisoncore.util.LoreHandler;


@UtilityClass
public class PickaxeManager {
    private final JavaPlugin plugin = PrisonCore.getInstance();
    private final NamespacedKey KEY = new NamespacedKey(plugin, "pickaxe");

    public void startLoreUpdater() {
        TaskManager.runAsyncRepeat(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                givePickaxe(player);
            }
        }, 40L);
    }

    public void givePickaxe(Player player) {
        ItemStack itemStack = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta meta = itemStack.getItemMeta();
        meta.displayName(StringUtils.modernMessage("&a%s's Pickaxe".formatted(player.getName())).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        meta.getPersistentDataContainer().set(KEY, PersistentDataType.BOOLEAN, true);
        itemStack.setItemMeta(meta);
        player.getInventory().setItem(0, LoreHandler.formatLore(itemStack));
    }

    public boolean isPickaxe(@Nullable ItemStack itemStack) {
        if (itemStack == null) return false;
        return !itemStack.getPersistentDataContainer().isEmpty() && itemStack.getPersistentDataContainer().has(KEY);
    }
}
