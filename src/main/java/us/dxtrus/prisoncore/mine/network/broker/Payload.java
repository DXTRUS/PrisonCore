package us.dxtrus.prisoncore.mine.network.broker;

import com.google.gson.annotations.Expose;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class Payload {
    @Nullable
    @Expose
    private UUID uuid;

    @Nullable
    @Expose
    private Notification notification;

    @Nullable
    @Expose
    private Broadcast broadcast;

    @Nullable
    @Expose
    private String string;

    @Nullable
    @Expose
    public Response response;

    /**
     * Returns an empty cross-server message payload.
     *
     * @return an empty payload
     */
    @NotNull
    public static Payload empty() {
        return new Payload();
    }

    /**
     * Returns a payload containing a {@link UUID}.
     *
     * @param uuid the uuid to send
     * @return a payload containing the uuid
     */
    @NotNull
    public static Payload withUUID(@NotNull UUID uuid) {
        final Payload payload = new Payload();
        payload.uuid = uuid;
        return payload;
    }

    /**
     * Returns a payload containing a message and a recipient.
     *
     * @param playerUUID the player to send the message to
     * @param message the message to send
     * @return a payload containing the message
     */
    @NotNull
    public static Payload withNotification(@NotNull UUID playerUUID, @NotNull String message) {
        final Payload payload = new Payload();
        payload.notification = new Notification(playerUUID, message);
        return payload;
    }

    /**
     * Returns a payload containing a message to send to the entire network.
     *
     * @param message the message to send
     * @return a payload containing the message
     */
    @NotNull
    public static Payload withBroadcast(@NotNull String message, @Nullable String clickCommand) {
        final Payload payload = new Payload();
        payload.broadcast = new Broadcast(message, clickCommand);
        return payload;
    }

    /**
     * Returns a payload containing a string.
     *
     * @param string the string
     * @return a payload containing the string
     */
    @NotNull
    public static Payload withString(@NotNull String string) {
        final Payload payload = new Payload();
        payload.string = string;
        return payload;
    }

    /**
     * Returns a payload containing a string.
     *
     * @param string the string
     * @return a payload containing the string
     */
    @NotNull
    public static Payload withResponse(@NotNull Response response) {
        final Payload payload = new Payload();
        payload.response = response;
        return payload;
    }

    public Optional<UUID> getUUID() {
        return Optional.ofNullable(uuid);
    }

    public Optional<Notification> getNotification() {
        return Optional.ofNullable(notification);
    }

    public Optional<Broadcast> getBroadcast() { return Optional.ofNullable(broadcast); }

    public Optional<String> getString() {
        return Optional.ofNullable(string);
    }

    public Optional<Response> getResponse() {
        return Optional.ofNullable(response);
    }
}
