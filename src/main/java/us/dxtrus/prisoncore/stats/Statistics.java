package us.dxtrus.prisoncore.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import us.dxtrus.commons.database.DatabaseObject;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class Statistics implements DatabaseObject {
    private final UUID uuid;
    private BigDecimal tokens = new BigDecimal("0");
    private BigDecimal gems = new BigDecimal("0");
    private BigDecimal blocksBroken = new BigDecimal("0");

    public void giveTokens(BigDecimal amount) {
        tokens = tokens.add(amount);
    }

    public void giveTokens(long amount) {
        giveTokens(new BigDecimal(String.valueOf(amount)));
    }

    public void giveGems(BigDecimal amount) {
        gems = gems.add(amount);
    }

    public void giveGems(long amount) {
        giveGems(new BigDecimal(String.valueOf(amount)));
    }

    public void incrementBrokenBlocks() {
        blocksBroken = blocksBroken.add(new BigDecimal("1"));
    }
}