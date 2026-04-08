package com.yixuan.yh.video;

import com.yixuan.yh.common.utils.AWSUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AWSUtilsTest {

    @Autowired
    private AWSUtils awsUtils;

    @Test
    void test() {
//        String key = awsUtils.generateKey();
//
//        // 初始化上传分片
//        String uploadId = awsUtils.createMultipartUpload(key, "video/mp4");
//
//        // 生成上传分片URL
//        System.out.println(awsUtils.presignUploadPart(key, uploadId, List.of(1, 2)));
//
//        // 查询已上传分块
//        System.out.println(awsUtils.listUploadedParts(key, uploadId));
    }
}