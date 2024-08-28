package us.dxtrus.prisoncore.config;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import us.dxtrus.commons.config.*;
import us.dxtrus.prisoncore.PrisonCore;
import us.dxtrus.prisoncore.mine.models.MineMaterial;
import us.dxtrus.prisoncore.mine.network.loadbalancer.Distributor;
import us.dxtrus.prisoncore.storage.DatabaseType;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Getter
@Configuration
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("FieldMayBeFinal")
public class Config {
    private static Config instance;

    private static final String CONFIG_HEADER = """
            #########################################
            #                PrisonCore             #
            #########################################
            """;

    private static final YamlConfigurationProperties PROPERTIES = YamlConfigurationProperties.newBuilder()
            .charset(StandardCharsets.UTF_8)
            .setNameFormatter(NameFormatters.LOWER_KEBAB_CASE)
            .header(CONFIG_HEADER).build();

    private Map<Integer, List<MineMaterial>> ranks = Map.of(0, List.of(
            new MineMaterial(Material.NETHERITE_BLOCK, 80),
            new MineMaterial(Material.ANCIENT_DEBRIS, 10),
            new MineMaterial(Material.BLACK_CONCRETE, 10)
    ),1, List.of(
            new MineMaterial(Material.GREEN_CONCRETE, 80),
            new MineMaterial(Material.SLIME_BLOCK, 10),
            new MineMaterial(Material.LIME_TERRACOTTA, 10)
    ));

    private Commands commands = new Commands();

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Commands {
        private boolean main = true;
        private boolean home = true;
        private boolean reset = true;
    }

    private Storage storage = new Storage();

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Storage {
        @Comment("Allowed: MYSQL, MARIADB")
        private DatabaseType type = DatabaseType.MARIADB;

        private String host = "localhost";
        private int port = 3306;
        private String database = "PrisonCore";
        private String username = "root";
        private String password = "";
        private boolean useSsl = false;
    }

    private Servers servers = new Servers();

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Servers {
        private boolean singleInstance = true;

        @Comment("Allowed: LOWEST_PLAYER, LOWEST_USAGE, ROUND_ROBIN, RANDOM")
        private Distributor distributionRule = Distributor.LOWEST_PLAYER;

        private List<String> mineServers = List.of(
                "op-prisons-mines-01",
                "op-prisons-mines-02"
        );
        private List<String> spawnServers = List.of(
                "op-prisons-spawn-01"
        );
    }

    private Redis redis = new Redis();

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Redis {
        private String host = "localhost";
        private int port = 3306;
        private String password = "";
    }

    public static void reload() {
        instance = YamlConfigurations.load(new File(PrisonCore.getInstance().getDataFolder(), "config.yml").toPath(), Config.class, PROPERTIES);
    }

    public static Config getInstance() {
        if (instance != null) {
            return instance;
        }

        return instance = YamlConfigurations.update(new File(PrisonCore.getInstance().getDataFolder(), "config.yml").toPath(), Config.class, PROPERTIES);
    }
}