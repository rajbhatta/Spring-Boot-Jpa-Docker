/*
SQLyog Trial v13.1.8 (64 bit)
MySQL - 10.4.22-MariaDB : Database - guusto_db
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`guusto_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;

USE `guusto_db`;

/*Table structure for table `tbl_client_transaction` */

DROP TABLE IF EXISTS `tbl_client_transaction`;

CREATE TABLE `tbl_client_transaction` (
  `client_transaction_id` int(11) NOT NULL AUTO_INCREMENT,
  `amount` double DEFAULT NULL,
  `gift` int(11) DEFAULT NULL,
  `quantity` int(11) DEFAULT NULL,
  `remind_balance` double DEFAULT NULL,
  `id` int(11) NOT NULL,
  PRIMARY KEY (`client_transaction_id`),
  KEY `FKta4c83wpay8j40m6fuq7nw0rn` (`id`),
  CONSTRAINT `FKta4c83wpay8j40m6fuq7nw0rn` FOREIGN KEY (`id`) REFERENCES `tbl_client` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4;

/*Data for the table `tbl_client_transaction` */

insert  into `tbl_client_transaction`(`client_transaction_id`,`amount`,`gift`,`quantity`,`remind_balance`,`id`) values 
(1,10,2,2,80,1),
(2,10,2,2,60,1),
(3,3,5,5,45,1),
(4,4,10,10,5,1);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
