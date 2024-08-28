package us.dxtrus.prisoncore.slime;

import com.infernalsuite.aswm.api.AdvancedSlimePaperAPI;
import com.infernalsuite.aswm.api.loaders.SlimeLoader;
import com.infernalsuite.aswm.api.world.SlimeWorld;
import com.infernalsuite.aswm.api.world.properties.SlimeProperties;
import com.infernalsuite.aswm.api.world.properties.SlimePropertyMap;
import com.infernalsuite.aswm.loaders.mysql.MysqlLoader;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.zaxxer.hikari.HikariDataSource;
import us.dxtrus.prisoncore.PrisonCore;
import us.dxtrus.prisoncore.config.Config;
import us.dxtrus.prisoncore.mine.Mine;
import us.dxtrus.prisoncore.stats.Statistics;
import us.dxtrus.prisoncore.stats.StatsManager;
import us.dxtrus.prisoncore.storage.MySQLHandler;
import us.dxtrus.prisoncore.util.Cuboid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class SlimeManager {
    private static SlimeManager instance;
    private AdvancedSlimePaperAPI api;
    private SlimeLoader loader;

    private Map<Player, SlimeWorld> worlds;
    private Map<String, Mine> mines;

    private SlimeManager(HikariDataSource dataSource) {
        Config.Sql sql = Config.getInstance().getSql();

        try {
            api = AdvancedSlimePaperAPI.instance();
            worlds = new HashMap<>();
            mines = new HashMap<>();
            loader = new MysqlLoader(dataSource.getJdbcUrl(), sql.getHost(), sql.getPort(), sql.getDatabase(), false, sql.getUsername(), sql.getPassword());

        } catch (Exception ex) {
            PrisonCore.getInstance().getLogger().log(Level.SEVERE, "ASWM SQL LOADER FAIL", ex);
            Bukkit.getPluginManager().disablePlugin(PrisonCore.getInstance());
        }
    }

    public boolean mineLoaded(Player player) {
        return worlds.containsKey(player);
    }

    @Nullable
    public SlimeWorld loadPlayerWorld(Player player) {
        if (worlds.containsKey(player)) {
            return worlds.get(player);
        }

        try {
            String worldName = worldName(player);
            SlimeWorld world;

            SlimePropertyMap properties = new SlimePropertyMap();

            properties.setValue(SlimeProperties.DIFFICULTY, "easy");
            properties.setValue(SlimeProperties.SPAWN_X, 0);
            properties.setValue(SlimeProperties.SPAWN_Y, 65);
            properties.setValue(SlimeProperties.SPAWN_Z, 0);
            properties.setValue(SlimeProperties.ALLOW_ANIMALS, false);
            properties.setValue(SlimeProperties.ALLOW_MONSTERS, false);
            properties.setValue(SlimeProperties.DRAGON_BATTLE, false);
            properties.setValue(SlimeProperties.PVP, false);
            properties.setValue(SlimeProperties.ENVIRONMENT, "normal");
            properties.setValue(SlimeProperties.WORLD_TYPE, "DEFAULT");
            properties.setValue(SlimeProperties.DEFAULT_BIOME, "minecraft:plains");

            if (loader.worldExists(worldName)) {
                world = api.readWorld(loader, worldName, false, properties);
            } else {
                world = api.createEmptyWorld(worldName, false, properties, loader);
                loader.saveWorld(worldName, world.getPersistentDataContainer().serializeToBytes());
            }

            world = api.loadWorld(world, true);
            worlds.put(player, world);

            // Init mine

            Vector topLeft = new Vector(10, 65, -10);
            Vector bottomRight = new Vector(40, -45, 20);
            Vector spawn = new Vector(0.5d, 65d, 0.5d);

            Mine mine = new Mine(player.getUniqueId(), worldName, topLeft, bottomRight, spawn, spawn);
            mines.put(worldName, mine);

            Cuboid walls = mine.setWalls();

            World bukkitWorld = Bukkit.getWorld(worldName);
            Location l = new Location(bukkitWorld, -40, 65, -60);
            Location r = new Location(bukkitWorld, 160, 65, 100);

            Cuboid floor = new Cuboid(l, r);
            floor.forEach(block -> {
                if (!walls.contains(block.getLocation())) {
                    block.setType(Material.BEDROCK);
                }
            });

            mine.init();


            return world;
        } catch (Exception ex) {
            PrisonCore.getInstance().getLogger().log(Level.SEVERE, "ASWM SW Loading failed", ex);
            player.sendMessage("FAILED! PLEASE OPEN A TICKET!");
            return null;
        }
    }

    public void saveAndUnload(Player player, SlimeWorld world) {
        try {
            String worldName = worldName(player);
            World world1 = Bukkit.getWorld(worldName);
            List<Player> players = world1.getPlayers();

            api.saveWorld(world);
            CompletableFuture<Void> cf = CompletableFuture.allOf(players.stream().map(plr -> plr.teleportAsync(new Location(Bukkit.getWorld("world"), 0, 65, 0))).toList().toArray(CompletableFuture[]::new));
            cf.thenRun(() -> {
                Bukkit.getScheduler().runTask(PrisonCore.getInstance(), () -> {
                   Bukkit.unloadWorld(world1, true);
                });
            });

            mines.remove(worldName);
            MySQLHandler.getInstance().save(Statistics.class, StatsManager.getInstance().getStatistics(player.getUniqueId()));
            worlds.remove(player);
        } catch (Exception ex) {
            PrisonCore.getInstance().getLogger().log(Level.SEVERE, "ASWM SW Saving failed", ex);
            player.sendMessage("FAILED! PLEASE OPEN A TICKET!");
        }
    }

    @Nullable
    public Mine getMine(Player player) {
        String name = worldName(player);
        if (!mines.containsKey(name)) {
            return null;
        }

        return mines.get(worldName(player));
    }

    public @NotNull AdvancedSlimePaperAPI getApi() {
        return api;
    }

    public static SlimeManager getInstance() {
        return instance == null ? instance = new SlimeManager(MySQLHandler.getInstance().getDataSource()) : instance;
    }

    public static String worldName(Player who) {
        return "mine_" + who.getUniqueId();
    }
}
