package us.dxtrus.prisoncore.pickaxe.enchants;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import us.dxtrus.prisoncore.pickaxe.enchants.impl.token.JackhammerEnchant;

public class JackhammerTest {

    @Test
    public void testProcChance() {
        for (int i = 0; i <= 100; i++) {
            double chance = JackhammerEnchant.calculateChance(i);
            System.out.println(chance);
            if (i == 100) {
                Assertions.assertEquals(4, chance);
            }
        }
    }
}
