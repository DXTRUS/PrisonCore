package us.dxtrus.prisoncore.pickaxe.enchants.models;

import java.util.Arrays;
import java.util.List;

public abstract class GemEnchant implements Enchant {
    private final EnchantInfo enchantInfo;

    protected GemEnchant() {
        this.enchantInfo = this.getClass().getAnnotation(EnchantInfo.class);
        if (enchantInfo == null)
            throw new RuntimeException("GemEnchant %s must be annotated with @EnchantInfo".formatted(this.getClass().getSimpleName()));
    }

    @Override
    public String getId() {
        return enchantInfo.id();
    }

    @Override
    public String getName() {
        return enchantInfo.name();
    }

    @Override
    public List<String> getDescription() {
        return Arrays.stream(enchantInfo.description()).toList();
    }

    @Override
    public int getMaxLevel() {
        return enchantInfo.maxLevel();
    }


    @Override
    public EnchantType getType() {
        return EnchantType.GEM;
    }
}