package us.dxtrus.prisoncore.commands.subcommands.admin;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import us.dxtrus.commons.command.BasicSubCommand;
import us.dxtrus.commons.command.Command;
import us.dxtrus.commons.command.user.CommandUser;
import us.dxtrus.commons.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class SetStatisticCommand extends BasicSubCommand {

    @Command(name = "set-stat", permission = "prisoncore.admin")
    public SetStatisticCommand() {
        super();
    }

    private static Map<String, Class<?>> statistics = new HashMap<>();

    static {
        statistics.put("blocks_broken", Long.class);
        statistics.put("tokens", Double.class);
        statistics.put("gems", Double.class);
    }


    @Override
    public void execute(CommandUser commandUser, String[] args) {
        if (args.length < 1) {
            commandUser.sendMessage(StringUtils.modernMessage("&cYou must specify a player!"));
            return;
        }

        Optional<OfflinePlayer> optionalPlayer = Optional.ofNullable(Bukkit.getPlayer(args[0]))
                .map(p -> (OfflinePlayer) p)
                .or(() -> {
                    try {
                        UUID uuid = UUID.fromString(args[0]);
                        return Optional.of(Bukkit.getOfflinePlayer(uuid));
                    } catch (IllegalArgumentException e) {
                        return Optional.empty();
                    }
                }).filter(p -> p.hasPlayedBefore() || p.isOnline());

        if (optionalPlayer.isEmpty()) {
            commandUser.sendMessage(StringUtils.modernMessage("&cUnknown player!"));
            return;
        }

        OfflinePlayer player = optionalPlayer.get();


        Class<?> statistic = statistics.getOrDefault(args[1].toLowerCase().replace("-", "_"), null);
        if (statistic == null) {
            commandUser.sendMessage(StringUtils.modernMessage("Unknown statistic."));
            return;
        }

        switch (statistic.getSimpleName()) {
            case "String" -> {
            }
            case "Integer" -> {
            }
            case "Double" -> {
            }
            case "Float" -> {
            }
            default ->
                    commandUser.sendMessage(StringUtils.modernMessage("&cUnknown type &e" + statistic.getSimpleName()));
        }

//        TODO: Statistic setting lol
    }
}
