package us.dxtrus.prisoncore.commands.subcommands.admin;

import us.dxtrus.commons.command.BasicSubCommand;
import us.dxtrus.commons.command.Command;
import us.dxtrus.commons.command.user.CommandUser;

public class ReloadCommand extends BasicSubCommand {
    @Command(name = "reload", permission = "prisoncore.admin")
    public ReloadCommand() {
        super();
    }
    @Override
    public void execute(CommandUser commandUser, String[] strings) {

    }
}
