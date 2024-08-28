package us.dxtrus.prisoncore.mine.models;

import us.dxtrus.commons.database.DatabaseObject;

import java.util.UUID;

public interface PrivateWorld extends DatabaseObject {
    boolean isLoaded();
    Server getServer();
    UUID getOwner();
    String getWorldName();
    LocRef getSpawnLocation();
    LocRef getNpcLocation();

    void setLoaded(boolean loaded);
    void setServer(Server server);
    void setNpcLocation(LocRef npc);
}
