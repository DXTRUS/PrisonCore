package us.dxtrus.prisoncore.commands.subcommands;

import us.dxtrus.commons.command.BasicSubCommand;
import us.dxtrus.commons.command.Command;
import us.dxtrus.commons.command.user.CommandUser;
import us.dxtrus.prisoncore.config.Config;
import us.dxtrus.prisoncore.config.Lang;
import us.dxtrus.prisoncore.util.MessageUtils;

public class MineHomeCommand extends BasicSubCommand {
    @Command(name = "home", aliases = {"go", "tp", "goto", "teleport", "travel"}, permission = "prisoncore.use")
    public MineHomeCommand() {
        super();
    }

    @Override
    public void execute(CommandUser commandUser, String[] strings) {
        if (!Config.getInstance().getCommands().isHome()) {
            MessageUtils.send(commandUser.getAudience(), Lang.getInstance().getCommand().getDisabled());
            return;
        }

        // TODO: Teleport to mine
        MessageUtils.send(commandUser.getAudience(), Lang.getInstance().getCommand().getMine().getTeleport());
    }
}
