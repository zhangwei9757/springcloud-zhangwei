package com.microservice.repository;

import com.microservice.dto.Article;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author zhangwei
 * @date 2020-06-28
 * <p>
 */
@Repository
public interface ArticleRepository extends ElasticsearchRepository<Article, Long> {
}
