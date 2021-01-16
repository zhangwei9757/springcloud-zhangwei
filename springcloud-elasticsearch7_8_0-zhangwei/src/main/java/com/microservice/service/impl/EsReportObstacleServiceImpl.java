package com.microservice.service.impl;

import com.microservice.entity.EsReportObstacle;
import com.microservice.mapper.EsReportObstacleMapper;
import com.microservice.service.IEsReportObstacleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhangwei
 * @since 2020-07-01
 */
@Service
public class EsReportObstacleServiceImpl extends ServiceImpl<EsReportObstacleMapper, EsReportObstacle> implements IEsReportObstacleService {

    @Resource
    private EsReportObstacleMapper esReportObstacleMapper;

    @Override
    public Integer truncateTable() {
        return esReportObstacleMapper.truncateTable();
    }

    @Override
    public Integer insertBatch(List<EsReportObstacle> list) {
        return esReportObstacleMapper.insertBatch(list);
    }
}
