package us.dxtrus.prisoncore;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import us.dxtrus.commons.gui.FastInvManager;
import us.dxtrus.prisoncore.commands.CommandMine;

public final class PrisonCore extends JavaPlugin {

    @Getter private static PrisonCore instance;

    @Override
    public void onEnable() {
        instance = this;

        FastInvManager.register(this);
        getCommand("mine").setExecutor(new CommandMine());
    }


}
