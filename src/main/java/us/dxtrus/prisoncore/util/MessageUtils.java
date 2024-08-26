package us.dxtrus.prisoncore.util;

import net.kyori.adventure.audience.Audience;
import org.bukkit.command.CommandSender;
import us.dxtrus.commons.utils.StringUtils;
import us.dxtrus.prisoncore.config.Lang;

public final class MessageUtils {
    public static void send(Audience sender, String message, Object... replacements) {
        sender.sendMessage(StringUtils.modernMessage(message.replace("{prefix}", Lang.getInstance().getPrefix()), replacements));
    }
}
