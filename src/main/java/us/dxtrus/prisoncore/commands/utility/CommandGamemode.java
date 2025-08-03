package us.dxtrus.prisoncore.commands.utility;

import org.bukkit.GameMode;
import org.bukkit.plugin.java.JavaPlugin;
import us.dxtrus.commons.command.Command;
import us.dxtrus.commons.command.user.BukkitUser;
import us.dxtrus.commons.command.user.CommandUser;
import us.dxtrus.commons.utils.StringUtils;
import us.dxtrus.prisoncore.PrisonCommand;
import us.dxtrus.prisoncore.config.Lang;

import java.util.HashMap;
import java.util.Map;

public class CommandGamemode extends PrisonCommand {
    @Command(name = "gamemode", permission = "prisoncore.admin", aliases = {"gm", "gmc", "gms", "gmsp", "gma"})
    public CommandGamemode(JavaPlugin plugin) {
        super(plugin);
    }

    private static final Map<String, GameMode> MODE_ALIASES = new HashMap<>();
    static {
        MODE_ALIASES.put("c", GameMode.CREATIVE);
        MODE_ALIASES.put("gmc", GameMode.CREATIVE);

        MODE_ALIASES.put("s", GameMode.SURVIVAL);
        MODE_ALIASES.put("gms", GameMode.SURVIVAL);

        MODE_ALIASES.put("a", GameMode.ADVENTURE);
        MODE_ALIASES.put("gma", GameMode.ADVENTURE);

        MODE_ALIASES.put("sp", GameMode.SPECTATOR);
        MODE_ALIASES.put("gmsp", GameMode.SPECTATOR);
    }



    @Override
    public void execute(CommandUser sender, String alias, String[] args) {
        if (!sender.hasPermission("prisoncore.admin")) {
            sendError(ErrorType.NO_PERMISSION, sender);
            return;
        }

        if(sender instanceof BukkitUser user) {
            if (MODE_ALIASES.containsKey(alias.toLowerCase())) {
                setMode(user, MODE_ALIASES.get(alias.toLowerCase()));
                return;
            }
            if(args.length < 1) {
                sendError(ErrorType.ARGS_REQUIRED, user, "a gamemode");
                return;
            }

            String specified = args[0];
            try {
                GameMode mode = MODE_ALIASES.getOrDefault(specified.toLowerCase(), null);
                if (mode == null) mode = GameMode.valueOf(specified.toUpperCase());

                setMode(user, mode);
            } catch (EnumConstantNotPresentException | IllegalArgumentException ex) {
                sendError(ErrorType.ARGS_REQUIRED, user, "a valid gamemode");
            }

            return;
        }

        sendError(ErrorType.MUST_BE_PLAYER, sender);
    }

    private void setMode(BukkitUser user, GameMode mode) {
        user.getAudience().setGameMode(mode);
        user.sendMessage(
                StringUtils.modernMessage(
                        Lang.getInstance().getCommand().getGamemodeUpdated().replace("{0}", mode.name().toLowerCase())
                )
        );
    }
}
