package us.dxtrus.prisoncore.mine.network.broker;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import us.dxtrus.commons.utils.StringUtils;
import us.dxtrus.commons.utils.TaskManager;
import us.dxtrus.prisoncore.PrisonCore;
import us.dxtrus.prisoncore.config.Config;
import us.dxtrus.prisoncore.config.Lang;
import us.dxtrus.prisoncore.mine.LocalMineManager;
import us.dxtrus.prisoncore.mine.MineManager;
import us.dxtrus.prisoncore.mine.models.PrivateMine;
import us.dxtrus.prisoncore.mine.network.ServerManager;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Broker {
    public static final Map<UUID, CompletableFuture<Response>> responses = new ConcurrentHashMap<>();
    protected final PrisonCore plugin;
    protected final Gson gson;

    protected Broker(@NotNull PrisonCore plugin) {
        this.plugin = plugin;
        this.gson = new Gson();
    }

    protected void handle(@NotNull Message message) {
        switch (message.getType()) {
            case MINE_LOAD -> message.getPayload()
                    .getRequest().ifPresent(request -> {
                        Bukkit.broadcastMessage(request.toString());
                        if (!request.getPerformingServer()
                                .equals(ServerManager.getInstance().getThisServer().getName())) {
                            return;
                        }
                        PrivateMine mine = MineManager.getInstance().getMine(request.getMine());
                        LocalMineManager.getInstance().load(mine);
                    });

            case MINE_UNLOAD -> message.getPayload()
                    .getRequest().ifPresent(request -> {
                        if (!request.getPerformingServer()
                                .equals(ServerManager.getInstance().getThisServer().getName())) {
                            return;
                        }
                        PrivateMine mine = MineManager.getInstance().getMine(request.getMine());
                        LocalMineManager.getInstance().unload(mine);
                    });

            case MINE_LOAD_RESPONSE, MINE_UNLOAD_RESPONSE -> message.getPayload()
                    .getResponse().ifPresent(response -> {
                        Bukkit.broadcastMessage(response.toString());
                        CompletableFuture<Response> future = responses.remove(response.getMine());
                        if (future == null) {
                            // do nothing, this instance doesn't hold the handler for the world (un)load.
                            Bukkit.broadcastMessage("abort mission, we dont want this response");
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

            case HEARTBEAT -> message.getPayload()
                    .getServer().ifPresent(server -> ServerManager.getInstance().update(server));

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
