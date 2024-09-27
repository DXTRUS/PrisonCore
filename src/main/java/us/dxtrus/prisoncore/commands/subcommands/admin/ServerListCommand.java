package us.dxtrus.prisoncore.commands.subcommands.admin;

import us.dxtrus.commons.command.BasicSubCommand;
import us.dxtrus.commons.command.Command;
import us.dxtrus.commons.command.user.CommandUser;
import us.dxtrus.prisoncore.mine.models.Server;
import us.dxtrus.prisoncore.mine.network.ServerManager;

public class ServerListCommand extends BasicSubCommand {
    @Command(name = "servers", permission = "prisoncore.admin", async = true, inGameOnly = false)
    public ServerListCommand() {
        super();
    }

    @Override
    public void execute(CommandUser commandUser, String[] strings) {
        for (Server server : ServerManager.getInstance().getAllServers()) {
            commandUser.sendMessage(server.toString());
        }
    }
}
