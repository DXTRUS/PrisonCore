package us.dxtrus.prisoncore.mine;

import com.infernalsuite.aswm.api.AdvancedSlimePaperAPI;
import com.infernalsuite.aswm.api.exceptions.CorruptedWorldException;
import com.infernalsuite.aswm.api.exceptions.NewerFormatException;
import com.infernalsuite.aswm.api.exceptions.UnknownWorldException;
import com.infernalsuite.aswm.api.loaders.SlimeLoader;
import com.infernalsuite.aswm.api.world.SlimeWorld;
import com.infernalsuite.aswm.api.world.properties.SlimeProperties;
import com.infernalsuite.aswm.api.world.properties.SlimePropertyMap;
import us.dxtrus.prisoncore.PrisonCore;
import us.dxtrus.prisoncore.mine.loader.LoaderManager;
import us.dxtrus.prisoncore.mine.models.Mine;
import us.dxtrus.prisoncore.mine.network.broker.Message;
import us.dxtrus.prisoncore.mine.network.broker.Payload;
import us.dxtrus.prisoncore.mine.network.broker.Response;

import java.io.IOException;

/**
 * Manages and loads mines that are destined or currently on this local server.
 */
public class LocalMineManager {
    private static LocalMineManager instance;

    private final AdvancedSlimePaperAPI swmApi = AdvancedSlimePaperAPI.instance();
    private final SlimeLoader swmLoader;

    private static final SlimePropertyMap SLIME_PROPERTY_MAP = new SlimePropertyMap();

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

    private LocalMineManager() {
        swmLoader = LoaderManager.getInstance().getLoader();
    }

    /**
     * Loads a mine on this server.
     * @param mine the mine to load
     * @return the update mine object
     */
    public Mine load(Mine mine) {
        SlimePropertyMap temp = SLIME_PROPERTY_MAP.clone();
        temp.setValue(SlimeProperties.SPAWN_X, (int) mine.getSpawnLocation().getX());
        temp.setValue(SlimeProperties.SPAWN_Y, (int) mine.getSpawnLocation().getY());
        temp.setValue(SlimeProperties.SPAWN_Z, (int) mine.getSpawnLocation().getZ());
        try {
            SlimeWorld world = swmApi.readWorld(swmLoader, mine.getWorldName(), false, temp);
        } catch (UnknownWorldException e) {
            Message.builder()
                    .type(Message.Type.MINE_LOAD_RESPONSE)
                    .payload(Payload.withResponse(Response.FAIL_NO_WORLD))
                    .build().send(PrisonCore.getInstance().getBroker());
        } catch (IOException e) {
            Message.builder()
                    .type(Message.Type.MINE_LOAD_RESPONSE)
                    .payload(Payload.withResponse(Response.FAIL_GENERIC))
                    .build().send(PrisonCore.getInstance().getBroker());
        } catch (CorruptedWorldException e) {
            Message.builder()
                    .type(Message.Type.MINE_LOAD_RESPONSE)
                    .payload(Payload.withResponse(Response.FAIL_CORRUPTED))
                    .build().send(PrisonCore.getInstance().getBroker());
        } catch (NewerFormatException e) {
            Message.builder()
                    .type(Message.Type.MINE_LOAD_RESPONSE)
                    .payload(Payload.withResponse(Response.FAIL_OLD))
                    .build().send(PrisonCore.getInstance().getBroker());
        }
        return mine;
    }

    public static LocalMineManager getInstance() {
        if (instance == null) {
            instance = new LocalMineManager();
        }
        return instance;
    }
}
