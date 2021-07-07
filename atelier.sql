-- --------------------------------------------------------
-- ホスト:                          127.0.0.1
-- サーバーのバージョン:                   8.0.17 - MySQL Community Server - GPL
-- サーバー OS:                      Win64
-- HeidiSQL バージョン:               11.3.0.6295
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


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
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;

-- テーブル atelier.accounts: ~4 rows (約) のデータをダンプしています
/*!40000 ALTER TABLE `accounts` DISABLE KEYS */;
INSERT INTO `accounts` (`id`, `uuid`, `email`, `password`, `level`, `exp`, `alchemyLevel`, `alchemyExp`, `maxHp`, `hp`, `maxMp`, `mp`, `atk`, `def`, `speed`, `money`) VALUES
	(1, 'a476c5ce-a07f-4330-95ac-028018e1282b', NULL, NULL, 43, 1409, 1, 0, 78, 78, 89, 89, 24, 19, 5, 49854),
	(2, 'd9c1fa1c-6662-429a-b0cf-ac8b0df4dc26', NULL, NULL, 14, 195, 1, 1, 43, 43, 54, 54, 10, 9, 3, 989759),
	(6, '235ea7dd-f11f-46a9-a58f-2ccc95cf86f2', NULL, NULL, 1, 0, 1, 0, 28, 28, 39, 39, 3, 5, 2, 0),
	(7, 'f345f790-2ccb-4ddb-9858-deb3c6bcdc29', NULL, NULL, 19, 271, 1, 0, 48, 48, 59, 59, 12, 11, 3, 0);
/*!40000 ALTER TABLE `accounts` ENABLE KEYS */;

--  テーブル atelier.bagitems の構造をダンプしています
CREATE TABLE IF NOT EXISTS `bagitems` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` int(11) NOT NULL,
  `json` json NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `userId` (`userId`),
  CONSTRAINT `bagitems_userId` FOREIGN KEY (`userId`) REFERENCES `accounts` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4083 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- テーブル atelier.bagitems: ~0 rows (約) のデータをダンプしています
/*!40000 ALTER TABLE `bagitems` DISABLE KEYS */;
INSERT INTO `bagitems` (`id`, `userId`, `json`) VALUES
	(2, 2, '{"jsonItems": [{"amount": 64, "material": {"type": "OAK_PLANKS", "version": 2724}, "durability": 0, "customModel": -1, "jsonItemStatus": "{\\"alchemyMaterial\\":\\"oak_planks\\",\\"size\\":[1,1,0,0,0,0,0,0,0],\\"categories\\":[\\"WOOD\\",\\"COAL\\"],\\"quality\\":8,\\"ingredients\\":[\\"I100R25\\"],\\"characteristics\\":[],\\"activeEffects\\":[],\\"hp\\":0,\\"mp\\":0,\\"atk\\":0,\\"def\\":0,\\"speed\\":0,\\"usableCount\\":0,\\"consumedCount\\":0,\\"prefix\\":[],\\"dataContainer\\":{}}"}]}'),
	(3565, 1, '{"jsonItems": []}');
/*!40000 ALTER TABLE `bagitems` ENABLE KEYS */;

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
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` int(11) NOT NULL,
  `showDamage` tinyint(1) NOT NULL,
  `showOthersDamage` tinyint(1) NOT NULL,
  `showPlayerChat` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `userId` (`userId`),
  CONSTRAINT `charSettings_userId` FOREIGN KEY (`userId`) REFERENCES `accounts` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4054 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- テーブル atelier.charsettings: ~3 rows (約) のデータをダンプしています
/*!40000 ALTER TABLE `charsettings` DISABLE KEYS */;
INSERT INTO `charsettings` (`id`, `userId`, `showDamage`, `showOthersDamage`, `showPlayerChat`) VALUES
	(1, 1, 1, 1, 1),
	(2, 2, 1, 1, 1),
	(3, 7, 1, 1, 1);
/*!40000 ALTER TABLE `charsettings` ENABLE KEYS */;

--  テーブル atelier.discord の構造をダンプしています
CREATE TABLE IF NOT EXISTS `discord` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` int(11) NOT NULL DEFAULT '0',
  `discordId` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `discordId` (`discordId`),
  KEY `discord_userId` (`userId`),
  CONSTRAINT `discord_userId` FOREIGN KEY (`userId`) REFERENCES `accounts` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- テーブル atelier.discord: ~0 rows (約) のデータをダンプしています
/*!40000 ALTER TABLE `discord` DISABLE KEYS */;
INSERT INTO `discord` (`id`, `userId`, `discordId`) VALUES
	(3, 2, 251617741864173570);
/*!40000 ALTER TABLE `discord` ENABLE KEYS */;

--  ビュー atelier.discordview の構造をダンプしています
-- VIEW 依存エラーを克服するために、一時テーブルを作成
CREATE TABLE `discordview` (
	`userId` INT(11) NOT NULL,
	`discordId` BIGINT(20) NOT NULL,
	`uuid` VARCHAR(100) NOT NULL COLLATE 'utf8_general_ci'
) ENGINE=MyISAM;

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
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

-- テーブル atelier.npcs: ~3 rows (約) のデータをダンプしています
/*!40000 ALTER TABLE `npcs` DISABLE KEYS */;
INSERT INTO `npcs` (`id`, `name`, `script`, `entityType`, `world`, `x`, `y`, `z`, `skinUUID`, `villagerType`, `profession`) VALUES
	(3, '&a雑貨屋', 'shop01.js', 'WANDERING_TRADER', 'world', -8.5, 76, 34.5, NULL, NULL, NULL),
	(4, 'A', 'test.js', 'PLAYER', 'world', 0.5, 68, -34.5, 'd9c1fa1c-6662-429a-b0cf-ac8b0df4dc26', NULL, NULL),
	(5, '&7鍛冶屋', 'blacksmith.js', 'VILLAGER', 'world', 29.5, 68, -13, NULL, 'PLAINS', 'WEAPONSMITH'),
	(6, '&a司書', 'shop02.js', 'VILLAGER', 'world', -4.5, 76, 34.5, NULL, 'PLAINS', 'LIBRARIAN'),
	(7, '&a鑑定師', 'appraisal.js', 'VILLAGER', 'world', 17.5, 67, -24, NULL, 'PLAINS', 'LIBRARIAN');
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
  `idea` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT '',
  UNIQUE KEY `recipe_levels_user_id_IDX` (`userId`,`recipeId`) USING BTREE,
  CONSTRAINT `recipeLevels_userId` FOREIGN KEY (`userId`) REFERENCES `accounts` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- テーブル atelier.recipelevels: ~8 rows (約) のデータをダンプしています
/*!40000 ALTER TABLE `recipelevels` DISABLE KEYS */;
INSERT INTO `recipelevels` (`userId`, `recipeId`, `acquired`, `level`, `exp`, `idea`) VALUES
	(1, 'atelier_book', 1, 0, 0, NULL),
	(1, 'blasting_flam', 1, 0, 0, NULL),
	(1, 'blue', 1, 0, 0, NULL),
	(1, 'flam', 1, 3, 155, NULL),
	(1, 'flamberge', 1, 3, 25, NULL),
	(1, 'isyairazu', 1, 2, 16, NULL),
	(1, 'pickaxe', 1, 3, 225, NULL),
	(2, 'flam', 1, 1, 0, NULL),
	(2, 'isyairazu', 1, 1, 25, NULL);
/*!40000 ALTER TABLE `recipelevels` ENABLE KEYS */;

--  テーブル atelier.skills の構造をダンプしています
CREATE TABLE IF NOT EXISTS `skills` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` int(11) NOT NULL,
  `skill` varchar(50) NOT NULL,
  `isPassive` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `skills_userId` (`userId`),
  CONSTRAINT `skills_userId` FOREIGN KEY (`userId`) REFERENCES `accounts` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- テーブル atelier.skills: ~0 rows (約) のデータをダンプしています
/*!40000 ALTER TABLE `skills` DISABLE KEYS */;
INSERT INTO `skills` (`id`, `userId`, `skill`, `isPassive`) VALUES
	(1, 2, 'FLAM', 1);
/*!40000 ALTER TABLE `skills` ENABLE KEYS */;

--  ビュー atelier.discordview の構造をダンプしています
-- 一時テーブルを削除して、最終的な VIEW 構造を作成
DROP TABLE IF EXISTS `discordview`;
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `discordview` AS select `accounts`.`id` AS `userId`,`discord`.`discordId` AS `discordId`,`accounts`.`uuid` AS `uuid` from (`discord` join `accounts` on((`accounts`.`id` = `discord`.`userId`)));

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
