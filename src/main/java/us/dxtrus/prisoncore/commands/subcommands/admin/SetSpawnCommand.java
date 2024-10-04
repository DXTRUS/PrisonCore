package us.dxtrus.prisoncore.commands.subcommands.admin;

import org.bukkit.entity.Player;
import us.dxtrus.commons.command.BasicSubCommand;
import us.dxtrus.commons.command.Command;
import us.dxtrus.commons.command.user.CommandUser;
import us.dxtrus.prisoncore.PrisonCore;
import us.dxtrus.prisoncore.locations.Loc;
import us.dxtrus.prisoncore.locations.Location;
import us.dxtrus.prisoncore.locations.Locations;
import us.dxtrus.prisoncore.mine.network.broker.Message;
import us.dxtrus.prisoncore.mine.network.broker.Payload;
import us.dxtrus.prisoncore.storage.StorageManager;

public class SetSpawnCommand extends BasicSubCommand {
    @Command(name = "setspawn", permission = "prisoncore.admin", async = true)
    public SetSpawnCommand() {
        super();
    }

    @Override
    public void execute(CommandUser commandUser, String[] strings) {
        Player player = (Player) commandUser.getAudience();
        Loc loc = new Loc(Location.SPAWN, player.getWorld().getName(),
                player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ(),
                player.getPitch(), player.getYaw());
        Locations.registerLocation(loc);
        StorageManager.getInstance().save(Loc.class, loc);
        Message.builder()
                .type(Message.Type.UPDATE_LOCATIONS)
                .payload(Payload.empty())
                .build().send(PrisonCore.getInstance().getBroker());
        commandUser.sendMessage("Spawn Location Updated!");
    }
}
