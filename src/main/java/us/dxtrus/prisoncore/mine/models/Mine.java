package us.dxtrus.prisoncore.mine.models;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import us.dxtrus.prisoncore.PrisonCore;
import us.dxtrus.prisoncore.config.Config;
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
        this.bounds = bounds.expand(Cuboid.CuboidDirection.Down, 60);

        this.mineCenter = center;

        materials = Config.getInstance().getRanks().getBlocks().getOrDefault(linkage.getLevel(), List.of(new MineMaterial(Material.STONE, 100)));

        bounds.forEach(block -> total += 1);
    }

    public void reset() {
        this.materials = Config.getInstance().getRanks().getBlocks().getOrDefault(linkage.getLevel(), List.of(new MineMaterial(Material.STONE, 100)));
        this.random = RandomSelector.weighted(materials, MineMaterial::getPercentage);
        Bukkit.getOnlinePlayers().stream()
                .filter(player -> bounds.contains(player.getLocation()))
                .forEach(player -> player.teleport(new Location(world,
                        linkage.getSpawnLocation().getX() + 0.5d,
                        linkage.getSpawnLocation().getY(),
                        linkage.getSpawnLocation().getZ() + 0.5d, 90, 0)));


        bounds.forEach(block ->
                block.setType(random.next(PrisonCore.getInstance().getRandom()).getMaterial(), false));
        broken = 0;
    }

    public void setWalls(int level) {
        Cuboid c = getBounds().outset(Cuboid.CuboidDirection.Both, level + 1);

        Cuboid a = c.getFace(Cuboid.CuboidDirection.Down).expand(Cuboid.CuboidDirection.Down, 2);
        a.forEach(block -> block.setType(Material.PURPLE_STAINED_GLASS));
        a.expand(Cuboid.CuboidDirection.Down, 1).getFace(Cuboid.CuboidDirection.Down).forEach(block -> block.setType(Material.SEA_LANTERN));
        c.getFace(Cuboid.CuboidDirection.North).expand(Cuboid.CuboidDirection.Down, 3).forEach(block -> block.setType(Material.BEDROCK));
        c.getFace(Cuboid.CuboidDirection.East).expand(Cuboid.CuboidDirection.Down, 3).forEach(block -> block.setType(Material.BEDROCK));
        c.getFace(Cuboid.CuboidDirection.South).expand(Cuboid.CuboidDirection.Down, 3).forEach(block -> block.setType(Material.BEDROCK));
        c.getFace(Cuboid.CuboidDirection.West).expand(Cuboid.CuboidDirection.Down, 3).forEach(block -> block.setType(Material.BEDROCK));
        c.getFace(Cuboid.CuboidDirection.Up).expand(Cuboid.CuboidDirection.Up, 1).expand(Cuboid.CuboidDirection.Down, 1).forEach(block -> block.setType(Material.AIR));


        c.getFace(Cuboid.CuboidDirection.Up).expand(Cuboid.CuboidDirection.Up, 1).expand(Cuboid.CuboidDirection.Down, 1).forEach(block -> block.setType(Material.AIR));
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