package org.geotools.minio;
/*
* 上传文件至minIO服务器
*
* */

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import io.minio.errors.MinioException;

public class FileUploader {
    public static void main(String[] args) throws Exception{
        try {
            MinioClient minioClient =
                    //创建MinIO Client
                    MinioClient.builder()
                            .endpoint("http://localhost:9000")
                            //输入登录用户名和密码
                            .credentials("minioadmin", "minioadmin")
                            .build();

            //判断我们要上传到的 bucket 是否存在
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket("gtfiles").build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket("gtfiles").build());
            } else {
                System.out.println("Bucket 'gtfiles' already exists.");
            }

            minioClient.uploadObject(UploadObjectArgs.builder()
                    //上传到那个桶中
                    .bucket("gtfiles")
                    //指定上传MinIO中后叫什么名字
                    .object("triangle1.shp")
                    //指定上传的文件路径
                    .filename("D:\\shapeFiles\\test1\\triangle1\\triangle1.shp")
                    .build());
            System.out.println("SUCCESS!!!!");
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
            System.out.println("HTTP trace: " + e.httpTrace());
        }
    }

}
