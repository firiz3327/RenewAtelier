-- --------------------------------------------------------
-- ホスト:                          127.0.0.1
-- サーバーのバージョン:                   8.0.17 - MySQL Community Server - GPL
-- サーバー OS:                      Win64
-- HeidiSQL バージョン:               11.0.0.5919
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- atelier のデータベース構造をダンプしています
CREATE DATABASE IF NOT EXISTS `atelier` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `atelier`;

--  テーブル atelier.accounts の構造をダンプしています
CREATE TABLE IF NOT EXISTS `accounts` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uuid` varchar(100) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `password` varchar(100) DEFAULT NULL,
  `level` int(11) NOT NULL DEFAULT '1',
  `exp` bigint(20) NOT NULL DEFAULT '0',
  `alchemyLevel` int(11) NOT NULL DEFAULT '1',
  `alchemyExp` int(11) NOT NULL DEFAULT '0',
  `maxHp` int(11) NOT NULL DEFAULT '28',
  `hp` int(11) NOT NULL DEFAULT '28',
  `maxMp` int(11) NOT NULL DEFAULT '39',
  `mp` int(11) NOT NULL DEFAULT '39',
  `atk` int(11) NOT NULL DEFAULT '3',
  `def` int(11) NOT NULL DEFAULT '5',
  `speed` int(11) NOT NULL DEFAULT '2',
  `money` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uuid` (`uuid`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;

-- テーブル atelier.accounts: ~4 rows (約) のデータをダンプしています
/*!40000 ALTER TABLE `accounts` DISABLE KEYS */;
INSERT INTO `accounts` (`id`, `uuid`, `email`, `password`, `level`, `exp`, `alchemyLevel`, `alchemyExp`, `maxHp`, `hp`, `maxMp`, `mp`, `atk`, `def`, `speed`, `money`) VALUES
	(1, 'a476c5ce-a07f-4330-95ac-028018e1282b', NULL, NULL, 42, 2170, 1, 0, 77, 77, 88, 88, 24, 19, 5, 999884),
	(2, 'd9c1fa1c-6662-429a-b0cf-ac8b0df4dc26', NULL, NULL, 4, 66, 1, 0, 31, 21, 42, 42, 5, 6, 2, 989804),
	(6, '235ea7dd-f11f-46a9-a58f-2ccc95cf86f2', NULL, NULL, 1, 0, 1, 0, 28, 28, 39, 39, 3, 5, 2, 0),
	(7, 'f345f790-2ccb-4ddb-9858-deb3c6bcdc29', NULL, NULL, 19, 271, 1, 0, 48, 48, 59, 59, 12, 11, 3, 0);
/*!40000 ALTER TABLE `accounts` ENABLE KEYS */;

--  テーブル atelier.bag の構造をダンプしています
CREATE TABLE IF NOT EXISTS `bag` (
  `userId` int(11) NOT NULL,
  `itemJson` json NOT NULL,
  PRIMARY KEY (`userId`),
  CONSTRAINT `userId` FOREIGN KEY (`userId`) REFERENCES `accounts` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- テーブル atelier.bag: ~0 rows (約) のデータをダンプしています
/*!40000 ALTER TABLE `bag` DISABLE KEYS */;
/*!40000 ALTER TABLE `bag` ENABLE KEYS */;

--  テーブル atelier.buffs の構造をダンプしています
CREATE TABLE IF NOT EXISTS `buffs` (
  `id` bigint(20) NOT NULL DEFAULT '0',
  `userId` int(11) NOT NULL,
  `buffValueType` varchar(50) NOT NULL,
  `level` int(11) NOT NULL,
  `buffType` varchar(50) NOT NULL,
  `duration` int(11) NOT NULL,
  `limitDuration` int(11) NOT NULL,
  `x` int(11) NOT NULL,
  `y` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `buffs_userId` (`userId`),
  CONSTRAINT `buffs_userId` FOREIGN KEY (`userId`) REFERENCES `accounts` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- テーブル atelier.buffs: ~0 rows (約) のデータをダンプしています
/*!40000 ALTER TABLE `buffs` DISABLE KEYS */;
/*!40000 ALTER TABLE `buffs` ENABLE KEYS */;

--  テーブル atelier.charsettings の構造をダンプしています
CREATE TABLE IF NOT EXISTS `charsettings` (
  `userId` int(11) NOT NULL,
  `showDamage` tinyint(1) NOT NULL,
  `showOthersDamage` tinyint(1) NOT NULL,
  PRIMARY KEY (`userId`),
  CONSTRAINT `charSettings_userId` FOREIGN KEY (`userId`) REFERENCES `accounts` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- テーブル atelier.charsettings: ~2 rows (約) のデータをダンプしています
/*!40000 ALTER TABLE `charsettings` DISABLE KEYS */;
INSERT INTO `charsettings` (`userId`, `showDamage`, `showOthersDamage`) VALUES
	(1, 1, 1),
	(2, 1, 1),
	(7, 1, 1);
/*!40000 ALTER TABLE `charsettings` ENABLE KEYS */;

--  テーブル atelier.fallingblock の構造をダンプしています
CREATE TABLE IF NOT EXISTS `fallingblock` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `world` varchar(50) NOT NULL,
  `x` float NOT NULL DEFAULT '0',
  `y` float NOT NULL DEFAULT '0',
  `z` float NOT NULL DEFAULT '0',
  `block` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- テーブル atelier.fallingblock: ~0 rows (約) のデータをダンプしています
/*!40000 ALTER TABLE `fallingblock` DISABLE KEYS */;
/*!40000 ALTER TABLE `fallingblock` ENABLE KEYS */;

--  テーブル atelier.npcs の構造をダンプしています
CREATE TABLE IF NOT EXISTS `npcs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `script` varchar(100) NOT NULL,
  `entityType` varchar(100) NOT NULL,
  `world` varchar(100) NOT NULL,
  `x` double NOT NULL,
  `y` double NOT NULL,
  `z` double NOT NULL,
  `skinUUID` varchar(100) DEFAULT NULL,
  `villagerType` varchar(100) DEFAULT NULL,
  `profession` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- テーブル atelier.npcs: ~3 rows (約) のデータをダンプしています
/*!40000 ALTER TABLE `npcs` DISABLE KEYS */;
INSERT INTO `npcs` (`id`, `name`, `script`, `entityType`, `world`, `x`, `y`, `z`, `skinUUID`, `villagerType`, `profession`) VALUES
	(3, '&a雑貨屋', 'shop01.js', 'WANDERING_TRADER', 'world', 125.5, 64, 55.5, NULL, NULL, NULL),
	(4, 'hidden', 'test.js', 'VILLAGER', 'world', 141.5, 64, 54.5, NULL, NULL, NULL),
	(5, '&7鍛冶屋', 'blacksmith.js', 'VILLAGER', 'world', 90.5, 64, 41.5, NULL, 'PLAINS', 'WEAPONSMITH'),
	(6, 'あ', 'shop02.js', 'PLAYER', 'world', 125.5, 64, 51.5, 'a5f73ae1-8d8c-4b4c-b174-d214562e288d', NULL, NULL);
/*!40000 ALTER TABLE `npcs` ENABLE KEYS */;

--  テーブル atelier.questdatas の構造をダンプしています
CREATE TABLE IF NOT EXISTS `questdatas` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` int(11) NOT NULL COMMENT 'accounts.id',
  `questId` varchar(100) NOT NULL,
  `clear` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `questDatas_user_id_IDX` (`userId`,`questId`) USING BTREE,
  CONSTRAINT `questdatas_userId` FOREIGN KEY (`userId`) REFERENCES `accounts` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=119 DEFAULT CHARSET=utf8;

-- テーブル atelier.questdatas: ~2 rows (約) のデータをダンプしています
/*!40000 ALTER TABLE `questdatas` DISABLE KEYS */;
INSERT INTO `questdatas` (`id`, `userId`, `questId`, `clear`) VALUES
	(115, 2, 'flam_tutorial_0', 1),
	(117, 2, 'flam_tutorial_1', 1);
/*!40000 ALTER TABLE `questdatas` ENABLE KEYS */;

--  テーブル atelier.recipelevels の構造をダンプしています
CREATE TABLE IF NOT EXISTS `recipelevels` (
  `userId` int(11) NOT NULL COMMENT 'accounts.id',
  `recipeId` varchar(100) NOT NULL,
  `acquired` tinyint(1) NOT NULL DEFAULT '0',
  `level` int(11) NOT NULL,
  `exp` int(11) NOT NULL,
  `idea` varchar(50) DEFAULT NULL,
  UNIQUE KEY `recipe_levels_user_id_IDX` (`userId`,`recipeId`) USING BTREE,
  CONSTRAINT `recipeLevels_userId` FOREIGN KEY (`userId`) REFERENCES `accounts` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- テーブル atelier.recipelevels: ~4 rows (約) のデータをダンプしています
/*!40000 ALTER TABLE `recipelevels` DISABLE KEYS */;
INSERT INTO `recipelevels` (`userId`, `recipeId`, `acquired`, `level`, `exp`, `idea`) VALUES
	(1, 'atelier_book', 1, 0, 0, NULL),
	(1, 'flam', 1, 3, 55, NULL),
	(1, 'flamberge', 1, 3, 25, NULL),
	(1, 'isyairazu', 1, 2, 16, NULL),
	(1, 'pickaxe', 1, 3, 225, NULL),
	(2, 'flam', 0, 1, 0, NULL);
/*!40000 ALTER TABLE `recipelevels` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
