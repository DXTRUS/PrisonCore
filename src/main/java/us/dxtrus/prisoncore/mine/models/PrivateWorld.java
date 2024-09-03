package us.dxtrus.prisoncore.mine.models;

import us.dxtrus.commons.database.DatabaseObject;

import java.util.UUID;

public interface PrivateWorld extends DatabaseObject {
    boolean isLoaded();

    void setLoaded(boolean loaded);

    Server getServer();

    void setServer(Server server);

    UUID getOwner();

    String getWorldName();

    LocRef getSpawnLocation();

    LocRef getNpcLocation();

    void setNpcLocation(LocRef npc);
}
