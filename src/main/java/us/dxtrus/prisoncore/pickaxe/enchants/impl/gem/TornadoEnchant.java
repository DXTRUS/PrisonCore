package us.dxtrus.prisoncore.pickaxe.enchants.impl.gem;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import us.dxtrus.commons.utils.TaskManager;
import us.dxtrus.prisoncore.PrisonCore;
import us.dxtrus.prisoncore.mine.models.Mine;
import us.dxtrus.prisoncore.mine.models.MineMaterial;
import us.dxtrus.prisoncore.pickaxe.PickaxeManager;
import us.dxtrus.prisoncore.pickaxe.enchants.models.EnchantInfo;
import us.dxtrus.prisoncore.pickaxe.enchants.models.EnchantTriggerData;
import us.dxtrus.prisoncore.pickaxe.enchants.models.GemEnchant;
import us.dxtrus.prisoncore.util.RandomSelector;

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
    private final Map<UUID, List<UUID>> armourStands = new HashMap<>();

    @Override
    public void trigger(EnchantTriggerData data) {
        if (!(data.getCallingEvent() instanceof BlockBreakEvent event)) return;
        if (!shouldProc(data.getLevel())) return;
        startTornado(data.getPlayer().getUniqueId(), event.getBlock().getLocation(), data.getMine().getLinkage().getMaterials(), 20, 10);
        removeBlocks(data.getPlayer(), data.getMine().getLinkage(), event.getBlock().getLocation(),20, 10);
        TaskManager.runSyncDelayed(PrisonCore.getInstance(),
                () -> stopTornado(data.getPlayer().getUniqueId(), event.getBlock().getWorld()), 130L);
    }

    private void startTornado(UUID player, Location center, List<MineMaterial> blockTypes, int height, int baseRadius) {
        List<UUID> ars = new ArrayList<>();

        RandomSelector<MineMaterial> sel = RandomSelector.weighted(blockTypes, MineMaterial::getPercentage);
        for (int i = 0; i < height; i++) {
            double currentRadius = baseRadius * (1 - (double) i / (height - 1));
            int numStands = 25;
            for (int j = 0; j < numStands; j++) {
                double angle = (Math.PI * 2 / numStands) * j;
                double x = currentRadius * Math.cos(angle);
                double z = currentRadius * Math.sin(angle);
                Location loc = center.clone().add(x, -i + 20, z);

                ArmorStand armorStand = spawnArmorStand(center.getWorld(), loc, sel.next(PrisonCore.getInstance().getRandom()).getMaterial());
                ars.add(armorStand.getUniqueId());
            }
        }

        stopTornado(player, center.getWorld());
        armourStands.put(player, ars);

        new BukkitRunnable() {
            double angle = 0;
            final double wobbleFrequency = 0.3;
            final double wobbleAmplitude = 2.0;
            final double spinSpeed = Math.PI / 32;

            @Override
            public void run() {
                if (ars.isEmpty()) {
                    cancel();
                    return;
                }

                angle += Math.PI / 16;

                for (int i = 0; i < ars.size(); i++) {
                    ArmorStand armorStand = (ArmorStand) center.getWorld().getEntity(ars.get(i));
                    if (armorStand == null) continue;

                    int layer = i / 16;
                    double currentRadius = baseRadius * (1 - (double) layer / (height - 1));
                    double currentAngle = angle + (Math.PI * 2 / 16) * (i % 16);

                    double wobbleX = wobbleAmplitude * Math.sin(angle * wobbleFrequency + i);
                    double wobbleZ = wobbleAmplitude * Math.cos(angle * wobbleFrequency + i);

                    double x = currentRadius * Math.cos(currentAngle) + wobbleX;
                    double z = currentRadius * Math.sin(currentAngle) + wobbleZ;

                    if (layer == 0) {
                        double spinAngle = angle * spinSpeed;
                        x = currentRadius * Math.cos(currentAngle + spinAngle) + wobbleX;
                        z = currentRadius * Math.sin(currentAngle + spinAngle) + wobbleZ;
                    }

                    Location loc = center.clone().add(x, -layer + 20, z);
                    armorStand.teleportAsync(loc);
                }
            }
        }.runTaskTimer(PrisonCore.getInstance(), 0, 1);
    }

    private ArmorStand spawnArmorStand(World world, Location location, Material blockType) {
        ArmorStand armorStand = world.spawn(location, ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setItem(EquipmentSlot.HEAD, new ItemStack(blockType));
        armorStand.setGravity(false);
        armorStand.setMarker(true);
        return armorStand;
    }

    private void removeBlocks(Player player, Mine mine, Location center, int height, int baseRadius) {
        World world = center.getWorld();
        int blocksBroken = 0;

        for (int y = 0; y < height; y++) {
            double currentRadius = baseRadius * (1 - (double) y / height);
            int radiusCeil = (int) Math.ceil(currentRadius);

            for (int x = -radiusCeil; x <= radiusCeil; x++) {
                for (int z = -radiusCeil; z <= radiusCeil; z++) {
                    Location blockLoc = center.clone().add(x, -y, z);
                    double distanceSquared = x * x + z * z;
                    
                    if (distanceSquared <= currentRadius * currentRadius) {
                        Block block = world.getBlockAt(blockLoc);
                        if (!mine.getBounds().contains(blockLoc)) continue;
                        if (PrisonCore.getInstance().getRandom().nextDouble() < (1 - (double) y / height)) {
                            block.setType(Material.AIR);
                            blocksBroken += 1;
                        }
                    }
                }
            }
        }

        PickaxeManager.incrementExp(player, blocksBroken);
        mine.incrementBroken(blocksBroken);
    }

    private void stopTornado(UUID player, World world) {
        List<UUID> ars = armourStands.get(player);
        if (ars == null) return;
        ars.forEach(stand -> {
            Entity entity = world.getEntity(stand);
            if (entity == null) return;
            entity.remove();
        });
    }

    private boolean shouldProc(int level) {
        return PrisonCore.getInstance().getRandomBoolean(1 * Math.sin((level * Math.PI) / (2 * getMaxLevel())));
    }
}
