package us.dxtrus.prisoncore;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.plugin.java.JavaPlugin;
import us.dxtrus.commons.command.BukkitCommandManager;
import us.dxtrus.commons.gui.FastInvManager;
import us.dxtrus.prisoncore.commands.AdminCommand;
import us.dxtrus.prisoncore.commands.CommandMine;
import us.dxtrus.prisoncore.commands.SpawnCommand;
import us.dxtrus.prisoncore.config.Config;
import us.dxtrus.prisoncore.config.Lang;
import us.dxtrus.prisoncore.hooks.HuskSyncHook;
import us.dxtrus.prisoncore.hooks.PAPIHook;
import us.dxtrus.prisoncore.jobs.SaveJobs;
import us.dxtrus.prisoncore.listeners.MineListener;
import us.dxtrus.prisoncore.listeners.PlayerListener;
import us.dxtrus.prisoncore.locations.Loc;
import us.dxtrus.prisoncore.locations.Location;
import us.dxtrus.prisoncore.locations.Locations;
import us.dxtrus.prisoncore.mine.LocalMineManager;
import us.dxtrus.prisoncore.mine.MineManager;
import us.dxtrus.prisoncore.mine.models.PrivateMine;
import us.dxtrus.prisoncore.mine.network.ServerManager;
import us.dxtrus.prisoncore.mine.network.TransferManager;
import us.dxtrus.prisoncore.mine.network.broker.Broker;
import us.dxtrus.prisoncore.mine.network.broker.RedisBroker;
import us.dxtrus.prisoncore.pickaxe.PickaxeManager;
import us.dxtrus.prisoncore.pickaxe.enchants.EnchantManager;
import us.dxtrus.prisoncore.pickaxe.enchants.impl.gem.TornadoEnchant;
import us.dxtrus.prisoncore.pickaxe.enchants.impl.token.GemFinderEnchant;
import us.dxtrus.prisoncore.pickaxe.enchants.impl.token.JackhammerEnchant;
import us.dxtrus.prisoncore.pickaxe.listeners.PickaxeListeners;
import us.dxtrus.prisoncore.pickaxe.listeners.ToolListeners;
import us.dxtrus.prisoncore.storage.StorageManager;
import us.dxtrus.prisoncore.util.BungeeMessage;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Stream;

@Getter
public final class PrisonCore extends JavaPlugin {
    @Getter private static PrisonCore instance;
    private final Random random = new Random(System.currentTimeMillis());
    private final BungeeMessage messenger = new BungeeMessage(this);
    private Broker broker;

    @Override
    public void onEnable() {
        instance = this;

        broker = new RedisBroker(this);
        broker.connect();

        // Init all managers
        Config.getInstance();
        Lang.getInstance();
        ServerManager.getInstance();
        StorageManager.getInstance();
        LocalMineManager.getInstance();
        MineManager.getInstance();
        TransferManager.getInstance();
        PickaxeManager.startLoreUpdater();

        FastInvManager.register(this);

        Stream.of(
                new CommandMine(this),
                new AdminCommand(this),
                new SpawnCommand(this)
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

        Arrays.stream(Location.values())
                .forEach(location -> StorageManager.getInstance().search(Loc.class, location.name())
                .thenAccept(loc -> loc.ifPresent(Locations::registerLocation)));

        if (Config.getInstance().getServers().isSingleInstance()) { // Saves are done every update on multi-instance
            SaveJobs.startAll();
        }

        new PAPIHook().register();

        if (Bukkit.getPluginManager().getPlugin("HuskSync") != null) {
            Bukkit.getPluginManager().registerEvents(new HuskSyncHook(), this);
        }
    }

    @Override
    public void onDisable() {
        for (PrivateMine mine : MineManager.getInstance().getAllMines()) {
            LocalMineManager.getInstance().unload(mine); // block the thread so that all islands are unloaded
            mine.setLoaded(false);
            mine.setServer(null);
        }
        SaveJobs.forceRunAll();
        SaveJobs.shutdownAll();
        broker.destroy();
        StorageManager.getInstance().shutdown();
        for (World world : Bukkit.getWorlds()) {
            world.getEntities().forEach(e -> {
                if (!(e instanceof ArmorStand ars)) return;
                if (ars.isVisible()) return;
                ars.remove();
            });
        }
    }

    public boolean getRandomBoolean(double percentage) {
        return random.nextDouble() < (percentage / 100);
    }
}
