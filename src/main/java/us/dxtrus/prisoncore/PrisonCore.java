package us.dxtrus.prisoncore;

import com.infernalsuite.aswm.api.AdvancedSlimePaperAPI;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.clip.placeholderapi.PlaceholderAPI;
import net.dxtrus.scoreboard.DexterousBoard;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import us.dxtrus.commons.command.BukkitCommandManager;
import us.dxtrus.commons.gui.FastInvManager;
import us.dxtrus.commons.utils.StringUtils;
import us.dxtrus.commons.utils.TaskManager;
import us.dxtrus.prisoncore.commands.AdminCommand;
import us.dxtrus.prisoncore.commands.CommandMine;
import us.dxtrus.prisoncore.mine.Mine;
import us.dxtrus.prisoncore.mine.listeners.MineListener;
import us.dxtrus.prisoncore.stats.Statistics;
import us.dxtrus.prisoncore.stats.StatsManager;
import us.dxtrus.prisoncore.stats.papi.Placeholders;
import us.dxtrus.prisoncore.storage.MySQLHandler;
import us.dxtrus.prisoncore.util.Cuboid;
import us.dxtrus.prisoncore.util.Util;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;

public final class PrisonCore extends JavaPlugin implements Listener {
    @Getter private static PrisonCore instance;
    @Getter private Random random;

    @Getter private AdvancedSlimePaperAPI api;

    ////////// DEBUG UUID 5f087be3-d369-4a5a-b9e2-6ae48affb534
    @Getter private Mine debugMine;
    private UUID debugUUID = UUID.fromString("5f087be3-d369-4a5a-b9e2-6ae48affb534");

    @Override
    public void onEnable() {
        instance = this;
        FastInvManager.register(this);

        MySQLHandler.getInstance().connect();
        api = AdvancedSlimePaperAPI.instance();
        new Placeholders().register();

        Stream.of(
                new CommandMine(this),
                new AdminCommand(this)
        ).forEach(BukkitCommandManager.getInstance()::registerCommand);

        Bukkit.getPluginManager().registerEvents(new MineListener(), this);
    }

}
