package com.microservice.controller;

import com.microservice.dto.Article;
import com.microservice.repository.ArticleRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * @author zhangwei
 * @date 2020-06-28
 * <p>
 */
@RestController
@RequestMapping(value = "/es")
@Api(value = "elasticsearch", description = "elasticsearch管理")
public class ArticleController {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @GetMapping("/createIndex")
    @PostMapping(value = "/create")
    @ApiOperation(value = "创建索引及类型", tags = "elasticsearch管理")
    public String createIndex() {
        return elasticsearchTemplate.createIndex(Article.class) ? "创建索引及类型成功" : "创建索引及类型失败";
    }

    @PostMapping(value = "/addDocumnet")
    @ApiOperation(value = "创建文档", tags = "elasticsearch管理")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "article", value = "文档内容", required = true,  dataType = "Article", paramType = "body")
    })
    public Article addDocumnet(@RequestBody Article article) {
        return articleRepository.save(article);
    }
}
