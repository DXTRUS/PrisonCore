package us.dxtrus.prisoncore;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import us.dxtrus.commons.command.BukkitCommandManager;
import us.dxtrus.commons.gui.FastInvManager;
import us.dxtrus.commons.utils.BungeeMessenger;
import us.dxtrus.prisoncore.commands.AdminCommand;
import us.dxtrus.prisoncore.commands.CommandMine;
import us.dxtrus.prisoncore.config.Config;
import us.dxtrus.prisoncore.config.Lang;
import us.dxtrus.prisoncore.pickaxe.PickaxeManager;
import us.dxtrus.prisoncore.pickaxe.enchants.EnchantManager;
import us.dxtrus.prisoncore.pickaxe.enchants.impl.gem.TornadoEnchant;
import us.dxtrus.prisoncore.pickaxe.enchants.impl.token.GemFinderEnchant;
import us.dxtrus.prisoncore.pickaxe.enchants.impl.token.JackhammerEnchant;
import us.dxtrus.prisoncore.pickaxe.listeners.ToolListeners;
import us.dxtrus.prisoncore.hooks.PAPIHook;
import us.dxtrus.prisoncore.listeners.MineListener;
import us.dxtrus.prisoncore.pickaxe.listeners.PickaxeListeners;
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
public final class PrisonCore extends JavaPlugin {
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
        PickaxeManager.startLoreUpdater();

        FastInvManager.register(this);

        Stream.of(
                new CommandMine(this),
                new AdminCommand(this)
        ).forEach(BukkitCommandManager.getInstance()::registerCommand);

        Stream.of(
                new TornadoEnchant(),
                new JackhammerEnchant(),
                new GemFinderEnchant()
        ).forEach(EnchantManager.getInstance()::registerEnchant);

        Stream.of(
                new PlayerListener(this),
                new MineListener(),
                new PickaxeListeners(),
                new ToolListeners()
        ).forEach(e -> Bukkit.getPluginManager().registerEvents(e, this));

        new PAPIHook().register();
    }

    @Override
    public void onDisable() {
        broker.destroy();
        StorageManager.getInstance().shutdown();
    }

    public boolean getRandomBoolean(double percentage) {
        return random.nextDouble() < (percentage / 100);
    }
}
