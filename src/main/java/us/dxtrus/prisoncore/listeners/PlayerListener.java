package us.dxtrus.prisoncore.listeners;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import us.dxtrus.commons.utils.TaskManager;
import us.dxtrus.prisoncore.mine.network.TransferManager;
import us.dxtrus.prisoncore.pickaxe.PickaxeManager;
import us.dxtrus.prisoncore.stats.StatsManager;

@RequiredArgsConstructor
public class PlayerListener implements Listener {
    private final JavaPlugin plugin;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        PickaxeManager.getJoining().add(event.getPlayer().getUniqueId());
        StatsManager.getInstance().load(event.getPlayer().getUniqueId());
        Player player = event.getPlayer();
        TaskManager.runAsync(plugin, () -> TransferManager.getInstance().checkout(player));
        PickaxeManager.populateCache(event.getPlayer());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        StatsManager.getInstance().save(event.getPlayer().getUniqueId());
        Player player = event.getPlayer();
        // todo: unload some other way, maybe add a global timer on each server to unload all mines if they are empty
        //MineManager.getInstance().unload(MineManager.getInstance().getMine(player.getUniqueId()));
    }

    @EventHandler
    public void commandPreProcess(PlayerCommandPreprocessEvent event) {
        String msg = event.getMessage();

        int spaceIndex = msg.indexOf(' ');
        if (spaceIndex == -1) {
            msg = msg.toLowerCase();
        } else {
            String command = msg.substring(0, spaceIndex).toLowerCase();
            String args = msg.substring(spaceIndex);
            msg = command + args;
        }

        event.setMessage(msg);
    }
}
