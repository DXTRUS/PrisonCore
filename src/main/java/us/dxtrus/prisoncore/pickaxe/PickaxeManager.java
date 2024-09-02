package us.dxtrus.prisoncore.pickaxe;

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
import us.dxtrus.prisoncore.PrisonCore;
import us.dxtrus.prisoncore.pickaxe.enchants.EnchantManager;
import us.dxtrus.prisoncore.pickaxe.enchants.models.Enchant;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


@UtilityClass
public class PickaxeManager {
    private final JavaPlugin plugin = PrisonCore.getInstance();

    private final NamespacedKey KEY = new NamespacedKey(plugin, "pickaxe");
    private static final String PDC_FORMAT = "%level%:%exp%";

    private final Map<UUID, Integer> levelCache = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> expCache = new ConcurrentHashMap<>();

    public void startLoreUpdater() {
        TaskManager.runAsyncRepeat(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                updatePickaxe(player);
            }
        }, 40L);
    }

    public void enchant(Player player, Enchant enchant, int level) {
        ItemStack itemStack = player.getInventory().getItem(0);
        if (itemStack == null) {
            itemStack = new ItemStack(Material.DIAMOND_PICKAXE);
        }
        itemStack = EnchantManager.getInstance().applyEnchant(enchant, level, itemStack);
        player.getInventory().setItem(0, itemStack);
    }

    public void populateCache(Player player) {
        ItemStack itemStack = player.getInventory().getItem(0);
        if (itemStack == null) {
            itemStack = new ItemStack(Material.DIAMOND_PICKAXE);
        }
        PickaxeStats stats = getStats(itemStack);
        levelCache.put(player.getUniqueId(), stats.getLevel());
        expCache.put(player.getUniqueId(), stats.getExperience());
    }

    public int expRequirement(int level) {
        return (int) Math.round(level * 1000 * 0.75);
    }

    public void incrementExp(Player player) {
        incrementExp(player, 1);
    }

    public void incrementExp(Player player, int amount) {
        PickaxeStats stats = new PickaxeStats(levelCache.get(player.getUniqueId()), expCache.get(player.getUniqueId()));
        int expReq = expRequirement(stats.getLevel());
        if (stats.getExperience() + amount >= expReq) {
            if (amount == 1) {
                incrementLevel(player);
                return;
            }
            incrementLevel(player, amount / expReq);
            incrementExp(player, amount % expReq);
            return;
        }
        expCache.put(player.getUniqueId(), expCache.get(player.getUniqueId()) + amount);
    }

    public void incrementLevel(Player player) {
        incrementLevel(player, 1);
    }

    public void incrementLevel(Player player, int amount) {
        levelCache.put(player.getUniqueId(), levelCache.get(player.getUniqueId()) + amount);
        expCache.put(player.getUniqueId(), 0);
    }

    public PickaxeStats getStats(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        String data = meta.getPersistentDataContainer().get(KEY, PersistentDataType.STRING);
        if (data == null) {
            data = "1:0";
        }
        int level = Integer.parseInt(data.split(":")[0]);
        int exp = Integer.parseInt(data.split(":")[1]);
        return new PickaxeStats(level, exp);
    }

    public void givePickaxe(Player player) {
        ItemStack itemStack = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta meta = itemStack.getItemMeta();
        meta.displayName(StringUtils.modernMessage("&a%s's Pickaxe".formatted(player.getName())).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        PickaxeStats stats = getStats(itemStack);
        meta.getPersistentDataContainer().set(KEY, PersistentDataType.STRING,
                getDataString(expCache.getOrDefault(player.getUniqueId(), stats.getExperience()),
                        levelCache.getOrDefault(player.getUniqueId(), stats.getLevel())));
        itemStack.setItemMeta(meta);
        itemStack = EnchantManager.getInstance().applyAllEnchants(itemStack);
        player.getInventory().setItem(0, LoreHandler.formatLore(itemStack));
    }

    public void updatePickaxe(Player player) {
        ItemStack itemStack = player.getInventory().getItem(0);
        if (itemStack == null) {
            givePickaxe(player);
            return;
        }
        PickaxeStats stats = getStats(itemStack);
        ItemMeta meta = itemStack.getItemMeta();
        meta.getPersistentDataContainer().set(KEY, PersistentDataType.STRING,
                getDataString(expCache.getOrDefault(player.getUniqueId(), stats.getExperience()),
                        levelCache.getOrDefault(player.getUniqueId(), stats.getLevel())));
        itemStack.setItemMeta(meta);
        itemStack = EnchantManager.getInstance().applyAllEnchants(itemStack); // update the enchants in case we added a new one!
        player.getInventory().setItem(0, LoreHandler.formatLore(itemStack));
    }

    public boolean isPickaxe(@Nullable ItemStack itemStack) {
        if (itemStack == null) return false;
        return !itemStack.getPersistentDataContainer().isEmpty() && itemStack.getPersistentDataContainer().has(KEY);
    }

    private String getDataString(int exp, int level) {
        return PDC_FORMAT
                .replace("%exp%", String.valueOf(exp))
                .replace("%level%", String.valueOf(level));
    }
}
