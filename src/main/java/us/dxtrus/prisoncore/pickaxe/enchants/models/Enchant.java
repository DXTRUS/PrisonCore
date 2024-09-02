package us.dxtrus.prisoncore.pickaxe.enchants.models;

import java.util.List;

public interface Enchant {
    void trigger(EnchantTriggerData data);

    String getId();

    String getName();

    List<String> getDescription();

    int getMaxLevel();

    EnchantType getType();
}