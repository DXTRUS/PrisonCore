package us.dxtrus.prisoncore.config;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import us.dxtrus.commons.config.Configuration;
import us.dxtrus.commons.config.NameFormatters;
import us.dxtrus.commons.config.YamlConfigurationProperties;
import us.dxtrus.commons.config.YamlConfigurations;
import us.dxtrus.prisoncore.PrisonCore;

import java.io.File;
import java.nio.charset.StandardCharsets;

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


    private Commands commands = new Commands();

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Commands {
        private boolean main = true;
        private boolean home = true;
        private boolean reset = true;
    }

    private Sql sql = new Sql();

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Sql {
        private String host = "127.0.0.1";
        private int port = 3306;
        private String password = "root";
        private String username = "root";
        private String database = "prisoncore";
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