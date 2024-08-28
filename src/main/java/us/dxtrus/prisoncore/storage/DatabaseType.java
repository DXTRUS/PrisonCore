package us.dxtrus.prisoncore.storage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DatabaseType {
    MYSQL("mysql", "MySQL", "us.dxtrus.commons.shaded.mysql.jdbc.Driver"),
    MARIADB("mariadb", "MariaDB", "us.dxtrus.commons.shaded.mariadb.jdbc.Driver"),
    ;

    private final String id;
    private final String friendlyName;
    private final String driverClass;
}
