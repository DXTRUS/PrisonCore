package us.dxtrus.prisoncore.pickaxe.enchants.models;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface Enchant {
    void trigger(Player player, ItemStack enchantedItem, int level, Event callingEvent);

    String getId();

    String getName();

    List<String> getDescription();

    int getMaxLevel();

    EnchantType getType();
}