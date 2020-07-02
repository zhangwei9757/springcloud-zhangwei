# Spring Cloud 微服务系列六： Elasticsearch  [7.8.0]



## 1.  Elasticsearch 简介

>  官网： https://www.elastic.co/cn/elasticsearch/ 
>
>  ElasticSearch是一个基于Lucene的搜索服务器。它提供了一个分布式多用户能力的全文搜索引擎，基于RESTful web接口。Elasticsearch是用Java开发的，并作为Apache许可条款下的开放源码发布，是当前流行的企业级搜索引擎。设计用于云计算中，能够达到实时搜索，稳定，可靠，快速，安装使用方便。相较于 Solr 的不同就是实时性更好，在大数据量面前效率更高，且天然分布式。因此我们可以基于不同的场景自由的选择使用，没有绝对的好坏
>
>  9200 RESTful接口使用的端口
>
>  9300 TCP端口
>
>  bin 存放 elasticSearch 运行命令  
>
>  config 存放配置文件  
>
>  lib  存放 elasticSearch 运行依赖 jar 包  
>
>  modules 存放 elasticSearch 模块  
>
>  plugins存放插件  

## 2. Elasticsearch 数据类型

```txt
1. String类型，又分两种：

    text：可分词，不可参与聚合
    keyword：不可分词，数据会作为完整字段进行匹配，可以参与聚合
    Numerical：数值类型，分两类

2. 基本数据类型：long、interger、short、byte、double、float、half_float
3. 浮点数的高精度类型：scaled_float
	需要指定一个精度因子，比如10或100。elasticsearch会把真实值乘以这个因子后存储，取出时再还原。
4. Date：日期类型

elasticsearch可以对日期格式化为字符串存储，但是建议我们存储为毫秒值，存储为long，节省空间
```



## 3.  Elasticsearch 安装

> https://mirrors.huaweicloud.com/elasticsearch/?C=N&O=D  下载并解压 zip或者tar包



## 4.  Elasticsearch 图形化界面安装

> 1. 下载地址: https://github.com/mobz/elasticsearch-head 
>
> 2. 安装nodejs
>
> 3. 安装grunt: npm install -g grunt-cli
>
> 4. 进入到 head 解压缩包目录内, 打开 CMD命令窗口
>
> 5. npm install
>
> 6. grunt server
>
> 7. 进入到 elasticsearch-7.8.0\config\elasticsearch.yml修改配置
>
>       http.cors.enabled: true
>
>       http.cors.allow-origin: "*"
>
> 8. 重启 elasticsearch
>
> 9. 访问如下提示: localhost:9100

```json
Microsoft Windows [版本 10.0.18362.657]
(c) 2019 Microsoft Corporation。保留所有权利。

D:\DevInstall\elasticsearch-head-master>grunt server
Running "connect:server" (connect) task
Waiting forever...
Started connect web server on http://localhost:9100
```



## 5. Elasticsearch Chrom插件安装

>1. 下载地址: https://github.com/mobz/elasticsearch-head 
>2. 进入解压目录 elasticsearch-head-master\crx
>3. 修改文件 es-head.crx 为 .zip 并解压
>4. 打开浏览器，扩展程序，添加解压的目录即可
>5. 点击新添加的扩展程序图标，点击连接



## 6.  Elasticsearch 核心概念

> 快速开始:  https://www.elastic.co/guide/en/elasticsearch/reference/current/getting-started.html 
>
> 索引 -> 类型 -> 文档 -> 属性
>
> 数据库 -> 表 -> 行 ->  列
>
> 7.x 以后废弃了类型，默认只有一个类型: _doc
>
> 查询类型: match_all:查所有， match:匹配查询，term:词条查询 ， range:范围查询





## 7.  Elasticsearch 使用RESTful [单集群]

### 1. 添加索引

```json
PUT http://127.0.0.1:9200/blog
{
  "mappings": {
    "properties": {
      "id": {
        "type": "long",
        "store": true,
        "index": true
      },
      "title": {
        "type": "text",
        "store": true,
        "index": true
      },
      "content": {
        "type": "text",
        "store": true,
        "index": true
      }
    }
  },
  "settings": {
        "index": {
            "number_of_shards": 5,
            "number_of_replicas": 1
        }
    }
}

返回结果:
{
    "acknowledged": true,
    "shards_acknowledged": true,
    "index": "blog"
}
```



### 2. 删除索引

```json
DELETE http://127.0.0.1:9200/blog

响应结果：
{
    "acknowledged": true
}
```



### 3. 添加文档

```json
POST http://127.0.0.1:9200/blog/_doc/1
{
    "id":1,
    "title":"添加的文档",
    "content": "添加的文档的内容"
}

响应结果：
{
    "_index": "blog",
    "_type": "_doc",
    "_id": "1",
    "_version": 1,
    "result": "created",
    "_shards": {
        "total": 2,
        "successful": 2,
        "failed": 0
    },
    "_seq_no": 0,
    "_primary_term": 1
}
```



### 5. 删除文档

```json
DELETE http://127.0.0.1:9200/blog/_doc/2

响应结果：
{
    "_index": "blog",
    "_type": "_doc",
    "_id": "1",
    "_version": 2,
    "result": "deleted",
    "_shards": {
        "total": 2,
        "successful": 2,
        "failed": 0
    },
    "_seq_no": 1,
    "_primary_term": 1
}
```



### 6. 修改文档 

> 注意： put 是直接覆盖，post 也是只覆盖，7.x 以后推荐使用post 加 _update 修改，可以部分修改。很实用!!!

```json
POST http://127.0.0.1:9200/blog/_doc/1/_update
{
    "doc":{
        "title": "修改新添加的文档1111111111111"
    }
}

响应结果：
{
    "_index": "blog",
    "_type": "_doc",
    "_id": "1",
    "_version": 6,
    "result": "updated",
    "_shards": {
        "total": 2,
        "successful": 2,
        "failed": 0
    },
    "_seq_no": 5,
    "_primary_term": 1
}
```



### 7. 根据id 查询

```json
GET http://127.0.0.1:9200/blog/_doc/1

响应结果：
{
    "_index": "blog",
    "_type": "_doc",
    "_id": "1",
    "_version": 6,
    "_seq_no": 5,
    "_primary_term": 1,
    "found": true,
    "_source": {
        "id": 1,
        "title": "修改新添加的文档1111111111111",
        "content": "新添加的文档1的内容"
    }
}
```



### 8. 根据关键词查询

```json
POST http://127.0.0.1:9200/blog/_doc/_search
{
    "query":{
        "term":{
            "title": "5"
        }
    }
}

响应结果：
{
    "took": 311,
    "timed_out": false,
    "_shards": {
        "total": 5,
        "successful": 5,
        "skipped": 0,
        "failed": 0
    },
    "hits": {
        "total": {
            "value": 1,
            "relation": "eq"
        },
        "max_score": 0.6931471,
        "hits": [
            {
                "_index": "blog",
                "_type": "_doc",
                "_id": "5",
                "_score": 0.6931471,
                "_source": {
                    "id": 5,
                    "title": "添加的文档5",
                    "content": "添加的文档的内容5"
                }
            }
        ]
    }
}
```



### 9. QueryString 查询

```json
POST http://127.0.0.1:9200/blog/_doc/_search
{
    "query":{
        "query_string":{
            "default_field": "title",
            "query": "文档"
        }
    }
}

响应结果：
{
    "took": 9,
    "timed_out": false,
    "_shards": {
        "total": 5,
        "successful": 5,
        "skipped": 0,
        "failed": 0
    },
    "hits": {
        "total": {
            "value": 5,
            "relation": "eq"
        },
        "max_score": 0.5753642,
        "hits": [
            {
                "_index": "blog",
                "_type": "_doc",
                "_id": "4",
                "_score": 0.5753642,
                "_source": {
                    "id": 4,
                    "title": "添加的文档4",
                    "content": "添加的文档的内容4"
                }
            },
            {
                "_index": "blog",
                "_type": "_doc",
                "_id": "1",
                "_score": 0.5753642,
                "_source": {
                    "id": 1,
                    "title": "修改新添加的文档1111111111111",
                    "content": "新添加的文档1的内容"
                }
            },
            {
                "_index": "blog",
                "_type": "_doc",
                "_id": "2",
                "_score": 0.26706278,
                "_source": {
                    "id": 2,
                    "title": "添加的文档2",
                    "content": "添加的文档的内容2"
                }
            },
            {
                "_index": "blog",
                "_type": "_doc",
                "_id": "5",
                "_score": 0.21072102,
                "_source": {
                    "id": 5,
                    "title": "添加的文档5",
                    "content": "添加的文档的内容5"
                }
            },
            {
                "_index": "blog",
                "_type": "_doc",
                "_id": "3",
                "_score": 0.21072102,
                "_source": {
                    "id": 3,
                    "title": "添加的文档3",
                    "content": "添加的文档的内容3"
                }
            }
        ]
    }
}
```

### 10. 高亮搜索

```json
GET zw/_search
{
  "query": {
    "match_phrase": {
      "problemDesc": "警方"
    }
  },
  "highlight": {
    "fields": {
      "problemDesc": {}
    },
    "pre_tags": ["<zw>"],
    "post_tags": ["</zw>"]
  }
}

返回结果:
{
  "took" : 3,
  "timed_out" : false,
  "_shards" : {
    "total" : 5,
    "successful" : 5,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 3,
      "relation" : "eq"
    },
    "max_score" : 2.9715075,
    "hits" : [
      {
        "_index" : "zw",
        "_type" : "_doc",
        "_id" : "59",
        "_score" : 2.9715075,
        "_source" : {
          "id" : 1593590939047,
          "troubleNo" : 1593590939047,
          "problemTitle" : "4a1a1180-0b02-4018-b0f9-4aee2cadf874普陀警方",
          "problemDesc" : "普陀警方",
          "troubleTime" : 1593590939047,
          "systemName" : "物罪，被",
          "moduleName" : "，被"
        },
        "highlight" : {
          "problemDesc" : [
            "普陀<zw>警方</zw>"
          ]
        }
      },
      {
        "_index" : "zw",
        "_type" : "_doc",
        "_id" : "34",
        "_score" : 2.5510652,
        "_source" : {
          "id" : 1593590939047,
          "troubleNo" : 1593590939047,
          "problemTitle" : "abbbbbb8-5cd6-44bc-b1da-cab3a9d41c41警方很快",
          "problemDesc" : "警方很快",
          "troubleTime" : 1593590939047,
          "systemName" : "档车辆。",
          "moduleName" : ""
        },
        "highlight" : {
          "problemDesc" : [
            "<zw>警方</zw>很快"
          ]
        }
      },
      {
        "_index" : "zw",
        "_type" : "_doc",
        "_id" : "18",
        "_score" : 2.1754484,
        "_source" : {
          "id" : 1593590939047,
          "troubleNo" : 1593590939047,
          "problemTitle" : "a9b679a6-ba42-4778-810b-cb688cd0c61c陀警方依",
          "problemDesc" : "陀警方依",
          "troubleTime" : 1593590939047,
          "systemName" : "罪，被普",
          "moduleName" : "被普"
        },
        "highlight" : {
          "problemDesc" : [
            "陀<zw>警方</zw>依"
          ]
        }
      }
    ]
  }
}
```







## 8.  Elasticsearch 安装 IK分词 插件

### 1.  下载

> 国内下载源:  https://gitee.com/mirrors/elasticsearch-analysis-ik 



### 2. 版本关系

| IK version | ES version       |
| ---------- | ---------------- |
| master     | 7.x -> master    |
| 6.x        | 6.x              |
| 5.x        | 5.x              |
| 1.10.6     | 2.4.6            |
| 1.9.5      | 2.3.5            |
| 1.8.1      | 2.2.1            |
| 1.7.0      | 2.1.1            |
| 1.5.0      | 2.0.0            |
| 1.2.6      | 1.0.0            |
| 1.2.5      | 0.90.x           |
| 1.1.3      | 0.20.x           |
| 1.0.0      | 0.16.2 -> 0.19.0 |



### 3. 解压, 安装

> 把解压缩目录复制, 到: elasticsearch-7.8.0\plugins\ik

### 4. 重启 elasticsearch

> elasticsearch-7.8.0\bin  运行 elasticsearch

```she
[2020-06-28T17:43:47,693][INFO ][o.e.p.PluginsService     ] [ZIhp38F] loaded plugin [analysis-ik]
```



### 5. 测试分词器效果 

>1. standard
>2. ik_smart
>3. ik_max_word

```json
POST http://127.0.0.1:9201/_analyze
{
    "analyzer":"ik_smart",
    "text":"张伟的书本"
}

响应结果：
{
    "tokens": [
        {
            "token": "张伟",
            "start_offset": 0,
            "end_offset": 2,
            "type": "CN_WORD",
            "position": 0
        },
        {
            "token": "的",
            "start_offset": 2,
            "end_offset": 3,
            "type": "CN_CHAR",
            "position": 1
        },
        {
            "token": "书本",
            "start_offset": 3,
            "end_offset": 5,
            "type": "CN_WORD",
            "position": 2
        }
    ]
}
```



## 9. Elasticsearch 集群

### 1. 复制es 解压包

> 1. 创建 elasticsearch-cluster 文件夹
> 2. 在内部复制三个elasticsearch服务(要删除data目录), **为了防止脑裂，最好集群数量是奇数**
> 3. 如果是不同的机器，直接scp 即可

### 1. 生成证书

> 1. 在elasticsearch的bin目录下执行如下命令，会在目录elasticsearch的文件目录生成elastic-stack-ca.p12文件
>
> elasticsearch-certutil ca
>
> 
>
> Please enter the desired output file [elastic-stack-ca.p12]: elastic-stack-ca.p12
> Enter password for elastic-stack-ca.p12 :
>
> 2. 生成 elastic-stack-ca.p12后，执行命令elasticsearch-certutil，需要注意的是elastic-stack-ca.p12文件必须是完整路径
>
> elasticsearch-certutil cert --ca /home/elasticsearch/elastic-stack-ca.p12
>
> 
>
> Enter password for CA (/home/elasticsearch/elastic-stack-ca.p12) : 
> Please enter the desired output file [elastic-certificates.p12]: elastic-certificates.p12
> Enter password for elastic-certificates.p12 : 
>
> 3. 生成的elastic-certificates.p12文件拷贝到每个节点的config目录下  

### 2.  修改配置elasticsearch.yml 

> elasticsearch.yml 添加如下配置:
> 
> xpack.security.enabled: true
> xpack.security.transport.ssl.enabled: true
> xpack.security.transport.ssl.verification_mode: certificate
>xpack.security.transport.ssl.keystore.path: elastic-certificates.p12
> xpack.security.transport.ssl.truststore.path: elastic-certificates.p12


```yml
# 设置集群名称，集群内所有节点的名称必须一致。
cluster.name: my-elasticsearch
# 设置节点名称，集群内节点名称必须唯一。
node.name: node1
# 表示该节点会不会作为主节点，true表示会；false表示不会
node.master: true
# 当前节点是否用于存储数据，是：true、否：false
node.data: true
# 最大集群节点数
node.max_local_storage_nodes: 3
# 索引数据存放的位置
#path.data: /opt/elasticsearch/data
# 日志文件存放的位置
#path.logs: /opt/elasticsearch/logs
# 需求锁住物理内存，是：true、否：false
#bootstrap.memory_lock: true
# 监听地址，用于访问该es
network.host: 127.0.0.1
# es对外提供的http端口，默认 9200
http.port: 9201
# TCP的默认监听端口，默认 9300
transport.tcp.port: 9301
# 设置这个参数来保证集群中的节点可以知道其它N个有master资格的节点。默认为1，对于大的集群来说，可以设置大一点的值（2-4）
discovery.zen.minimum_master_nodes: 2
# es7.x 之后新增的配置，写入候选主节点的设备地址，在开启服务后可以被选为主节点
discovery.seed_hosts: ["127.0.0.1:9301", "127.0.0.1:9302", "127.0.0.1:9303"] 
discovery.zen.fd.ping_timeout: 1m
discovery.zen.fd.ping_retries: 5
# es7.x 之后新增的配置，初始化一个新的集群时需要此配置来选举master
cluster.initial_master_nodes: ["node1", "node2", "node3"]

# action.destructive_requires_name: true
# action.auto_create_index: .security,.monitoring*,.watches,.triggered_watches,.watcher-history*
# xpack.security.enabled: false
# xpack.monitoring.enabled: true
# xpack.graph.enabled: false
# xpack.watcher.enabled: false
# xpack.ml.enabled: false

http.cors.enabled: true
http.cors.allow-origin: "*"
http.cors.allow-headers: Authorization,X-Requested-With,Content-Length,Content-Type 

xpack.security.enabled: true
xpack.security.transport.ssl.enabled: true
xpack.security.transport.ssl.verification_mode: certificate
xpack.security.transport.ssl.keystore.path: elastic-certificates.p12
xpack.security.transport.ssl.truststore.path: elastic-certificates.p12
```



### 3. 启动集群各个服务

> 1. 分别进: elasticsearch-cluster\节点\bin\ 目录, 运行elasticsearch 脚本启动
> 2. 页面访问: http://节点IP:9200 返回如下提示，表示密码配置已生效，后面重置密码即可

```json
{
    "error": {
        "root_cause": [
            {
                "type": "security_exception",
                "reason": "missing authentication credentials for REST request [/]",
                "header": {
                    "WWW-Authenticate": "Basic realm=\"security\" charset=\"UTF-8\""
                }
            }
        ],
        "type": "security_exception",
        "reason": "missing authentication credentials for REST request [/]",
        "header": {
            "WWW-Authenticate": "Basic realm=\"security\" charset=\"UTF-8\""
        }
    },
    "status": 401
}
```



### 4. 配置密码

> 在bin目录下输入下列命令【elastic 为超级管理员】
>
> elasticsearch-setup-passwords interactive

```shell
warning: ignoring JAVA_OPTS=-server -Xms2048m -Xmx2048m; pass JVM parameters via ES_JAVA_OPTS
future versions of Elasticsearch will require Java 11; your Java version from [D:\Program Files\Java\jdk1.8.0_25\jre] does not meet this requirement
Initiating the setup of passwords for reserved users elastic,apm_system,kibana,kibana_system,logstash_system,beats_system,remote_monitoring_user.
You will be prompted to enter passwords as the process progresses.
Please confirm that you would like to continue [y/N]y


Enter password for [elastic]:
Reenter password for [elastic]:
Enter password for [apm_system]:
Reenter password for [apm_system]:
Enter password for [kibana_system]:
Reenter password for [kibana_system]:
Enter password for [logstash_system]:
Reenter password for [logstash_system]:
Enter password for [beats_system]:
Reenter password for [beats_system]:
Enter password for [remote_monitoring_user]:
Reenter password for [remote_monitoring_user]:
Changed password for user [apm_system]
Changed password for user [kibana_system]
Changed password for user [kibana]
Changed password for user [logstash_system]
Changed password for user [beats_system]
Changed password for user [remote_monitoring_user]
Changed password for user [elastic]
```



### 5. 验证密码配置是否成功

> 页面访问: http://节点IP:9200

```json
{
  "name" : "node1",
  "cluster_name" : "my-elasticsearch",
  "cluster_uuid" : "-GbciZUpQKynoKDTViokJg",
  "version" : {
    "number" : "7.8.0",
    "build_flavor" : "default",
    "build_type" : "zip",
    "build_hash" : "757314695644ea9a1dc2fecd26d1a43856725e65",
    "build_date" : "2020-06-14T19:35:50.234439Z",
    "build_snapshot" : false,
    "lucene_version" : "8.5.1",
    "minimum_wire_compatibility_version" : "6.8.0",
    "minimum_index_compatibility_version" : "6.0.0-beta1"
  },
  "tagline" : "You Know, for Search"
}

```

```json
{
  "name" : "node2",
  "cluster_name" : "my-elasticsearch",
  "cluster_uuid" : "-GbciZUpQKynoKDTViokJg",
  "version" : {
    "number" : "7.8.0",
    "build_flavor" : "default",
    "build_type" : "zip",
    "build_hash" : "757314695644ea9a1dc2fecd26d1a43856725e65",
    "build_date" : "2020-06-14T19:35:50.234439Z",
    "build_snapshot" : false,
    "lucene_version" : "8.5.1",
    "minimum_wire_compatibility_version" : "6.8.0",
    "minimum_index_compatibility_version" : "6.0.0-beta1"
  },
  "tagline" : "You Know, for Search"
}
```

```json
{
  "name" : "node3",
  "cluster_name" : "my-elasticsearch",
  "cluster_uuid" : "-GbciZUpQKynoKDTViokJg",
  "version" : {
    "number" : "7.8.0",
    "build_flavor" : "default",
    "build_type" : "zip",
    "build_hash" : "757314695644ea9a1dc2fecd26d1a43856725e65",
    "build_date" : "2020-06-14T19:35:50.234439Z",
    "build_snapshot" : false,
    "lucene_version" : "8.5.1",
    "minimum_wire_compatibility_version" : "6.8.0",
    "minimum_index_compatibility_version" : "6.0.0-beta1"
  },
  "tagline" : "You Know, for Search"
}

```



### 6. 使用 head插件验证集群


>1. 页面访问: http://localhost:9100 或者点击 head 浏览器扩展插件图标
>
>2. 出现如下效果表示集群正常配置密码且启动成功
>
>3. ☆表示主节点，O表示非主节点
>
>4. ES集群的三种状态:
>
>   1) Green: 所有主分片和备份分片都准备就绪,分配成功, 即使有一台机器挂了(假设一台机器实例),数据都不会丢失,但是会变成yellow状态.
>
>   2) Yellow: 所有主分片准备就绪,但至少一个主分片(假设是A)对应的备份分片没有就绪,此时集群处于告警状态,意味着高可用和容灾能力下降.如果刚好A所在的机器挂了,并且你只设置了一个备份(已处于未继续状态), 那么A的数据就会丢失(查询不完整),此时集群处于Red状态.
>
>   3) Red:至少有一个主分片没有就绪(直接原因是找不到对应的备份分片成为新的主分片),此时查询的结果会出现数据丢失(不完整).

* **如下图第一行最中间显示 green 说明我们的集群运行正常**

![es780_search_cluste]( https://gitee.com/zhangwei9757/images/raw/master/es780_search_cluster.png )



### 7. 测试集群添加索引

> 1. 使用head 插件添加一个索引  "index-hello", 我们可以发现，分了五片且数量为1，一起10份，存放在不同服务器

![es780_create_index]( https://gitee.com/zhangwei9757/images/raw/master/es780_create_index.png )



>2. 查看状态

```json
GET http://localhost:9201/_cat/indices?v

返回结果:
green open .kibana-event-log-7.8.0-000001 Ihc8JiZcQIWXhkPdAE-0Iw 1 1  1 0  10.6kb  5.3kb
green open .apm-custom-link               rbuvS2ZgQv266C66-mnrsw 1 1  0 0    416b   208b
green open .kibana_task_manager_1         KJTfQ3DFTNi7mix8k24Tig 1 1  5 8 137.7kb 49.4kb
green open .apm-agent-configuration       mGsZAAAaSqSifN9bXcO7VQ 1 1  0 0    416b   208b
green open blog                           oA-OhNaMR0K2IDHU1s8HnQ 5 1  0 0     2kb    1kb
green open .kibana_1                      RWAqd9QqRW-AFVL-bxVPpA 1 1 15 2  73.8kb   44kb

```

> 3. 查看健康状态

```json
GET http://localhost:9201/_cat/health?v

返回结果:
epoch      timestamp cluster          status node.total node.data shards pri relo init unassign pending_tasks max_task_wait_time active_shards_percent
1593404730 04:25:30  my-elasticsearch green           3         3     22  11    0    0        0             0                  -                100.0%

```

> 4. 查看所有节点

```json
GET http://localhost:9201/_cat/nodes?v

返回结果:
ip        heap.percent ram.percent cpu load_1m load_5m load_15m node.role master name
127.0.0.1           24          83  12                          dimrt     -      node1
127.0.0.1           22          83  12                          dimrt     *      node3
127.0.0.1           42          83  12                          dimrt     -      node2
```



## 10.  Elasticsearch 使用 RESTful [多集群]

### 10.1  CRUD

> 与单机集群版本一样, 略过......

### 10.2  如何优雅的实现分页

* 方式一 :from ,size 面对少量数据很好用

* 方式二: scroll + scan 方式深度分页，官方也提示使用这种方式

> 1. 首先, es 目前支持最大的 skip 值是 max_result_window ，默认为 10000 。也就是当 from + size > max_result_window 时，es 将返回错误
> 2. from, sizw 看似好用，其实每次查询是查询出各自分片服务器对应分布参数的队列结果，然后一起汇总再分页，其实就是把查询出来的from值以前的数据丢掉，返回from至size的结果集，数据小还可以使用这种方式 ，当面对大数据，成百上千万的数据量时，会直接使服务器瘫痪， 有兴趣的盆友可以用本地环境测试一下

### 10.3   from ,size分页  

#### 1. 首先先确认下我们的数据量 

> 我们可以看到下方返回的结果中 hits>total>value:119992, 总共有十一万条

```json
{
  "_scroll_id" : "FGluY2x1ZGVfY29udGV4dF91dWlkDnF1ZXJ5VGhlbkZldGNoBRRHS1ZhQlhNQjJsZkNQQWRTbVhPOAAAAAAAAACDFlRKM0dYQ2FXVGZpT3VZVlhBeFlNSkEUR2FWYUJYTUIybGZDUEFkU21YTzgAAAAAAAAAhBZUSjNHWENhV1RmaU91WVZYQXhZTUpBFG9zRmFCWE1CbEVxcE0wV0htWGk4AAAAAAAABcIWTmZaX0ZOVG9TME9XZHlpWUN0YXRPURRPbTVhQlhNQjhLaTB1ajlSbVd5OAAAAAAAAAkEFlZMRVlKU1Y5UmlTUjZpdHVXNGRnVXcUb2NGYUJYTUJsRXFwTTBXSG1YaTgAAAAAAAAFwRZOZlpfRk5Ub1MwT1dkeWlZQ3RhdE9R",
  "took" : 1,
  "timed_out" : false,
  "_shards" : {
    "total" : 5,
    "successful" : 5,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 119992,
      "relation" : "eq"
    },
    "max_score" : 1.0,
    "hits" : [
      {
        "_index" : "troublereport-sever",
        "_type" : "_doc",
        "_id" : "3",
        "_score" : 1.0,
        "_source" : {
          "id" : 1593442298448,
          "troubleNo" : 1593442298448,
          "problemTitle" : "0d90e447-fe66-4a45-9d00-567e978c2a1d",
          "problemDesc" : "记和党务骨干培训，提升工作规范化水平。要加强督促落实，确保《中国共产党基层组织选举工作条例》各项规定要求落到实处。会议还研究了其他事项。",
          "troubleTime" : 1593442298448,
          "systemName" : "党组织书",
          "moduleName" : "织书"
        }
      }
    ]
  }
}
```

#### 2. 演示普通查询

```json
# 普通查询只有一万条记录 hits>total>value>10000
GET troublereport-sever/_search
{
  "_source": "systemName", 
  "query": {
    "query_string": {
      "default_field": "problemDesc",
      "query": "党"
    }
  }
}
返回结果:
{
  "took" : 10,
  "timed_out" : false,
  "_shards" : {
    "total" : 5,
    "successful" : 5,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 10000,
      "relation" : "gte"
    },
    "max_score" : 1.8624322,
    "hits" : [
      {
        "_index" : "troublereport-sever",
        "_type" : "_doc",
        "_id" : "1409",
        "_score" : 1.8624322,
        "_source" : {
          "systemName" : "中国共产"
        }
      },
      {
        "_index" : "troublereport-sever",
        "_type" : "_doc",
        "_id" : "5977",
        "_score" : 1.8624322,
        "_source" : {
          "systemName" : "中国共产"
        }
      },
      {
        "_index" : "troublereport-sever",
        "_type" : "_doc",
        "_id" : "6397",
        "_score" : 1.8624322,
        "_source" : {
          "systemName" : "中国共产"
        }
      },
      {
        "_index" : "troublereport-sever",
        "_type" : "_doc",
        "_id" : "6816",
        "_score" : 1.8624322,
        "_source" : {
          "systemName" : "中国共产"
        }
      },
      {
        "_index" : "troublereport-sever",
        "_type" : "_doc",
        "_id" : "15320",
        "_score" : 1.8624322,
        "_source" : {
          "systemName" : "《中国共"
        }
      },
      {
        "_index" : "troublereport-sever",
        "_type" : "_doc",
        "_id" : "19868",
        "_score" : 1.8624322,
        "_source" : {
          "systemName" : "《中国共"
        }
      },
      {
        "_index" : "troublereport-sever",
        "_type" : "_doc",
        "_id" : "22887",
        "_score" : 1.8624322,
        "_source" : {
          "systemName" : "中国共产"
        }
      },
      {
        "_index" : "troublereport-sever",
        "_type" : "_doc",
        "_id" : "28427",
        "_score" : 1.8624322,
        "_source" : {
          "systemName" : "中国共产"
        }
      },
      {
        "_index" : "troublereport-sever",
        "_type" : "_doc",
        "_id" : "28669",
        "_score" : 1.8624322,
        "_source" : {
          "systemName" : "《中国共"
        }
      },
      {
        "_index" : "troublereport-sever",
        "_type" : "_doc",
        "_id" : "29022",
        "_score" : 1.8624322,
        "_source" : {
          "systemName" : "《中国共"
        }
      }
    ]
  }
}
```

#### 3. 演示 from + size 分页

> 1. 由于数据量太大，返回结果就不展示了，按步骤是可以显示的, 因为没有超过10000条

```json
GET troublereport-sever/_search
{
  "_source": "systemName", 
  "query": {
    "query_string": {
      "default_field": "problemDesc",
      "query": "党"
    }
  },
  "from": 9000,
  "size": 1000
}
```

> 2. 请求内容一样，但是只是size比原来多了一条, 会报错, 因为已经超过10000条

```json
GET troublereport-sever/_search
{
  "_source": "systemName", 
  "query": {
    "query_string": {
      "default_field": "problemDesc",
      "query": "党"
    }
  },
  "from": 9000,
  "size": 1001
}
返回结果
{
  "error" : {
    "root_cause" : [
      {
        "type" : "illegal_argument_exception",
        "reason" : "Result window is too large, from + size must be less than or equal to: [10000] but was [10001]. See the scroll api for a more efficient way to request large data sets. This limit can be set by changing the [index.max_result_window] index level setting."
      }
    ],
    "type" : "search_phase_execution_exception",
    "reason" : "all shards failed",
    "phase" : "query",
    "grouped" : true,
    "failed_shards" : [
      {
        "shard" : 0,
        "index" : "troublereport-sever",
        "node" : "TJ3GXCaWTfiOuYVXAxYMJA",
        "reason" : {
          "type" : "illegal_argument_exception",
          "reason" : "Result window is too large, from + size must be less than or equal to: [10000] but was [10001]. See the scroll api for a more efficient way to request large data sets. This limit can be set by changing the [index.max_result_window] index level setting."
        }
      }
    ],
    "caused_by" : {
      "type" : "illegal_argument_exception",
      "reason" : "Result window is too large, from + size must be less than or equal to: [10000] but was [10001]. See the scroll api for a more efficient way to request large data sets. This limit can be set by changing the [index.max_result_window] index level setting.",
      "caused_by" : {
        "type" : "illegal_argument_exception",
        "reason" : "Result window is too large, from + size must be less than or equal to: [10000] but was [10001]. See the scroll api for a more efficient way to request large data sets. This limit can be set by changing the [index.max_result_window] index level setting."
      }
    }
  },
  "status" : 400
}
```



### 10. 4   scroll分页

#### 1. 演示 scroll  分页

> 我们查询所有的数据，此时设置了游标刷新时间为1分钟，此时已添加快照，修改数据是不受影响的, 由于数据量大量就不展示数据结果集了，但是大家按步骤操作是会显示_id一直在变化

```json
# 1. 先指定scroll 查询
GET troublereport-sever/_search?scroll=1m
{
  "query": {
    "match_all": {}
  },
  "size": 1000
}

# 2. 开始分页，翻页查询, 一分钟内scroll_id不会变化，如果变化换返回的结果里新值即可, 一直到查询无结果为止
GET _search/scroll
{
    "scroll" : "1m", 
    "scroll_id" : "FGluY2x1ZGVfY29udGV4dF91dWlkDnF1ZXJ5VGhlbkZldGNoBRQzc0ZmQlhNQmxFcXBNMFdISlhpTQAAAAAAAAX-Fk5mWl9GTlRvUzBPV2R5aVlDdGF0T1EUM01GZkJYTUJsRXFwTTBXSEpYaU0AAAAAAAAF_BZOZlpfRk5Ub1MwT1dkeWlZQ3RhdE9RFDNjRmZCWE1CbEVxcE0wV0hKWGlNAAAAAAAABf0WTmZaX0ZOVG9TME9XZHlpWUN0YXRPURRoMjVmQlhNQjhLaTB1ajlSSld5TQAAAAAAAAlRFlZMRVlKU1Y5UmlTUjZpdHVXNGRnVXcUaUc1ZkJYTUI4S2kwdWo5UkpXeU0AAAAAAAAJUhZWTEVZSlNWOVJpU1I2aXR1VzRkZ1V3" 
}
```

#### 2. 查看集群 scroll 状态

```json
GET _nodes/stats/indices/search?pretty

返回结果:
{
  "_nodes" : {
    "total" : 3,
    "successful" : 3,
    "failed" : 0
  },
  "cluster_name" : "my-elasticsearch",
  "nodes" : {
    "NfZ_FNToS0OWdyiYCtatOQ" : {
      "timestamp" : 1593523344742,
      "name" : "node2",
      "transport_address" : "127.0.0.1:9302",
      "host" : "127.0.0.1",
      "ip" : "127.0.0.1:9302",
      "roles" : [
        "data",
        "ingest",
        "master",
        "ml",
        "remote_cluster_client",
        "transform"
      ],
      "attributes" : {
        "ml.machine_memory" : "17066790912",
        "ml.max_open_jobs" : "20",
        "xpack.installed" : "true",
        "transform.node" : "true"
      },
      "indices" : {
        "search" : {
          "open_contexts" : 0,
          "query_total" : 2474,
          "query_time_in_millis" : 1036,
          "query_current" : 0,
          "fetch_total" : 1887,
          "fetch_time_in_millis" : 1771,
          "fetch_current" : 0,
          "scroll_total" : 1625,
          "scroll_time_in_millis" : 2978725,
          "scroll_current" : 0,
          "suggest_total" : 0,
          "suggest_time_in_millis" : 0,
          "suggest_current" : 0
        }
      }
    },
    "TJ3GXCaWTfiOuYVXAxYMJA" : {
      "timestamp" : 1593523344742,
      "name" : "node1",
      "transport_address" : "127.0.0.1:9301",
      "host" : "127.0.0.1",
      "ip" : "127.0.0.1:9301",
      "roles" : [
        "data",
        "ingest",
        "master",
        "ml",
        "remote_cluster_client",
        "transform"
      ],
      "attributes" : {
        "ml.machine_memory" : "17066790912",
        "xpack.installed" : "true",
        "transform.node" : "true",
        "ml.max_open_jobs" : "20"
      },
      "indices" : {
        "search" : {
          "open_contexts" : 1,
          "query_total" : 189,
          "query_time_in_millis" : 764,
          "query_current" : 0,
          "fetch_total" : 117,
          "fetch_time_in_millis" : 305,
          "fetch_current" : 0,
          "scroll_total" : 13,
          "scroll_time_in_millis" : 1505604,
          "scroll_current" : 1,
          "suggest_total" : 0,
          "suggest_time_in_millis" : 0,
          "suggest_current" : 0
        }
      }
    },
    "VLEYJSV9RiSR6ituW4dgUw" : {
      "timestamp" : 1593523344742,
      "name" : "node3",
      "transport_address" : "127.0.0.1:9303",
      "host" : "127.0.0.1",
      "ip" : "127.0.0.1:9303",
      "roles" : [
        "data",
        "ingest",
        "master",
        "ml",
        "remote_cluster_client",
        "transform"
      ],
      "attributes" : {
        "ml.machine_memory" : "17066790912",
        "ml.max_open_jobs" : "20",
        "xpack.installed" : "true",
        "transform.node" : "true"
      },
      "indices" : {
        "search" : {
          "open_contexts" : 2,
          "query_total" : 3125,
          "query_time_in_millis" : 931,
          "query_current" : 0,
          "fetch_total" : 2532,
          "fetch_time_in_millis" : 2307,
          "fetch_current" : 0,
          "scroll_total" : 2432,
          "scroll_time_in_millis" : 1954530,
          "scroll_current" : 2,
          "suggest_total" : 0,
          "suggest_time_in_millis" : 0,
          "suggest_current" : 0
        }
      }
    }
  }
}
```

#### 3. 删除 scroll

> 删除后再查询肯定会报错，因为scroll 已被删除

```json
DELETE _search/scroll
{
  "scroll_id" : "FGluY2x1ZGVfY29udGV4dF91dWlkDnF1ZXJ5VGhlbkZldGNoBRRKcVZsQlhNQjJsZkNQQWRTaVhNYgAAAAAAAACRFlRKM0dYQ2FXVGZpT3VZVlhBeFlNSkEUSjZWbEJYTUIybGZDUEFkU2lYTWMAAAAAAAAAkhZUSjNHWENhV1RmaU91WVZYQXhZTUpBFGdjRmxCWE1CbEVxcE0wV0hpWGtjAAAAAAAABqEWTmZaX0ZOVG9TME9XZHlpWUN0YXRPURRaRzVsQlhNQjhLaTB1ajlSaVcwYwAAAAAAAAouFlZMRVlKU1Y5UmlTUjZpdHVXNGRnVXcUWlc1bEJYTUI4S2kwdWo5UmlXMGQAAAAAAAAKLxZWTEVZSlNWOVJpU1I2aXR1VzRkZ1V3"
}

返回结果:
{
  "succeeded" : true,
  "num_freed" : 5
}

GET _search/scroll
{
    "scroll" : "1m", 
    "scroll_id" : "FGluY2x1ZGVfY29udGV4dF91dWlkDnF1ZXJ5VGhlbkZldGNoBRRKcVZsQlhNQjJsZkNQQWRTaVhNYgAAAAAAAACRFlRKM0dYQ2FXVGZpT3VZVlhBeFlNSkEUSjZWbEJYTUIybGZDUEFkU2lYTWMAAAAAAAAAkhZUSjNHWENhV1RmaU91WVZYQXhZTUpBFGdjRmxCWE1CbEVxcE0wV0hpWGtjAAAAAAAABqEWTmZaX0ZOVG9TME9XZHlpWUN0YXRPURRaRzVsQlhNQjhLaTB1ajlSaVcwYwAAAAAAAAouFlZMRVlKU1Y5UmlTUjZpdHVXNGRnVXcUWlc1bEJYTUI4S2kwdWo5UmlXMGQAAAAAAAAKLxZWTEVZSlNWOVJpU1I2aXR1VzRkZ1V3" 
}
删除后返回结果
{
  "error" : {
    "root_cause" : [
      {
        "type" : "search_context_missing_exception",
        "reason" : "No search context found for id [145]"
      },
      {
        "type" : "search_context_missing_exception",
        "reason" : "No search context found for id [146]"
      },
      {
        "type" : "search_context_missing_exception",
        "reason" : "No search context found for id [2607]"
      },
      {
        "type" : "search_context_missing_exception",
        "reason" : "No search context found for id [2606]"
      },
      {
        "type" : "search_context_missing_exception",
        "reason" : "No search context found for id [1697]"
      }
    ],
    "type" : "search_phase_execution_exception",
    "reason" : "all shards failed",
    "phase" : "query",
    "grouped" : true,
    "failed_shards" : [
      {
        "shard" : -1,
        "index" : null,
        "reason" : {
          "type" : "search_context_missing_exception",
          "reason" : "No search context found for id [145]"
        }
      },
      {
        "shard" : -1,
        "index" : null,
        "reason" : {
          "type" : "search_context_missing_exception",
          "reason" : "No search context found for id [146]"
        }
      },
      {
        "shard" : -1,
        "index" : null,
        "reason" : {
          "type" : "search_context_missing_exception",
          "reason" : "No search context found for id [2607]"
        }
      },
      {
        "shard" : -1,
        "index" : null,
        "reason" : {
          "type" : "search_context_missing_exception",
          "reason" : "No search context found for id [2606]"
        }
      },
      {
        "shard" : -1,
        "index" : null,
        "reason" : {
          "type" : "search_context_missing_exception",
          "reason" : "No search context found for id [1697]"
        }
      }
    ],
    "caused_by" : {
      "type" : "search_context_missing_exception",
      "reason" : "No search context found for id [1697]"
    }
  },
  "status" : 404
}
```

#### 4. 删除所有scroll

> **个人建议这种少用，因为会删除所有的 scroll **

```json
DELETE _search/scroll/_all

返回结果
{
  "succeeded" : true,
  "num_freed" : 3
}
```

#### 5. 演示 search after

> 1. search_after 分页的方式是根据上一页的最后一条数据来确定下一页的位置，同时在分页请求的过程中，如果有索引数据的增删改查，这些变更也会实时的反映到游标上。
> 2. 为了找到每一页最后一条数据，每个文档必须有一个全局唯一值，官方推荐使用 _uid 作为全局唯一值，其实使用业务层的 _id 也可以

##### 1.先用sort把一个业务逻辑排序

```json
POST troublereport-sever/_search
{
  "_source": "troubleTime", 
  "query": {
    "match": {
      "problemDesc":"《中国共产党基层组织选举工作条例》"
    }
  },
  "size": 15,
  "sort": [
    {
      "_id": "desc"
    }
  ]
}

返回结果
{
  "took" : 34,
  "timed_out" : false,
  "_shards" : {
    "total" : 5,
    "successful" : 5,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 10000,
      "relation" : "gte"
    },
    "max_score" : null,
    "hits" : [
      {
        "_index" : "troublereport-sever",
        "_type" : "_doc",
        "_id" : "9999",
        "_score" : null,
        "_source" : {
          "troubleTime" : 1593443744999
        },
        "sort" : [
          "9999"
        ]
      },
      {
        "_index" : "troublereport-sever",
        "_type" : "_doc",
        "_id" : "9998",
        "_score" : null,
        "_source" : {
          "troubleTime" : 1593443744999
        },
        "sort" : [
          "9998"
        ]
      },
      {
        "_index" : "troublereport-sever",
        "_type" : "_doc",
        "_id" : "9997",
        "_score" : null,
        "_source" : {
          "troubleTime" : 1593443744999
        },
        "sort" : [
          "9997"
        ]
      },
      {
        "_index" : "troublereport-sever",
        "_type" : "_doc",
        "_id" : "9996",
        "_score" : null,
        "_source" : {
          "troubleTime" : 1593443744999
        },
        "sort" : [
          "9996"
        ]
      },
      {
        "_index" : "troublereport-sever",
        "_type" : "_doc",
        "_id" : "9995",
        "_score" : null,
        "_source" : {
          "troubleTime" : 1593443744999
        },
        "sort" : [
          "9995"
        ]
      },
      {
        "_index" : "troublereport-sever",
        "_type" : "_doc",
        "_id" : "9994",
        "_score" : null,
        "_source" : {
          "troubleTime" : 1593443744999
        },
        "sort" : [
          "9994"
        ]
      },
      {
        "_index" : "troublereport-sever",
        "_type" : "_doc",
        "_id" : "9993",
        "_score" : null,
        "_source" : {
          "troubleTime" : 1593443744999
        },
        "sort" : [
          "9993"
        ]
      },
      {
        "_index" : "troublereport-sever",
        "_type" : "_doc",
        "_id" : "9992",
        "_score" : null,
        "_source" : {
          "troubleTime" : 1593443744999
        },
        "sort" : [
          "9992"
        ]
      },
      {
        "_index" : "troublereport-sever",
        "_type" : "_doc",
        "_id" : "9991",
        "_score" : null,
        "_source" : {
          "troubleTime" : 1593443744999
        },
        "sort" : [
          "9991"
        ]
      },
      {
        "_index" : "troublereport-sever",
        "_type" : "_doc",
        "_id" : "9990",
        "_score" : null,
        "_source" : {
          "troubleTime" : 1593443744999
        },
        "sort" : [
          "9990"
        ]
      },
      {
        "_index" : "troublereport-sever",
        "_type" : "_doc",
        "_id" : "999",
        "_score" : null,
        "_source" : {
          "troubleTime" : 1593442546213
        },
        "sort" : [
          "999"
        ]
      },
      {
        "_index" : "troublereport-sever",
        "_type" : "_doc",
        "_id" : "9989",
        "_score" : null,
        "_source" : {
          "troubleTime" : 1593443744999
        },
        "sort" : [
          "9989"
        ]
      },
      {
        "_index" : "troublereport-sever",
        "_type" : "_doc",
        "_id" : "9988",
        "_score" : null,
        "_source" : {
          "troubleTime" : 1593443744999
        },
        "sort" : [
          "9988"
        ]
      },
      {
        "_index" : "troublereport-sever",
        "_type" : "_doc",
        "_id" : "9987",
        "_score" : null,
        "_source" : {
          "troubleTime" : 1593443744999
        },
        "sort" : [
          "9987"
        ]
      },
      {
        "_index" : "troublereport-sever",
        "_type" : "_doc",
        "_id" : "9986",
        "_score" : null,
        "_source" : {
          "troubleTime" : 1593443744999
        },
        "sort" : [
          "9986"
        ]
      }
    ]
  }
}
```

##### 2.下一次查询带上一次查询出最后一条记录的_id值，放在 search_after里面

```json
GET troublereport-sever/_search
{
  "_source": "troubleTime", 
  "query": {
    "match": {
      "problemDesc":"《中国共产党基层组织选举工作条例》"
    }
  },
  "size": 15,
  "search_after": [9986],
  "sort": [
    {
      "_id": "desc"
    }
  ]
}

返回结果
{
  "took" : 31,
  "timed_out" : false,
  "_shards" : {
    "total" : 5,
    "successful" : 5,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 10000,
      "relation" : "gte"
    },
    "max_score" : null,
    "hits" : [
      {
        "_index" : "troublereport-sever",
        "_type" : "_doc",
        "_id" : "9985",
        "_score" : null,
        "_source" : {
          "troubleTime" : 1593443744999
        },
        "sort" : [
          "9985"
        ]
      },
      {
        "_index" : "troublereport-sever",
        "_type" : "_doc",
        "_id" : "9984",
        "_score" : null,
        "_source" : {
          "troubleTime" : 1593443744999
        },
        "sort" : [
          "9984"
        ]
      },
      {
        "_index" : "troublereport-sever",
        "_type" : "_doc",
        "_id" : "9983",
        "_score" : null,
        "_source" : {
          "troubleTime" : 1593443744999
        },
        "sort" : [
          "9983"
        ]
      },
      {
        "_index" : "troublereport-sever",
        "_type" : "_doc",
        "_id" : "9982",
        "_score" : null,
        "_source" : {
          "troubleTime" : 1593443744999
        },
        "sort" : [
          "9982"
        ]
      },
      {
        "_index" : "troublereport-sever",
        "_type" : "_doc",
        "_id" : "9981",
        "_score" : null,
        "_source" : {
          "troubleTime" : 1593443744999
        },
        "sort" : [
          "9981"
        ]
      },
      {
        "_index" : "troublereport-sever",
        "_type" : "_doc",
        "_id" : "9980",
        "_score" : null,
        "_source" : {
          "troubleTime" : 1593443744999
        },
        "sort" : [
          "9980"
        ]
      },
      {
        "_index" : "troublereport-sever",
        "_type" : "_doc",
        "_id" : "9979",
        "_score" : null,
        "_source" : {
          "troubleTime" : 1593443744999
        },
        "sort" : [
          "9979"
        ]
      },
      {
        "_index" : "troublereport-sever",
        "_type" : "_doc",
        "_id" : "9978",
        "_score" : null,
        "_source" : {
          "troubleTime" : 1593443744999
        },
        "sort" : [
          "9978"
        ]
      },
      {
        "_index" : "troublereport-sever",
        "_type" : "_doc",
        "_id" : "9977",
        "_score" : null,
        "_source" : {
          "troubleTime" : 1593443744999
        },
        "sort" : [
          "9977"
        ]
      },
      {
        "_index" : "troublereport-sever",
        "_type" : "_doc",
        "_id" : "9976",
        "_score" : null,
        "_source" : {
          "troubleTime" : 1593443744999
        },
        "sort" : [
          "9976"
        ]
      },
      {
        "_index" : "troublereport-sever",
        "_type" : "_doc",
        "_id" : "9975",
        "_score" : null,
        "_source" : {
          "troubleTime" : 1593443744999
        },
        "sort" : [
          "9975"
        ]
      },
      {
        "_index" : "troublereport-sever",
        "_type" : "_doc",
        "_id" : "9974",
        "_score" : null,
        "_source" : {
          "troubleTime" : 1593443744999
        },
        "sort" : [
          "9974"
        ]
      },
      {
        "_index" : "troublereport-sever",
        "_type" : "_doc",
        "_id" : "9973",
        "_score" : null,
        "_source" : {
          "troubleTime" : 1593443744999
        },
        "sort" : [
          "9973"
        ]
      },
      {
        "_index" : "troublereport-sever",
        "_type" : "_doc",
        "_id" : "9972",
        "_score" : null,
        "_source" : {
          "troubleTime" : 1593443744999
        },
        "sort" : [
          "9972"
        ]
      },
      {
        "_index" : "troublereport-sever",
        "_type" : "_doc",
        "_id" : "9971",
        "_score" : null,
        "_source" : {
          "troubleTime" : 1593443744999
        },
        "sort" : [
          "9971"
        ]
      }
    ]
  }
}
```



### 10.5 分页功能总结

>1. es的并发scroll不适合深度翻页，只适合拉取所有数据。若使用scroll的话，尽管能读取许多数据，但是查询出来的结果都是无序的。
>
>2. es search_after也不适合做深度分页，分页多了，内存不够，将查询失败。
>
>3. es from+size的话，from + size 默认不能超过1万条数据。
>
>   使用from + size方式是将请求达到分片节点上，如果有n个副分片，则查询数据是 n * (from+size) 如果from很大的话，会造成oom或者网络资源的浪费。
>
>总结： 
>
>scroll 适合全部拉取，但是不包含最新的实时性差，且要回收scroll。
>
>from+size只适合少量数据时使用，比如用户查的数据肯定差异性大，重复数据少。
>
>search_after 适合对数据实时准确性要求高的场景



## 11.  SpringBoot 使用 Elasticsearch

### 1. 创建项目

> github源码： https://github.com/zhangwei9757/springcloud-zhangwei.git
>
> 工程名: springcloud-elasticsearch7_8_0-zhangwei



### 2. 核心概念

> QueryBuilder是ES封装的查询接口。
>
> 我们会发现在ElasticSearch启动时，会占用两个端口9200和9300。
> 他们具体的作用如下：
>
> 9200 是ES节点与外部通讯使用的端口。它是http协议的RESTful接口（各种CRUD操作都是走的该端口,如查询：http://localhost:9200/index/_search）。
>
> 9300是ES节点之间通讯使用的端口。它是tcp通讯端口，集群间和TCPclient都走的它。（java程序中使用ES时，在配置文件中要配置该端口）



### 3. 版本关系

| spring data elasticsearch | elasticsearch |
| ------------------------- | ------------- |
| 3.2.x                     | 6.5.0         |
| 3.1.x                     | 6.2.2         |
| 3.0.x                     | 5.5.0         |
| 2.1.x                     | 2.4.0         |
| 2.0.x                     | 2.2.0         |
| 1.3.x                     | 1.5.2         |



## 测试总结 ：Elasticsearch  VS  Mysql 

>  Elasticsearch与Mysql 经对比测试，十万的数据量场景下，和Elasticsearch差不多有十倍的差距，如果面对更大的数据量，差距更大，而Elasticsearch 性能几乎没有什么波动

