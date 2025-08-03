package us.dxtrus.prisoncore.commands.utility;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import us.dxtrus.commons.command.BukkitCommand;
import us.dxtrus.commons.command.Command;
import us.dxtrus.commons.command.user.BukkitUser;
import us.dxtrus.commons.command.user.CommandUser;
import us.dxtrus.commons.utils.StringUtils;
import us.dxtrus.prisoncore.PrisonCommand;
import us.dxtrus.prisoncore.config.Lang;

public class CommandFly extends PrisonCommand {
    @Command(name = "fly", permission = "", aliases = {"togglefly", "efly"})
    public CommandFly(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandUser sender, String alias, String[] args) {
        if (sender instanceof BukkitUser user) {
            Player player = user.getAudience();

            player.setAllowFlight(!player.getAllowFlight());
            player.setFlying(player.getAllowFlight());

            user.sendMessage(StringUtils.modernMessage(
                    Lang.getInstance().getCommand().getFlyToggled()
                            .replace("{0}", (player.getAllowFlight() ? "enabled" : "disabled"))
            ));

            return;
        }

        sendError(ErrorType.MUST_BE_PLAYER, sender);
    }
}
