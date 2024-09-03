package us.dxtrus.prisoncore.pickaxe.enchants.impl.gem.tornado;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import us.dxtrus.prisoncore.PrisonCore;
import us.dxtrus.prisoncore.mine.models.MineMaterial;
import us.dxtrus.prisoncore.util.RandomSelector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TornadoRunnable extends BukkitRunnable {
    private final UUID player;
    private final Location center;
    private final List<MineMaterial> blockTypes;
    private final int height;
    private final int baseRadius;
    private final List<UUID> armorStands = new ArrayList<>();
    private double angle = 0;
    private final double wobbleFrequency = 0.3;
    private final double wobbleAmplitude = 2.0;
    private final double spinSpeed = Math.PI / 32;

    public TornadoRunnable(UUID player, Location center, List<MineMaterial> blockTypes, int height, int baseRadius) {
        this.player = player;
        this.center = center;
        this.blockTypes = blockTypes;
        this.height = height;
        this.baseRadius = baseRadius;
        spawnArmorStands();
    }

    private void spawnArmorStands() {
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
                armorStands.add(armorStand.getUniqueId());
            }
        }
    }

    private ArmorStand spawnArmorStand(World world, Location location, Material blockType) {
        ArmorStand armorStand = world.spawn(location, ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setItem(EquipmentSlot.HEAD, new ItemStack(blockType));
        armorStand.setGravity(false);
        armorStand.setMarker(true);
        return armorStand;
    }

    @Override
    public void run() {
        if (armorStands.isEmpty()) {
            cancel();
            return;
        }

        angle += Math.PI / 16;

        for (int i = 0; i < armorStands.size(); i++) {
            ArmorStand armorStand = (ArmorStand) center.getWorld().getEntity(armorStands.get(i));
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

    public void stop() {
        World world = center.getWorld();
        for (UUID standId : armorStands) {
            Entity entity = world.getEntity(standId);
            if (entity != null) {
                entity.remove();
            }
        }
        cancel();
    }
}
