package com.zhangwei.utils;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * @Created by liuchujian On 2018/7/3
 * java实现腾讯云存储服务（COSClient）
 */
public class CosClientTest {

    private static final String ACCESSKEY = "XXXX";
    private static final String SECRETKEY = "XXXX";
    private static final String BUCKETNAME = "MyFile-120000000";
    private static final String APPID = "1200000000";
    private static final String REGIONID = "ap-shanghai";//区域
    private static final String KEY = "MyFile/xxx.jpg";

    /**
     * 初始化CosClient相关配置， appid、accessKey、secretKey、region
     *
     * @return
     */
    public static COSClient getCosClient() {
        // 1 init userInfo (secretId, secretKey)
        COSCredentials cred = new BasicCOSCredentials(APPID, ACCESSKEY, SECRETKEY);
        //COSCredentials cred = new BasicCOSCredentials(ACCESSKEY, SECRETKEY); 不传APPID也可以，APPID和ACCESSKE已经关联过
        // 2 set bucket region
        ClientConfig clientConfig = new ClientConfig(new Region(REGIONID));
        // 3 init cosclient
        COSClient cosclient = new COSClient(cred, clientConfig);
        // bucket name protos   must be {name}-{appid}
        return cosclient;
    }

    /**
     * 上传文件
     *
     * @return
     */
    public static String uploadFile() {
        File localFile = new File("src/test/resources/xxx.jpg");//绝对路径和相对路径都OK
        PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKETNAME, KEY, localFile);

        // 设置存储类型, 默认是标准(Standard), 低频(standard_ia)
        putObjectRequest.setStorageClass(StorageClass.Standard_IA);

        COSClient cc = getCosClient();
        try {
            PutObjectResult putObjectResult = cc.putObject(putObjectRequest);
            // putobjectResult会返回文件的etag
            String etag = putObjectResult.getETag();
            System.out.println(etag);
        } catch (CosServiceException e) {
            e.printStackTrace();
        } catch (CosClientException e) {
            e.printStackTrace();
        }
        // 关闭客户端
        cc.shutdown();
        return null;
    }

    /**
     * 下载文件
     *
     * @param bucketName
     * @param key
     * @return
     */
    public String downLoadFile(String bucketName, String key) {
        File downFile = new File("src/test/resources/xxx.jpg");
        COSClient cc = getCosClient();
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);

        ObjectMetadata downObjectMeta = cc.getObject(getObjectRequest, downFile);
        cc.shutdown();
        String etag = downObjectMeta.getETag();
        return etag;
    }

    /**
     * 删除文件
     *
     * @param bucketName
     * @param key
     */
    public void deleteFile(String bucketName, String key) {
        COSClient cc = getCosClient();
        try {
            cc.deleteObject(bucketName, key);
        } catch (CosClientException e) {
            e.printStackTrace();
        } finally {
            cc.shutdown();
        }

    }

    /**
     * 创建桶
     *
     * @param bucketName
     * @return
     * @throws CosClientException
     * @throws CosServiceException
     */
    public Bucket createBucket(String bucketName) throws CosClientException, CosServiceException {
        COSClient cc = getCosClient();
        Bucket bucket = null;
        try {
            bucket = cc.createBucket(bucketName);
        } catch (CosClientException e) {
            e.printStackTrace();
        } finally {
        }
        return bucket;
    }

    ;

    /**
     * 删除桶
     *
     * @param bucketName
     * @throws CosClientException
     * @throws CosServiceException
     */
    public void deleteBucket(String bucketName) throws CosClientException, CosServiceException {
        COSClient cc = getCosClient();
        try {
            cc.deleteBucket(bucketName);
        } catch (CosClientException e) {
            e.printStackTrace();
        } finally {
        }
    }

    ;

    /**
     * 判断桶是否存在
     *
     * @param bucketName
     * @return
     * @throws CosClientException
     * @throws CosServiceException
     */
    public static boolean doesBucketExist(String bucketName) throws CosClientException, CosServiceException {
        COSClient cc = getCosClient();
        boolean bucketExistFlag = cc.doesBucketExist(bucketName);
        return bucketExistFlag;
    }

    ;

    /**
     * 查看桶文件
     *
     * @param bucketName
     * @return
     * @throws CosClientException
     * @throws CosServiceException
     */
    public ObjectListing listObjects(String bucketName) throws CosClientException, CosServiceException {
        COSClient cc = getCosClient();

        // 获取 bucket 下成员（设置 delimiter）
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
        listObjectsRequest.setBucketName(bucketName);
        // 设置 list 的 prefix, 表示 list 出来的文件 key 都是以这个 prefix 开始
        listObjectsRequest.setPrefix("");
        // 设置 delimiter 为/, 即获取的是直接成员，不包含目录下的递归子成员
        listObjectsRequest.setDelimiter("/");
        // 设置 marker, (marker 由上一次 list 获取到, 或者第一次 list marker 为空)
        listObjectsRequest.setMarker("");
        // 设置最多 list 100 个成员,（如果不设置, 默认为 1000 个，最大允许一次 list 1000 个 key）
        listObjectsRequest.setMaxKeys(100);

        ObjectListing objectListing = cc.listObjects(listObjectsRequest);
        // 获取下次 list 的 marker
        String nextMarker = objectListing.getNextMarker();
        // 判断是否已经 list 完, 如果 list 结束, 则 isTruncated 为 false, 否则为 true
        boolean isTruncated = objectListing.isTruncated();
        List<COSObjectSummary> objectSummaries = objectListing.getObjectSummaries();
        for (COSObjectSummary cosObjectSummary : objectSummaries) {
            // get file path
            String key = cosObjectSummary.getKey();
            // get file length
            long fileSize = cosObjectSummary.getSize();
            // get file etag
            String eTag = cosObjectSummary.getETag();
            // get last modify time
            Date lastModified = cosObjectSummary.getLastModified();
            // get file save type
            String StorageClassStr = cosObjectSummary.getStorageClass();
        }
        return objectListing;
    }

    public static void main(String[] args) {
        //uploadFile();
        //downLoadFile();
        //deleteFile();
        //createBucket();
        //deleteBucket();
        //doesBucketExist();
        //listObjects();
    }


}