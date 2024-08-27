package us.dxtrus.prisoncore.exceptions;

import lombok.Getter;

@Getter
public class MineLoadException extends RuntimeException {
    private final MineLoadFailReason failReason;
    public MineLoadException(String message, MineLoadFailReason failReason) {
        this.failReason = failReason;
    }
}
