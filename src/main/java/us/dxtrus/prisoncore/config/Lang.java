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
public class Lang {
    private static Lang instance;

    private static final String CONFIG_HEADER = """
            #########################################
            #                PrisonCore             #
            #########################################
            """;

    private static final YamlConfigurationProperties PROPERTIES = YamlConfigurationProperties.newBuilder()
            .charset(StandardCharsets.UTF_8)
            .setNameFormatter(NameFormatters.LOWER_KEBAB_CASE)
            .header(CONFIG_HEADER).build();


    private Command command = new Command();
    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Command {
        private String noPermission = "&c&l(!)&r &fInsufficient Permission";
        private String unknownArgs = "&c&l(!)&r &fUnknown Arguments.";
        private String disabled = "&c&l(!)&r &fCommand is disabled.";

        private Mine mine = new Mine();
        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class Mine {
            private String guiTitle = "Mine Options";
            private String teleport = "&#9555ff&lMINE &8&l»&r Teleporting to mine...";
            private String reset = "&#9555ff&lMINE &8&l»&r Resetting mine...";


            private String gotoMineItem = "&aGo to Mine";
            private String resetMineItem = "&f&lReset Mine";

            private String prestigeMineItem = "&8&l&k||| &c&lPrestige &8&l&k|||";
            private String prestigeMineLore = "&7Progress to prestige &c{0}";

            private String pickBlocksItem = "&6&lPick Blocks";
            private String pickBlocksLore = "&cRequires Level: 1234";
        }

        private Reload reload = new Reload();
        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class Reload {
            private String success = "{prefix} &fReload &asuccess";
            private String fail = "{prefix} %fReload &cfailed&f. Check console.";
        }
    }

    public static void reload() {
        instance = YamlConfigurations.load(new File(PrisonCore.getInstance().getDataFolder(), "lang.yml").toPath(), Lang.class, PROPERTIES);
    }

    public static Lang getInstance() {
        if (instance != null) {
            return instance;
        }

        return instance = YamlConfigurations.update(new File(PrisonCore.getInstance().getDataFolder(), "lang.yml").toPath(), Lang.class, PROPERTIES);
    }
}