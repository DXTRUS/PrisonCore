package us.dxtrus.prisoncore.ranks;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RankManager {
    private static RankManager instance;


    public static RankManager getInstance() {
        return instance == null ? instance = new RankManager() : instance;
    }
}
