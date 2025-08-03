package us.dxtrus.prisoncore.commands.subcommands.admin;

import us.dxtrus.commons.command.BasicSubCommand;
import us.dxtrus.commons.command.Command;
import us.dxtrus.commons.command.user.CommandUser;
import us.dxtrus.prisoncore.mine.models.PrivateMine;

public class SetMultiplierCommand extends BasicSubCommand {

    @Command(name = "set-multiplier", permission = "prisoncore.admin.setmultiplier", aliases = {"multi"})
    public SetMultiplierCommand() {super(); }

    @Override
    public void execute(CommandUser sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("What multiplier do you want bruh?");
            return;
        }

        try {
            double multi = Double.parseDouble(args[0]);
            if (!Double.isFinite(multi)) {
                sender.sendMessage("Not finite!");
                return;
            }

            PrivateMine.LEVEL_MULTIPLIER = multi;
            sender.sendMessage("Multiplier is now " + multi);
        } catch (Exception ex) {
            sender.sendMessage("ERROR: " + ex.getMessage());
        }
    }
}
