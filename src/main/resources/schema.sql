CREATE TABLE IF NOT EXISTS `ranks` (`id` INT NOT NULL AUTO_INCREMENT , `uuid` TEXT NOT NULL , `rank` TEXT NOT NULL DEFAULT 'default' , `expires_at` BIGINT NOT NULL )