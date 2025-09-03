package com.yixuan.yh.video;

import com.yixuan.yh.video.mapper.VideoMapper;
import com.yixuan.yh.video.pojo.request.PostCommentRequest;
import com.yixuan.yh.video.service.InteractionService;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class InteractionTest {

    @Autowired
    private InteractionService interactionService;
    @Autowired
    private VideoMapper videoMapper;

    @Test
    public void comment() throws BadRequestException {
        // 视频不存在校验
        Long videoId = 1L;
        Long userId = 1L;
        PostCommentRequest postCommentRequest = new PostCommentRequest();
        postCommentRequest.setContent("视频很不错！");
        assertThrows(BadRequestException.class, () -> interactionService.comment(videoId, userId, postCommentRequest));

        // 成功评论
        Long videoId2 = videoMapper.selectFirst().getId();
        Long userId2 = 1L;
        PostCommentRequest postCommentRequest2 = new PostCommentRequest();
        postCommentRequest2.setContent("视频很不错！");
        interactionService.comment(videoId2, userId2, postCommentRequest2);
        assertNotNull(interactionService.directComment(videoId2), "评论插入异常！");
    }

}
