package us.dxtrus.prisoncore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.dxtrus.prisoncore.config.Config;
import us.dxtrus.prisoncore.config.Lang;
import us.dxtrus.prisoncore.gui.MineGui;
import us.dxtrus.prisoncore.util.MessageUtils;
import us.dxtrus.prisoncore.util.StringUtil;

public class CommandMine extends CoreCommand {
    @Override public boolean executePlayer(Player sender, Command cmd, String label, String[] args) {
        if (args.length < 1) {
            MineGui.getInstance().open(sender);
            return true;
        }

        if (StringUtil.matchAny(args[0], true , "go", "tp", "goto", "teleport", "travel")) {
            // TODO: Teleport to mine

            MessageUtils.send(sender, Lang.getInstance().getCommand().getMine().getTeleport());
            return true;
        }

        if (StringUtil.matchAny(args[0], true, "reset", "refresh", "clear")) {
            // TODO: Reset mine
            MessageUtils.send(sender, Lang.getInstance().getCommand().getMine().getReset());
            return true;
        }

        if (!sender.hasPermission(Config.getInstance().getPermissions().getMineAdmin())) {
            MessageUtils.send(sender, Lang.getInstance().getCommand().getUnknownArgs());
            return true;
        }

        MessageUtils.send(sender, Lang.getInstance().getCommand().getUnknownArgs());
        return true;
    }

    @Override public boolean executeConsole(CommandSender sender, Command cmd, String label, String[] args) {
        return false;
    }
}
