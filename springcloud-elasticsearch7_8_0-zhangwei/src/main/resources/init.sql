CREATE TABLE `es_report_obstacle` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `obstacleNo` bigint(30) DEFAULT NULL COMMENT '单号',
  `obstacleTitle` varchar(1024) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '问题标题',
  `obstacleDesc` varchar(1024) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '问题描述',
  `obstacleTime` bigint(30) DEFAULT NULL COMMENT '生成单时间',
  `systemName` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '系统名',
  `moduleName` varchar(200) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '模块名',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin