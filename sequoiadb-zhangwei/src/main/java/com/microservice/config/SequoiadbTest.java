package com.microservice.config;

import com.sequoiadb.base.DBCursor;
import com.sequoiadb.base.Sequoiadb;
import com.sequoiadb.exception.BaseException;

/**
 * @author zw
 * @date 2020-10-01
 * <p>
 */
public class SequoiadbTest {

    public static void main(String[] args) {
        String connString = "192.168.40.134:11810";
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
}
