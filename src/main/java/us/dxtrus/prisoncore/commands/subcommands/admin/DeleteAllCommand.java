package us.dxtrus.prisoncore.commands.subcommands.admin;

import us.dxtrus.commons.command.BasicSubCommand;
import us.dxtrus.commons.command.Command;
import us.dxtrus.commons.command.user.CommandUser;
import us.dxtrus.prisoncore.mine.LocalMineManager;
import us.dxtrus.prisoncore.util.MessageUtils;

public class DeleteAllCommand extends BasicSubCommand {
    @Command(name = "delete-all-mines", aliases = "prune", permission = "prisoncore.admin", async = true)
    public DeleteAllCommand() {
        super();
    }
    @Override
    public void execute(CommandUser commandUser, String[] strings) {
        MessageUtils.send(commandUser.getAudience(), "Deleting...");
        LocalMineManager.getInstance().deleteAll();
        MessageUtils.send(commandUser.getAudience(), "Deleted all mines");
    }
}
