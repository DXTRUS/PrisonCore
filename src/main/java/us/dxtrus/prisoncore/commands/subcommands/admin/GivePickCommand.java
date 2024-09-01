package us.dxtrus.prisoncore.commands.subcommands.admin;

import org.bukkit.entity.Player;
import us.dxtrus.commons.command.BasicSubCommand;
import us.dxtrus.commons.command.Command;
import us.dxtrus.commons.command.user.CommandUser;
import us.dxtrus.prisoncore.pickaxe.PickaxeManager;

public class GivePickCommand extends BasicSubCommand {
    @Command(name = "give-pick", permission = "prisoncore.admin")
    public GivePickCommand() {
        super();
    }

    @Override
    public void execute(CommandUser commandUser, String[] strings) {
        Player player = (Player) commandUser.getAudience();
        PickaxeManager.givePickaxe(player);
    }
}
