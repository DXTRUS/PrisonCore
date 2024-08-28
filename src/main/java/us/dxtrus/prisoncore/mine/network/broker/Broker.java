package us.dxtrus.prisoncore.mine.network.broker;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;
import us.dxtrus.commons.utils.StringUtils;
import us.dxtrus.commons.utils.TaskManager;
import us.dxtrus.prisoncore.PrisonCore;
import us.dxtrus.prisoncore.config.Config;
import us.dxtrus.prisoncore.config.Lang;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Broker {
    protected final PrisonCore plugin;
    protected final Gson gson;
    public static final Map<String, CompletableFuture<Response>> responses = new ConcurrentHashMap<>();

    protected Broker(@NotNull PrisonCore plugin) {
        this.plugin = plugin;
        this.gson = new Gson();
    }

    protected void handle(@NotNull Message message) {
        switch (message.getType()) {

            case MINE_LOAD_RESPONSE -> message.getPayload()
                    .getResponse().ifPresent(response -> {
                        CompletableFuture<Response> future = responses.remove(response.getMineWorld());
                        if (future == null) {
                            // do nothing, this instance doesnt hold the handler for the world load.
                            return;
                        }
                        future.complete(response);
                    });

            case NOTIFICATION -> message.getPayload()
                    .getNotification().ifPresentOrElse(notification -> {
                        Player player = Bukkit.getPlayer(notification.getPlayer());
                        if (player == null) return;
                        player.sendMessage(StringUtils.modernMessage(notification.getMessage()));
                    }, () -> {
                        throw new IllegalStateException("Notification message received with no notification info!");
                    });

            case BROADCAST -> message.getPayload()
                    .getBroadcast().ifPresentOrElse(broadcast -> {
                        TaskManager.runAsync(plugin, () -> {
                            Component textComponent = MiniMessage.miniMessage().deserialize(StringUtils.legacyToMiniMessage(broadcast.getMessage()));
                            if (broadcast.getClickCommand() != null) {
                                textComponent = textComponent.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, broadcast.getClickCommand()));
                            }
                            for (Player announce : Bukkit.getOnlinePlayers()) {
                                announce.sendMessage(textComponent);
                            }
                        });
                    }, () -> {
                        throw new IllegalStateException("Broadcast message received with no broadcast info!");
                    });

            case RELOAD -> {
                Config.reload();
                Lang.reload();
            }

            case TOGGLE -> message.getPayload()
                    .getString().ifPresentOrElse(command -> {

                    }, () -> {
                        throw new IllegalStateException("Broadcast message received with no broadcast info!");
                    });

            default -> throw new IllegalStateException("Unexpected value: " + message.getType());
        }
    }

    public abstract void connect();

    protected abstract void send(@NotNull Message message);

    public abstract void destroy();

    @Getter
    @AllArgsConstructor
    public enum Type {
        REDIS("Redis"),
        ;
        private final String displayName;
    }
}
