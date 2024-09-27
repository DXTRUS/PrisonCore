package us.dxtrus.prisoncore.mine.network.broker;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class Response {
    private final ResponseType responseType;
    private final UUID mine;
    private final String trackingCode;

    public enum ResponseType {
        SUCCESS,
        FAIL_GENERIC,
        FAIL_NO_WORLD,
        FAIL_CORRUPTED,
        FAIL_OLD
    }

    @Override
    public String toString() {
        return "Response[type='%s', mine='%s', trackingCode='%s']"
                .formatted(responseType, mine, trackingCode);
    }
}
