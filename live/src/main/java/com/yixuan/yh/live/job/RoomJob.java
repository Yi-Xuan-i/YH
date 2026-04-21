package com.yixuan.yh.live.job;

import com.xxl.job.core.handler.annotation.XxlJob;
import com.yixuan.yh.common.utils.SRSUtils;
import com.yixuan.yh.live.document.LiveDocument;
import com.yixuan.yh.live.websocket.pojo.LiveMessage;
import lombok.RequiredArgsConstructor;
import org.redisson.api.*;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RoomJob {

    private final RedissonClient redissonClient;
    private final SRSUtils srsUtils;
    private final SimpMessagingTemplate messagingTemplate;
    private final ElasticsearchTemplate elasticsearchTemplate;

    @XxlJob("clearInactiveRoom")
    public void clearInactiveRoom() {
        /* 实际活跃Room */
        Set<Long> activeRoomSet = srsUtils.getStreams();

        /* redis中存储的Room */
        RSet<Long> rSet = redissonClient.getSet("live:room");
        Set<Long> redisRoomSet = rSet.readAll();

        /* 遍历获取要剔除的Room */
        List<Long> inactiveRoomList = new ArrayList<>();
        for (Long room : redisRoomSet) {
            if (!activeRoomSet.contains(room)) {
                inactiveRoomList.add(room);
            }
        }

        /* 执行剔除相关逻辑 */
        if (inactiveRoomList.isEmpty()) return;
        // 更新直播状态
        Map<String, Object> updateFields = Collections.singletonMap("status", LiveDocument.LiveStatus.ENDED.getCode());
        Document document = Document.from(updateFields);

        List<UpdateQuery> updateQueries = inactiveRoomList.stream().map(String::valueOf).toList()
                .stream()
                .map(id -> UpdateQuery.builder(id.toString())
                        .withDocument(document) // 复用同一个 document
                        .build())
                .collect(Collectors.toList());

        elasticsearchTemplate.bulkUpdate(updateQueries, IndexCoordinates.of("live"));
        // redis中剔除房间
        RBatch rBatch = redissonClient.createBatch();
        for (Long roomId : inactiveRoomList) {
            RSetAsync<Long> set = rBatch.getSet("live:online:" + roomId);
            set.deleteAsync();
        }
        rBatch.execute();
        rSet.removeAll(inactiveRoomList);
    }

    @XxlJob("pushOnline")
    public void pushOnline() {
        /* 获取redis维护的房间 */
        RSet<Long> rSet = redissonClient.getSet("live:room");
        Set<Long> redisRoomSet = rSet.readAll();

        /* 使用 Pipeline 批量获取各房间人数 */
        RBatch batch = redissonClient.createBatch();

        List<Long> roomIds = new ArrayList<>(redisRoomSet);
        for (Long roomId : roomIds) {
            batch.getSet("live:online:" + roomId).sizeAsync();
        }

        BatchResult<?> batchResult = batch.execute();
        List<Integer> counts = (List<Integer>) batchResult.getResponses();

        /* 遍历推送或处理人数 */
        for (int i = 0; i < roomIds.size(); i++) {
            Long roomId = roomIds.get(i);
            Integer onlineCount = counts.get(i);

            messagingTemplate.convertAndSend("/topic/room." + roomId, new LiveMessage(LiveMessage.MessageType.ONLINE, onlineCount));
        }
    }

}
