package us.dxtrus.prisoncore.commands.subcommands.admin;

import us.dxtrus.commons.command.BasicSubCommand;
import us.dxtrus.commons.command.Command;
import us.dxtrus.commons.command.user.CommandUser;
import us.dxtrus.prisoncore.mine.models.PrivateMine;

public class TestLevelCommand extends BasicSubCommand {

    @Command(name = "test-level", permission = "prisoncore.admin.setmultiplier", aliases = {"whatlevel", "lvl"})
    public TestLevelCommand() {super();}

    @Override
    public void execute(CommandUser sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("what level bruh?");
            return;
        }

        double multi = PrivateMine.LEVEL_MULTIPLIER;
        try {
            int level = Integer.parseInt(args[0]);
            sender.sendMessage("""
                    Multiplier: %s
                    Level: %s
                    Total XP: %s
                    """.formatted(
                            String.valueOf(multi),
                            String.valueOf(level),
                            String.valueOf(PrivateMine.totalXPToLevel(level))
                    )
            );
        } catch (Exception ex) {
            sender.sendMessage("ERROR: " + ex.getMessage());
        }
    }
}
