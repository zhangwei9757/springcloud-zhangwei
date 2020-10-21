package com.microservice;

import com.sequoiadb.base.*;
import com.sequoiadb.exception.BaseException;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import org.bson.util.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SequoiadbZhangweiApplicationTests {

    @Test
    public void connTest() {
        String connString = "192.168.40.128:11810";
        try {
            // 建立 SequoiaDB 数据库连接
            Sequoiadb sdb = new Sequoiadb(connString, "", "");
            // 获取所有 Collection 信息，并打印出来
            DBCursor cursor = sdb.listCollections();
            try {
                while (cursor.hasNext()) {
                    System.out.println(cursor.getNext());
                }
            } finally {
                cursor.close();
            }
        } catch (BaseException e) {
            System.out.println("Sequoiadb driver error, error description:" + e.getErrorType());
        }
    }

    @Test
    public void insertTest() {
        String connString = "192.168.40.128:11810";
        try {
            Sequoiadb sdb = new Sequoiadb(connString, "", "");
            CollectionSpace db = sdb.createCollectionSpace("space");
            DBCollection cl = db.createCollection("collection");
            // 创建一个插入的 bson 对象
            BSONObject obj = new BasicBSONObject();
            obj.put("name", "tom");
            obj.put("age", 24);
            cl.insert(obj);
        } catch (BaseException e) {
            System.out.println("Sequoiadb driver error, error description:" + e.getErrorType());
        }
    }

    @Test
    public void findTest() {
        // 定义一个游标对象
        String connString = "192.168.40.128:11810";
        Sequoiadb sdb = new Sequoiadb(connString, "", "");
        CollectionSpace db = sdb.getCollectionSpace("space");
        DBCollection cl = db.getCollection("collection");

        BSONObject queryCondition = (BSONObject) JSON.parse("{age:{$ne:20}}");
        // 查询所有记录，并把查询结果放在游标对象中
        DBCursor cursor = cl.query(queryCondition, null, null, null);
        // 从游标中显示所有记录
        try {
            while (cursor.hasNext()) {
                BSONObject record = cursor.getNext();
                String name = (String) record.get("name");
                System.out.println("name=" + name);
            }
        } finally {
            cursor.close();
        }
    }

    @Test
    public void createGroup(){
        String connString = "192.168.40.128:11810";
        try {
            Sequoiadb sdb = new Sequoiadb(connString, "", "");
            ReplicaGroup rg = sdb.createReplicaGroup("group1");
            rg.createNode("dbserver-1", 11820, "/opt/sequoiadb/database/data/11820");
            rg.start();
        } catch (BaseException e) {
            System.out.println("Sequoiadb driver error, error description" + e.getErrorType());
        }
    }
}
