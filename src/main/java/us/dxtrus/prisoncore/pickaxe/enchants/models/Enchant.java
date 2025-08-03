package us.dxtrus.prisoncore.pickaxe.enchants.models;

import us.dxtrus.prisoncore.pickaxe.PickaxeManager;

import java.util.List;
import java.util.UUID;

public interface Enchant {
    void trigger(EnchantTriggerData data);

    String getId();

    String getName();

    List<String> getDescription();

    int getMaxLevel();

    EnchantType getType();

    default boolean checkForceProc(UUID uuid) {
        Enchant forceProcEnchant = PickaxeManager.forceProc.getOrDefault(uuid, null);
        return forceProcEnchant != null && forceProcEnchant.getId().equalsIgnoreCase(getId());
    }

    default void removeForceProc(UUID uuid) {
        PickaxeManager.forceProc.remove(uuid);
    }
}