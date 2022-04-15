-- MySQL dump 10.13  Distrib 5.7.34, for Win64 (x86_64)
--
-- Host: localhost    Database: watermark
-- ------------------------------------------------------
-- Server version	5.7.34

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
-- Table structure for table `record`
--

DROP TABLE IF EXISTS `record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `password` varchar(256) NOT NULL,
  `secret_key` varchar(256) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `record_password_uindex` (`password`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `record`
--

LOCK TABLES `record` WRITE;
/*!40000 ALTER TABLE `record` DISABLE KEYS */;
INSERT INTO `record` VALUES (1,'3e35fff99221231c399db9405c93dfb3','7-12-6-0.1-4-500-287'),(2,'f0a3ae1046dd8cc0ee4806c396a93761','8-16-7-0.1-7-500-237'),(3,'1c707001271cda31227a06dae7cf6c2b','12-13-8-0.1-5-500-123'),(4,'5b55de5e07a092649674b29a0ac847b1','14-11-9-0.1-13-500-257'),(5,'dae4bc4970e92304ffe1199fdc177144','8-11-8-0.1-8-500-249');
/*!40000 ALTER TABLE `record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `watermark_info`
--

DROP TABLE IF EXISTS `watermark_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `watermark_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `hash_value` varchar(255) NOT NULL COMMENT '哈希值',
  `width` int(11) NOT NULL COMMENT '水印的宽度',
  `height` int(11) NOT NULL COMMENT '水印的高度',
  `type` int(11) NOT NULL COMMENT '算法类型',
  PRIMARY KEY (`id`),
  UNIQUE KEY `watermark_info_key_uindex` (`hash_value`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COMMENT='水印信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `watermark_info`
--

LOCK TABLES `watermark_info` WRITE;
/*!40000 ALTER TABLE `watermark_info` DISABLE KEYS */;
INSERT INTO `watermark_info` VALUES (1,'4e3f218863188eef70aaf616011c7e93',128,128,1),(2,'efc4c9011cf4c5865ae35068d09b26fd',128,128,1),(3,'93034b69ea05e313a021c67ae94a0e9e',128,128,1),(4,'152febbae067fe093eee6100a62cba7f',128,128,1),(5,'5b6f956fb595692ac98d131e3426d0b2',128,128,1),(6,'c9f69a8c60544d028431631b8d99106d',128,128,1),(7,'9c3f3d6d3baaf0662e48a7bb00f171fb',128,128,1);
/*!40000 ALTER TABLE `watermark_info` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-04-15 10:39:26
