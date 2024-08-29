package us.dxtrus.prisoncore.mine;

import com.infernalsuite.aswm.api.AdvancedSlimePaperAPI;
import com.infernalsuite.aswm.api.exceptions.CorruptedWorldException;
import com.infernalsuite.aswm.api.exceptions.NewerFormatException;
import com.infernalsuite.aswm.api.exceptions.UnknownWorldException;
import com.infernalsuite.aswm.api.loaders.SlimeLoader;
import com.infernalsuite.aswm.api.world.SlimeWorld;
import com.infernalsuite.aswm.api.world.properties.SlimeProperties;
import com.infernalsuite.aswm.api.world.properties.SlimePropertyMap;
import com.infernalsuite.aswm.loaders.mysql.MysqlLoader;
import org.bukkit.Bukkit;
import org.bukkit.World;
import us.dxtrus.commons.utils.TaskManager;
import us.dxtrus.prisoncore.PrisonCore;
import us.dxtrus.prisoncore.config.Config;
import us.dxtrus.prisoncore.mine.models.Mine;
import us.dxtrus.prisoncore.mine.models.PrivateMine;
import us.dxtrus.prisoncore.mine.network.broker.Message;
import us.dxtrus.prisoncore.mine.network.broker.Payload;
import us.dxtrus.prisoncore.mine.network.broker.Response;
import us.dxtrus.prisoncore.mine.network.broker.Response.ResponseType;
import us.dxtrus.prisoncore.util.StringUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages and loads mines that are destined or currently on this local server.
 */
public class LocalMineManager {
    private static final SlimePropertyMap SLIME_PROPERTY_MAP = new SlimePropertyMap();
    private static LocalMineManager instance;

    static {
        SLIME_PROPERTY_MAP.setValue(SlimeProperties.DIFFICULTY, "peaceful");
        SLIME_PROPERTY_MAP.setValue(SlimeProperties.ALLOW_MONSTERS, false);
        SLIME_PROPERTY_MAP.setValue(SlimeProperties.ALLOW_ANIMALS, false);
        SLIME_PROPERTY_MAP.setValue(SlimeProperties.DRAGON_BATTLE, false);
        SLIME_PROPERTY_MAP.setValue(SlimeProperties.PVP, false);
        SLIME_PROPERTY_MAP.setValue(SlimeProperties.ENVIRONMENT, "NORMAL");
        SLIME_PROPERTY_MAP.setValue(SlimeProperties.WORLD_TYPE, "DEFAULT");
        SLIME_PROPERTY_MAP.setValue(SlimeProperties.DEFAULT_BIOME, "minecraft:jungle");
    }

    private final AdvancedSlimePaperAPI swmApi = AdvancedSlimePaperAPI.instance();
    private final SlimeLoader swmLoader;
    private final Map<UUID, Mine> cuboidMines = new ConcurrentHashMap<>();

    private LocalMineManager() {
        Config.Storage storage = Config.getInstance().getStorage();
        String sqlString = "jdbc:mysql://{host}:{port}/{database}?autoReconnect=true&allowMultiQueries=true&useSSL={usessl}";
        swmLoader = switch (storage.getType()) {
            case MYSQL, MARIADB -> {
                try {
                    yield new MysqlLoader(sqlString, storage.getHost(), storage.getPort(),
                            storage.getDatabase(), storage.isUseSsl(), storage.getUsername(), storage.getPassword());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + storage.getType());
        };
    }

    public static LocalMineManager getInstance() {
        if (instance == null) {
            instance = new LocalMineManager();
        }
        return instance;
    }

    /**
     * Loads a mine on this server. Or creates the mine.
     *
     * @param mine the mine to load
     */
    public void load(PrivateMine mine) {
        SlimePropertyMap temp = SLIME_PROPERTY_MAP.clone();
        temp.setValue(SlimeProperties.SPAWN_X, (int) mine.getSpawnLocation().getX());
        temp.setValue(SlimeProperties.SPAWN_Y, (int) mine.getSpawnLocation().getY());
        temp.setValue(SlimeProperties.SPAWN_Z, (int) mine.getSpawnLocation().getZ());

        try {
            SlimeWorld world;
            if (swmLoader.worldExists(mine.getWorldName())) {
                world = swmApi.readWorld(swmLoader, mine.getWorldName(), false, temp);
            } else {
                world = swmApi.createEmptyWorld(mine.getWorldName(), false, temp, swmLoader);
                swmApi.saveWorld(world);
            }

            TaskManager.runSync(PrisonCore.getInstance(), () -> {
                swmApi.loadWorld(world, true);
                Mine link = mine.getLinkage();
                link.setWalls();
                link.reset();

                TaskManager.runAsync(PrisonCore.getInstance(), () ->
                        Message.builder()
                                .type(Message.Type.MINE_LOAD_RESPONSE)
                                .payload(Payload.withResponse(new Response(ResponseType.SUCCESS, mine.getWorldName(), "")))
                                .build().send(PrisonCore.getInstance().getBroker()));
            });

        } catch (UnknownWorldException e) {
            String trackingCode = StringUtil.getRandomString(6);
            Message.builder()
                    .type(Message.Type.MINE_LOAD_RESPONSE)
                    .payload(Payload.withResponse(new Response(ResponseType.FAIL_NO_WORLD, mine.getWorldName(), trackingCode)))
                    .build().send(PrisonCore.getInstance().getBroker());
            throw new RuntimeException("UnknownWorldException on world %s tracking code %s".formatted(mine.getWorldName(), trackingCode), e);
        } catch (CorruptedWorldException e) {
            String trackingCode = StringUtil.getRandomString(6);
            Message.builder()
                    .type(Message.Type.MINE_LOAD_RESPONSE)
                    .payload(Payload.withResponse(new Response(ResponseType.FAIL_CORRUPTED, mine.getWorldName(), trackingCode)))
                    .build().send(PrisonCore.getInstance().getBroker());
            throw new RuntimeException("CorruptedWorldException on world %s tracking code %s".formatted(mine.getWorldName(), trackingCode), e);
        } catch (NewerFormatException e) {
            String trackingCode = StringUtil.getRandomString(6);
            Message.builder()
                    .type(Message.Type.MINE_LOAD_RESPONSE)
                    .payload(Payload.withResponse(new Response(ResponseType.FAIL_OLD, mine.getWorldName(), trackingCode)))
                    .build().send(PrisonCore.getInstance().getBroker());
            throw new RuntimeException("NewerFormatException on world %s tracking code %s".formatted(mine.getWorldName(), trackingCode), e);
        } catch (Exception e) {
            String trackingCode = StringUtil.getRandomString(6);
            Message.builder()
                    .type(Message.Type.MINE_LOAD_RESPONSE)
                    .payload(Payload.withResponse(new Response(ResponseType.FAIL_GENERIC, mine.getWorldName(), trackingCode)))
                    .build().send(PrisonCore.getInstance().getBroker());
            throw new RuntimeException("IOException on world %s tracking code %s".formatted(mine.getWorldName(), trackingCode), e);
        }
    }

    public void unload(PrivateMine mine) {
        World world = Bukkit.getWorld(mine.getWorldName());
        if (world == null) {
            String trackingCode = StringUtil.getRandomString(6);
            Message.builder()
                    .type(Message.Type.MINE_UNLOAD_RESPONSE)
                    .payload(Payload.withResponse(new Response(ResponseType.FAIL_NO_WORLD, mine.getWorldName(), trackingCode)))
                    .build().send(PrisonCore.getInstance().getBroker());
            throw new RuntimeException("World not found %s tracking code %s".formatted(mine.getWorldName(), trackingCode));
        }
        Bukkit.unloadWorld(world, true);
        Message.builder()
                .type(Message.Type.MINE_UNLOAD_RESPONSE)
                .payload(Payload.withResponse(new Response(ResponseType.SUCCESS, mine.getWorldName(), "")))
                .build().send(PrisonCore.getInstance().getBroker());
    }

    public Mine getBreakableMine(UUID player) {
        return cuboidMines.get(player);
    }

    public void loadBreakableMine(Mine mine, UUID player) {
        cuboidMines.put(player, mine);
    }

    public void deleteAll() {
        try {
            for (String world : swmLoader.listWorlds()) {
                swmLoader.deleteWorld(world);
            }
        } catch (IOException | UnknownWorldException e) {
            throw new RuntimeException(e);
        }
    }
}
