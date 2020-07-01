package com.zhangwei.mapper;

import com.zhangwei.entity.EsReportObstacle;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zhangwei
 * @since 2020-07-01
 */
public interface EsReportObstacleMapper extends BaseMapper<EsReportObstacle> {

    Integer truncateTable();

    Integer insertBatch(List<EsReportObstacle> list);
}
