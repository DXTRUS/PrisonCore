package us.dxtrus.prisoncore.util;

import org.bukkit.command.CommandSender;
import us.dxtrus.commons.utils.StringUtils;

public final class MessageUtils {
    public static void send(CommandSender sender, String message, Object... replacements) {
        sender.sendMessage(StringUtils.modernMessage(message, replacements));
    }
}
