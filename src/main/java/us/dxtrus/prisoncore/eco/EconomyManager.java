package us.dxtrus.prisoncore.eco;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EconomyManager {

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Tokens {
        private final UUID uuid;
        private BigInteger count = new BigInteger("0");

        public void give(BigInteger amount) {
            count = count.add(amount);
        }
    }

    private static Map<UUID, Tokens> tokens = new HashMap<>();

    public static Tokens getTokens(UUID uuid) {

        return tokens.containsKey(uuid) ? tokens.get(uuid) : tokens.put(uuid, new Tokens(uuid));
    }

    public static void registerPapi() {

    }
}
