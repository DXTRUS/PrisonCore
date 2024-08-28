package us.dxtrus.prisoncore.mine;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import us.dxtrus.commons.utils.StringUtils;
import us.dxtrus.commons.utils.TaskManager;
import us.dxtrus.prisoncore.PrisonCore;
import us.dxtrus.prisoncore.config.Lang;
import us.dxtrus.prisoncore.util.Cuboid;
import us.dxtrus.prisoncore.util.RandomSelector;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Getter
@SuppressWarnings("all") // IntelliJ warnings can suck my hairy ballsack. IT WILL NOT BE FUCKING NULL.
public class Mine {
    private Cuboid bounds;
    private UUID owner;
    private String worldUuid;
    private World world;
    private Vector spawn;
    private Vector mineCenter;
    @Setter private List<MineMaterial> materials;
    private RandomSelector<MineMaterial> random;
    private Random seed;

    private int broken = 0;
    private int total = 0;

    private double resetAtPercentageBroken = 0.80d;

    public Mine(@NotNull UUID owner, @NotNull String world, @NotNull Vector topLeft, @NotNull Vector bottomRight, @NotNull Vector spawn, @NotNull Vector center) {
        this.owner = owner;
        this.worldUuid = world;
        this.world = Bukkit.getWorld(world);
        this.bounds = new Cuboid(
                new Location(this.world, topLeft.getBlockX(), topLeft.getBlockY(), topLeft.getBlockZ()),
                new Location(this.world, bottomRight.getBlockX(), bottomRight.getBlockY(), bottomRight.getBlockZ())
        );

        this.spawn = spawn;
        this.mineCenter = center;

        seed = new Random((System.currentTimeMillis() / 1094216) * 10452);
        materials = Lists.newArrayList();

        bounds.forEach(block -> total += 1);
    }

    // DEBUG DATA
    public void init() {
        PrisonCore.getInstance().getLogger().info("Initialising mine for uuid " + owner + " (World UUID: " + worldUuid + ")");

        materials.add(new MineMaterial(75d, Material.NETHERITE_BLOCK));
        materials.add(new MineMaterial(20d, Material.ANCIENT_DEBRIS));
        materials.add(new MineMaterial(5d, Material.BLACK_CONCRETE));
        this.random = RandomSelector.weighted(materials, (material) -> material.percentage());

        reset();
    }

    public void reset() {
        Bukkit.getOnlinePlayers().stream()
                .filter(player -> bounds.contains(player.getLocation()))
                .forEach(player -> player.teleport(new Location(world, spawn.getBlockX() + 0.5d, spawn.getBlockY() + 1, spawn.getBlockZ() + 0.5d, -90f, 0f)));


        bounds.forEach(block ->
                block.setType(random.next(seed).material(), false));
        broken = 0;
    }


    public void enter(Player who) {
        if (!owner.equals(who.getUniqueId()) && !who.hasPermission("prisoncore.admin")) {
            who.sendMessage(StringUtils.modernMessage(Lang.getInstance().getCommand().getNoPermission()));
            return;
        }


        who.teleport(new Location(world, spawn.getBlockX() + 0.5d, spawn.getBlockY() + 1, spawn.getBlockZ() + 0.5d, -90f, 0f));
        who.setGameMode(GameMode.SURVIVAL);
        who.setAllowFlight(true);
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

    public Cuboid setWalls() {
        Cuboid c = getBounds().outset(Cuboid.CuboidDirection.Both, 2);
        c.getFace(Cuboid.CuboidDirection.Down).expand(Cuboid.CuboidDirection.Down, 1).forEach(block -> block.setType(Material.BEDROCK));
        c.getFace(Cuboid.CuboidDirection.North).forEach(block -> block.setType(Material.BEDROCK));
        c.getFace(Cuboid.CuboidDirection.East).forEach(block -> block.setType(Material.BEDROCK));
        c.getFace(Cuboid.CuboidDirection.South).forEach(block -> block.setType(Material.BEDROCK));
        c.getFace(Cuboid.CuboidDirection.West).forEach(block -> block.setType(Material.BEDROCK));

        c.getFace(Cuboid.CuboidDirection.Up).expand(Cuboid.CuboidDirection.Up, 1).expand(Cuboid.CuboidDirection.Down, 1).forEach(block -> block.setType(Material.AIR));
        return c;
    }

    public double percentageBroken() {
        return (double) broken / (double) total;
    }
}
