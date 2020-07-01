package com.zhangwei.bean;

import com.zhangwei.utils.Defs;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author zhangwei
 * @date 2020-06-29
 * <p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = Defs.ES_INDEX_NAME)
public class ReportObstacle {

    @Id
    private Long id;
    /**
     * 单号
     */
    @Field(type = FieldType.Long, store = true, analyzer = Defs.ES_ANALYSIS_TYPE, searchAnalyzer = Defs.ES_ANALYSIS_TYPE)
    private Long troubleNo;
    /**
     * 问题标题
     */
    @Field(type = FieldType.Text, store = true, analyzer = Defs.ES_ANALYSIS_TYPE, searchAnalyzer = Defs.ES_ANALYSIS_TYPE)
    private String problemTitle;
    /**
     * 问题描述
     */
    @Field(type = FieldType.Text, store = true, analyzer = Defs.ES_ANALYSIS_TYPE, searchAnalyzer = Defs.ES_ANALYSIS_TYPE)
    private String problemDesc;
    /**
     * 生成单时间
     */
    @Field(type = FieldType.Long, store = true, analyzer = Defs.ES_ANALYSIS_TYPE, searchAnalyzer = Defs.ES_ANALYSIS_TYPE)
    private Long troubleTime;
    /**
     * 系统名
     */
    @Field(type = FieldType.Text, store = true, analyzer = Defs.ES_ANALYSIS_TYPE, searchAnalyzer = Defs.ES_ANALYSIS_TYPE)
    private String systemName;
    /**
     * 模块名
     */
    @Field(type = FieldType.Text, store = true, analyzer = Defs.ES_ANALYSIS_TYPE, searchAnalyzer = Defs.ES_ANALYSIS_TYPE)
    private String moduleName;
}
