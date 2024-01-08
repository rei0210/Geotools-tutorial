package org.geotools.minio;

import io.minio.DownloadObjectArgs;
import io.minio.MinioClient;

public class FileDownloader {

    public static void main(String[] args) {
        try {
            MinioClient minioClient =
                    //创建MinIO Client
                    MinioClient.builder()
                            .endpoint("http://localhost:9000")
                            //输入登录用户名和密码
                            .credentials("minioadmin", "minioadmin")
                            .build();

            minioClient.downloadObject(
                    DownloadObjectArgs.builder()
                            .bucket("gtfiles")//桶名
                            .object("star2.shp")//文件名
                            .filename("D:\\shapeFiles\\test5\\mystar.shp")//下载到的位置和文件名
                            .build());

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
