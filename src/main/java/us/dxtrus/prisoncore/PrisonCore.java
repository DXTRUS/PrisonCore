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
import us.dxtrus.prisoncore.commands.AdminCommand;
import us.dxtrus.prisoncore.commands.CommandMine;
import us.dxtrus.prisoncore.eco.EconomyManager;
import us.dxtrus.prisoncore.eco.papi.PlaceholderTokens;
import us.dxtrus.prisoncore.util.StringUtil;

import java.math.BigInteger;
import java.util.stream.Stream;

public final class PrisonCore extends JavaPlugin implements Listener {
    @Getter private static PrisonCore instance;

    @Override
    public void onEnable() {
        instance = this;

        FastInvManager.register(this);

        Stream.of(
                new CommandMine(this),
                new AdminCommand(this)
        ).forEach(BukkitCommandManager.getInstance()::registerCommand);

        new PlaceholderTokens().register();
        Bukkit.getPluginManager().registerEvents(this, this);
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
