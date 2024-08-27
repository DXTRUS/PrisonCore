package us.dxtrus.prisoncore.mines.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;

@Getter
@AllArgsConstructor
public class LocRef {
    private final double x;
    private final double y;
    private final double z;

    public static LocRef fromLocation(Location location) {
        return new LocRef(location.getBlockX() + 0.5, location.getBlockY() + 0.5, location.getBlockZ() + 0.5);
    }

    public Location toBukkit(World world) {
        return new Location(world, x, y, z);
    }


    public String serialize() {
        return "%s,%s,%s".formatted(x, y, z);
    }

    public static LocRef deserialize(String serialized) {
        String[] split = serialized.split(",");
        return new LocRef(Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]));
    }

    @Override
    public String toString() {
        return "X %s, Y %s, Z %s".formatted(x, y, z);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LocRef otherLoc)) return false;
        return otherLoc.getX() == this.getX() && otherLoc.getY() == this.getY() && otherLoc.getZ() == this.getZ();
    }

    @Override
    public int hashCode() {
        return Double.hashCode(x) + Double.hashCode(y) + Double.hashCode(z);
    }
}