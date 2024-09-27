package us.dxtrus.prisoncore.commands.subcommands.admin;

import us.dxtrus.commons.command.BasicSubCommand;
import us.dxtrus.commons.command.Command;
import us.dxtrus.commons.command.user.CommandUser;
import us.dxtrus.prisoncore.config.Config;
import us.dxtrus.prisoncore.config.Lang;
import us.dxtrus.prisoncore.util.MessageUtils;

public class ReloadCommand extends BasicSubCommand {
    @Command(name = "reload", permission = "prisoncore.admin", async = true, inGameOnly = false)
    public ReloadCommand() {
        super();
    }

    @Override
    public void execute(CommandUser commandUser, String[] strings) {
        try {
            Config.reload();
            Lang.reload();
            MessageUtils.send(commandUser.getAudience(), Lang.getInstance().getCommand().getReload().getSuccess());
        } catch (Exception e) {
            MessageUtils.send(commandUser.getAudience(), Lang.getInstance().getCommand().getReload().getFail());
        }
    }
}
