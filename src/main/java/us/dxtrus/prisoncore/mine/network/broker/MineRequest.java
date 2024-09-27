package us.dxtrus.prisoncore.mine.network.broker;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MineRequest {
    @Expose private final String worldName;
    @Expose private final String performingServer;
}
