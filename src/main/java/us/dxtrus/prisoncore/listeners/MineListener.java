package us.dxtrus.prisoncore.listeners;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import us.dxtrus.prisoncore.mine.MineManager;
import us.dxtrus.prisoncore.mine.models.PrivateMine;

import java.util.UUID;

public class MineListener implements Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        UUID playerId = player.getUniqueId();
        Location blockLocation = e.getBlock().getLocation();

        PrivateMine mine = MineManager.getInstance().getMine(playerId);
        boolean hasBypassPerms = player.hasPermission("prison.creative.bypass") && player.getGameMode() == GameMode.CREATIVE;

        // Cancel if not in the same world and doesn't have bypass
        if (!mine.getWorldName().equals(blockLocation.getWorld().getName()) && !hasBypassPerms) {
            e.setCancelled(true);
            return;
        }

        boolean inMineBounds = mine.getLinkage().getBounds().contains(blockLocation);

        // Cancel if breaking outside the mine without bypass OR inside the mine *with* bypass
        if (inMineBounds == hasBypassPerms) {
            e.setCancelled(true);
            return;
        }

        // Handle legitimate break inside mine
        if (!hasBypassPerms) {
            e.setDropItems(false);
            e.setExpToDrop(0);
            mine.getLinkage().incrementBroken();
        }
    }
}
