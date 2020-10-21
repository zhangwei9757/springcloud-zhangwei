package com.microservice.service;

import com.microservice.entity.EsReportObstacle;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhangwei
 * @since 2020-07-01
 */
public interface IEsReportObstacleService extends IService<EsReportObstacle> {

    Integer truncateTable();

    Integer insertBatch(List<EsReportObstacle> list);
}
