package us.dxtrus.prisoncore.pickaxe.enchants.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import us.dxtrus.prisoncore.mine.models.PrivateMine;

@Getter
@AllArgsConstructor
public final class EnchantTriggerData {
    private final Player player;
    private final ItemStack pickaxe;
    private final int level;
    private final PrivateMine mine;
    private final Event callingEvent;
}
