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
  `money` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uuid` (`uuid`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;

-- エクスポートするデータが選択されていません

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
  PRIMARY KEY (`id`),
  KEY `buffs_userId` (`userId`),
  CONSTRAINT `buffs_userId` FOREIGN KEY (`userId`) REFERENCES `accounts` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- エクスポートするデータが選択されていません

--  テーブル atelier.charsettings の構造をダンプしています
CREATE TABLE IF NOT EXISTS `charsettings` (
  `userId` int(11) NOT NULL,
  `showDamage` tinyint(1) NOT NULL,
  `showOthersDamage` tinyint(1) NOT NULL,
  PRIMARY KEY (`userId`),
  CONSTRAINT `charSettings_userId` FOREIGN KEY (`userId`) REFERENCES `accounts` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- エクスポートするデータが選択されていません

--  テーブル atelier.discoveredrecipes の構造をダンプしています
CREATE TABLE IF NOT EXISTS `discoveredrecipes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` int(11) NOT NULL,
  `itemId` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `discoveredrecipes` (`userId`),
  CONSTRAINT `discoveredrecipes` FOREIGN KEY (`userId`) REFERENCES `accounts` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- エクスポートするデータが選択されていません

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

-- エクスポートするデータが選択されていません

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

-- エクスポートするデータが選択されていません

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

-- エクスポートするデータが選択されていません

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
