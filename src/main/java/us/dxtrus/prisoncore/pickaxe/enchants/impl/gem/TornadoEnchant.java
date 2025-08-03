package us.dxtrus.prisoncore.pickaxe.enchants.impl.gem;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import us.dxtrus.commons.utils.TaskManager;
import us.dxtrus.prisoncore.PrisonCore;
import us.dxtrus.prisoncore.mine.models.Mine;
import us.dxtrus.prisoncore.mine.models.MineMaterial;
import us.dxtrus.prisoncore.pickaxe.PickaxeManager;
import us.dxtrus.prisoncore.pickaxe.enchants.impl.gem.tornado.TornadoRunnable;
import us.dxtrus.prisoncore.pickaxe.enchants.models.Enchant;
import us.dxtrus.prisoncore.pickaxe.enchants.models.EnchantInfo;
import us.dxtrus.prisoncore.pickaxe.enchants.models.EnchantTriggerData;
import us.dxtrus.prisoncore.pickaxe.enchants.models.GemEnchant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
        if (!shouldProc(data.getLevel()) && !checkForceProc(data.getPlayer().getUniqueId())) return;
        removeForceProc(data.getPlayer().getUniqueId());

        int height = (int) calcHeight(data.getLevel());
        int radius = (int) calcRadius(data.getLevel());

        startTornado(data.getPlayer().getUniqueId(), event.getBlock().getLocation(), data.getMine().getLinkage().getMaterials(), height, radius);

        removeBlocks(data.getPlayer(), data.getMine().getLinkage(), event.getBlock().getLocation(), height, radius);

        TaskManager.runSyncDelayed(PrisonCore.getInstance(),
                () -> stopTornado(data.getPlayer().getUniqueId()), 130L);
    }

    private void startTornado(UUID player, Location center, List<MineMaterial> blockTypes, int height, int baseRadius) {
        stopTornado(player);

        TornadoRunnable tornadoRunnable = new TornadoRunnable(center, blockTypes, height, baseRadius);
        armourStandsRunnable.put(player, tornadoRunnable);
        tornadoRunnable.runTaskTimer(PrisonCore.getInstance(), 0, 1);
    }

    public void stopAllTornadoes() {
        armourStandsRunnable.forEach((uuid, runnable) -> {
            runnable.stop();
        });

        armourStandsRunnable.clear();
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
        if (PrisonCore.getInstance().getRandom().nextDouble() < breakChance && !Material.AIR.equals(block.getType())) {
            block.setType(Material.AIR);
            return true;
        }
        return false;
    }

    private boolean shouldProc(int level) {
        return PrisonCore.getInstance().getRandomBoolean(0.5 * Math.sin((level * Math.PI) / (2 * getMaxLevel())));
    }

    private double calcHeight(int level) {
        return 30 * Math.sin((level * Math.PI) / (2 * getMaxLevel()));
    }

    private double calcRadius(int level) {
        return 15 * Math.sin((level * Math.PI) / (2 * getMaxLevel()));
    }
}
