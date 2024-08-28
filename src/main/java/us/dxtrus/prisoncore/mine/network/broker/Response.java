package us.dxtrus.prisoncore.mine.network.broker;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Response {
    private final ResponseType responseType;
    private final String mineWorld;
    private final String trackingCode;

    public enum ResponseType {
        SUCCESS,
        FAIL_GENERIC,
        FAIL_NO_WORLD,
        FAIL_CORRUPTED,
        FAIL_OLD
    }
}
