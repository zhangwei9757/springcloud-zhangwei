//package com.zhangwei.config;
//
//import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
//import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
//import com.zaxxer.hikari.HikariDataSource;
//import org.apache.ibatis.plugin.Interceptor;
//import org.apache.ibatis.session.SqlSessionFactory;
//import org.mybatis.spring.SqlSessionTemplate;
//import org.mybatis.spring.annotation.MapperScan;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.jdbc.DataSourceBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.jdbc.datasource.DataSourceTransactionManager;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
//import javax.sql.DataSource;
//
//@EnableTransactionManagement
//@MapperScan(value = {"com.zhangwei.mapper"}, sqlSessionTemplateRef = "sqlSessionTemplate")
//@Configuration
//public class DataSourceConfig {
//
//    @Bean
//    public PaginationInterceptor paginationInterceptor() {
//        return new PaginationInterceptor();
//    }
//
//    @Bean(name = "sqlSessionFactory")
//    public SqlSessionFactory sqlSessionFactory(DataSource dataSource, @Qualifier("paginationInterceptor") PaginationInterceptor paginationInterceptor) throws Exception {
//        /**坑1：注意这里一定要是MybatisPlus的MybatisSqlxxBean，而不是正常的SqlXXBean，否者会出现statement绑定无效*/
//        MybatisSqlSessionFactoryBean dto = new MybatisSqlSessionFactoryBean();
//        dto.setDataSource(dataSource);
//        /**坑2：注意，一定要设置 MyBatis-Plus的分页插件，否者分页结果无效*/
//        Interceptor[] plugins = {paginationInterceptor};
//        dto.setPlugins(plugins);
//        return dto.getObject();
//    }
//
//    @Bean(name = "transactionManager")
//    public DataSourceTransactionManager transactionManager(DataSource dataSource) {
//        return new DataSourceTransactionManager(dataSource);
//    }
//
//    @Bean(name = "sqlSessionTemplate")
//    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
//        return new SqlSessionTemplate(sqlSessionFactory);
//    }
//}