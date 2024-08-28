package us.dxtrus.prisoncore.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import us.dxtrus.prisoncore.mine.MineManager;
import us.dxtrus.prisoncore.mine.models.PrivateMine;

public class MineListener implements Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        PrivateMine mine = MineManager.getInstance().getMine(e.getPlayer().getUniqueId());
        if (mine.getWorldName().equals(e.getBlock().getWorld().getName())) {
            e.setCancelled(true);
            return;
        }

        e.setDropItems(false);
        e.setExpToDrop(0);

        mine.getLinkage().incrementBroken();
    }
}
