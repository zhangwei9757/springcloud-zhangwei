package com.microservice.bean;

import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.druid.DruidConfig;
import lombok.Data;

/**
 * @author zw
 * @date 2020-11-21
 * <p>
 * master_2:
 * username: root
 * password: jzbr
 * driver-class-name: com.mysql.jdbc.Driver
 * url: jdbc:mysql://127.0.0.1:3306/xxx?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&zeroDateTimeBehavior=convertToNull&useSSL=false
 * druid: #以下均为默认值
 * initial-size: 3
 * max-active: 8
 * min-idle: 2
 * max-wait: -1
 * min-evictable-idle-time-millis: 30000
 * max-evictable-idle-time-millis: 30000
 * time-between-eviction-runs-millis: 0
 * validation-query: select 1
 * validation-query-timeout: -1
 * test-on-borrow: false
 * test-on-return: false
 * test-while-idle: true
 * pool-prepared-statements: true
 * max-open-prepared-statements: 100
 * filters: stat,wall
 * share-prepared-statements: true
 */
@Data
public class DataSourceProperties {

    private String poolName;

    private String username;
    private String password;
    private String driverClassName;
    private String url;

    private DruidConfig druid;
//    {
//        "driverClassName": "com.mysql.jdbc.Driver",
//            "druid": {
//        "asyncInit": true,
//                "breakAfterAcquireFailure": true,
//                "clearFiltersEnable": true,
//                "connectionErrorRetryAttempts": 0,
//                "connectionProperties": {},
//        "failFast": true,
//                "filters": "stat,wall",
//                "initConnectionSqls": "",
//                "initGlobalVariants": true,
//                "initVariants": true,
//                "initialSize": 3,
//                "keepAlive": true,
//                "killWhenSocketReadTimeout": true,
//                "logAbandoned": true,
//                "maxActive": 8,
//                "maxEvictableIdleTimeMillis": 30000,
//                "maxPoolPreparedStatementPerConnectionSize": 0,
//                "maxWait": -1,
//                "maxWaitThreadCount": 0,
//                "minEvictableIdleTimeMillis": 30000,
//                "minIdle": 2,
//                "notFullTimeoutRetryCount": 0,
//                "phyTimeoutMillis": 0,
//                "poolPreparedStatements": true,
//                "proxyFilters": [],
//        "publicKey": "",
//                "queryTimeout": 0,
//                "removeAbandoned": true,
//                "removeAbandonedTimeoutMillis": 0,
//                "resetStatEnable": true,
//                "sharePreparedStatements": true,
//                "slf4j": {
//            "enable": true,
//                    "statementExecutableSqlLogEnable": true
//        },
//        "stat": {
//            "logSlowSql": true,
//                    "mergeSql": true,
//                    "slowSqlMillis": 0
//        },
//        "statSqlMaxSize": 0,
//                "testOnBorrow": false,
//                "testOnReturn": false,
//                "testWhileIdle": true,
//                "timeBetweenEvictionRunsMillis": 0,
//                "timeBetweenLogStatsMillis": 0,
//                "transactionQueryTimeout": 0,
//                "useGlobalDataSourceStat": true,
//                "useUnfairLock": true,
//                "validationQuery": "select 1",
//                "validationQueryTimeout": -1,
//                "wall": {
//            "alterTableAllow": true,
//                    "blockAllow": true,
//                    "callAllow": true,
//                    "caseConditionConstAllow": true,
//                    "commentAllow": true,
//                    "commitAllow": true,
//                    "completeInsertValuesCheck": true,
//                    "conditionAndAlwayFalseAllow": true,
//                    "conditionAndAlwayTrueAllow": true,
//                    "conditionDoubleConstAllow": true,
//                    "conditionLikeTrueAllow": true,
//                    "conditionOpBitwseAllow": true,
//                    "conditionOpXorAllow": true,
//                    "constArithmeticAllow": true,
//                    "createTableAllow": true,
//                    "deleteAllow": true,
//                    "deleteWhereAlwayTrueCheck": true,
//                    "deleteWhereNoneCheck": true,
//                    "describeAllow": true,
//                    "dir": "",
//                    "doPrivilegedAllow": true,
//                    "dropTableAllow": true,
//                    "functionCheck": true,
//                    "hintAllow": true,
//                    "insertAllow": true,
//                    "insertValuesCheckSize": 0,
//                    "intersectAllow": true,
//                    "limitZeroAllow": true,
//                    "lockTableAllow": true,
//                    "mergeAllow": true,
//                    "metadataAllow": true,
//                    "minusAllow": true,
//                    "multiStatementAllow": true,
//                    "mustParameterized": true,
//                    "noneBaseStatementAllow": true,
//                    "objectCheck": true,
//                    "renameTableAllow": true,
//                    "replaceAllow": true,
//                    "rollbackAllow": true,
//                    "schemaCheck": true,
//                    "selectAllColumnAllow": true,
//                    "selectAllow": true,
//                    "selectExceptCheck": true,
//                    "selectHavingAlwayTrueCheck": true,
//                    "selectIntersectCheck": true,
//                    "selectIntoAllow": true,
//                    "selectIntoOutfileAllow": true,
//                    "selectLimit": 0,
//                    "selectMinusCheck": true,
//                    "selectUnionCheck": true,
//                    "selectWhereAlwayTrueCheck": true,
//                    "setAllow": true,
//                    "showAllow": true,
//                    "startTransactionAllow": true,
//                    "strictSyntaxCheck": true,
//                    "tableCheck": true,
//                    "tenantColumn": "",
//                    "tenantTablePattern": "",
//                    "truncateAllow": true,
//                    "updateAllow": true,
//                    "updateWhereAlayTrueCheck": true,
//                    "updateWhereNoneCheck": true,
//                    "useAllow": true,
//                    "variantCheck": true,
//                    "wrapAllow": true
//        }
//    },
//        "password": "jzbr",
//            "poolName": "master_3",
//            "url": "jdbc:mysql://127.0.0.1:3306/xxl_job?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&zeroDateTimeBehavior=convertToNull&useSSL=false",
//            "username": "root"
//    }
}
