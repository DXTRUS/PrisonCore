package us.dxtrus.prisoncore.mines;

import com.infernalsuite.aswm.api.AdvancedSlimePaperAPI;
import com.infernalsuite.aswm.api.exceptions.CorruptedWorldException;
import com.infernalsuite.aswm.api.exceptions.NewerFormatException;
import com.infernalsuite.aswm.api.exceptions.UnknownWorldException;
import com.infernalsuite.aswm.api.loaders.SlimeLoader;
import com.infernalsuite.aswm.api.world.SlimeWorld;
import com.infernalsuite.aswm.api.world.properties.SlimeProperties;
import com.infernalsuite.aswm.api.world.properties.SlimePropertyMap;
import org.bukkit.World;
import us.dxtrus.prisoncore.exceptions.MineLoadException;
import us.dxtrus.prisoncore.mines.loader.LoaderManager;
import us.dxtrus.prisoncore.mines.models.Mine;

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
     */
    public void load(Mine mine) {
        SlimePropertyMap temp = SLIME_PROPERTY_MAP.clone();
        temp.setValue(SlimeProperties.SPAWN_X, (int) mine.getSpawnLocation().getX());
        temp.setValue(SlimeProperties.SPAWN_Y, (int) mine.getSpawnLocation().getY());
        temp.setValue(SlimeProperties.SPAWN_Z, (int) mine.getSpawnLocation().getZ());
        try {
            SlimeWorld world = swmApi.readWorld(swmLoader, mine.getWorldName(), false, temp);
        } catch (UnknownWorldException e) {

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (CorruptedWorldException e) {
            throw new RuntimeException(e);
        } catch (NewerFormatException e) {
            throw new RuntimeException(e);
        }
    }

    public static LocalMineManager getInstance() {
        if (instance == null) {
            instance = new LocalMineManager();
        }
        return instance;
    }
}
