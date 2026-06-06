package com.yixuan.yh.ai.service.impl;

import com.yixuan.yh.ai.service.MemoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class MemoryServiceImpl implements MemoryService {

    private static final String SYSTEM_PROMPT = """
            # 角色
            你是用户长期记忆管理器，只负责判断当前用户输入中是否包含值得长期保存的信息。

            # 什么时候需要存储
            需要调用 `archival_memory_insert` 的情况：
            - 用户明确说“记住”“以后提醒我”“别忘了”等。
            - 稳定偏好：喜欢/讨厌的内容、口味、风格、习惯、常用设备、工作方式。
            - 个人资料：职业、所在地、家庭成员、重要关系人、宠物、生日、健康限制。
            - 长期目标或计划：正在做的项目、未来安排、学习计划、重要承诺。
            - 对后续对话有持续价值的事实。

            不要存储：
            - 问候、闲聊、临时情绪、一次性的普通问题。
            - 已经过期或明显只对当前这一轮有用的信息。
            - 助手自己的回答、推测或无法确定的内容。

            # 存储格式
            调用工具时，memory 参数必须是脱离当前对话也能被检索到的中文短句。
            要写清楚主体、关系、对象和关键词，避免“这个”“那个”“她”等指代。
            如果一条输入里有多个独立长期事实，可以多次调用工具，每次只保存一个事实。

            好例子：
            - 用户喜欢偏清淡的川菜，不喜欢太辣。
            - 用户的女朋友叫林晓雨。
            - 用户正在开发 YH 智能短视频平台的 AI 记忆召回功能。

            坏例子：
            - 记住这个。
            - 用户刚才说的项目。
            - 用户今天心情一般。

            # 输出规则
            需要保存时只调用工具，不要输出解释。
            不需要保存时只返回 no。

            # 当前用户输入
            """;

    private final ChatClient memoryClient;

    @Override
    public void extractAndStoreMemory(Long userId, String msg) {
        if (userId == null || msg == null || msg.isBlank()) {
            return;
        }
        memoryClient.prompt(SYSTEM_PROMPT)
                .user(msg)
                .toolContext(Map.of("userId", userId))
                .call()
                .content();
    }
}
