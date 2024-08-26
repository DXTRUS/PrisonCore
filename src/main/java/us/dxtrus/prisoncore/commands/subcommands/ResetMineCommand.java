package us.dxtrus.prisoncore.commands.subcommands;

import us.dxtrus.commons.command.BasicSubCommand;
import us.dxtrus.commons.command.Command;
import us.dxtrus.commons.command.user.CommandUser;
import us.dxtrus.prisoncore.PrisonCore;
import us.dxtrus.prisoncore.config.Config;
import us.dxtrus.prisoncore.config.Lang;
import us.dxtrus.prisoncore.util.MessageUtils;

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

        // TODO: Reset mine
        PrisonCore.getInstance().getDebugMine().reset();
        MessageUtils.send(commandUser.getAudience(), Lang.getInstance().getCommand().getMine().getReset());
    }
}
