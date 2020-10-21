package com.microservice.config;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class AWSClient {
    static AmazonS3 s3;

    private static void init() throws Exception {
        AWSCredentials credentials = new BasicAWSCredentials("ABCDEFGHIJKLMNOPQRST",
                "abcdefghijklmnopqrstuvwxyz0123456789ABCD");

        ClientConfiguration configuration = new ClientConfiguration();
        configuration.setProtocol(Protocol.HTTP);
        configuration.setUseExpectContinue(false);

        String endPoint = "192.168.40.134:8002";
        AwsClientBuilder.EndpointConfiguration endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(
                endPoint, null);

        s3 = AmazonS3ClientBuilder.standard().withEndpointConfiguration(endpointConfiguration)
                .withClientConfiguration(configuration)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                //.withChunkedEncodingDisabled(true)
                .withPathStyleAccessEnabled(true).build();
    }

    public static void deleteObject(String bucket, String object) {
        try {
            s3.deleteObject(bucket, object);
        } catch (AmazonServiceException e) {
            System.out.println("status code:" + e.getStatusCode());
        } catch (AmazonClientException e2) {
            System.out.println("status code:" + e2.getMessage());
        }
    }

    public static void putObject(String bucket, String object) {
        try {
            PutObjectRequest request = new PutObjectRequest(bucket, object,
                    new File("E:\\zfs\\java_work_space\\springcloud-zhangwei\\sequoiadb-zhangwei\\src\\main\\resources\\1.png"));
            s3.putObject(request);
        } catch (AmazonServiceException e) {
            System.out.println("status code:" + e.getStatusCode());
        } catch (AmazonClientException e2) {
            System.out.println("status code:" + e2.getMessage());
        }
    }

    public static void getObject(String bucket, String object) {
        try {
            GetObjectRequest request = new GetObjectRequest(bucket, object, null);
            System.out.println(object.toString());
            S3Object result = s3.getObject(request);

            S3ObjectInputStream s3is = result.getObjectContent();
            FileOutputStream fos = new FileOutputStream(new File("E:\\zfs\\java_work_space\\springcloud-zhangwei\\sequoiadb-zhangwei\\src\\main\\resources\\temp\\" + object));
            byte[] read_buf = new byte[1024 * 34];
            int read_len = 0;
            while ((read_len = s3is.read(read_buf)) > 0) {
                fos.write(read_buf, 0, read_len);
            }
            s3is.close();
            fos.close();
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void listObjects(String bucket) {
        try {
            ListObjectsV2Request request = new ListObjectsV2Request();
            request.setBucketName(bucket);
            ListObjectsV2Result result = s3.listObjectsV2(request);

            List<String> commonPrefix = result.getCommonPrefixes();
            for (int i = 0; i < commonPrefix.size(); i++) {
                System.out.println("commonPrefix:" + commonPrefix.get(i));
            }
            List<S3ObjectSummary> objectList = result.getObjectSummaries();
            for (int i = 0; i < objectList.size(); i++) {
                System.out.println("key:" + objectList.get(i).getKey());
            }
        } catch (AmazonServiceException e) {
            System.out.println("status code:" + e.getStatusCode());
        } catch (AmazonClientException e2) {
            System.out.println("status code:" + e2.getMessage());
        }
    }

    public static void putBucket(String bucket) {
        try {
            s3.createBucket(bucket);
        } catch (AmazonServiceException e) {
            System.err.println(e.getStatusCode());
            System.err.println(e.getErrorCode());
            System.err.println(e.getErrorMessage());
        }
    }

    //运行主函数
    public static void main(String[] args) throws Exception {
//        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2,SSLv3");
//        System.setProperty("http.protocols", "TLSv1,TLSv1.1,TLSv1.2,SSLv3");
        String bucketName = "mybucket";
        String keyName = "1.png";
        //初始化连接
        init();
        //创建桶
//        putBucket(bucketName);
        //添加对象
//        putObject(bucketName, keyName);
        //获取对象
        getObject(bucketName, keyName);
        //删除对象
//        deleteObject(bucketName, keyName);
        //枚举对象列表
//        listObjects(bucketName);
    }
}