package com.yixuan.yh.common.utils;

import io.minio.ComposeObjectArgs;
import io.minio.ComposeSource;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@Data
@ConfigurationProperties(prefix = "minio")
@Lazy
public class MinioUtils {

    private String minioUrl;

    private String accessKey;

    private String secretKey;

    private String bucket;

    private MinioClient minioClient;

    @PostConstruct
    public void init() {
        this.minioClient = MinioClient.builder().endpoint(this.getMinioUrl()).credentials(this.accessKey, this.secretKey).build();
    }

    /* 上传（完整文件） */
    public String upload(MultipartFile file) throws Exception {

        LocalDateTime localDateTime = LocalDateTime.now();
        String prefix = localDateTime.getYear() + "/" + localDateTime.getMonthValue() + "/" + localDateTime.getDayOfMonth();

        String fileName = file.getOriginalFilename();
        String extendName = fileName.substring(fileName.lastIndexOf("."));
        String newFilename = UUID.randomUUID() + extendName;

        PutObjectArgs args = PutObjectArgs.builder()
                .bucket(this.bucket)
                .object(prefix + "/" + newFilename)
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(file.getContentType())
                .build();
        minioClient.putObject(args);

        return this.minioUrl + "/" + this.bucket + "/" + prefix + "/" + newFilename;
    }

    /* 上传分块 */
    public String uploadPart(long uploadId, long partNumber, MultipartFile file) throws Exception {
        String partName = uploadId + "-" + partNumber;

        PutObjectArgs args = PutObjectArgs.builder()
                .bucket(this.bucket)
                .object(partName)
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(file.getContentType())
                .build();
        minioClient.putObject(args);

        return partName;
    }

    /* 合并分块 */
    public String completeUploadPart(List<ComposeSource> composeSourceList) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        LocalDateTime localDateTime = LocalDateTime.now();
        String prefix = localDateTime.getYear() + "/" + localDateTime.getMonthValue() + "/" + localDateTime.getDayOfMonth();

        String newFileName = UUID.randomUUID().toString();
        String fullFilePath = prefix + "/" + newFileName;

        minioClient.composeObject(
                ComposeObjectArgs.builder()
                        .bucket(this.bucket)
                        .object(fullFilePath)
                        .sources(composeSourceList)
                        .build()
        );

        return fullFilePath;
    }
}
