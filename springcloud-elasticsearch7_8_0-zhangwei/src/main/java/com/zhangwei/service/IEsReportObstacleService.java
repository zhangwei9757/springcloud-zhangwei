package com.zhangwei.service;

import com.zhangwei.entity.EsReportObstacle;
import com.baomidou.mybatisplus.extension.service.IService;

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
}
