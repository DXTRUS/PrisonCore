package us.dxtrus.prisoncore.mine.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import us.dxtrus.commons.config.SerializeWith;

@Getter
@AllArgsConstructor
@SerializeWith(serializer = MineMaterialSerializer.class)
public class MineMaterial {
    private final Material material;
    private final double percentage;
}