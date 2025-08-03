package us.dxtrus.prisoncore.commands.utility;

import org.bukkit.GameMode;
import org.bukkit.plugin.java.JavaPlugin;
import us.dxtrus.commons.command.Command;
import us.dxtrus.commons.command.user.BukkitUser;
import us.dxtrus.commons.command.user.CommandUser;
import us.dxtrus.commons.utils.TaskManager;
import us.dxtrus.prisoncore.PrisonCore;
import us.dxtrus.prisoncore.PrisonCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommandRtxOn extends PrisonCommand {
    @Command(name = "rtx-on", permission = "")
    public CommandRtxOn(JavaPlugin plugin) {
        super(plugin);
    }

    private List<UUID> onCooldown = new ArrayList<>();

    @Override
    public void execute(CommandUser sender, String alias, String[] args) {
        if (sender instanceof BukkitUser user) {
            if (onCooldown.contains(user.getUniqueId()))  return;
            if (sender.hasPermission("prisoncore.admin")) user.getAudience().setGameMode(GameMode.SURVIVAL);

            user.getAudience().setFireTicks(Integer.MAX_VALUE);
            user.sendMessage("You tried to use a 5090 and set yourself on fire.");

            onCooldown.add(user.getUniqueId());
            TaskManager.runSyncDelayed(PrisonCore.getInstance(), () -> {
                onCooldown.remove(user.getUniqueId());
            }, 120);
        }
    }
}
