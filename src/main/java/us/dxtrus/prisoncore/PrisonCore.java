package us.dxtrus.prisoncore;

import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import us.dxtrus.commons.command.BukkitCommand;
import us.dxtrus.commons.command.BukkitCommandManager;
import us.dxtrus.commons.gui.FastInvManager;
import us.dxtrus.commons.loader.LogManager;
import us.dxtrus.commons.utils.BungeeMessenger;
import us.dxtrus.prisoncore.commands.AdminCommand;
import us.dxtrus.prisoncore.commands.CommandMine;
import us.dxtrus.prisoncore.config.Config;
import us.dxtrus.prisoncore.config.Lang;
import us.dxtrus.prisoncore.eco.EconomyManager;
import us.dxtrus.prisoncore.eco.papi.PlaceholderTokens;
import us.dxtrus.prisoncore.listeners.PlayerListener;
import us.dxtrus.prisoncore.mine.LocalMineManager;
import us.dxtrus.prisoncore.mine.MineManager;
import us.dxtrus.prisoncore.mine.network.HeartBeat;
import us.dxtrus.prisoncore.mine.network.ServerManager;
import us.dxtrus.prisoncore.mine.network.TransferManager;
import us.dxtrus.prisoncore.mine.network.broker.Broker;
import us.dxtrus.prisoncore.mine.network.broker.RedisBroker;
import us.dxtrus.prisoncore.storage.StorageManager;
import us.dxtrus.prisoncore.util.StringUtil;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Random;
import java.util.stream.Stream;

@Getter
public final class PrisonCore extends JavaPlugin implements Listener {
    @Getter private static PrisonCore instance;
    private final Random random = new Random(System.currentTimeMillis());
    private final BungeeMessenger messenger = new BungeeMessenger(this);
    private Broker broker;

    @Override
    public void onEnable() {
        instance = this;

        broker = new RedisBroker(this);
        broker.connect();

        // Init all managers
        StorageManager.getInstance();
        Config.getInstance();
        Lang.getInstance();
        ServerManager.getInstance();
        LocalMineManager.getInstance();
        MineManager.getInstance();
        TransferManager.getInstance();
        HeartBeat.getInstance();

        FastInvManager.register(this);

        Stream.of(
                new CommandMine(this),
                new AdminCommand(this)
        ).forEach(BukkitCommandManager.getInstance()::registerCommand);

        Stream.of(
                new PlayerListener(this)
        ).forEach(e -> Bukkit.getPluginManager().registerEvents(e, this));

        new PlaceholderTokens().register();
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        broker.destroy();
        StorageManager.getInstance().shutdown();
    }

//    ECONOMY
    // todo: move this to a listeners class
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.getBlock().getType().equals(Material.AMETHYST_BLOCK)  || !event.getPlayer().getGameMode().equals(GameMode.SURVIVAL)) {
            return;
        }

       EconomyManager.Tokens tokens =  EconomyManager.getTokens(event.getPlayer().getUniqueId());
        tokens.give(new BigInteger("50000"));
        event.getPlayer().sendActionBar(PlaceholderAPI.setPlaceholders(event.getPlayer(), StringUtil.tl("&e⛃ %prisoncore_tokens%")));
    }
}
