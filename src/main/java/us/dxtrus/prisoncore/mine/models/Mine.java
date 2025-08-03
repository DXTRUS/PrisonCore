package us.dxtrus.prisoncore.mine.models;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import us.dxtrus.prisoncore.PrisonCore;
import us.dxtrus.prisoncore.config.Config;
import us.dxtrus.prisoncore.stats.Statistics;
import us.dxtrus.prisoncore.util.RandomSelector;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

@Getter
public class Mine {
    private final PrivateMine linkage;
    private Cuboid bounds;
    private final World world;
    private final LocRef mineCenter;
    @Setter private List<MineMaterial> materials;
    private RandomSelector<MineMaterial> random;
    private int size;
    private LocRef center;

    private int broken = 0;
    @Getter @Setter private int total = 0;

    private final double resetAtPercentageBroken = 0.80d;

    public Mine(@NotNull PrivateMine linkage, @NotNull LocRef center, int size) {
        this.linkage = linkage;
        this.size = size;
        this.center = center;
        this.world = Bukkit.getWorld(linkage.getWorldName());
        this.bounds = new Cuboid(center.toBukkit(world));

        remakeBounds();
        this.mineCenter = center;
        materials = Config.getInstance().getRanks().getBlocks().getOrDefault(linkage.getLevel(), List.of(new MineMaterial(Material.STONE, 100)));


    }

    public void remakeBounds() {
        size = getLinkage().determineSize();

        this.bounds = new Cuboid(center.toBukkit(world));
        this.bounds = bounds.outset(Cuboid.CuboidDirection.Horizontal, size / 2);
        this.bounds = bounds.expand(Cuboid.CuboidDirection.Down, 60);

        bounds.forEach(block -> total += 1);

        setWalls();
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

    @Deprecated(forRemoval = true)
    public void setWallsOld() {
        Cuboid c = getBounds().outset(Cuboid.CuboidDirection.Both, linkage.getLevel() + 1);
        Cuboid a = c.getFace(Cuboid.CuboidDirection.Down).expand(Cuboid.CuboidDirection.Down, 2);
        a.forEach(block -> block.setType(Material.PURPLE_STAINED_GLASS));
        a.expand(Cuboid.CuboidDirection.Down, 1).getFace(Cuboid.CuboidDirection.Down).forEach(block -> block.setType(Material.SEA_LANTERN));

        c.getFace(Cuboid.CuboidDirection.North).expand(Cuboid.CuboidDirection.Down, 3).forEach(block -> block.setType(Material.BEDROCK));
        c.getFace(Cuboid.CuboidDirection.East).expand(Cuboid.CuboidDirection.Down, 3).forEach(block -> block.setType(Material.BEDROCK));
        c.getFace(Cuboid.CuboidDirection.South).expand(Cuboid.CuboidDirection.Down, 3).forEach(block -> block.setType(Material.BEDROCK));
        c.getFace(Cuboid.CuboidDirection.West).expand(Cuboid.CuboidDirection.Down, 3).forEach(block -> block.setType(Material.BEDROCK));
        c.getFace(Cuboid.CuboidDirection.Up).expand(Cuboid.CuboidDirection.Up, 1).expand(Cuboid.CuboidDirection.Down, 1).forEach(block -> block.setType(Material.AIR));
        c.getFace(Cuboid.CuboidDirection.Up).expand(Cuboid.CuboidDirection.Up, 1).expand(Cuboid.CuboidDirection.Down, 1).forEach(block -> block.setType(Material.AIR));

        double lZ = getBounds().getLowerZ() - 2;
        double uZ = getBounds().getUpperZ() + 2;
        Location l1 = new Location(getWorld(), getBounds().getUpperX() + 3, 64d, lZ);
        Location l2 = new Location(getWorld(), getBounds().getUpperX() + 12 , 64d, uZ);

        Cuboid cb = new Cuboid(l1, l2);
        cb.forEach(block -> block.setType(Material.BEDROCK));
    }

//    public void setWalls() {
//        Cuboid bounds = getBounds();
//        int expansion = 1;
//        Cuboid c = bounds.outset(Cuboid.CuboidDirection.Both, expansion);
//
//        Cuboid floor = c.getFace(Cuboid.CuboidDirection.Down).expand(Cuboid.CuboidDirection.Down, 2);
//        floor.forEach(block -> block.setType(Material.PURPLE_STAINED_GLASS));
//
//        floor.expand(Cuboid.CuboidDirection.Down, 1)
//                .getFace(Cuboid.CuboidDirection.Down)
//                .forEach(block -> block.setType(Material.SEA_LANTERN));
//
//        Stream.of(
//                Cuboid.CuboidDirection.North,
//                Cuboid.CuboidDirection.East,
//                Cuboid.CuboidDirection.South,
//                Cuboid.CuboidDirection.West
//        ).forEach(dir ->
//                c.getFace(dir)
//                        .expand(Cuboid.CuboidDirection.Down, 3)
//                        .forEach(block -> block.setType(Material.BEDROCK))
//        );
//
//        c.getFace(Cuboid.CuboidDirection.Up)
//                .expand(Cuboid.CuboidDirection.Up, 1)
//                .expand(Cuboid.CuboidDirection.Down, 1)
//                .forEach(block -> block.setType(Material.AIR));
//
//        double lowerZ = bounds.getLowerZ() - 2;
//        double upperZ = bounds.getUpperZ() + 2;
//        Location l1 = new Location(getWorld(), bounds.getUpperX() + 3, 64d, lowerZ);
//        Location l2 = new Location(getWorld(), bounds.getUpperX() + 12, 64d, upperZ);
//        new Cuboid(l1, l2).forEach(block -> block.setType(Material.BEDROCK));
//    }

    public void setWalls() {
        Cuboid bounds = getBounds();

        Location l1 = bounds.getUpperSW(); // (-x, +z)
        Location l2 = bounds.getLowerNE(); // (+x, -z);

        Cuboid increased = new Cuboid(l1.add(-1d, 0d, 1d), l2.add(1d, 0d, -1d));
        Cuboid walls = new Cuboid(l1.add(-2d, 0d, 2d), l2.add(2d, 0d, -2d));

        Cuboid[] faces = new Cuboid[12];
        Cuboid.CuboidDirection[] dirs = new Cuboid.CuboidDirection[] {
                Cuboid.CuboidDirection.North,
                Cuboid.CuboidDirection.South,
                Cuboid.CuboidDirection.East,
                Cuboid.CuboidDirection.West,
                Cuboid.CuboidDirection.Up,
                Cuboid.CuboidDirection.Down
        };

        for (int i = 0, j = 0; i < faces.length / 2; i++) {
            faces[i] = increased.getFace(dirs[j]);
            faces[i + 6] = walls.getFace(dirs[j]);

            if (i % 2 == 0) j++;
        }

        Stream.of(
                faces[0],
                faces[1],
                faces[2],
                faces[3]
        ).forEach(cuboid -> cuboid.faweSetAll(Material.AIR));
        Stream.of(
                faces[6],
                faces[7],
                faces[8],
                faces[9]
        ).forEach(cuboid -> cuboid.faweSetAll(Material.AIR));

        faces[11].expand(Cuboid.CuboidDirection.Down, 1).faweSetAll(Material.PURPLE_STAINED_GLASS);
        faces[11].shift(Cuboid.CuboidDirection.Down, 2).faweSetAll(Material.SEA_LANTERN);
    }

    public void incrementBroken() {
        incrementBroken(1);
    }

    public void incrementBroken(int count) {
        broken += count;
        linkage.getPlayerStats().incrementBrokenBlocks(count);
        if ((double)broken / (double) total >= resetAtPercentageBroken) {
            reset();
        }
    }

    public double percentageBroken() {
        return (double) broken / (double) total;
    }
}