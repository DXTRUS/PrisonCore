package us.dxtrus.prisoncore.commands;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import us.dxtrus.prisoncore.PrisonCore;
import us.dxtrus.prisoncore.util.Disableable;

import java.util.logging.Level;

@Setter
@Getter
public abstract class CoreCommand implements CommandExecutor, Disableable {
    private boolean disabled;

    @Override
    public final boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (isDisabled()) {
            return true;
        }

        boolean result = true;
        try {
            result = sender instanceof Player ? executePlayer((Player) sender, command, label, args) : executeConsole(sender, command, label, args);
        } catch(Exception ex) {
            PrisonCore.getInstance().getLogger().log(Level.SEVERE, "Command execution for prison command "
                    + command + " (" + getClass().getName() + ") has failed for sender '" + sender.getName()
                    + "'. Entire command: /" + command + " " + String.join(" ", args), ex);
        }

        return result;
    }

    public abstract boolean executePlayer(Player sender, Command cmd, String label, String[] args);
    public abstract boolean executeConsole(CommandSender sender, Command cmd, String label, String[] args);
}
