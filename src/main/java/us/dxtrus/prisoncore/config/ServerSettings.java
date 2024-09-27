package us.dxtrus.prisoncore.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import us.dxtrus.commons.config.*;
import us.dxtrus.prisoncore.PrisonCore;
import us.dxtrus.prisoncore.mine.models.ServerType;

import java.io.File;
import java.nio.charset.StandardCharsets;

@Getter
@Configuration
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("FieldMayBeFinal")
public class ServerSettings {
    private static ServerSettings instance;

    private static final String CONFIG_HEADER = """
            #########################################
            #                  Server               #
            #       This file must be unique!       #
            #########################################
            """;

    private static final YamlConfigurationProperties PROPERTIES = YamlConfigurationProperties.newBuilder()
            .charset(StandardCharsets.UTF_8)
            .setNameFormatter(NameFormatters.LOWER_KEBAB_CASE)
            .header(CONFIG_HEADER).build();

    private String serverName = "prison-lobby-01";

    @Comment("Types: SPAWN, MINE")
    private ServerType serverType = ServerType.SPAWN;

    public static ServerSettings getInstance() {
        if (instance != null) {
            return instance;
        }
        return instance = YamlConfigurations.update(new File(PrisonCore.getInstance().getDataFolder(), "server.yml").toPath(), ServerSettings.class, PROPERTIES);
    }
}