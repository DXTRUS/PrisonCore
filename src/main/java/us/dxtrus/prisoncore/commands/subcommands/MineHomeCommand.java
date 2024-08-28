package us.dxtrus.prisoncore.commands.subcommands;

import com.infernalsuite.aswm.api.world.SlimeWorld;
import net.kyori.adventure.identity.Identity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import us.dxtrus.commons.command.BasicSubCommand;
import us.dxtrus.commons.command.Command;
import us.dxtrus.commons.command.user.CommandUser;
import us.dxtrus.prisoncore.config.Config;
import us.dxtrus.prisoncore.config.Lang;
import us.dxtrus.prisoncore.mine.Mine;
import us.dxtrus.prisoncore.slime.SlimeManager;
import us.dxtrus.prisoncore.util.MessageUtils;

import java.util.Optional;
import java.util.UUID;

public class MineHomeCommand extends BasicSubCommand {
    @Command(name = "home", aliases = {"go", "tp", "goto", "teleport", "travel"}, permission = "prisoncore.use")
    public MineHomeCommand() {
        super();
    }

    @Override
    public void execute(CommandUser commandUser, String[] args) {
        if (!Config.getInstance().getCommands().isHome()) {
            MessageUtils.send(commandUser.getAudience(), Lang.getInstance().getCommand().getDisabled());
            return;
        }

        @NotNull Optional<UUID> optional = commandUser.getAudience().get(Identity.UUID);
        Player who = Bukkit.getPlayer(optional.get());
        SlimeWorld world = SlimeManager.getInstance().loadPlayerWorld(who);
        Mine mine = SlimeManager.getInstance().getMine(who);

        if (args.length < 1) {
            MessageUtils.send(commandUser.getAudience(), world.getName());
            World world2 = Bukkit.getWorld(world.getName());
            mine.enter(who);
        } else {
            SlimeManager.getInstance().saveAndUnload(who, world);
        }



        // TODO: Teleport to mine
        MessageUtils.send(commandUser.getAudience(), Lang.getInstance().getCommand().getMine().getTeleport());
    }
}
