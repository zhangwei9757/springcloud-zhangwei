package com.zhangwei.controller;

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
import org.springframework.web.bind.annotation.*;

import java.util.*;
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
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.scroll(TimeValue.timeValueSeconds(60));

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.timeout(TimeValue.timeValueSeconds(60));
        searchSourceBuilder.size(10000);
        searchSourceBuilder.sort("troubleTime", SortOrder.DESC);

        searchRequest.source(searchSourceBuilder);
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        String scrollId = search.getScrollId();
        List<String> scrollIds = new ArrayList<>();
        scrollIds.add(scrollId);
        String temp = scrollId;

        SearchHit[] hits = search.getHits().getHits();
        List<Map<String, Object>> collect = Arrays.stream(hits).map(SearchHit::getSourceAsMap).collect(Collectors.toList());
        // TODO 同步数据第一次
        syncSize += collect.size();

        while (true) {
            SearchScrollRequest scrollRequest = new SearchScrollRequest(temp);
            scrollRequest.scroll(TimeValue.timeValueSeconds(60));

            SearchResponse scroll = restHighLevelClient.scroll(scrollRequest, RequestOptions.DEFAULT);
            SearchHit[] hits1 = scroll.getHits().getHits();
            if (hits1 == null || hits1.length <= 0) {
                break;
            }
            List<Map<String, Object>> collect1 = Arrays.stream(hits1).map(SearchHit::getSourceAsMap).collect(Collectors.toList());
            // TODO 循环时同步数据第N次
            syncSize += collect1.size();

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
}
