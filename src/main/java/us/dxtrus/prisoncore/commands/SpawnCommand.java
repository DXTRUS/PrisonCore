package us.dxtrus.prisoncore.commands;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import us.dxtrus.commons.command.BukkitCommand;
import us.dxtrus.commons.command.Command;
import us.dxtrus.commons.command.user.CommandUser;
import us.dxtrus.prisoncore.config.Config;
import us.dxtrus.prisoncore.config.Lang;
import us.dxtrus.prisoncore.locations.Loc;
import us.dxtrus.prisoncore.locations.Location;
import us.dxtrus.prisoncore.locations.Locations;
import us.dxtrus.prisoncore.mine.models.Server;
import us.dxtrus.prisoncore.mine.models.ServerType;
import us.dxtrus.prisoncore.mine.network.ServerManager;
import us.dxtrus.prisoncore.mine.network.TransferManager;
import us.dxtrus.prisoncore.util.MessageUtils;


public class SpawnCommand extends BukkitCommand {
    @Command(name = "spawn", permission = "prisoncore.spawn", async = true)
    public SpawnCommand(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandUser commandUser, String label, String[] strings) {
        if (!Config.getInstance().getCommands().isSpawn()) {
            MessageUtils.send(commandUser.getAudience(), Lang.getInstance().getCommand().getDisabled());
            return;
        }
        Player player = (Player) commandUser.getAudience();
        if (ServerManager.getInstance().getThisServer().getType() == ServerType.SPAWN) {
            Loc loc = Locations.getLocation(Location.SPAWN);
            org.bukkit.Location location = loc.toBukkit();
            location.setYaw(90);
            location.setPitch(0);
            player.teleportAsync(location).thenAccept(success -> {
                MessageUtils.send(player, Lang.getInstance().getCommand().getSpawn().getTeleportComplete());
            });
            return;
        }

        Server server = ServerManager.getInstance().getRandomServer(ServerType.SPAWN);
        MessageUtils.send(commandUser.getAudience(), Lang.getInstance().getCommand().getSpawn().getTeleport());
        TransferManager.getInstance().addPlayerForTransfer(player, server,
                TransferManager.TransferReason.LOCATION_TELEPORT, Location.SPAWN);
        server.transferPlayer(player);
    }
}
