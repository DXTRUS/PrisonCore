package us.dxtrus.prisoncore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandMineAdmin extends CoreCommand {
    @Override
    public boolean executePlayer(Player sender, Command cmd, String label, String[] args) {
        return false;
    }

    @Override
    public boolean executeConsole(CommandSender sender, Command cmd, String label, String[] args) {
        return false;
    }
}
