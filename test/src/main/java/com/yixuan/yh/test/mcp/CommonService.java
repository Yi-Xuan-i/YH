package com.yixuan.yh.test.mcp;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class CommonService {

    @Tool(description = "能够获取YH信息")
    public String getYHMessage() {
        return  "YH 是一款基于 微服务架构 的智能短视频平台，融合短视频创作、社交互动、直播娱乐、商城购物等功能于一体。项目采用前沿技术栈实现高并发、高可用性，并通过AI技术赋能，致力于打造沉浸式短视频体验。";
    }
}