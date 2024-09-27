package us.dxtrus.prisoncore.hooks;

import net.william278.husksync.event.BukkitSyncCompleteEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import us.dxtrus.prisoncore.pickaxe.PickaxeManager;

public class HuskSyncHook implements Listener {
    @EventHandler
    public void onDataSync(BukkitSyncCompleteEvent event) {
        Player player = (Player) event.getUser().getAudience();
        PickaxeManager.populateCache(player);
    }
}
