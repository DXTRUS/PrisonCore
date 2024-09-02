package us.dxtrus.prisoncore.commands;

import org.bukkit.plugin.java.JavaPlugin;
import us.dxtrus.commons.command.BukkitCommand;
import us.dxtrus.commons.command.Command;
import us.dxtrus.commons.command.user.CommandUser;
import us.dxtrus.prisoncore.commands.subcommands.admin.AddEnchantCommand;
import us.dxtrus.prisoncore.commands.subcommands.admin.DeleteAllCommand;
import us.dxtrus.prisoncore.commands.subcommands.admin.GivePickCommand;
import us.dxtrus.prisoncore.commands.subcommands.admin.ReloadCommand;
import us.dxtrus.prisoncore.config.Lang;
import us.dxtrus.prisoncore.util.MessageUtils;

import java.util.stream.Stream;

public class AdminCommand extends BukkitCommand {
    @Command(name = "not-for-you", aliases = "nfy", permission = "prisoncore.admin")
    public AdminCommand(JavaPlugin plugin) {
        super(plugin);
        Stream.of(
                new ReloadCommand(),
                new DeleteAllCommand(),
                new GivePickCommand(),
                new AddEnchantCommand()
        ).forEach(getSubCommands()::add);
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
