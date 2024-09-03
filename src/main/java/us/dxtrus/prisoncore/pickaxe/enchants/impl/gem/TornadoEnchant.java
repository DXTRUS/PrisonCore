package us.dxtrus.prisoncore.pickaxe.enchants.impl.gem;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import us.dxtrus.commons.utils.TaskManager;
import us.dxtrus.prisoncore.PrisonCore;
import us.dxtrus.prisoncore.mine.models.Mine;
import us.dxtrus.prisoncore.mine.models.MineMaterial;
import us.dxtrus.prisoncore.pickaxe.PickaxeManager;
import us.dxtrus.prisoncore.pickaxe.enchants.impl.gem.tornado.TornadoRunnable;
import us.dxtrus.prisoncore.pickaxe.enchants.models.EnchantInfo;
import us.dxtrus.prisoncore.pickaxe.enchants.models.EnchantTriggerData;
import us.dxtrus.prisoncore.pickaxe.enchants.models.GemEnchant;

import java.util.*;

@EnchantInfo(
        id = "tornado",
        name = "&fTornado",
        description = {
                "&fSpawns a tornado in your mine",
                "&fand sucks up blocks to sell!"
        },
        icon = Material.BONE_MEAL,
        maxLevel = 100
)
public class TornadoEnchant extends GemEnchant {
    private final Map<UUID, TornadoRunnable> armourStandsRunnable = new HashMap<>();

    @Override
    public void trigger(EnchantTriggerData data) {
        if (!(data.getCallingEvent() instanceof BlockBreakEvent event)) return;
        if (!shouldProc(data.getLevel())) return;

        startTornado(data.getPlayer().getUniqueId(), event.getBlock().getLocation(), data.getMine().getLinkage().getMaterials(), 20, 10);

        removeBlocks(data.getPlayer(), data.getMine().getLinkage(), event.getBlock().getLocation(), 20, 10);

        TaskManager.runSyncDelayed(PrisonCore.getInstance(),
                () -> stopTornado(data.getPlayer().getUniqueId()), 130L);
    }

    private void startTornado(UUID player, Location center, List<MineMaterial> blockTypes, int height, int baseRadius) {
        stopTornado(player);

        TornadoRunnable tornadoRunnable = new TornadoRunnable(player, center, blockTypes, height, baseRadius);
        armourStandsRunnable.put(player, tornadoRunnable);
        tornadoRunnable.runTaskTimer(PrisonCore.getInstance(), 0, 1);
    }

    private void stopTornado(UUID player) {
        TornadoRunnable tornadoRunnable = armourStandsRunnable.get(player);
        if (tornadoRunnable != null) {
            tornadoRunnable.stop();
            armourStandsRunnable.remove(player);
        }
    }

    private void removeBlocks(Player player, Mine mine, Location center, int height, int baseRadius) {
        int blocksBroken = 0;

        for (int y = 0; y < height; y++) {
            double currentRadius = baseRadius * (1 - (double) y / height);
            int radiusCeil = (int) Math.ceil(currentRadius);

            for (int x = -radiusCeil; x <= radiusCeil; x++) {
                for (int z = -radiusCeil; z <= radiusCeil; z++) {
                    if (x * x + z * z <= currentRadius * currentRadius) {
                        Location blockLoc = center.clone().add(x, -y, z);
                        blocksBroken += tryBreakBlock(mine, blockLoc, y, height) ? 1 : 0;
                    }
                }
            }
        }

        PickaxeManager.incrementExp(player, blocksBroken);
        mine.incrementBroken(blocksBroken);
    }

    private boolean tryBreakBlock(Mine mine, Location blockLoc, int y, int height) {
        Block block = blockLoc.getWorld().getBlockAt(blockLoc);
        if (!mine.getBounds().contains(blockLoc)) return false;

        double breakChance = 1 - (double) y / height;
        if (PrisonCore.getInstance().getRandom().nextDouble() < breakChance) {
            block.setType(Material.AIR);
            return true;
        }
        return false;
    }

    private boolean shouldProc(int level) {
        return PrisonCore.getInstance().getRandomBoolean(1 * Math.sin((level * Math.PI) / (2 * getMaxLevel())));
    }
}
