package us.dxtrus.prisoncore.commands;

import org.bukkit.plugin.java.JavaPlugin;
import us.dxtrus.commons.command.BukkitCommand;
import us.dxtrus.commons.command.Command;
import us.dxtrus.commons.command.user.CommandUser;
import us.dxtrus.prisoncore.config.Lang;
import us.dxtrus.prisoncore.util.MessageUtils;

public class AdminCommand extends BukkitCommand {
    @Command(name = "not-for-you", aliases = "nfy", permission = "prisoncore.admin")
    public AdminCommand(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandUser commandUser, String[] strings) {
        if (strings.length >= 1) {
            if (subCommandExecutor(commandUser, strings)) return;
            MessageUtils.send(commandUser.getAudience(), Lang.getInstance().getCommand().getUnknownArgs());
            return;
        }

        MessageUtils.send(commandUser.getAudience(), Lang.getInstance().getCommand().getUnknownArgs());
    }
}
