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

        setItem(11, new ItemBuilder(Material.EMERALD_BLOCK)
                .name(StringUtil.tl(Lang.getInstance().getCommand().getMine().getResetMineItem()))
                .build(), this::resetMine);
        setItem(13, new ItemBuilder(Material.OAK_DOOR)
                .name(StringUtil.tl(Lang.getInstance().getCommand().getMine().getGotoMineItem()))
                .build(), this::gotoMine);


        setItem(31, new ItemBuilder(Material.COAL_ORE)
                .name(StringUtil.tl(Lang.getInstance().getCommand().getMine().getPickBlocksItem()))
                .lore(StringUtil.tl(Lang.getInstance().getCommand().getMine().getPickBlocksLore()))
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

    void pickBlockGui(InventoryClickEvent event) {

    }

    public static MineGui getInstance() {
        return instance == null ? instance = new MineGui(Lang.getInstance().getCommand().getMine().getGuiTitle()) : instance;
    }
}
