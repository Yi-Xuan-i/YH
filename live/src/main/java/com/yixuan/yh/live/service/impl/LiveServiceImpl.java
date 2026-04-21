package com.yixuan.yh.live.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yixuan.yh.common.utils.AWSUtils;
import com.yixuan.yh.common.utils.JwtUtils;
import com.yixuan.yh.common.utils.SnowflakeUtils;
import com.yixuan.yh.live.document.LiveDocument;
import com.yixuan.yh.live.entity.Live;
import com.yixuan.yh.live.mapper.LiveMapper;
import com.yixuan.yh.live.mapstruct.LiveMapstruct;
import com.yixuan.yh.live.repository.LiveRepository;
import com.yixuan.yh.live.request.StartLiveRequest;
import com.yixuan.yh.live.response.GetLiveResponse;
import com.yixuan.yh.live.service.LiveService;
import com.yixuan.yh.user.feign.UserPrivateClient;
import io.jsonwebtoken.Claims;
import org.apache.coyote.BadRequestException;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class LiveServiceImpl implements LiveService {

    @Autowired
    private LiveMapper liveMapper;
    @Autowired
    private LiveRepository liveRepository;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private SnowflakeUtils snowflakeUtils;
    @Autowired
    private AWSUtils awsUtils;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private ElasticsearchOperations elasticsearchOperations;
    @Autowired
    private UserPrivateClient userPrivateClient;
    @Autowired
    private RedissonClient redissonClient;

    @Override
    public String postStartLive(Long userId, StartLiveRequest startLiveRequest) throws IOException {
        Live live = new Live();
        live.setAnchorId(userId);
        live.setTitle(startLiveRequest.getTitle());
        live.setCoverUrl(awsUtils.putObject(startLiveRequest.getCoverFile()));
        liveMapper.insert(live);

        return live.getRoomId().toString();
    }

    @Override
    public void publishCallback(Long roomId, String clientId, String token) throws BadRequestException {
        // 获取所需表数据
        Live live = liveMapper.selectOne(new LambdaQueryWrapper<Live>()
                .select(Live::getAnchorId, Live::getTitle, Live::getCoverUrl)
                .eq(Live::getRoomId, roomId));

        // 鉴权
        Claims claims = jwtUtils.parseJwt(token);
        if (!live.getAnchorId().equals(claims.get("id"))) {
            throw new BadRequestException("你没有权限！");
        }

        // 更新表数据（暂时未利用 client_id）
        liveMapper.updateClientIdById(roomId, clientId);

        // 插入es
        LiveDocument liveDocument = new LiveDocument();
        liveDocument.setRoomId(roomId);
        liveDocument.setAnchorId(live.getAnchorId());
        liveDocument.setAnchorName(userPrivateClient.getName(live.getAnchorId()).getData());
        liveDocument.setTitle(live.getTitle());
        liveDocument.setCoverUrl(live.getCoverUrl());
        liveDocument.setStatus(LiveDocument.LiveStatus.LIVING.getCode());
        liveDocument.setCreatedTime(LocalDateTime.now());
        liveRepository.save(liveDocument);

        // 房间加入 redis 房间集合
        redissonClient.getSet("live:room").add(roomId);
    }

    @Override
    public void unpublishCallback(Long roomId, String clientId) {
        /* 无鉴权（假设该接口只暴露给 srs 服务器） */

        // 构建更新字段
        Map<String, Object> updateFields = Collections.singletonMap("status", LiveDocument.LiveStatus.ENDED.getCode());

        // 创建UpdateQuery
        UpdateQuery updateQuery = UpdateQuery.builder(roomId.toString())
                .withDocument(Document.from(updateFields))
                .build();

        // 执行部分更新
        elasticsearchTemplate.update(updateQuery,
                IndexCoordinates.of("live"));

        // 房间离开 redis 房间集合
        redissonClient.getSet("live:online:" + roomId).delete();
        redissonClient.getSet("live:room").remove(roomId);
    }

    @Override
    public void play(Long roomId, String clientId) {
    }

    @Override
    public void stop(Long roomId, String clientId) {
    }

    @Override
    public Integer getOnline(Long roomId) {
        return Math.toIntExact(redissonClient.getSet("live:online:" + roomId).size());
    }

    @Override
    public List<GetLiveResponse> getLivesByQuery(String query) {
        Criteria titleCriteria = new Criteria("title").contains(query);
        Criteria statusCriteria = new Criteria("status").is(LiveDocument.LiveStatus.LIVING.getCode());
        Criteria combinedCriteria = titleCriteria.and(statusCriteria);
        Query nativeQuery = NativeQuery.builder()
                .withQuery(new CriteriaQuery(combinedCriteria))
                .withSourceFilter(new FetchSourceFilter(null, new String[]{"status"}))
                .build();

        return elasticsearchOperations.search(nativeQuery, LiveDocument.class)
                .getSearchHits()
                .stream()
                .map(hit -> {
                    GetLiveResponse response = LiveMapstruct.INSTANCE.liveDocumentToGetLiveResponse(hit.getContent());
                    response.setCoverUrl(awsUtils.generateAccessUrl(response.getCoverUrl()));
                    return response;
                })
                .toList();
    }
}
