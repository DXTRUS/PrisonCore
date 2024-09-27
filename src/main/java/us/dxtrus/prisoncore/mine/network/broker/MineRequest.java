package us.dxtrus.prisoncore.mine.network.broker;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class MineRequest {
    @Expose private final UUID mine;
    @Expose private final String performingServer;

    @Override
    public String toString() {
        return "MineRequest[mine=" + mine + ", performingServer=" + performingServer + "]";
    }
}
