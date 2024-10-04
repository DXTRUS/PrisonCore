package us.dxtrus.prisoncore.locations;

import lombok.experimental.UtilityClass;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@UtilityClass
public class Locations {
    private final Map<Location, Loc> locationMap = new ConcurrentHashMap<>();

    public void registerLocation(Loc locRef) {
        locationMap.put(locRef.getLocation(), locRef);
    }

    public Loc getLocation(Location location) {
        return locationMap.getOrDefault(location, new Loc(location, "world",0,100,0, 0, 0));
    }
}
