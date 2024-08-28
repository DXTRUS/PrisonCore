package us.dxtrus.prisoncore.mine.network.broker;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class Message {
    @Expose private Type type;
    @Expose private Payload payload;

    public void send(Broker broker) {
        if (broker == null) return;
        broker.send(this);
    }

    public enum Type {
        ISLAND_CREATE,
        ISLAND_CREATE_RESPONSE,

        ISLAND_DELETE,
        ISLAND_DELETE_RESPONSE,

        ISLAND_LOAD,
        ISLAND_LOAD_RESPONSE,

        ISLAND_UNLOAD,
        ISLAND_UNLOAD_RESPONSE,

        NOTIFICATION,
        BROADCAST,
        RELOAD,
        TOGGLE,
    }
}
