package com.server.pnd.s3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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

    public String upload(File file, String userName, String fileName) {
        String name = userName + "/" + fileName;

        // 메타데이터 설정
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.length()); // 파일 크기 설정
        metadata.setContentType("image/svg+xml"); // 파일의 MIME 타입 설정 (필요에 따라 수정)

        // S3에 파일 업로드
        amazonS3.putObject(new PutObjectRequest(githubReportImageBucket, name, file));

        // 업로드된 파일의 URL 반환
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
