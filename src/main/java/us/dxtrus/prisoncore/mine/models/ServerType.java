package us.dxtrus.prisoncore.mine.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ServerType {
    SPAWN("Spawn Server"),
    MINE("Mine Server"),
    ;
    private final String name;
}
