package us.dxtrus.prisoncore.mine.models;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Getter
@Setter
public class MineImpl implements Mine {
    private boolean loaded;
    private String server;
    private final UUID owner;
    private final String worldName;

    private final LocRef spawnLocation;
    private LocRef npcLocation;

    private final LocRef center;
    private int size = 12;

    public MineImpl(@NotNull UUID owner) {
        this.owner = owner;
        this.worldName = owner.toString();
        this.spawnLocation = new LocRef(0, 64, 0);
        this.center = new LocRef(80, 64, 0);
    }
}
