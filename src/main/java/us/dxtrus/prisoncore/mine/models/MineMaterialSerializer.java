package us.dxtrus.prisoncore.mine.models;

import org.bukkit.Material;
import us.dxtrus.commons.config.Serializer;

public class MineMaterialSerializer implements Serializer<MineMaterial, String> {
    @Override
    public String serialize(MineMaterial mineMaterial) {
        return mineMaterial.getMaterial().name() + ":" + mineMaterial.getPercentage();
    }

    @Override
    public MineMaterial deserialize(String element) {
        String[] parts = element.split(":");
        Material material;
        try {
            material = Material.valueOf(parts[0].toUpperCase());
        } catch (Exception e) {
            material = Material.STONE;
        }
        double percent = Double.parseDouble(parts[1]);
        return new MineMaterial(material, percent);
    }
}
