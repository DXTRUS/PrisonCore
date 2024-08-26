package us.dxtrus.prisoncore.commands;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import us.dxtrus.commons.command.BukkitCommand;
import us.dxtrus.commons.command.Command;
import us.dxtrus.commons.command.user.CommandUser;
import us.dxtrus.prisoncore.commands.subcommands.MineHomeCommand;
import us.dxtrus.prisoncore.commands.subcommands.ResetMineCommand;
import us.dxtrus.prisoncore.config.Config;
import us.dxtrus.prisoncore.config.Lang;
import us.dxtrus.prisoncore.gui.MineGui;
import us.dxtrus.prisoncore.util.MessageUtils;

import java.util.stream.Stream;

public class CommandMine extends BukkitCommand {
    @Command(name = "mine", permission = "prisoncore.use")
    public CommandMine(JavaPlugin plugin) {
        super(plugin);
        Stream.of(
                new MineHomeCommand(),
                new ResetMineCommand()
        ).forEach(getSubCommands()::add);
    }

    @Override
    public void execute(CommandUser commandUser, String[] strings) {
        if (strings.length >= 1) {
            if (subCommandExecutor(commandUser, strings)) return;
            MessageUtils.send(commandUser.getAudience(), Lang.getInstance().getCommand().getUnknownArgs());
            return;
        }

        if (!Config.getInstance().getCommands().isMain()) {
            MessageUtils.send(commandUser.getAudience(), Lang.getInstance().getCommand().getDisabled());
            return;
        }

        MineGui.getInstance().open(((Player) commandUser.getAudience()));
    }
}
