-- MySQL dump 10.13  Distrib 5.7.27, for Linux (x86_64)
--
-- Host: localhost    Database: atelier
-- ------------------------------------------------------
-- Server version	5.7.27-0ubuntu0.18.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `accounts`
--

DROP TABLE IF EXISTS `accounts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `accounts` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uuid` varchar(100) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `alchemy_level` int(11) NOT NULL DEFAULT '1',
  `alchemy_exp` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `accounts`
--

LOCK TABLES `accounts` WRITE;
/*!40000 ALTER TABLE `accounts` DISABLE KEYS */;
INSERT INTO `accounts` VALUES (1,'a476c5ce-a07f-4330-95ac-028018e1282b',NULL,1,0),(2,'d9c1fa1c-6662-429a-b0cf-ac8b0df4dc26',NULL,1,0),(3,'a476c5ce-a07f-4330-95ac-028018e1282b',NULL,1,0),(4,'a476c5ce-a07f-4330-95ac-028018e1282b',NULL,1,0),(5,'a476c5ce-a07f-4330-95ac-028018e1282b',NULL,1,0);
/*!40000 ALTER TABLE `accounts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `discoveredRecipes`
--

DROP TABLE IF EXISTS `discoveredRecipes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `discoveredRecipes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `item_id` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `discoveredRecipes_UN` (`user_id`,`item_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `discoveredRecipes`
--

LOCK TABLES `discoveredRecipes` WRITE;
/*!40000 ALTER TABLE `discoveredRecipes` DISABLE KEYS */;
INSERT INTO `discoveredRecipes` VALUES (1,1,'minecraft:cauldron');
/*!40000 ALTER TABLE `discoveredRecipes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `npcs`
--

DROP TABLE IF EXISTS `npcs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `npcs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `script` varchar(100) NOT NULL,
  `entityType` varchar(100) NOT NULL,
  `world` varchar(100) NOT NULL,
  `x` double NOT NULL,
  `y` double NOT NULL,
  `z` double NOT NULL,
  `skin_uuid` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `npcs`
--

LOCK TABLES `npcs` WRITE;
/*!40000 ALTER TABLE `npcs` DISABLE KEYS */;
INSERT INTO `npcs` VALUES (1,'tetetete.py','tetetete.3.py','PLAYER','world',-212.9579646582707,65,92.96626520080326,'b7e5e74d-ad60-4724-ac43-abd61b72e41b'),(2,'tetetete','tetetete.js','PLAYER','world',-214.9290253942732,65,93.28566307528428,'b7e5e74d-ad60-4724-ac43-abd61b72e41b');
/*!40000 ALTER TABLE `npcs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `questDatas`
--

DROP TABLE IF EXISTS `questDatas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `questDatas` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT 'accounts.id',
  `quest_id` varchar(100) NOT NULL,
  `clear` smallint(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `questDatas_user_id_IDX` (`user_id`,`quest_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=118 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `questDatas`
--

LOCK TABLES `questDatas` WRITE;
/*!40000 ALTER TABLE `questDatas` DISABLE KEYS */;
INSERT INTO `questDatas` VALUES (115,2,'flam_tutorial_0',1),(117,2,'flam_tutorial_1',1);
/*!40000 ALTER TABLE `questDatas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `recipe_levels`
--

DROP TABLE IF EXISTS `recipe_levels`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `recipe_levels` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT 'accounts.id',
  `recipe_id` varchar(100) NOT NULL,
  `level` int(11) NOT NULL,
  `exp` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `recipe_levels_user_id_IDX` (`user_id`,`recipe_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `recipe_levels`
--

LOCK TABLES `recipe_levels` WRITE;
/*!40000 ALTER TABLE `recipe_levels` DISABLE KEYS */;
INSERT INTO `recipe_levels` VALUES (16,1,'flam',2,95),(17,1,'atelier_book',4,5),(53,2,'flam',1,0),(54,1,'kongarimeat',3,250);
/*!40000 ALTER TABLE `recipe_levels` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-08-02 13:39:43
