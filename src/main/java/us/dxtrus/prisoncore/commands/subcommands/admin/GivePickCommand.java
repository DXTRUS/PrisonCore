package us.dxtrus.prisoncore.commands.subcommands.admin;

import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.dxtrus.commons.command.BasicSubCommand;
import us.dxtrus.commons.command.Command;
import us.dxtrus.commons.command.user.CommandUser;
import us.dxtrus.commons.utils.StringUtils;
import us.dxtrus.prisoncore.PickaxeManager;
import us.dxtrus.prisoncore.util.LoreHandler;

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
