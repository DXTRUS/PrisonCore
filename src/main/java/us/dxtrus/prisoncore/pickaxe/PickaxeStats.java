package us.dxtrus.prisoncore.pickaxe;

import us.dxtrus.commons.database.DatabaseObject;
import us.dxtrus.commons.utils.Tuple;

public class PickaxeStats extends Tuple<Integer, Integer> implements DatabaseObject {
    public PickaxeStats(Integer level, Integer experience) {
        super(level, experience);
    }

    public Integer getLevel() {
        return getFirst();
    }

    public Integer getExperience() {
        return getSecond();
    }

    @Override
    public String toString() {
        return "Lvl: %s, Exp: %s".formatted(getLevel(), getExperience());
    }
}
