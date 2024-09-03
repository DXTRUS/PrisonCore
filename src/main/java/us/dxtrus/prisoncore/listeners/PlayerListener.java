package us.dxtrus.prisoncore.listeners;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import us.dxtrus.commons.utils.TaskManager;
import us.dxtrus.prisoncore.mine.MineManager;
import us.dxtrus.prisoncore.mine.network.TransferManager;
import us.dxtrus.prisoncore.pickaxe.PickaxeManager;

@RequiredArgsConstructor
public class PlayerListener implements Listener {
    private final JavaPlugin plugin;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        TaskManager.runAsync(plugin, () -> TransferManager.getInstance().checkout(player));
        if (!player.hasPlayedBefore()) {
            PickaxeManager.givePickaxe(player);
        }
        PickaxeManager.populateCache(player);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        MineManager.getInstance().unload(MineManager.getInstance().getMine(player.getUniqueId()));
    }
}
