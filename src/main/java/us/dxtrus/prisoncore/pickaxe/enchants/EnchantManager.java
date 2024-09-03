package us.dxtrus.prisoncore.pickaxe.enchants;

import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import us.dxtrus.prisoncore.PrisonCore;
import us.dxtrus.prisoncore.pickaxe.enchants.models.Enchant;
import us.dxtrus.prisoncore.pickaxe.enchants.models.EnchantReference;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EnchantManager {
    private static EnchantManager instance;

    private final JavaPlugin plugin;
    private static final String PDC_FORMAT = "%enchant%:%level%";

    /**
     * Stores a map of enchant Name to ID
     */
    @Getter private final Map<String, String> enchantIdsCache = new ConcurrentHashMap<>();
    @Getter private final Map<String, Enchant> enchants = new ConcurrentHashMap<>();

    private EnchantManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public ItemStack applyVanillaEnchants(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setUnbreakable(true);
        itemStack.setItemMeta(meta);
        itemStack.addUnsafeEnchantment(Enchantment.EFFICIENCY, 255);
        itemStack.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP, ItemFlag.HIDE_UNBREAKABLE);
        return itemStack;
    }

    public int getLevelFromItemStack(Enchant customEnchant, ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
        NamespacedKey key = getNamespacedKey(customEnchant);
        if (!pdc.has(key, PersistentDataType.STRING)) return 0;
        return getLevelFromData(pdc.get(key, PersistentDataType.STRING));
    }

    public boolean isMaxLevel(Enchant customEnchant, ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
        NamespacedKey key = getNamespacedKey(customEnchant);
        if (!pdc.has(key, PersistentDataType.STRING)) return false;
        return getLevelFromData(pdc.get(key, PersistentDataType.STRING)) == customEnchant.getMaxLevel();
    }

    public ItemStack applyEnchant(Enchant customEnchant, int level, ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
        NamespacedKey key = getNamespacedKey(customEnchant);
        if (pdc.has(key, PersistentDataType.STRING)) {
            pdc.remove(key);
        }

        itemMeta.getPersistentDataContainer().set(getNamespacedKey(customEnchant), PersistentDataType.STRING, getDataString(customEnchant, level));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public void registerEnchant(Enchant enchant) {
        enchants.put(enchant.getId(), enchant);
    }

    public List<EnchantReference> getEnchantRefs(ItemStack itemStack) {
        List<EnchantReference> customEnchants = new ArrayList<>();
        if (itemStack.getItemMeta() == null) return customEnchants;
        PersistentDataContainer pdc = itemStack.getItemMeta().getPersistentDataContainer();
        for (Enchant enchant : enchants.values()) {
            NamespacedKey key = getNamespacedKey(enchant);
            if (pdc.has(key)) customEnchants.add(new EnchantReference(enchant, getLevelFromData(pdc.get(key, PersistentDataType.STRING))));
        }
        customEnchants.sort(Comparator.comparingInt(EnchantReference::level).reversed());
        return customEnchants;
    }

    public List<Enchant> getEnchants(ItemStack itemStack) {
        List<Enchant> customEnchants = new ArrayList<>();
        if (itemStack.getItemMeta() == null) return customEnchants;
        PersistentDataContainer pdc = itemStack.getItemMeta().getPersistentDataContainer();
        for (Enchant enchant : enchants.values()) {
            if (pdc.has(getNamespacedKey(enchant))) customEnchants.add(enchant);
        }
        return customEnchants;
    }

    private NamespacedKey getNamespacedKey(Enchant enchant) {
        return new NamespacedKey(plugin, enchant.getId());
    }

    private String getDataString(Enchant enchant, int level) {
        return PDC_FORMAT
                .replace("%enchant%", enchant.getId())
                .replace("%level%", String.valueOf(level));
    }

    private int getLevelFromData(String dataString) {
        return Integer.parseInt(dataString.split(":")[1]);
    }


    public static EnchantManager getInstance() {
        if (instance == null) {
            instance = new EnchantManager(PrisonCore.getInstance());
        }
        return instance;
    }
}
