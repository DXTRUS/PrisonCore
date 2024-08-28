package us.dxtrus.prisoncore.commands.subcommands;

import net.kyori.adventure.identity.Identity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import us.dxtrus.commons.command.BasicSubCommand;
import us.dxtrus.commons.command.Command;
import us.dxtrus.commons.command.user.CommandUser;
import us.dxtrus.prisoncore.PrisonCore;
import us.dxtrus.prisoncore.config.Config;
import us.dxtrus.prisoncore.config.Lang;
import us.dxtrus.prisoncore.mine.Mine;
import us.dxtrus.prisoncore.slime.SlimeManager;
import us.dxtrus.prisoncore.util.MessageUtils;

import java.util.Optional;
import java.util.UUID;

public class ResetMineCommand extends BasicSubCommand {
    @Command(name = "reset", aliases = {"refresh", "clear"}, permission = "prisoncore.use")
    public ResetMineCommand() {
        super();
    }
    @Override
    public void execute(CommandUser commandUser, String[] strings) {
        if (!Config.getInstance().getCommands().isReset()) {
            MessageUtils.send(commandUser.getAudience(), Lang.getInstance().getCommand().getDisabled());
            return;
        }

        Optional<UUID> optional = commandUser.getAudience().get(Identity.UUID);
        Player who = Bukkit.getPlayer(optional.get());

        Mine mine = SlimeManager.getInstance().getMine(who);
        if (mine != null) {
            mine.reset();
            MessageUtils.send(commandUser.getAudience(), Lang.getInstance().getCommand().getMine().getReset());
        }
    }
}
