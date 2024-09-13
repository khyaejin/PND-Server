package com.server.pnd.s3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/* S3Service.java */
@Slf4j
@RequiredArgsConstructor
@Service
public class S3Service {
    @Value("${cloud.aws.s3.githubReportImageBucketName}")
    private String githubReportImageBucket;
    @Value("${cloud.aws.s3.userImageBucketName}")
    private String userImageBucket;
    private final AmazonS3 amazonS3;

    public String upload(MultipartFile multipartFile, String dirName, String fileName) throws IOException {
        String name = dirName + "/" + fileName;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        amazonS3.putObject(githubReportImageBucket, name, multipartFile.getInputStream(), metadata);
        return amazonS3.getUrl(githubReportImageBucket, name).toString();
    }

    // name: User PK
    public String modifyUserImage(MultipartFile multipartFile, String name) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        amazonS3.putObject(userImageBucket, name, multipartFile.getInputStream(), metadata);
        return amazonS3.getUrl(userImageBucket, name).toString();
    }
}
