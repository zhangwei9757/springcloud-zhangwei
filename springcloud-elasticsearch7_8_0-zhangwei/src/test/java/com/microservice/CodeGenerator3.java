package com.microservice;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

/**
 * @author zhangwei
 * @date 2020-06-29
 * <p>
 */
public class CodeGenerator3 {

    private static String PROJECT_GENERATE_DISK = System.getProperty("user.dir") + "\\" + "springcloud-elasticsearch7_8_0-zhangwei";
    private static String DB_URL = "jdbc:mysql://47.103.222.118:3306/elasticsearch?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false";
    private static String DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
    private static String DB_TYPE = DbType.MYSQL.name();
    private static String USER = "root";
    private static String PASSWORD = "Tpl!f81qsxas";
    private static String[] INCLUDE_TABLE_NAMES = new String[]{"es_report_obstacle"};
    private static String[] EXCLUDE_TABLE_NAMES = new String[]{};
    private static String AUTHOR = "zhangwei";
    private static boolean ENABLE_TABLE_FIELD_ANNOTATION = true;
    private static IdType TABLE_ID_TYPE = IdType.ID_WORKER;
    /**
     * 是否去掉生成实体的属性名前缀,如表有t_table1,m_table2，则这个值可以为 = new String[]{"t","m"}
     */
    private static String[] FIELD_PREFIX = null;

    /**
     * 全局配置
     *
     * @return
     */
    private static GlobalConfig GlobalGenerate() {
        GlobalConfig config = new GlobalConfig();
        /*不需要ActiveRecord特性的请改为false*/
        config.setActiveRecord(false)
                .setIdType(TABLE_ID_TYPE)
                /*是否启用二级缓存*/
                .setEnableCache(false)
                .setAuthor(AUTHOR)
                /*生成完之后，不弹窗，告知我在生成在哪个目录了*/
                .setOpen(false)
                /*XML 设置映射结果 ResultMap*/
                .setBaseResultMap(true)
                /*XML 设置表列 ColumnList*/
                .setBaseColumnList(true)
                /*设置生产的文件（包）在哪，一般是相对于本项目而言*/
                .setOutputDir(PROJECT_GENERATE_DISK)
                /*每次生成，是否覆盖之前的文件（慎重考虑啊）*/
                .setFileOverride(true)
                /*自定义文件命名，注意 %s 会自动填充表实体属性！*/
                .setControllerName("%sController")
                .setServiceName("%sService")
                .setServiceImplName("%sServiceImpl")
                .setMapperName("%sMapper")
                .setXmlName("%sMapper");
        return config;
    }

    /**
     * 数据源配置
     *
     * @return
     */
    private static DataSourceConfig DaoSourceGenerate() {
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        DbType type = null;
        if ("oracle".equals(DB_TYPE)) {
            type = DbType.ORACLE;
        } else if ("sql_server".equals(DB_TYPE)) {
            type = DbType.SQL_SERVER;
        } else if ("mysql".equals(DB_TYPE)) {
            type = DbType.MYSQL;
        } else if ("postgre_sql".equals(DB_TYPE)) {
            type = DbType.POSTGRE_SQL;
        }
        dataSourceConfig.setDbType(type)
                .setDriverName(DRIVER_CLASS_NAME)
                .setUrl(DB_URL)
                .setUsername(USER)
                .setPassword(PASSWORD);
        return dataSourceConfig;
    }

    /**
     * 策略配置
     *
     * @return
     */
    private static StrategyConfig StrategyGenerate() {
        StrategyConfig strategyConfig = new StrategyConfig();
        strategyConfig
                .setVersionFieldName("version")
                /*全局大写命名 ORACLE 注意*/
                .setCapitalMode(true)
                /*生成RestController*/
                .setRestControllerStyle(true)
                /*是否使用Lombok省略getter、setter*/
                .setEntityLombokModel(false)
//                .setDbColumnUnderline(true)
                /*表名生成策略 -- 驼峰*/
                .setNaming(NamingStrategy.underline_to_camel)
                .setEntityTableFieldAnnotationEnable(ENABLE_TABLE_FIELD_ANNOTATION)
                /* 生成指定的xxxController、、xxService等是否去掉数据库表名的前缀，如t_user -> user -> UserController*/
                .setFieldPrefix(FIELD_PREFIX)
                /*设置哪些表参与逆向工程，多个表名传数组*/
                .setInclude(INCLUDE_TABLE_NAMES)
                /*设置哪些表不参与逆向工程，多个表名传数组；注意，不能和Include一起使用*/
//                .setExclude(EXCLUDE_TABLE_NAMES)
                /*此处可以修改为您的表前缀*/
                .setTablePrefix(null)
                /*自定义实体，公共字段*/
                .setSuperEntityColumns(null)
                /*自定义 mapper 父类*/
                .setSuperMapperClass(null)
                /*自定义 service 父类*/
                .setSuperServiceClass(null)
                /*自定义 serviceImpl 父类*/
                .setSuperServiceImplClass(null)
                /*自定义 controller 父类*/
                .setSuperControllerClass(null)
                /*【实体】是否生成字段常量（默认 false）public static final String ID = "test_id";*/
                .setEntityColumnConstant(false)//
                /*【实体】是否为构建者模型（默认 false）public User setName(String name) {this.name = name; return this;}*/
                .setEntityBuilderModel(false)
                /*【实体】是否为lombok模型（默认 false）*/
                .setEntityLombokModel(false)
                /*Boolean类型字段是否移除is前缀处理*/
                .setEntityBooleanColumnRemoveIsPrefix(true);
        return strategyConfig;
    }

    private static TemplateConfig TemplateGenerate() {
        return new TemplateConfig()
                .setController("/templates/controller.java")
                .setMapper("/templates/mapper.java")
                .setXml("/templates/mapper.xml")
                .setService("/templates/service.java")
                .setServiceImpl("/templates/serviceImpl.java");
    }

    private static PackageConfig PackageGenerate() {
        return new PackageConfig()
                .setParent("com.microservice")
                .setController("controller")
                .setService("service")
                .setServiceImpl("service.impl")
                .setEntity("entity")
                .setMapper("mapper")
                .setXml("src.main.resources.mapper");
    }

    private void generateByTablesWithInjectConfig() {
        GlobalConfig config = CodeGenerator3.GlobalGenerate();
        DataSourceConfig dataSourceConfig = CodeGenerator3.DaoSourceGenerate();
        StrategyConfig strategyConfig = CodeGenerator3.StrategyGenerate();
        PackageConfig packageConfig = CodeGenerator3.PackageGenerate();
        new AutoGenerator()
                .setGlobalConfig(config)
                .setDataSource(dataSourceConfig)
                .setStrategy(strategyConfig)
                .setPackageInfo(packageConfig)
                .execute();
    }

    public static void main(String[] args) {
        new CodeGenerator3().generateByTablesWithInjectConfig();
    }
}