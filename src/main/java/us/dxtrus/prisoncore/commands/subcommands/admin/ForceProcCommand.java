package us.dxtrus.prisoncore.commands.subcommands.admin;

import us.dxtrus.commons.command.BasicSubCommand;
import us.dxtrus.commons.command.Command;
import us.dxtrus.commons.command.user.BukkitUser;
import us.dxtrus.commons.command.user.CommandUser;
import us.dxtrus.prisoncore.pickaxe.PickaxeManager;
import us.dxtrus.prisoncore.pickaxe.enchants.EnchantManager;
import us.dxtrus.prisoncore.pickaxe.enchants.models.Enchant;

import java.util.List;

public class ForceProcCommand extends BasicSubCommand {

    @Command(name = "force-proc", permission = "prisoncore.admin.forceproc", aliases = {"forceproc", "procnext", "proc-next"})
    public ForceProcCommand() {
        super();
    }

    @Override
    public void execute(CommandUser commandUser, String[] args) {
        if (args.length < 1) return;
        if (!(commandUser instanceof BukkitUser user)) {
            return;
        }

        Enchant enchant = EnchantManager.getInstance().getEnchants().getOrDefault(args[0].toLowerCase(), null);
        if (enchant == null) {
            user.sendMessage("Unknown enchant ID: " + args[0]);
            return;
        }

        if (!PickaxeManager.forceProc.containsKey(user.getUniqueId())) {
            PickaxeManager.forceProc.put(user.getUniqueId(), enchant);
        } else {
            PickaxeManager.forceProc.replace(user.getUniqueId(), enchant);
        }

        user.sendMessage("Next block break in mine will proc " + args[0]);
    }

    @Override
    public List<String> tabComplete(CommandUser sender, String[] args) {
        if (!sender.hasPermission("prisoncore.admin.forceproc")) return List.of();
        return EnchantManager.getInstance().getEnchants().keySet().stream().toList();
    }
}
