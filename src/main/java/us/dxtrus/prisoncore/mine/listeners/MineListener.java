package us.dxtrus.prisoncore.mine.listeners;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.dxtrus.scoreboard.DexterousBoard;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import us.dxtrus.commons.utils.TaskManager;
import us.dxtrus.prisoncore.PrisonCore;
import us.dxtrus.prisoncore.mine.Mine;
import us.dxtrus.prisoncore.slime.SlimeManager;
import us.dxtrus.prisoncore.stats.Statistics;
import us.dxtrus.prisoncore.stats.StatsManager;
import us.dxtrus.prisoncore.util.Cuboid;

public class MineListener implements Listener {

    private Jackhammer debugJackhammer;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        Mine mine = SlimeManager.getInstance().getMine(event.getPlayer());
        if (mine == null) {
            return;
        }

        if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            return;
        }

        if (!mine.getBounds().contains(event.getBlock()) && event.getPlayer().getWorld().equals(mine.getWorld())) {
            event.setCancelled(true);
            return;
        }

        event.setDropItems(false);

        if (!mine.getBounds().contains(event.getBlock())) {
            event.setCancelled(true);
            return;
        }

        mine.incrementBroken();
        Statistics stats = StatsManager.getInstance().getStatistics(event.getPlayer().getUniqueId());
        stats.giveTokens(mine.getSeed().nextInt(50000));
        stats.incrementBrokenBlocks();

        TaskManager.runAsync(PrisonCore.getInstance(), () -> DexterousBoard.getInstance().getManager().getScoreboard(event.getPlayer()).update());

        if (debugJackhammer == null) {
            debugJackhammer = new Jackhammer();
        }
        if (debugJackhammer.proc(mine)) {
            Location tl = mine.getBounds().getLowerNE();
            tl = tl.clone();
            tl.setY(event.getBlock().getY());

            Location br = mine.getBounds().getUpperSW();
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

            mine.incrementBroken(broken.count);
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR, 1f, 1f);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player && player.getWorld().getName().startsWith("mine_")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player player && player.getWorld().getName().startsWith("mine_")) {
            event.setFoodLevel(20);
            player.setSaturation(20);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    private static class Jackhammer {
        private static Jackhammer instance;

        private double procChance = 0.03d, procAltChance = 0.1;

        public boolean proc(Mine mine) {
            return mine.getSeed().nextDouble() <= procChance;
        }
    }

}
