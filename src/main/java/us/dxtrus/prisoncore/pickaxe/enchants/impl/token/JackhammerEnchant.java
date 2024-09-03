package us.dxtrus.prisoncore.pickaxe.enchants.impl.token;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.TestOnly;
import us.dxtrus.prisoncore.PrisonCore;
import us.dxtrus.prisoncore.mine.models.Cuboid;
import us.dxtrus.prisoncore.mine.models.Mine;
import us.dxtrus.prisoncore.pickaxe.PickaxeManager;
import us.dxtrus.prisoncore.pickaxe.enchants.models.EnchantInfo;
import us.dxtrus.prisoncore.pickaxe.enchants.models.EnchantTriggerData;
import us.dxtrus.prisoncore.pickaxe.enchants.models.TokenEnchant;

import java.util.concurrent.atomic.AtomicInteger;

@EnchantInfo(
        id = "jackhammer",
        name = "&bJack Hammer",
        maxLevel = 10000,
        description = {
                "&fHas a chance to break a",
                "&flayer of your mine."
        },
        icon = Material.ANVIL
)
public class JackhammerEnchant extends TokenEnchant {
    @Override
    public void trigger(EnchantTriggerData data) {
        if (!(data.getCallingEvent() instanceof BlockBreakEvent event)) return;
        if (!shouldProc(data.getLevel())) return;
        Mine mine = data.getMine().getLinkage();
        Location tl = mine.getBounds().getLowerNE();
        tl = tl.clone();
        tl.setY(event.getBlock().getY());

        Location br = mine.getBounds().getUpperSW();
        br = br.clone();
        br.setY(event.getBlock().getY());

        AtomicInteger broken = new AtomicInteger();
        Cuboid jhCuboid = new Cuboid(tl, br);
        jhCuboid.forEach(block -> {
            broken.addAndGet(1);
            block.setType(Material.AIR);
        });

        mine.incrementBroken(broken.get());
        PickaxeManager.incrementExp(data.getPlayer(), broken.get());
        data.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR, 1f, 1f);
    }

    private boolean shouldProc(int level) {
        return PrisonCore.getInstance().getRandomBoolean(4 * Math.sin((level * Math.PI) / (2 * getMaxLevel())));
    }

    @TestOnly
    public static double calculateChance(int level) {
        return 4 * Math.sin((level * Math.PI) / (2 * 100));
    }
}
