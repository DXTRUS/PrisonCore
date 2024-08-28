package us.dxtrus.prisoncore.mine.models;

import us.dxtrus.commons.database.DatabaseObject;

import java.util.UUID;

public interface Mine extends DatabaseObject {
    boolean isLoaded();
    String getServer();
    UUID getOwner();
    String getWorldName();
    LocRef getSpawnLocation();
    LocRef getNpcLocation();

    LocRef getCenter();
    int getSize();

    void setLoaded(boolean loaded);
    void setServer(String server);
    void setNpcLocation(LocRef npc);
    void setSize(int size);
}
