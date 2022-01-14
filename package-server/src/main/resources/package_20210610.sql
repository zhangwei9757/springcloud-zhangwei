/*
SQLyog Ultimate v11.11 (64 bit)
MySQL - 5.7.20-log : Database - package
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`package` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `package`;

/*Table structure for table `package_build_job` */

DROP TABLE IF EXISTS `package_build_job`;

CREATE TABLE `package_build_job` (
  `bj_id` int(13) NOT NULL AUTO_INCREMENT,
  `bj_gp_id` int(13) DEFAULT NULL COMMENT 'gitlab代码库工程表主键id',
  `bj_jm_id` int(13) DEFAULT NULL COMMENT '本次构建jenkin主备多环境表主键id',
  `bj_cm_id` int(13) DEFAULT NULL COMMENT '证书管理表主键id',
  `bj_jobname` varchar(255) DEFAULT NULL COMMENT 'jenkins原生jobname',
  `bj_target_branch` varchar(255) DEFAULT NULL COMMENT '本次构建任务打包分支',
  `bj_build_params` text COMMENT '本次构建参数，josn字符串',
  `bj_build_type` varchar(255) DEFAULT NULL COMMENT '构建类型  1：全量  2：增量',
  `bj_build_version` varchar(255) DEFAULT NULL COMMENT '本次构建版本号',
  `bj_build_status` char(2) DEFAULT NULL COMMENT '0:等待构建 1:构建中 2:构建成功 3: 构建失败',
  `bj_build_promoter_name` varchar(255) DEFAULT NULL COMMENT '任务发起人',
  `bj_build_promoter_real_name` varchar(255) DEFAULT NULL COMMENT '任务发起人中文名',
  `bj_package_type` varchar(255) DEFAULT NULL COMMENT '打包类型 jar war android ios',
  `bj_package_name` varchar(255) DEFAULT NULL COMMENT '构建成功包名',
  `bj_package_download_url` varchar(255) DEFAULT NULL COMMENT '构建成功包下载路径',
  `bj_handle_no` int(5) DEFAULT NULL COMMENT '操作序号',
  `bj_package_time` datetime DEFAULT NULL COMMENT '本次打包任务时间',
  PRIMARY KEY (`bj_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `package_build_job` */

/*Table structure for table `package_build_job_his` */

DROP TABLE IF EXISTS `package_build_job_his`;

CREATE TABLE `package_build_job_his` (
  `bjh_id` int(13) NOT NULL AUTO_INCREMENT,
  `bjh_bj_id` int(13) DEFAULT NULL COMMENT '构建job表主键id',
  `bjh_gp_id` int(13) DEFAULT NULL COMMENT 'gitlab代码库工程表主键id',
  `bjh_jm_id` int(13) DEFAULT NULL COMMENT '本次构建jenkin主备多环境表主键id',
  `bjh_cm_id` int(13) DEFAULT NULL COMMENT '证书管理表主键id',
  `bjh_jobname` varchar(255) DEFAULT NULL COMMENT 'jenkins原生jobname',
  `bjh_target_branch` varchar(255) DEFAULT NULL COMMENT '本次构建任务打包分支',
  `bjh_build_params` text COMMENT '本次构建参数，josn字符串',
  `bjh_build_type` varchar(255) DEFAULT NULL COMMENT '构建类型  1：全量  2：增量',
  `bjh_build_version` varchar(255) DEFAULT NULL COMMENT '本次构建版本号',
  `bjh_build_status` char(2) DEFAULT NULL COMMENT '0:等待构建 1:构建中 2:构建成功 3: 构建失败',
  `bjh_build_promoter_name` varchar(255) DEFAULT NULL COMMENT '任务发起人',
  `bjh_build_promoter_real_name` varchar(255) DEFAULT NULL COMMENT '任务发起人中文名',
  `bjh_package_type` varchar(255) DEFAULT NULL COMMENT '打包类型 jar war android ios',
  `bjh_package_name` varchar(255) DEFAULT NULL COMMENT '构建成功包名',
  `bjh_package_download_url` varchar(255) DEFAULT NULL COMMENT '构建成功包下载路径',
  `bjh_handle_no` int(5) DEFAULT NULL COMMENT '操作序号',
  `bjh_package_time` datetime DEFAULT NULL COMMENT '本次打包任务时间',
  PRIMARY KEY (`bjh_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `package_build_job_his` */

/*Table structure for table `package_build_main` */

DROP TABLE IF EXISTS `package_build_main`;

CREATE TABLE `package_build_main` (
  `bm_id` int(13) NOT NULL AUTO_INCREMENT,
  `bm_build_host` varchar(255) DEFAULT NULL COMMENT 'ip地址',
  `bm_build_port` int(8) DEFAULT NULL COMMENT '端口',
  `bm_build_url` varchar(255) DEFAULT NULL COMMENT '主访问路径',
  `bm_build_user_name` varchar(255) DEFAULT NULL COMMENT '登录用户名',
  `bm_build_pass_word` varchar(255) DEFAULT NULL COMMENT '登录密码',
  `bm_tools_name` char(2) DEFAULT NULL COMMENT '工具名 如: jenkins',
  `bm_tools_standby` char(2) DEFAULT NULL COMMENT '主备类型 1:主 2:备',
  `bm_create_name` varchar(255) DEFAULT NULL,
  `bm_create_real_name` varchar(255) DEFAULT NULL,
  `bm_create_time` datetime DEFAULT NULL,
  `bm_update_name` varchar(255) DEFAULT NULL,
  `bm_update_real_name` varchar(255) DEFAULT NULL,
  `bm_update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`bm_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

/*Data for the table `package_build_main` */

insert  into `package_build_main`(`bm_id`,`bm_build_host`,`bm_build_port`,`bm_build_url`,`bm_build_user_name`,`bm_build_pass_word`,`bm_tools_name`,`bm_tools_standby`,`bm_create_name`,`bm_create_real_name`,`bm_create_time`,`bm_update_name`,`bm_update_real_name`,`bm_update_time`) values (1,'192.168.40.143',8080,'http://192.168.40.143:8080','admin','admin','1',NULL,'zhangwei','张伟','2021-04-12 11:12:16',NULL,NULL,NULL);

/*Table structure for table `package_certificate_main` */

DROP TABLE IF EXISTS `package_certificate_main`;

CREATE TABLE `package_certificate_main` (
  `cm_id` int(13) NOT NULL AUTO_INCREMENT,
  `cm_type` char(2) DEFAULT NULL COMMENT '证书类型 1: ios 2:android 3...',
  `cm_name` varchar(255) DEFAULT NULL COMMENT '证书文件名',
  `cm_full_path` varchar(255) DEFAULT NULL COMMENT '证书全路径',
  `cm_certificate_name` varchar(255) DEFAULT NULL COMMENT '证书名',
  `cm_certificate_type` varchar(255) DEFAULT NULL COMMENT '证书类型',
  `cm_expiration_time` datetime DEFAULT NULL COMMENT '证书过期日期',
  `cm_generate_time` datetime DEFAULT NULL COMMENT '证书生成时间',
  `cm_status` char(1) DEFAULT NULL COMMENT '证书状态  0: 已删除 1: 使用中',
  `cm_create_name` varchar(255) DEFAULT NULL COMMENT '操作人',
  `cm_create_real_name` varchar(255) DEFAULT NULL COMMENT '创建人中文名',
  `cm_create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `cm_update_name` varchar(255) DEFAULT NULL COMMENT '修改人',
  `cm_update_real_name` varchar(255) DEFAULT NULL COMMENT '修改人中文名',
  `cm_update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`cm_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `package_certificate_main` */

/*Table structure for table `package_gitlab_project` */

DROP TABLE IF EXISTS `package_gitlab_project`;

CREATE TABLE `package_gitlab_project` (
  `gp_id` int(13) NOT NULL AUTO_INCREMENT,
  `gp_name` varchar(255) DEFAULT NULL COMMENT '项目名',
  `gp_system_code` varchar(255) DEFAULT NULL COMMENT 'cmdb系统代码',
  `gp_system_name` varchar(255) DEFAULT NULL COMMENT 'cmdb系统名称',
  `gp_create_name` varchar(255) DEFAULT NULL COMMENT '创建人',
  `gp_create_real_name` varchar(255) DEFAULT NULL COMMENT '创建人名',
  `gp_create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`gp_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `package_gitlab_project` */

/*Table structure for table `package_gitlab_relationship` */

DROP TABLE IF EXISTS `package_gitlab_relationship`;

CREATE TABLE `package_gitlab_relationship` (
  `gr_id` int(13) NOT NULL AUTO_INCREMENT,
  `gr_parent_gp_id` int(13) DEFAULT NULL COMMENT '父项目主键id',
  `gr_children_gp_id` int(13) DEFAULT NULL COMMENT '子项目主键id',
  PRIMARY KEY (`gr_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `package_gitlab_relationship` */

/*Table structure for table `package_pkg_download_his` */

DROP TABLE IF EXISTS `package_pkg_download_his`;

CREATE TABLE `package_pkg_download_his` (
  `pdh_id` int(13) NOT NULL AUTO_INCREMENT,
  `pdh_bj_id` int(13) DEFAULT NULL COMMENT '构建任务表主键id',
  `pdh_sys_id` varchar(255) DEFAULT NULL COMMENT '运维管理平台申请系统',
  `pdh_apply_name` varchar(255) DEFAULT NULL COMMENT '申请人',
  `pdh_apply_real_name` varchar(255) DEFAULT NULL COMMENT '申请人中文名',
  `pdh_apply_time` datetime DEFAULT NULL COMMENT '申请时间',
  PRIMARY KEY (`pdh_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `package_pkg_download_his` */

/*Table structure for table `work_details` */

DROP TABLE IF EXISTS `work_details`;

CREATE TABLE `work_details` (
  `id` int(13) NOT NULL AUTO_INCREMENT,
  `work_type` varchar(255) DEFAULT NULL,
  `desc` varchar(255) DEFAULT NULL,
  `process` int(2) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;

/*Data for the table `work_details` */

insert  into `work_details`(`id`,`work_type`,`desc`,`process`) values (1,'环境准备','苹果主备二台 jenkins打包机器',0),(2,'开发事项','打包入口与任务排队(?)',0),(3,'开发事项','打包集成流水线',0),(4,'开发事项','包管理(?)与包下载',0),(5,'开发事项','打包任务历史记录，包下载记录',0),(6,'开发事项','对接发布执行，上传包环节',0),(7,'开发事项','对接移动平台(?)',0),(8,'开发事项','证书增删除改查',0),(9,'开发事项','操作日志，收集，清洗，审计',0),(10,'开发事项','集成打包平台',0),(11,'测试','功能测试',0),(12,'测试','兼容性测试',0),(13,'测试','队列性能测试',0),(14,'测试','联调测试',0);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
