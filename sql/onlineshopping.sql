CREATE DATABASE  IF NOT EXISTS `onlineshopping` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `onlineshopping`;
-- MySQL dump 10.13  Distrib 8.0.16, for Win64 (x86_64)
--
-- Host: localhost    Database: onlineshopping
-- ------------------------------------------------------
-- Server version	8.0.16

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
 SET NAMES utf8 ;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `item`
--

DROP TABLE IF EXISTS `item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `item` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
  `price` double NOT NULL DEFAULT '0',
  `description` varchar(500) COLLATE utf8_unicode_ci NOT NULL,
  `sales` int(11) NOT NULL DEFAULT '0',
  `img_url` varchar(500) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `item`
--

LOCK TABLES `item` WRITE;
/*!40000 ALTER TABLE `item` DISABLE KEYS */;
INSERT INTO `item` VALUES (1,'ipad Pro',5555,'2018版',31,'https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1557636754736&di=82da719d7ae60fcf893bb6cff435ef3a&imgtype=0&src=http%3A%2F%2Fimg3.myhsw.cn%2F2019-01-07%2F0by68k83.png'),(2,'iPad Air',3333,'2019新款',12,'https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1557636820612&di=67a1bb55b02f2c332f40f6c46046ec55&imgtype=0&src=http%3A%2F%2Fimg.pconline.com.cn%2Fimages%2Fupload%2Fupc%2Ftx%2Fpc_best%2F1905%2F06%2Fc135%2F146298435_1557127639133.jpg'),(3,'ipad mini',2000,'2019新款',8,'http://img14.360buyimg.com/n1/s450x450_jfs/t1/12013/8/11611/121395/5c906608E5b10fed7/c8b5b02d7c8a9529.jpg'),(4,'华为P30',4000,'2019新款',72,'https://image.suning.cn/uimg/b2c/newcatentries/0070830053-000000011065716867_1.jpg_800w_800h_4e'),(5,'华为P30PRO',6666,'2019年新品',22,'https://imgservice.suning.cn/uimg1/b2c/image/AenE3GK9Ljc-mr6o-zflcg.jpg_60w_60h_4e');
/*!40000 ALTER TABLE `item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `item_stock`
--

DROP TABLE IF EXISTS `item_stock`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `item_stock` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `stock` int(11) NOT NULL DEFAULT '0',
  `item_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `item_stock`
--

LOCK TABLES `item_stock` WRITE;
/*!40000 ALTER TABLE `item_stock` DISABLE KEYS */;
INSERT INTO `item_stock` VALUES (1,99,1),(2,150,2),(3,120,3),(4,98,4),(6,66,5);
/*!40000 ALTER TABLE `item_stock` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_info`
--

DROP TABLE IF EXISTS `order_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `order_info` (
  `id` varchar(32) COLLATE utf8_unicode_ci NOT NULL,
  `user_id` int(11) NOT NULL,
  `item_id` int(11) NOT NULL,
  `item_price` double NOT NULL,
  `amount` int(11) NOT NULL,
  `order_price` double NOT NULL,
  `promo_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_info`
--

LOCK TABLES `order_info` WRITE;
/*!40000 ALTER TABLE `order_info` DISABLE KEYS */;
INSERT INTO `order_info` VALUES ('2019051300000000',2,1,5555,1,5555,0),('2019051300000100',2,4,3988,1,3988,1),('2019051300000200',2,4,3988,1,3988,1);
/*!40000 ALTER TABLE `order_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `promo`
--

DROP TABLE IF EXISTS `promo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `promo` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `promo_name` varchar(45) COLLATE utf8_unicode_ci NOT NULL,
  `start_date` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `item_id` int(11) NOT NULL DEFAULT '0',
  `promo_item_price` double NOT NULL DEFAULT '0',
  `end_date` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `promo_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `promo`
--

LOCK TABLES `promo` WRITE;
/*!40000 ALTER TABLE `promo` DISABLE KEYS */;
INSERT INTO `promo` VALUES (1,'华为P30限量抢购','2019-05-13 11:55:00',4,3988,'2019-05-20 00:00:00',0);
/*!40000 ALTER TABLE `promo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sequence_info`
--

DROP TABLE IF EXISTS `sequence_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `sequence_info` (
  `name` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `current_value` int(11) NOT NULL DEFAULT '0',
  `step` int(11) NOT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sequence_info`
--

LOCK TABLES `sequence_info` WRITE;
/*!40000 ALTER TABLE `sequence_info` DISABLE KEYS */;
INSERT INTO `sequence_info` VALUES ('order_info',3,1);
/*!40000 ALTER TABLE `sequence_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_info`
--

DROP TABLE IF EXISTS `user_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `user_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
  `gender` tinyint(4) NOT NULL COMMENT '//1代表男性，2代表女性',
  `age` int(11) NOT NULL,
  `telephone` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
  `register_mode` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT '//byphone,bywechat,byalipay',
  `third_party_id` varchar(64) COLLATE utf8_unicode_ci DEFAULT 'null',
  PRIMARY KEY (`id`),
  UNIQUE KEY `telephone_unique_index` (`telephone`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_info`
--

LOCK TABLES `user_info` WRITE;
/*!40000 ALTER TABLE `user_info` DISABLE KEYS */;
INSERT INTO `user_info` VALUES (1,'test1',1,22,'12323232323','byphone','bywechat'),(2,'2',2,22,'12345678901','byphone','null'),(8,'3',2,33,'12345678902','byphone','null');
/*!40000 ALTER TABLE `user_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_password`
--

DROP TABLE IF EXISTS `user_password`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `user_password` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `encrpt_password` varchar(128) COLLATE utf8_unicode_ci NOT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_password`
--

LOCK TABLES `user_password` WRITE;
/*!40000 ALTER TABLE `user_password` DISABLE KEYS */;
INSERT INTO `user_password` VALUES (1,'123456',1),(2,'yB5yjZ1ML2NvBn+JzBSGLA==',2),(5,'7MvIfktc4v4oMI/Z8qe68w==',8);
/*!40000 ALTER TABLE `user_password` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-06-03  9:45:37
