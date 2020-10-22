/*
SQLyog Ultimate v11.11 (64 bit)
MySQL - 5.7.20-log : Database - scheduler
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`scheduler` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;

USE `scheduler`;

/*Table structure for table `scheduler_registry` */

DROP TABLE IF EXISTS `scheduler_registry`;

CREATE TABLE `scheduler_registry` (
  `registry_id` int(13) NOT NULL AUTO_INCREMENT,
  `registry_app_name` varchar(255) DEFAULT NULL COMMENT '应用名，服务名',
  `registry_desc` varchar(255) DEFAULT NULL COMMENT '中文描述',
  `registry_create_time` datetime DEFAULT NULL COMMENT '自动注册时间',
  PRIMARY KEY (`registry_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4;

/*Data for the table `scheduler_registry` */

insert  into `scheduler_registry`(`registry_id`,`registry_app_name`,`registry_desc`,`registry_create_time`) values (2,'scheduler-example-zhangwei','scheduler-example-zhangwei','2020-10-16 12:29:18'),(3,'scheduler-zhangwei-02','scheduler-zhangwei-02','2020-10-21 18:15:10'),(4,'scheduler-example-executor-dev2','scheduler-example-executor-dev2','2020-10-22 11:08:01');

/*Table structure for table `scheduler_registry_detail` */

DROP TABLE IF EXISTS `scheduler_registry_detail`;

CREATE TABLE `scheduler_registry_detail` (
  `register_detail_id` int(13) NOT NULL AUTO_INCREMENT,
  `register_detail_registry_id` int(13) DEFAULT NULL COMMENT '主表id',
  `register_detail_app_name` varchar(255) DEFAULT NULL COMMENT 'register表app_name',
  `register_detail_ip` varchar(255) DEFAULT NULL COMMENT 'ip',
  `register_detail_port` varchar(255) DEFAULT NULL COMMENT 'port',
  `register_detail_sort` int(2) DEFAULT NULL COMMENT '注册序号',
  `register_detail_status` char(1) DEFAULT '0' COMMENT '0：离线 1：在线， 默认离线',
  `register_detail_online_time` datetime DEFAULT NULL COMMENT '上线时间',
  `register_detail_offline_time` datetime DEFAULT NULL COMMENT '离线时间',
  `register_detail_create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`register_detail_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4;

/*Data for the table `scheduler_registry_detail` */

insert  into `scheduler_registry_detail`(`register_detail_id`,`register_detail_registry_id`,`register_detail_app_name`,`register_detail_ip`,`register_detail_port`,`register_detail_sort`,`register_detail_status`,`register_detail_online_time`,`register_detail_offline_time`,`register_detail_create_time`) values (7,2,'scheduler-example-zhangwei','192.168.40.1','8889',2,'0','2020-10-21 20:14:15','2020-10-21 22:00:33','2020-10-16 19:28:30'),(9,2,'scheduler-example-zhangwei','192.168.40.1','8888',1,'0','2020-10-21 18:58:21','2020-10-21 20:14:29','2020-10-16 19:46:17'),(14,NULL,'scheduler-example-executor-dev2','192.168.40.1','8889',1,'1','2020-10-22 11:08:01',NULL,'2020-10-22 11:08:01'),(15,4,'scheduler-example-executor-dev2','192.168.40.1','8889',1,'1','2020-10-22 11:09:19',NULL,'2020-10-22 11:09:19');

/*Table structure for table `scheduler_task_cron` */

DROP TABLE IF EXISTS `scheduler_task_cron`;

CREATE TABLE `scheduler_task_cron` (
  `task_cron_id` int(13) NOT NULL AUTO_INCREMENT,
  `task_cron_registry_detail_id` int(13) DEFAULT NULL COMMENT '执行器注册节点详细信息表主键id',
  `task_cron_app_name` varchar(255) DEFAULT NULL COMMENT 'registry表app_name',
  `task_cron_desc` varchar(255) DEFAULT NULL COMMENT '中文描述',
  `task_cron_job_handler` varchar(255) DEFAULT NULL COMMENT '任务处理方法名',
  `task_cron_param` text COMMENT '参数',
  `task_cron_expression` varchar(255) DEFAULT NULL COMMENT 'cron表达式',
  `task_cron_status` char(1) DEFAULT '0' COMMENT '0: stop 1: running',
  `task_cron_create_time` datetime DEFAULT NULL COMMENT '任务生成时间',
  `task_cron_write_log` char(1) DEFAULT '0' COMMENT '0: 不写日志 1：写日志',
  PRIMARY KEY (`task_cron_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4;

/*Data for the table `scheduler_task_cron` */

insert  into `scheduler_task_cron`(`task_cron_id`,`task_cron_registry_detail_id`,`task_cron_app_name`,`task_cron_desc`,`task_cron_job_handler`,`task_cron_param`,`task_cron_expression`,`task_cron_status`,`task_cron_create_time`,`task_cron_write_log`) values (1,7,'scheduler-example-zhangwei','测试任务','test',NULL,'0/8 * * * * ?','0','2020-10-16 14:44:50','0');

/*Table structure for table `scheduler_task_given` */

DROP TABLE IF EXISTS `scheduler_task_given`;

CREATE TABLE `scheduler_task_given` (
  `task_given_id` int(13) NOT NULL AUTO_INCREMENT,
  `task_given_registry_detail_id` int(13) DEFAULT NULL COMMENT '执行器注册节点详细信息表主键id',
  `task_given_app_name` varchar(255) DEFAULT NULL COMMENT 'registry表app_name',
  `task_given_desc` varchar(255) DEFAULT NULL COMMENT '中文描述',
  `task_given_job_handler` varchar(255) DEFAULT NULL COMMENT '任务处理方法名',
  `task_given_param` text COMMENT '执行参数',
  `task_given_time` datetime DEFAULT NULL COMMENT '执行时间',
  `task_given_delayed` varchar(255) DEFAULT '0' COMMENT '延迟多少秒执行，配合执行时间使用',
  `task_given_status` char(1) DEFAULT '0' COMMENT '0: stop 1: running',
  `task_given_execute_status` char(1) DEFAULT NULL COMMENT '0: 未执行 1: 执行中 2: 待重试 3: 已执行',
  `task_given_last_execute_time` datetime DEFAULT NULL COMMENT '最近一次执行时间',
  `task_given_retry_count` int(13) DEFAULT '0' COMMENT '重试次数',
  `task_given_retry_delayed` int(13) DEFAULT '3' COMMENT '重试间隔, 默认间隔三秒重试',
  `task_given_retry_max` int(13) DEFAULT '3' COMMENT '重试最大次数',
  `task_given_create_time` datetime DEFAULT NULL COMMENT '任务生成时间',
  `task_given_write_log` char(1) DEFAULT '0' COMMENT '0: 不写日志 1：写日志',
  PRIMARY KEY (`task_given_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4;

/*Data for the table `scheduler_task_given` */

insert  into `scheduler_task_given`(`task_given_id`,`task_given_registry_detail_id`,`task_given_app_name`,`task_given_desc`,`task_given_job_handler`,`task_given_param`,`task_given_time`,`task_given_delayed`,`task_given_status`,`task_given_execute_status`,`task_given_last_execute_time`,`task_given_retry_count`,`task_given_retry_delayed`,`task_given_retry_max`,`task_given_create_time`,`task_given_write_log`) values (3,7,'scheduler-example-zhangwei','测试任务22222','test','nihao','2020-10-19 11:05:51','0','1','3','2020-10-21 13:20:42',3,3,3,'2020-10-19 11:05:59','1');

/*Table structure for table `scheduler_task_log` */

DROP TABLE IF EXISTS `scheduler_task_log`;

CREATE TABLE `scheduler_task_log` (
  `log_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `log_registry_detail_id` int(11) DEFAULT NULL COMMENT '执行器主键ID',
  `log_task_id` int(11) DEFAULT NULL COMMENT '任务，主键ID',
  `log_task_type` char(1) DEFAULT '0' COMMENT '0: cron任务 1: given任务',
  `log_executor_address` varchar(255) DEFAULT NULL COMMENT '执行器地址，本次执行的地址',
  `log_executor_handler` varchar(255) DEFAULT NULL COMMENT '执行器任务handler',
  `log_executor_param` varchar(512) DEFAULT NULL COMMENT '执行器任务参数',
  `log_trigger_time` datetime DEFAULT NULL COMMENT '调度-时间',
  `log_trigger_code` varchar(255) DEFAULT NULL COMMENT '调度-结果',
  `log_trigger_msg` text COMMENT '调度-日志',
  `log_handle_time` datetime DEFAULT NULL COMMENT '执行-时间',
  `log_handle_code` varchar(255) DEFAULT NULL COMMENT '执行-状态',
  `log_handle_msg` text COMMENT '执行-日志',
  `log_alarm_status` char(1) DEFAULT '0' COMMENT '告警状态：0-默认、1-无需告警、2-告警成功、3-告警失败',
  `log_create_time` datetime DEFAULT NULL COMMENT '日志生成时间',
  PRIMARY KEY (`log_id`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8mb4;

/*Data for the table `scheduler_task_log` */

insert  into `scheduler_task_log`(`log_id`,`log_registry_detail_id`,`log_task_id`,`log_task_type`,`log_executor_address`,`log_executor_handler`,`log_executor_param`,`log_trigger_time`,`log_trigger_code`,`log_trigger_msg`,`log_handle_time`,`log_handle_code`,`log_handle_msg`,`log_alarm_status`,`log_create_time`) values (36,7,1,'0','192.168.40.1:8889','test',NULL,'2020-10-20 12:13:55','200',NULL,'2020-10-20 12:13:55','200',NULL,'0','2020-10-20 12:13:59'),(37,7,1,'0','192.168.40.1:8889','test',NULL,'2020-10-20 12:13:58','200',NULL,'2020-10-20 12:13:58','200',NULL,'0','2020-10-20 12:13:58'),(38,7,1,'0','192.168.40.1:8889','test',NULL,'2020-10-20 12:14:01','200',NULL,'2020-10-20 12:14:01','200',NULL,'0','2020-10-20 12:14:01'),(39,7,1,'0','192.168.40.1:8889','test2',NULL,'2020-10-20 12:14:04','200',NULL,'2020-10-20 12:14:04','200',NULL,'0','2020-10-20 12:14:13'),(40,7,1,'0','192.168.40.1:8889','test',NULL,'2020-10-20 12:14:07','200',NULL,'2020-10-20 12:14:07','200',NULL,'0','2020-10-20 12:14:07'),(41,7,1,'0','192.168.40.1:8889','test2',NULL,'2020-10-20 12:14:12','200',NULL,'2020-10-20 12:14:12','200',NULL,'0','2020-10-20 12:14:12'),(42,7,3,'1','192.168.40.1:8889','test','nihao','2020-10-20 12:32:45','200',NULL,'2020-10-20 12:32:45','400','重试次数已达上限','0','2020-10-20 12:32:45'),(43,7,1,'0','192.168.40.1:8889','test',NULL,'2020-10-21 13:16:49','200',NULL,'2020-10-21 13:16:49','200',NULL,'0','2020-10-21 13:16:49'),(44,7,1,'0','192.168.40.1:8889','test',NULL,'2020-10-21 13:16:57','200',NULL,'2020-10-21 13:16:57','200',NULL,'0','2020-10-21 13:16:57'),(45,7,1,'0','192.168.40.1:8889','test',NULL,'2020-10-21 13:17:01','200',NULL,'2020-10-21 13:17:01','200',NULL,'0','2020-10-21 13:17:01'),(46,7,3,'1','192.168.40.1:8889','test','nihao','2020-10-21 13:19:42','200',NULL,'2020-10-21 13:19:42','400','重试次数已达上限','0','2020-10-21 13:19:42'),(47,7,3,'1','192.168.40.1:8889','test','nihao','2020-10-21 13:20:42','200',NULL,'2020-10-21 13:20:42','200',NULL,'0','2020-10-21 13:20:49');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
