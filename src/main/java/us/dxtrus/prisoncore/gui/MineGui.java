package us.dxtrus.prisoncore.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import us.dxtrus.commons.gui.ItemBuilder;
import us.dxtrus.prisoncore.config.Lang;
import us.dxtrus.prisoncore.mine.MineManager;
import us.dxtrus.prisoncore.mine.models.PrivateMine;
import us.dxtrus.prisoncore.mine.network.ServerManager;
import us.dxtrus.prisoncore.util.MessageUtils;
import us.dxtrus.prisoncore.util.StringUtil;

public class MineGui extends FastInvImproved {
    private static MineGui instance;

    public MineGui() {
        super(StringUtil.guiRows(5), Lang.getInstance().getCommand().getMine().getGuiTitle());

        setFillerMaterial(Material.GRAY_STAINED_GLASS_PANE).fill();
        Lang.Command.Mine lang = Lang.getInstance().getCommand().getMine();

        setItem(11, new ItemBuilder(Material.EMERALD_BLOCK)
                .name(StringUtil.tl(lang.getResetMineItem()))
                .build(), this::resetMine);
        setItem(13, new ItemBuilder(Material.OAK_DOOR)
                .name(StringUtil.tl(lang.getGotoMineItem()))
                .build(), this::gotoMine);
        setItem(15, new ItemBuilder(Material.END_CRYSTAL)
                .name(StringUtil.tl(lang.getPrestigeMineItem()))
                .lore(StringUtil.tl(lang.getPrestigeMineLore()).replace("{0}", "1"))
                .build(), this::prestige);

        // 21, 23

        setItem(31, new ItemBuilder(Material.COAL_ORE)
                .name(StringUtil.tl(lang.getPickBlocksItem()))
                .lore(StringUtil.tl(lang.getPickBlocksLore()))
                .build(), this::pickBlockGui);
    }

    void gotoMine(InventoryClickEvent event) {
        Player player = ((Player) event.getWhoClicked());
        PrivateMine temp = MineManager.getInstance().getMine(player.getUniqueId());
        MessageUtils.send(player, Lang.getInstance().getCommand().getMine().getTeleport());
        MineManager.getInstance().load(temp).thenAccept(mine -> {
            if (mine.getServer().equals(ServerManager.getInstance().getThisServer())) { // handle local
                mine.connectLocal(player);
                return;
            }
            mine.connect(player);
        });
        event.getWhoClicked().closeInventory();
    }

    void resetMine(InventoryClickEvent event) {
        Player player = ((Player) event.getWhoClicked());
        PrivateMine mine = MineManager.getInstance().getMine(player.getUniqueId());
        if (!mine.getServer().equals(ServerManager.getInstance().getThisServer())) {
            return;
        }
        mine.getLinkage().reset();
        MessageUtils.send(player, Lang.getInstance().getCommand().getMine().getReset());
        event.getWhoClicked().closeInventory();
    }

    void prestige(InventoryClickEvent event) {
        // TODO: Add confirmation for prestige
    }

    void pickBlockGui(InventoryClickEvent event) {
        /*
            // TODO:
            Open block GUI if level>= max decided level,
            If not, send message and close gui
         */


    }
}
