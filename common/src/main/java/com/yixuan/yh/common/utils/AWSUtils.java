package com.yixuan.yh.common.utils;


import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.*;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Component
@Data
@ConfigurationProperties(prefix = "aws")
@Lazy
public class AWSUtils {

    private String url;

    private String accessKey;

    private String secretKey;

    private String bucket;

    private S3Client client;

    private S3Presigner presigner;

    @PostConstruct
    public void init() {
        S3Configuration config = S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .build();

        client = S3Client.builder()
                .endpointOverride(URI.create(url)) // MinIO 地址
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .region(Region.US_EAST_1) // MinIO 必须填一个逻辑区域
                .serviceConfiguration(config)
                .build();

        presigner = S3Presigner.builder()
                .endpointOverride(URI.create(url))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .region(Region.US_EAST_1)
                .serviceConfiguration(config)
                .build();
    }

    /**
     * 上传对象
     */
    public String putObject(MultipartFile file) throws IOException {
        String key = generateKey();
        putObject(key, file);
        return key;
    }

    public void putObject(String key, MultipartFile file) throws IOException {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .build();

        client.putObject(
                request,
                RequestBody.fromInputStream(file.getInputStream(), file.getSize())
        );
    }

    /**
     * 删除对象
     */
    public void deleteObject(String key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        client.deleteObject(deleteObjectRequest);
    }

    /**
     * 创建分片上传
     */
    public String createMultipartUpload(String key, String contentType) {
        CreateMultipartUploadRequest request = CreateMultipartUploadRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        CreateMultipartUploadResponse response = client.createMultipartUpload(request);

        return response.uploadId();
    }

    /**
     * 生成普通上传（单文件）的预签名 URL
     */
    public String presignPutObject(String key, String contentType, Duration expireTime) {
        // 1. 构造底层的 PutObject 请求
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        // 2. 包装为预签名请求，设置过期时间
        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(expireTime)
                .putObjectRequest(putObjectRequest)
                .build();

        // 3. 生成预签名请求
        PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);

        // 4. 返回完整 URL
        return presignedRequest.url().toString();
    }

    /**
     * 生成获取对象（下载/预览）的预签名 URL
     */
    public String presignGetObject(String key, int durationMinutes) {
        // 1. 构造底层的 GetObject 请求
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        // 2. 包装为预签名请求，并设置过期时间
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(durationMinutes))
                .getObjectRequest(getObjectRequest)
                .build();

        // 3. 执行预签名操作
        PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);

        // 4. 返回 URL（如果是私有视频，前端拿到此 URL 直接放进 <video src="..."> 即可）
        return presignedRequest.url().toString();
    }

    /**
     * 批量生成分片上传的预签名 URL
     */
    public Map<Integer, String> presignUploadPart(String key, String uploadId, List<Integer> partNumbers, Duration expireTime) {
        Map<Integer, String> urlMap = new HashMap<>();

        for (Integer partNumber : partNumbers) {
            // 1. 构造底层的 UploadPart 请求
            UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .uploadId(uploadId)
                    .partNumber(partNumber)
                    .build();

            // 2. 包装为预签名请求，设置过期时间
            UploadPartPresignRequest presignRequest = UploadPartPresignRequest.builder()
                    .signatureDuration(expireTime)
                    .uploadPartRequest(uploadPartRequest)
                    .build();

            // 3. 生成预签名 URL
            PresignedUploadPartRequest presignedRequest = presigner.presignUploadPart(presignRequest);

            // 4. 将生成的 URL 放入 Map
            urlMap.put(partNumber, presignedRequest.url().toString());
        }

        return urlMap;
    }

    /**
     * 查询已上传分块
     */
    public List<Integer> listUploadedParts(String key, String uploadId) {
        List<Integer> uploadedParts = new ArrayList<>();

        int partNumberMarker = 0;

        while (true) {
            ListPartsRequest request = ListPartsRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .uploadId(uploadId)
                    .partNumberMarker(partNumberMarker)
                    .maxParts(1000)
                    .build();

            ListPartsResponse response = client.listParts(request);

            for (Part part : response.parts()) {
                uploadedParts.add(part.partNumber());
            }

            // 是否还有下一页
            if (response.isTruncated()) {
                partNumberMarker = response.nextPartNumberMarker();
            } else {
                break;
            }
        }

        return uploadedParts;
    }

    /**
     * 查询已上传分片（完整信息）
     */
    public List<CompletedPart> listUploadedCompletedParts(String key, String uploadId) {
        List<CompletedPart> completedParts = new ArrayList<>();
        int partNumberMarker = 0;

        while (true) {
            ListPartsRequest request = ListPartsRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .uploadId(uploadId)
                    .partNumberMarker(partNumberMarker)
                    .maxParts(1000)
                    .build();

            ListPartsResponse response = client.listParts(request);

            for (Part part : response.parts()) {
                completedParts.add(
                        CompletedPart.builder()
                                .partNumber(part.partNumber())
                                .eTag(part.eTag())
                                .build()
                );
            }

            if (response.isTruncated()) {
                partNumberMarker = response.nextPartNumberMarker();
            } else {
                break;
            }
        }

        completedParts.sort(Comparator.comparingInt(CompletedPart::partNumber));

        return completedParts;
    }

    /**
     * 完成分片上传
     */
    public List<Integer> completeMultipartUpload(String key, String uploadId, int expectedTotalChunks) {
        // 1. 直接向 MinIO 查询该 UploadId 下真实存在的完整分块
        List<CompletedPart> completedParts = listUploadedCompletedParts(key, uploadId);

        // 2. 校验分块数量是否符合预期（不符合返回已上传分块）
        if (completedParts.size() != expectedTotalChunks) {
            return completedParts.stream().map(CompletedPart::partNumber).toList();
        }

        // 3. 提交合并请求
        client.completeMultipartUpload(CompleteMultipartUploadRequest.builder()
                .bucket(bucket).key(key).uploadId(uploadId)
                .multipartUpload(CompletedMultipartUpload.builder().parts(completedParts).build())
                .build());

        return Collections.emptyList();
    }

    /**
     * 清理分片数据
     */
    public void abortMultipartUpload(String key, String uploadId) {
        AbortMultipartUploadRequest abortRequest = AbortMultipartUploadRequest.builder()
                .bucket(bucket)
                .key(key)
                .uploadId(uploadId)
                .build();

        // 执行后，会清理与该 uploadId 相关的所有碎片数据
        client.abortMultipartUpload(abortRequest);
    }

    /**
     * 查询对象是否存在
     */
    public boolean isObjectExist(String key) {
        try {
            client.headObject(builder -> builder.bucket(bucket).key(key));
            return true;
        } catch (NoSuchKeyException e) { // 其它异常抛出
            // 明确的对象不存在
            return false;
        }
    }

    public String generateAccessUrl(String key) {
        return url + "/" + bucket + "/" + key;
    }

    public String generateKey() {
        LocalDateTime localDateTime = LocalDateTime.now();
        return localDateTime.getYear() + "/" + localDateTime.getMonthValue() + "/" + localDateTime.getDayOfMonth() + "/" + UUID.randomUUID();
    }
}

