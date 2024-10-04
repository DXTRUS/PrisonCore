package us.dxtrus.prisoncore.locations;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import org.bukkit.Bukkit;
import us.dxtrus.prisoncore.mine.models.Angled;
import us.dxtrus.prisoncore.mine.models.LocRef;

@Getter
public class Loc extends LocRef implements Angled {
    @Expose
    private final Location location;
    @Expose
    private final String worldName;
    @Expose
    private final double pitch;
    @Expose
    private final double yaw;

    public Loc(Location location, String worldName, double x, double y, double z, double pitch, double yaw) {
        super(x, y, z);
        this.location = location;
        this.worldName = worldName;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public org.bukkit.Location toBukkit() {
        return toBukkit(Bukkit.getWorld(worldName));
    }
}
