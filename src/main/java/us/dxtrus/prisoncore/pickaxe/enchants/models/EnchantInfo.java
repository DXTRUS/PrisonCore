package us.dxtrus.prisoncore.pickaxe.enchants.models;

import org.bukkit.Material;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EnchantInfo {
    String id();

    String name();

    String[] description();

    Material icon();

    int maxLevel();
}