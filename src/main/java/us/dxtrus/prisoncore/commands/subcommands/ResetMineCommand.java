package us.dxtrus.prisoncore.commands.subcommands;

import org.bukkit.entity.Player;
import us.dxtrus.commons.command.BasicSubCommand;
import us.dxtrus.commons.command.Command;
import us.dxtrus.commons.command.user.CommandUser;
import us.dxtrus.prisoncore.config.Config;
import us.dxtrus.prisoncore.config.Lang;
import us.dxtrus.prisoncore.mine.LocalMineManager;
import us.dxtrus.prisoncore.mine.MineManager;
import us.dxtrus.prisoncore.mine.models.Mine;
import us.dxtrus.prisoncore.mine.models.PrivateMine;
import us.dxtrus.prisoncore.mine.network.ServerManager;
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

        PrivateMine mine = MineManager.getInstance().getMine(((Player) commandUser.getAudience()).getUniqueId());
        if (!mine.getServer().equals(ServerManager.getInstance().getThisServer())) {
            return;
        }
        mine.getLinkage().reset();
        MessageUtils.send(commandUser.getAudience(), Lang.getInstance().getCommand().getMine().getReset());
    }
}
