package com.zhangwei.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Stopwatch;
import com.zhangwei.dto.ResponseDto;
import com.zhangwei.entity.EsReportObstacle;
import com.zhangwei.mapper.EsReportObstacleMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author zhangwei
 * @date 2020-06-28
 * <p>
 */
@RestController
@RequestMapping(value = "/mysql")
@Api(value = "MYSQL搜索", tags = "MYSQL搜索")
@Slf4j
public class MysqlReportObstacleController {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Resource
    private EsReportObstacleMapper esReportObstacleMapper;

    @Autowired
    private Executor executor;

    /**
     * 从ES同步全量数据到数据库
     *
     * @param indexName
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/syncDatabase")
    @ApiOperation(value = "从ES同步全量数据到数据库", tags = "MYSQL搜索")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "indexName", value = "索引名", required = true, dataType = "String", paramType = "query")
    })
    public Object syncDatabase(@RequestParam("indexName") String indexName) throws Exception {
        if (StringUtils.isBlank(indexName)) {
            return "索引名不能为空";
        }
        long syncSize = 0;
        TimeValue timeValue = TimeValue.timeValueSeconds(300);
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.scroll(timeValue);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.timeout(timeValue);
        searchSourceBuilder.size(500);
        searchSourceBuilder.sort("obstacleTime", SortOrder.DESC);

        searchRequest.source(searchSourceBuilder);
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        String scrollId = search.getScrollId();
        List<String> scrollIds = new ArrayList<>();
        scrollIds.add(scrollId);
        String temp = scrollId;

        SearchHit[] hits = search.getHits().getHits();
        List<Map<String, Object>> collect = Arrays.stream(hits).map(SearchHit::getSourceAsMap).collect(Collectors.toList());

        syncSize += collect.size();
        List<EsReportObstacle> esReportObstacles = this.dto2Entity(collect);
        this.batchSave(esReportObstacles, 512);

        while (true) {
            SearchScrollRequest scrollRequest = new SearchScrollRequest(temp);
            scrollRequest.scroll(timeValue);

            SearchResponse scroll = restHighLevelClient.scroll(scrollRequest, RequestOptions.DEFAULT);
            SearchHit[] hits1 = scroll.getHits().getHits();
            if (hits1 == null || hits1.length <= 0) {
                break;
            }
            List<Map<String, Object>> collect1 = Arrays.stream(hits1).map(SearchHit::getSourceAsMap).collect(Collectors.toList());

            syncSize += collect1.size();
            List<EsReportObstacle> reportObstacles = this.dto2Entity(collect);
            this.batchSave(reportObstacles, 512);

            String scrollId1 = scroll.getScrollId();
            if (!Objects.deepEquals(scrollId1, temp)) {
                temp = scrollId1;
                scrollIds.add(scrollId1);
            }
        }

        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
        clearScrollRequest.setScrollIds(scrollIds);
        ClearScrollResponse clearScrollResponse = restHighLevelClient.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
        boolean succeeded = clearScrollResponse.isSucceeded();
        return succeeded ? String.format("%s 条数据, 同步成功", syncSize) : "同步失败";
    }

    /**
     * 清空MYSQL数据库
     *
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/clearDataBase")
    @ApiOperation(value = "清空MYSQL数据库", tags = "MYSQL搜索")
    public Object clearDataBase() throws Exception {
        esReportObstacleMapper.truncateTable();
        return "操作成功";
    }

    /**
     * 查询所有数据
     *
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/findAll")
    @ApiOperation(value = "查询所有数据[谨慎使用]", tags = "MYSQL搜索")
    public ResponseDto findAll() throws Exception {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Integer integer = esReportObstacleMapper.selectCount(null);
        if (integer != null && integer >= 10000) {
            return ResponseDto.error("总数已达到10000, 禁止查询所有");
        }
        List<EsReportObstacle> reportObstacles = esReportObstacleMapper.selectList(null);
        long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        stopwatch.stop();
        return ResponseDto.success(reportObstacles, elapsed);
    }

    /**
     * 查询数据
     *
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/findField")
    @ApiOperation(value = "查询数据", tags = "MYSQL搜索")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "queryField", value = "查询字段", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "queryContent", value = "查询内容", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "readOnlyCount", value = "只显示数量", required = true, dataType = "boolean", paramType = "query")
    })
    public Object findField(@RequestParam(value = "queryField", required = false) String queryField,
                            @RequestParam(value = "queryContent", required = false) String queryContent,
                            @RequestParam(value = "readOnlyCount", required = false) boolean readOnlyCount) throws Exception {
        if (StringUtils.isBlank(queryField)) {
            return "查询字段不能为空";
        }

        if (StringUtils.isBlank(queryContent)) {
            return "查询内容不能为空";
        }
        Stopwatch stopwatch = Stopwatch.createStarted();
        QueryWrapper<EsReportObstacle> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(queryField, queryContent);
        List<EsReportObstacle> reportObstacles = esReportObstacleMapper.selectList(queryWrapper);
        long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        stopwatch.stop();
        return readOnlyCount ? ResponseDto.success(reportObstacles.size(), elapsed) : ResponseDto.success(reportObstacles, elapsed);
    }

    /**
     * ES 数据转 Entity
     *
     * @param collect
     * @return
     */
    private List<EsReportObstacle> dto2Entity(List<Map<String, Object>> collect) {
        List<EsReportObstacle> list = new ArrayList<>(collect.size());
        collect.forEach(f -> {
            String moduleName = (String) f.getOrDefault("moduleName", "");
            String problemDesc = (String) f.getOrDefault("obstacleDesc", "");
            String problemTitle = (String) f.getOrDefault("obstacleTitle", "");
            String systemName = (String) f.getOrDefault("systemName", "");
            long currentTimeMillis = System.currentTimeMillis();
            EsReportObstacle esReportObstacle = new EsReportObstacle();
            esReportObstacle.setModuleName(moduleName);
            esReportObstacle.setObstacleDesc(problemDesc);
            esReportObstacle.setObstacleTitle(problemTitle);
            esReportObstacle.setSystemName(systemName);
            esReportObstacle.setObstacleNo(currentTimeMillis);
            esReportObstacle.setObstacleTime(currentTimeMillis);
            list.add(esReportObstacle);
        });
        return list;
    }

    /**
     * @param collect
     * @param dealSize 每个线程处理的数量
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchSave(List<EsReportObstacle> collect, int dealSize) throws Exception {
        if (CollectionUtils.isEmpty(collect)) {
            return;
        }

        int count = collect.size() / dealSize;
        int surplus = collect.size() % dealSize;
        if (surplus > 0) {
            count += 1;
        }
        CountDownLatch countDownLatch = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            int temp = i;
            executor.execute(() -> {
                int endNum = (temp + 1) * dealSize - 1;
                if (endNum >= collect.size()) {
                    endNum = collect.size() - 1;
                }
                esReportObstacleMapper.insertBatch(collect.subList(temp * dealSize, endNum));
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
    }
}
