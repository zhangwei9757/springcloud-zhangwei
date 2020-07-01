package com.zhangwei.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhangwei
 * @date 2020-06-29
 * <p>
 */
@Configuration
public class ElasticsearchClientConfig {

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        HttpHost host1 = new HttpHost("localhost", 9201, HttpHost.DEFAULT_SCHEME_NAME);
        HttpHost host2 = new HttpHost("localhost", 9202, HttpHost.DEFAULT_SCHEME_NAME);
        HttpHost host3 = new HttpHost("localhost", 9203, HttpHost.DEFAULT_SCHEME_NAME);

        RestClientBuilder builder = RestClient.builder(host1, host2, host3);
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("elastic","123456"));
        builder.setHttpClientConfigCallback(f -> f.setDefaultCredentialsProvider(credentialsProvider));

//        RestHighLevelClient client = new RestHighLevelClient(
//                RestClient.builder(
//                        new HttpHost("localhost", 9201, HttpHost.DEFAULT_SCHEME_NAME),
//                        new HttpHost("localhost", 9202, HttpHost.DEFAULT_SCHEME_NAME),
//                        new HttpHost("localhost", 9203, HttpHost.DEFAULT_SCHEME_NAME)));
        RestHighLevelClient restClient = new RestHighLevelClient(builder);
        return restClient;
    }
}
