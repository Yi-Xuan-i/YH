package com.yixuan.yh.video.service.impl;

import com.yixuan.mt.client.MTClient;
import com.yixuan.yh.commom.response.Result;
import com.yixuan.yh.commom.utils.SnowflakeUtils;
import com.yixuan.yh.video.entity.Video;
import com.yixuan.yh.video.mapper.VideoMapper;
import com.yixuan.yh.video.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private VideoMapper videoMapper;

    @Autowired
    private SnowflakeUtils snowflakeUtils;

    @Autowired
    private MTClient mtClient;
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public List<String> getVideos() {
        return videoMapper.selectRandom();
    }

    @Override
    public String postVideo(MultipartFile videoFile) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("video", new ByteArrayResource(videoFile.getBytes()) {
            @Override
            public String getFilename() {
                return videoFile.getOriginalFilename();
            }
        });

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<Result> response = restTemplate.postForEntity(
                "http://localhost:10092/check_duplicate",
                requestEntity,
                Result.class
        );

        Map<String, Long> dataMap = (Map<String, Long>) response.getBody().getData();
        Long videoId = null;
        Long similarId = null;
        if (dataMap != null) {
            videoId = dataMap.get("id");
            similarId = dataMap.get("similarId");
        }

        String videoUrl = mtClient.upload(videoFile);
        Video video = new Video();
        video.setId(videoId);
        video.setUrl(videoUrl);
        videoMapper.insert(video);

        if (similarId != null) {
            return videoMapper.selectVideoUrlById(similarId);
        }
        return null;
    }
}
