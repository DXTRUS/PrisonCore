package us.dxtrus.prisoncore.pickaxe.enchants.models;

import java.util.Arrays;
import java.util.List;

public abstract class TokenEnchant implements Enchant {
    private final EnchantInfo enchantInfo;

    protected TokenEnchant() {
        this.enchantInfo = this.getClass().getAnnotation(EnchantInfo.class);
        if (enchantInfo == null)
            throw new RuntimeException("TokenEnchant %s must be annotated with @EnchantInfo".formatted(this.getClass().getSimpleName()));
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
        return EnchantType.TOKEN;
    }
}