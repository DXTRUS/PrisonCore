package us.dxtrus.prisoncore;

import org.bukkit.plugin.java.JavaPlugin;
import us.dxtrus.commons.command.BukkitCommand;
import us.dxtrus.commons.command.user.CommandUser;
import us.dxtrus.commons.utils.StringUtils;
import us.dxtrus.prisoncore.config.Lang;

public abstract class PrisonCommand extends BukkitCommand {
    public PrisonCommand(JavaPlugin plugin) {
        super(plugin);
    }

    public void sendError(ErrorType type, CommandUser user, String... replacements){
        Lang.Errors errors = Lang.getInstance().getErrors();

        user.sendMessage(StringUtils.modernMessage(switch(type) {
            case NO_PERMISSION -> errors.getNoPermission();
            case MUST_BE_PLAYER -> errors.getMustBePlayer();
            case MUST_BE_CONSOLE -> errors.getMustBeConsole();
            case INVALID_ARGS -> errors.getInvalidCommandArgs();
            case ARGS_REQUIRED -> {
                String msg = errors.getArgsRequired();
                if (replacements.length < 1) yield msg;

                for (int i = 0; i < replacements.length; i++) msg = msg.replace("{" + i + "}", replacements[i]);
                yield msg;
            }

            default -> errors.getUnknown();
        }));
    }

    public enum ErrorType {
        NO_PERMISSION,
        MUST_BE_PLAYER,
        MUST_BE_CONSOLE,
        INVALID_ARGS,
        ARGS_REQUIRED
    }
}
