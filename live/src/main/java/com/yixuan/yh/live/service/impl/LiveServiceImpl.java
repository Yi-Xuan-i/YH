package com.yixuan.yh.live.service.impl;

import com.yixuan.mt.client.MTClient;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private MTClient mtClient;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private ElasticsearchOperations elasticsearchOperations;
    @Autowired
    private UserPrivateClient userPrivateClient;

    @Override
    @Transactional
    public Long postStartLive(Long userId, StartLiveRequest startLiveRequest) throws IOException {

        Long roomId = snowflakeUtils.nextId();

        Live live = new Live();
        live.setRoomId(roomId);
        live.setAnchorId(userId);
        liveMapper.insert(live);

        // 这个改放到 publish 更好
        LiveDocument liveDocument = new LiveDocument();
        liveDocument.setRoomId(roomId);
        liveDocument.setAnchorId(userId);
        liveDocument.setAnchorName(userPrivateClient.getName(userId).getData());
        liveDocument.setTitle(startLiveRequest.getTitle());
        liveDocument.setCoverUrl(mtClient.upload(startLiveRequest.getCoverFile()));
        liveDocument.setStatus(LiveDocument.LiveStatus.LIVING.getCode());
        liveDocument.setCreatedTime(LocalDateTime.now());
        liveRepository.save(liveDocument);

        return liveDocument.getRoomId();
    }

    @Override
    public void publishCallback(Long roomId, String clientId, String token) throws BadRequestException {
        // 鉴权
        Claims claims = jwtUtils.parseJwt(token);
        Long userId = liveMapper.selectAnchorIdById(roomId);
        if (!userId.equals(claims.get("id"))) {
            throw new BadRequestException("你没有权限！");
        }

        liveMapper.updateClientIdById(roomId, clientId);
    }

    @Override
    public void unpublishCallback(Long roomId, String clientId) throws BadRequestException {
        // 鉴权（这里只是简单用推流的 clientId 与结束推流的 clientId 进行对比，并不是特别可靠）
        if (!clientId.equals(liveMapper.selectClientIdById(roomId))) {
            throw new BadRequestException("你没有权限！");
        }

        // 构建更新字段
        Map<String, Object> updateFields = Collections.singletonMap("status", LiveDocument.LiveStatus.ENDED.getCode());

        // 创建UpdateQuery
        UpdateQuery updateQuery = UpdateQuery.builder(roomId.toString())
                .withDocument(Document.from(updateFields))
                .build();

        // 执行部分更新
        UpdateResponse response = elasticsearchTemplate.update(updateQuery,
                IndexCoordinates.of("live"));
        response.getResult();
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
                .map(hit -> LiveMapstruct.INSTANCE.liveDocumentToGetLiveResponse(hit.getContent()))
                .toList();
    }
}
