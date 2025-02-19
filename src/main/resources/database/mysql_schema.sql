SET DEFAULT_STORAGE_ENGINE = INNODB;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE IF NOT EXISTS `locations`
(
    `name` VARCHAR(40) NOT NULL PRIMARY KEY,
    `loc`  TEXT        NOT NULL
) CHARACTER SET utf8
  COLLATE utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `mines`
(
    `owner`    VARCHAR(36) NOT NULL PRIMARY KEY,
    `spawnLoc` TEXT        NOT NULL,
    `center`   TEXT        NOT NULL,
    `loaded`   BOOLEAN     NOT NULL,
    `server`   TEXT        NOT NULL,
    `npcLoc`   TEXT        NOT NULL,
    `level`    INTEGER     NOT NULL
) CHARACTER SET utf8
  COLLATE utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `ranks`
(
    `id`         INT    NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `uuid`       TEXT   NOT NULL,
    `rank`       TEXT   NOT NULL DEFAULT 'default',
    `expires_at` BIGINT NOT NULL
) CHARACTER SET utf8
  COLLATE utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `statistics`
(
    `id`            INT  NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `uuid`          TEXT NOT NULL,
    `tokens`        TEXT NOT NULL DEFAULT '0',
    `gems`          TEXT NOT NULL DEFAULT '0',
    `blocks_broken` TEXT NOT NULL DEFAULT '0'
) CHARACTER SET utf8
  COLLATE utf8_unicode_ci;