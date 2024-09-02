package us.dxtrus.prisoncore.commands.subcommands.admin;

import org.bukkit.entity.Player;
import us.dxtrus.commons.command.BasicSubCommand;
import us.dxtrus.commons.command.Command;
import us.dxtrus.commons.command.user.CommandUser;
import us.dxtrus.commons.utils.StringUtils;
import us.dxtrus.prisoncore.pickaxe.PickaxeManager;
import us.dxtrus.prisoncore.pickaxe.enchants.EnchantManager;
import us.dxtrus.prisoncore.pickaxe.enchants.models.Enchant;

import java.util.List;

public class AddEnchantCommand extends BasicSubCommand {
    @Command(name = "enchant", permission = "prisoncore.admin")
    public AddEnchantCommand() {
        super();
    }

    @Override
    public void execute(CommandUser commandUser, String[] strings) {
        Player player = (Player) commandUser.getAudience();
        if (strings.length < 2) {
            player.sendMessage(StringUtils.modernMessage("&cUsage: /nfy enchant <enchant> <level>"));
            return;
        }
        Enchant enchant = EnchantManager.getInstance().getEnchants().get(strings[0]);
        if (enchant == null) {
            player.sendMessage(StringUtils.modernMessage("&cNo enchant found..."));
            return;
        }
        int level = Integer.parseInt(strings[1]);
        if (level > enchant.getMaxLevel()) {
            player.sendMessage(StringUtils.modernMessage("&cLevel too high! (Max: %s)".formatted(enchant.getMaxLevel())));
            return;
        }
        PickaxeManager.enchant(player, enchant, level);
    }

    @Override
    public List<String> tabComplete(CommandUser commandUser, String[] strings) {
        return EnchantManager.getInstance().getEnchants().keySet().stream().toList();
    }
}