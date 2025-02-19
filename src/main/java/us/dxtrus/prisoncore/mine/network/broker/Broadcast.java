package us.dxtrus.prisoncore.mine.network.broker;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

@Getter
@AllArgsConstructor
public class Broadcast {
    @Expose private final String message;
    @Expose @Nullable private final String clickCommand;
}
