package us.dxtrus.prisoncore;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import us.dxtrus.commons.command.BukkitCommandManager;
import us.dxtrus.commons.gui.FastInvManager;
import us.dxtrus.commons.utils.BungeeMessenger;
import us.dxtrus.prisoncore.commands.AdminCommand;
import us.dxtrus.prisoncore.commands.CommandMine;
import us.dxtrus.prisoncore.config.Config;
import us.dxtrus.prisoncore.config.Lang;
import us.dxtrus.prisoncore.hooks.PAPIHook;
import us.dxtrus.prisoncore.listeners.MineListener;
import us.dxtrus.prisoncore.listeners.PlayerListener;
import us.dxtrus.prisoncore.mine.LocalMineManager;
import us.dxtrus.prisoncore.mine.MineManager;
import us.dxtrus.prisoncore.mine.network.HeartBeat;
import us.dxtrus.prisoncore.mine.network.ServerManager;
import us.dxtrus.prisoncore.mine.network.TransferManager;
import us.dxtrus.prisoncore.mine.network.broker.Broker;
import us.dxtrus.prisoncore.mine.network.broker.RedisBroker;
import us.dxtrus.prisoncore.storage.StorageManager;

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
                new PlayerListener(this),
                new MineListener()
        ).forEach(e -> Bukkit.getPluginManager().registerEvents(e, this));

        new PAPIHook().register();
    }

    @Override
    public void onDisable() {
        broker.destroy();
        StorageManager.getInstance().shutdown();
    }
}
