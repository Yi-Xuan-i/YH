package com.yixuan.yh.video.controller._public;

import com.yixuan.yh.commom.response.Result;
import com.yixuan.yh.video.service.VideoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

@Tag(name = "Video")
@RestController
@RequestMapping("/public")
public class VideoPublicController {

    @Autowired
    private VideoService videoService;

    @PostMapping("/stream-upload")
    public ResponseEntity<String> streamUpload(HttpServletRequest request) {
        try (InputStream inputStream = request.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 逐行处理数据（示例：打印内容）
                System.out.println(line);
            }
            return ResponseEntity.ok("Upload processed");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error processing upload");
        }
    }

    @GetMapping("/list")
    public Result<List<String>> getVideos() {
        return Result.success(videoService.getVideos());
    }
}
