package us.dxtrus.prisoncore.mine.models;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import us.dxtrus.prisoncore.PrisonCore;
import us.dxtrus.prisoncore.config.Config;
import us.dxtrus.prisoncore.util.LogUtil;
import us.dxtrus.prisoncore.util.RandomSelector;

import java.util.List;

@Getter
public class Mine {
    private final PrivateMine linkage;
    private Cuboid bounds;
    private final World world;
    private final LocRef mineCenter;
    @Setter private List<MineMaterial> materials;
    private RandomSelector<MineMaterial> random;

    private int broken = 0;
    private int total = 0;

    private final double resetAtPercentageBroken = 0.80d;

    public Mine(@NotNull PrivateMine linkage, @NotNull LocRef center, int size) {
        this.linkage = linkage;
        this.world = Bukkit.getWorld(linkage.getWorldName());
        this.bounds = new Cuboid(center.toBukkit(world));

        this.bounds = bounds.expand(Cuboid.CuboidDirection.North, size);
        this.bounds = bounds.expand(Cuboid.CuboidDirection.South, size);
        this.bounds = bounds.expand(Cuboid.CuboidDirection.East, size);
        this.bounds = bounds.expand(Cuboid.CuboidDirection.West, size);
        this.bounds = bounds.expand(Cuboid.CuboidDirection.Down, 100);

        this.mineCenter = center;

        materials = Config.getInstance().getRanks().get(1);

        bounds.forEach(block -> total += 1);
    }

    public void reset() {
        this.materials = Config.getInstance().getRanks().get(1);
        this.random = RandomSelector.weighted(materials, MineMaterial::getPercentage);
        Bukkit.getOnlinePlayers().stream()
                .filter(player -> bounds.contains(player.getLocation()))
                .forEach(player -> player.teleport(new Location(world,
                        linkage.getSpawnLocation().getX() + 0.5d,
                        linkage.getSpawnLocation().getY(),
                        linkage.getSpawnLocation().getZ() + 0.5d)));


        bounds.forEach(block ->
                block.setType(random.next(PrisonCore.getInstance().getRandom()).getMaterial(), false));
        broken = 0;
    }

    public void incrementBroken() {
        incrementBroken(1);
    }

    public void incrementBroken(int count) {
        broken += count;
        if ((double)broken / (double) total >= resetAtPercentageBroken) {
            reset();
        }
    }

    public double percentageBroken() {
        return (double) broken / (double) total;
    }
}