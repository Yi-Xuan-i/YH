package com.yixuan.yh.video.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.yixuan.yh.common.utils.AWSUtils;
import com.yixuan.yh.video.mapper.VideoUploadTaskMapper;
import com.yixuan.yh.video.pojo.entity.VideoUploadTask;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FileUploadCompensationJob {

    private final VideoUploadTaskMapper videoUploadTaskMapper;
    private final AWSUtils awsUtils;

    @XxlJob("videoUploadCompensation")
    public void videoUploadCompensation() {
        long lastId = 0;
        int batchSize = 100;

        while (true) {
            List<VideoUploadTask> list = videoUploadTaskMapper.selectList(
                    new LambdaQueryWrapper<VideoUploadTask>()
                            .select(VideoUploadTask::getId, VideoUploadTask::getUploadId, VideoUploadTask::getKey, VideoUploadTask::getUploadType, VideoUploadTask::getExpireAt)
                            .gt(VideoUploadTask::getId, lastId)
                            .eq(VideoUploadTask::getStatus, VideoUploadTask.UploadStatus.UPLOADING)
                            .lt(VideoUploadTask::getCreatedAt, LocalDateTime.now().minusHours(6))
                            .orderByAsc(VideoUploadTask::getId)
                            .last("LIMIT " + batchSize));

            if (list.isEmpty()) {
                break;
            }


            for (VideoUploadTask task : list) {
                // 先抢占操作锁
                int n = videoUploadTaskMapper.update(new LambdaUpdateWrapper<VideoUploadTask>()
                        .set(VideoUploadTask::getStatus, VideoUploadTask.UploadStatus.PROCESSING)
                        .eq(VideoUploadTask::getId, task.getId())
                        .eq(VideoUploadTask::getStatus, VideoUploadTask.UploadStatus.UPLOADING)
                        .eq(VideoUploadTask::getExpireAt, task.getExpireAt())); // ExpireAt 充当了版本号
                if (n == 0) {
                    continue;
                }

                // 对象存在
                if (awsUtils.isObjectExist(task.getKey())) {
                    awsUtils.deleteObject(task.getKey());
                }
                // 对象不存在，查看有没有分片任务
                else if (task.getUploadType() == VideoUploadTask.UploadType.MULTIPART && !awsUtils.listUploadedParts(task.getKey(), task.getUploadId()).isEmpty()) {
                    awsUtils.abortMultipartUpload(task.getKey(), task.getUploadId());
                }
            }

            // 修改为最终态
            videoUploadTaskMapper.update(new LambdaUpdateWrapper<VideoUploadTask>()
                    .set(VideoUploadTask::getStatus, VideoUploadTask.UploadStatus.EXPIRED)
                    .in(VideoUploadTask::getId, list.stream().map(VideoUploadTask::getId).toList()));

            // 更新lastId
            lastId = list.getLast().getId();
        }
    }

    @XxlJob("videoUploadProcessingRetry")
    public void videoUploadProcessingRetry() {
        long lastId = 0;
        int batchSize = 100;

        while (true) {
            List<Long> taskIdList = videoUploadTaskMapper.selectList(
                            new LambdaQueryWrapper<VideoUploadTask>()
                                    .select(VideoUploadTask::getId)
                                    .gt(VideoUploadTask::getId, lastId)
                                    .eq(VideoUploadTask::getStatus, VideoUploadTask.UploadStatus.PROCESSING)
                                    .lt(VideoUploadTask::getUpdatedAt, LocalDateTime.now().minusHours(10))
                                    .orderByAsc(VideoUploadTask::getId)
                                    .last("LIMIT " + batchSize))
                    .stream().map(VideoUploadTask::getId).toList();

            if (taskIdList.isEmpty()) {
                break;
            }

            // 状态回调
            videoUploadTaskMapper.update(new LambdaUpdateWrapper<VideoUploadTask>()
                    .set(VideoUploadTask::getStatus, VideoUploadTask.UploadStatus.UPLOADING)
                    .in(VideoUploadTask::getId, taskIdList));

            // 更新lastId
            lastId = taskIdList.getLast();
        }
    }
}