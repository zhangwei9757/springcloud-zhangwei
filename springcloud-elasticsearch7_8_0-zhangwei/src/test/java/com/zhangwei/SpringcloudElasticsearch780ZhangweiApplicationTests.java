package com.zhangwei;

import com.zhangwei.Beans.User;
import com.zhangwei.dto.ReportObstacleDto;
import com.zhangwei.utils.Defs;
import com.zhangwei.utils.JsonUtils;
import com.zhangwei.utils.RandomUtils;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringcloudElasticsearch780ZhangweiApplicationTests {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 创建索引
     *
     * @throws Exception
     */
    @Test
    public void testCreateIndex() throws Exception {
        CreateIndexRequest indexRequest = new CreateIndexRequest("users");

        indexRequest.settings(Settings.builder()
                .put("index.number_of_shards", 5)
                .put("index.number_of_replicas", 1)
        );

        CreateIndexResponse response = restHighLevelClient.indices().create(indexRequest, RequestOptions.DEFAULT);
        boolean acknowledged = response.isAcknowledged();
        System.out.println(acknowledged);
        System.out.println(response);
    }

    /**
     * 获取索引
     *
     * @throws Exception
     */
    @Test
    public void testExistsIndex() throws Exception {
        GetIndexRequest indexRequest = new GetIndexRequest("users");
        boolean exists = restHighLevelClient.indices().exists(indexRequest, RequestOptions.DEFAULT);
        System.out.println(exists);
//        GetIndexResponse getIndexResponse = restHighLevelClient.indices().get(indexRequest, RequestOptions.DEFAULT);
//        System.out.println(getIndexResponse.getDataStreams());
    }

    /**
     * 删除索引
     *
     * @throws Exception
     */
    @Test
    public void testDeleteIndex() throws Exception {
        DeleteIndexRequest indexRequest = new DeleteIndexRequest("users");
        AcknowledgedResponse delete = restHighLevelClient.indices().delete(indexRequest, RequestOptions.DEFAULT);
        System.out.println(delete);
    }

    /**
     * 添加文档
     *
     * @throws Exception
     */
    @Test
    public void testCreateDocument() throws Exception {
        User user = new User("zhangwei", 30);

        IndexRequest request = new IndexRequest("users");
        request.id("1");
        request.timeout(TimeValue.timeValueSeconds(1));
        request.timeout("1s");

        request.source(Objects.requireNonNull(JsonUtils.toJson(user)), XContentType.JSON);

        IndexResponse index = restHighLevelClient.index(request, RequestOptions.DEFAULT);
        System.out.println(index.toString());
        System.out.println(index.status());
    }

    /**
     * 文档是否存在
     *
     * @throws Exception
     */
    @Test
    public void testExistsDocument() throws Exception {

        GetRequest getRequest = new GetRequest("users", "1");
        getRequest.fetchSourceContext(new FetchSourceContext(false));

        boolean exists = restHighLevelClient.exists(getRequest, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    /**
     * 获取文档  GET users/_doc/1
     *
     * @throws Exception
     */
    @Test
    public void testGetDocument() throws Exception {

        GetRequest getRequest = new GetRequest("users", "1");

        GetResponse documentFields = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        System.out.println(documentFields);
        System.out.println(documentFields.getSourceAsString());
    }

    /**
     * 更新文档  POST users/_doc/1/_update {...}
     *
     * @throws Exception
     */
    @Test
    public void testUpdateDocument() throws Exception {

        UpdateRequest updateRequest = new UpdateRequest("users", "1");
        updateRequest.timeout("1s");
        User updateUser = new User(null, 22);
        updateRequest.doc(JsonUtils.toJson(updateUser), XContentType.JSON);

        UpdateResponse update = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        System.out.println(update);
        System.out.println(update.status());
    }

    /**
     * 删除文档  DELETE users/_doc/1
     *
     * @throws Exception
     */
    @Test
    public void testDeleteDocument() throws Exception {

        DeleteRequest deleteRequest = new DeleteRequest("users", "1");
        deleteRequest.timeout("1s");


        DeleteResponse delete = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println(delete);
        System.out.println(delete.status());
    }

    /**
     * 批量插入文档
     *
     * @throws Exception
     */
    @Test
    public void testBulkDocument() throws Exception {

        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout(TimeValue.timeValueSeconds(10));

        List list = new ArrayList(100);

        User 测试用户而已 = new User("测试用户而已", 0);

        for (int i = 0; i < 100; i++) {
            测试用户而已.setAge(i);
            bulkRequest.add(new IndexRequest("users")
                    .id("" + (i + 1))
                    .source(Objects.requireNonNull(JsonUtils.toJson(测试用户而已)), XContentType.JSON));
        }

        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(bulk);
        System.out.println(bulk.status());
    }

    /**
     * ES 常规查询 query query_string term bool match should
     *
     * @throws Exception
     */
    @Test
    public void testSearch() throws Exception {
        SearchRequest searchRequest = new SearchRequest("users");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
//        TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery("name", "测试");
        QueryStringQueryBuilder queryStringQueryBuilder = QueryBuilders.queryStringQuery("测试")
                .defaultField("name");
        searchSourceBuilder.query(queryStringQueryBuilder)
                .timeout(TimeValue.timeValueSeconds(2))
                .from(2)
                .size(20);

        searchRequest.source(searchSourceBuilder);

        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(search);
        System.out.println(search.getHits().getTotalHits().value);
        for (SearchHit hit : search.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    }

    @Test
    public void createTroubleIndex() throws Exception {
        String indexName = Defs.ES_INDEX_NAME;
        CreateIndexRequest indexRequest = new CreateIndexRequest(indexName);
        Settings build = Settings.builder()
                .put("number_of_shards", "5")
                .put("number_of_replicas", "1")
                .build();
        indexRequest.settings(build);
        String json = "{" +
                "  \"properties\": {" +
                "    \"troubleNo\": {" +
                "      \"type\":\"long\"," +
                "      \"store\": true" +
                "    }," +
                "    \"problemTitle\": {" +
                "      \"type\": \"text\"," +
                "      \"store\": true," +
                "      \"analyzer\": \"ik_smart\"" +
                "    }," +
                "    \"problemDesc\": {" +
                "      \"type\": \"text\"," +
                "      \"store\": true," +
                "      \"analyzer\": \"ik_smart\"" +
                "    }," +
                "    \"troubleTime\": {" +
                "      \"type\": \"long\"," +
                "      \"store\": true" +
                "    }," +
                "    \"systemName\": {" +
                "      \"type\": \"text\"," +
                "      \"store\": true," +
                "      \"analyzer\": \"ik_smart\"" +
                "    }," +
                "    \"moduleName\": {" +
                "      \"type\": \"text\"," +
                "      \"store\": true," +
                "      \"analyzer\": \"ik_smart\"" +
                "    }" +
                "  }" +
                "}";
        indexRequest.mapping(json, XContentType.JSON);
        CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(indexRequest, RequestOptions.DEFAULT);
        boolean acknowledged = createIndexResponse.isAcknowledged();
        indexName += acknowledged ? ",创建成功" : ",创建失败";
        System.out.println(indexName);
    }

    @Test
    public void addBulkDocumnet() throws Exception {
        long start = System.currentTimeMillis();
        StringBuilder contents = new StringBuilder(" 中国疾控中心流行病学首席专家吴尊友：北京市及时采取防控措施，后期发现的" +
                "确诊病例多来自于已经处于观察、控制之中的隔离者。目前知道的新冠肺炎最长潜伏期是14天，单次去新发地市场的人员发病" +
                "时间平均在4天，短的2天，长的13天 。从这些时间来判断的话，在未来的7天左右，北京的确诊病例将会清零。");
        int size = contents.length();
        int dist_start = 90001;
        int dist_end = 120000;

        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout(TimeValue.timeValueSeconds(60));

        for (int i = dist_start; i < dist_end; i++) {
            ReportObstacleDto reportObstacle = new ReportObstacleDto();
            reportObstacle.setId(System.currentTimeMillis());
            reportObstacle.setObstacleNo(System.currentTimeMillis());
            reportObstacle.setObstacleTitle(UUID.randomUUID().toString());
            int between = RandomUtils.getBetween(0, size);
            reportObstacle.setObstacleDesc(contents.substring(between));
            reportObstacle.setObstacleTime(System.currentTimeMillis());
            between = between < 4 ? 4 : between;
            reportObstacle.setSystemName(contents.substring(between - 4, between));
            between = between >= size ? size - 2 : between;
            reportObstacle.setModuleName(between >= size * 0.5 ? contents.substring(between - 2, between) : "");

            bulkRequest.add(new IndexRequest(Defs.ES_INDEX_NAME)
                    .id("" + (i + 1))
                    .source(Objects.requireNonNull(JsonUtils.toJson(reportObstacle)), XContentType.JSON));

        }

        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        long end = System.currentTimeMillis();
        String rtn = String.format("ES在索引%s中, 生成%s条数据%s, 耗时: %s秒", Defs.ES_INDEX_NAME, dist_end - dist_start + 1, !bulk.hasFailures(), (end - start) / 1000);
        System.out.println(rtn);
    }
}
