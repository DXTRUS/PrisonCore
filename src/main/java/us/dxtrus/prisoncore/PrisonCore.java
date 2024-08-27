package us.dxtrus.prisoncore;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.clip.placeholderapi.PlaceholderAPI;
import net.dxtrus.scoreboard.DexterousBoard;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import us.dxtrus.commons.command.BukkitCommandManager;
import us.dxtrus.commons.gui.FastInvManager;
import us.dxtrus.commons.utils.StringUtils;
import us.dxtrus.commons.utils.TaskManager;
import us.dxtrus.prisoncore.commands.AdminCommand;
import us.dxtrus.prisoncore.commands.CommandMine;
import us.dxtrus.prisoncore.mine.Mine;
import us.dxtrus.prisoncore.stats.Statistics;
import us.dxtrus.prisoncore.stats.StatsManager;
import us.dxtrus.prisoncore.stats.papi.Placeholders;
import us.dxtrus.prisoncore.util.Cuboid;
import us.dxtrus.prisoncore.util.Util;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;

public final class PrisonCore extends JavaPlugin implements Listener {
    @Getter private static PrisonCore instance;
    @Getter private Random random;

    ////////// DEBUG UUID 5f087be3-d369-4a5a-b9e2-6ae48affb534
    @Getter private Mine debugMine;
    private Jackhammer debugJackhammer;
    private UUID debugUUID = UUID.fromString("5f087be3-d369-4a5a-b9e2-6ae48affb534");

    @Override
    public void onEnable() {
        instance = this;

        FastInvManager.register(this);

        Stream.of(
                new CommandMine(this),
                new AdminCommand(this)
        ).forEach(BukkitCommandManager.getInstance()::registerCommand);

        new Placeholders().register();
        Bukkit.getPluginManager().registerEvents(this, this);

//        DEBUG

        World debugWorld = Bukkit.getWorld("world");
        Location topLeft = new Location(debugWorld, 189f, 54f, 23f);
        Location bottomRight = new Location(debugWorld, 144f, -29f, -22f);

        debugMine = new Mine(debugUUID, debugWorld.getUID(), topLeft.toVector(), bottomRight.toVector(), new Vector(135, 55, 0), new Vector(0,0,0));
        debugMine.init();
        debugJackhammer = new Jackhammer(debugMine);
    }


//    ECONOMY
    // todo: remove this. its debug code
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!debugMine.getBounds().contains(event.getBlock()) || !event.getPlayer().getGameMode().equals(GameMode.SURVIVAL)) {
            return;
        }

        if (!Util.anyMaterial(event.getPlayer().getInventory().getItemInMainHand().getType(), Material.NETHERITE_PICKAXE, Material.DIAMOND_PICKAXE, Material.IRON_PICKAXE,  Material.GOLDEN_PICKAXE, Material.STONE_PICKAXE, Material.WOODEN_PICKAXE)) {
            event.setCancelled(true);
            return;
        }

        if (!event.getPlayer().getAllowFlight()) {
            event.getPlayer().setAllowFlight(true);
        }

        if (random == null) {
            random = new Random();
        }

        Statistics stats =  StatsManager.getInstance().getStatistics(event.getPlayer().getUniqueId());
        stats.giveTokens(new BigDecimal(String.valueOf(random.nextInt(50000) + 1)));
        stats.incrementBrokenBlocks();
        debugMine.incrementBroken();

        if (random.nextInt(100) < 2) {
            stats.giveGems(random.nextInt(10) + 1);
        }

        if (debugJackhammer.proc()) {
            Location tl = debugMine.getBounds().getLowerNE();
            tl = tl.clone();
            tl.setY(event.getBlock().getY());

            Location br = debugMine.getBounds().getUpperSW();
            br = br.clone();
            br.setY(event.getBlock().getY());

            Cuboid jhCuboid = new Cuboid(tl, br);
            class Broken {
                int count = 0;
            }

            Broken broken = new Broken();
            jhCuboid.forEach(block -> {
                broken.count += 1;
                block.setType(Material.AIR);
            });

            debugMine.incrementBroken(broken.count);
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR, 1f, 1f);
        }

        TaskManager.runAsync(this, () -> DexterousBoard.getInstance().getManager().getScoreboard(event.getPlayer()).update());
        //event.getPlayer().sendActionBar(PlaceholderAPI.setPlaceholders(event.getPlayer(), StringUtil.tl("&c" + String.format("%.2f", debugMine.percentageBroken()) + " (" + debugMine.getBroken() + " / " + debugMine.getTotal() + ")")));
        event.getPlayer().sendActionBar(StringUtils.modernMessage(PlaceholderAPI.setPlaceholders(event.getPlayer(), "&e⛃ " + stats.getTokens().toString() + " (⛃ %prisoncore_tokens%)")));
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    private static class Jackhammer {
        private static Jackhammer instance;
        private Mine mine;

        Jackhammer(Mine mine) {
            this.mine = mine;
        }

        private double procChance = 0.03d, procAltChance = 0.1;

        public boolean proc() {
            return mine.getSeed().nextDouble() <= procChance;
        }
    }
}
