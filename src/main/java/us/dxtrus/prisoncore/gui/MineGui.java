package us.dxtrus.prisoncore.gui;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import us.dxtrus.commons.gui.ItemBuilder;
import us.dxtrus.prisoncore.config.Lang;
import us.dxtrus.prisoncore.util.MessageUtils;
import us.dxtrus.prisoncore.util.StringUtil;

public class MineGui extends FastInvImproved {
    private static MineGui instance;

    public MineGui(String title) {
        super(StringUtil.guiRows(5), title);

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
        // TODO: Teleport to mine
        MessageUtils.send(event.getWhoClicked(), Lang.getInstance().getCommand().getMine().getTeleport());

        open((Player) event.getWhoClicked());
        event.getWhoClicked().closeInventory();
    }

    void resetMine(InventoryClickEvent event) {
        // TODO: Reset mine
        MessageUtils.send(event.getWhoClicked(), Lang.getInstance().getCommand().getMine().getReset());
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

    public static MineGui getInstance() {
        return instance == null ? instance = new MineGui(Lang.getInstance().getCommand().getMine().getGuiTitle()) : instance;
    }
}
