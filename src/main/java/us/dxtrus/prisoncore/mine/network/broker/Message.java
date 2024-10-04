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
        MINE_CREATE,
        MINE_CREATE_RESPONSE,

        MINE_LOAD,
        MINE_LOAD_RESPONSE,

        MINE_UNLOAD,
        MINE_UNLOAD_RESPONSE,

        UPDATE_CACHE,
        UPDATE_LOCATIONS,

        NOTIFICATION,
        BROADCAST,
        RELOAD,
        TOGGLE,
        HEARTBEAT,
    }
}
