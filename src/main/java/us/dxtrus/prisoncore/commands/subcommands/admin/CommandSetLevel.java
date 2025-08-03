package us.dxtrus.prisoncore.commands.subcommands.admin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import us.dxtrus.commons.command.BasicSubCommand;
import us.dxtrus.commons.command.Command;
import us.dxtrus.commons.command.user.BukkitUser;
import us.dxtrus.commons.command.user.CommandUser;
import us.dxtrus.prisoncore.mine.MineManager;
import us.dxtrus.prisoncore.mine.models.PrivateMine;

public class CommandSetLevel extends BasicSubCommand {

    @Command(name = "set-level", permission = "prisoncore.admin.setlevel", aliases = {"setlevel", "setlvl"})
    public CommandSetLevel() {super();}

    @Override
    public void execute(CommandUser sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("You must specify who and what level.");
            return;
        }

        String whoName = args[0];
        try {
            int level = Integer.parseInt(args[1]);
            Player who = Bukkit.getPlayer(whoName);

            if (who == null) {
                sender.sendMessage("Player not online!");
                return;
            }

            MineManager.getInstance().getMine(who.getUniqueId()).levelUp(level);
            sender.sendMessage("%s (%s) mine level is now %s".formatted(who.getName(), who.getUniqueId().toString(), String.valueOf(level)));
        } catch (Exception ex) {
            sender.sendMessage("ERROR: " + ex.getMessage());
        }
    }
}
