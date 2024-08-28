package us.dxtrus.prisoncore.commands.subcommands;

import org.bukkit.entity.Player;
import us.dxtrus.commons.command.BasicSubCommand;
import us.dxtrus.commons.command.Command;
import us.dxtrus.commons.command.user.CommandUser;
import us.dxtrus.prisoncore.config.Config;
import us.dxtrus.prisoncore.config.Lang;
import us.dxtrus.prisoncore.mine.MineManager;
import us.dxtrus.prisoncore.mine.models.PrivateMine;
import us.dxtrus.prisoncore.mine.network.ServerManager;
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
        Player player = (Player) commandUser.getAudience();
        PrivateMine temp = MineManager.getInstance().getMine(player.getUniqueId());
        MessageUtils.send(commandUser.getAudience(), Lang.getInstance().getCommand().getMine().getTeleport());
        MineManager.getInstance().load(temp).thenAccept(mine -> {
            if (mine.getServer().equals(ServerManager.getInstance().getThisServer())) { // handle local
                mine.connectLocal(player);
                return;
            }
            mine.connect(player);
        });
    }
}
