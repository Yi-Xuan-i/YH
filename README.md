# <div align="center">YH - 智能短视频平台</div>

<div align="center">
<img src="https://gitee.com/YXXHYH/yh-be/raw/master/.file/yh.png" alt="YH-Logo" width="50" height="50" />
</div>

## 🌟 项目概述

**YH** 是一款基于 **微服务架构** 的智能短视频平台，融合短视频创作、社交互动、直播娱乐、商城购物等功能于一体。项目采用前沿技术栈实现高并发、高可用性，并通过AI技术赋能，致力于打造沉浸式短视频体验。

***

## 🚀 核心功能模块

| 模块                  | 功能说明                    |
| ------------------- | ----------------------- |
| **User服务**          | 用户注册登录、个人信息管理、关注关系、好友管理 |
| **Video服务**         | 短视频发布、视频交互、标签管理         |
| **Video-Processor** | 视频转码                    |
| **Live服务**          | 直播间创建管理、直播商品上架、实时互动聊天   |
| **Product服务**       | 商品发布、分类管理、SKU规格、库存管理    |
| **Order服务**         | 订单创建、支付处理、订单状态管理、购物车    |
| **Chat服务**          | 实时消息聊天                  |
| **AI服务**            | AI智能对话、语音聊天             |
| **Admin服务**         | 管理后台、权限控制、内容审核、系统管理     |
| **Gateway服务**       | API网关、路由转发、JWT认证过滤      |
| **MCP-Server**      | 模型上下文协议服务，AI插件支持        |

***

## 🎯 技术架构

### 🛠 后端技术栈

| 技术                   | 版本         | 用途          |
| -------------------- | ---------- | ----------- |
| Java                 | 21         | 编程语言        |
| Spring Boot          | 3.2.0      | 核心框架        |
| Spring Cloud         | 2023.0.0   | 微服务框架       |
| Spring Cloud Alibaba | 2023.0.3.3 | 阿里云微服务组件    |
| Spring AI            | 1.1.4      | AI集成框架      |
| MyBatis Plus         | 3.5.5      | ORM框架       |
| Redisson             | 3.13.6     | Redis分布式客户端 |

### 🌐 前端技术栈

| 技术             | 用途         |
| -------------- | ---------- |
| Vue3           | 核心框架       |
| ElementPlus    | UI组件库      |
| TD Chat for AI | AI Chat组件库 |
| WebRTC         | 实时音视频通信    |

### 📦 中间件与基础设施

| 中间件/基础设施      | 版本       | 用途           |
| ------------- |----------|--------------|
| Nacos         | 3.0.2    | 服务注册与配置中心    |
| SRS           | 5.0.213  | 直播流媒体服务器     |
| MySQL         | 8.0.45   | 关系型数据库       |
| Redis         | 7.0.15   | 分布式缓存        |
| RabbitMQ      | 3.9.0    | 消息队列         |
| XXL-Job       | 3.4.0    | 分布式任务调度      |
| Elasticsearch | 8.11.1   | 搜索引擎         |
| Seata | 1.6.0    | 分布式事务        |
***

## 🔮 项目亮点

- **AI深度融合**：集成Spring AI框架，支持智能对话、语音交互，结合MCP协议提供插件化能力
- **实时音视频**：基于SRS实现直播推流拉流，WebRTC支持实时通信
- **高并发架构**：Redisson分布式锁、Redis缓存、消息队列削峰填谷
- **微服务化**：服务拆分清晰，独立部署，支持水平扩展
- **视频处理**：支持分块上传、断点续传，专业视频处理流程

***

## 🌈 未来规划

- [ ] 个性化推荐算法
- [ ] 短视频智能审核
- [ ] 更多AI能力集成
- [ ] 性能优化与监控完善
- [ ] 容器化部署
- [ ] 单元测试覆盖率提升

***

## 📷 前端页面展示

<div align="center">
  <!-- 首页 -->
  <img src="https://gitee.com/YXXHYH/yh-be/raw/master/.file/home.png" alt="首页" width="700" />

  <!-- 直播 -->

  <img src="https://gitee.com/YXXHYH/yh-be/raw/master/.file/live-anchor.png" alt="直播" width="700" />

  <!-- 创作者平台 -->

  <img src="https://gitee.com/YXXHYH/yh-be/raw/master/.file/creator.png" alt="创作者平台" width="700" />

  <!-- AI文字聊天 -->

  <img src="https://gitee.com/YXXHYH/yh-be/raw/master/.file/ai.png" alt="AI文字聊天" width="700" />

  <!-- AI语音聊天 -->

  <img src="https://gitee.com/YXXHYH/yh-be/raw/master/.file/voice-chat.png" alt="AI语音聊天" width="700" />

  <!-- 商品详情 -->

  <img src="https://gitee.com/YXXHYH/yh-be/raw/master/.file/product-detail.png" alt="商品详情" width="700" />  
</div>


***

## 📦 项目结构

```
YH/
├── .file/               # 配置文件、SQL脚本、截图资源
├── parent/              # 父POM，统一依赖管理
├── common/              # 公共模块，工具类、配置、异常等
├── gateway/             # API网关
├── admin/               # 管理后台服务
├── user/                # 用户服务
├── video/               # 短视频服务
├── video-processor/     # 视频处理服务
├── product/             # 商品服务
├── order/               # 订单服务
├── live/                # 直播服务
├── ai/                  # AI服务
├── chat/                # 聊天服务
├── mcp-server/          # MCP服务
└── pom.xml              # 主POM
```

***

## 🔧 项目部署

- **数据库脚本**：位于 `.file/sql` 目录
- **Nacos配置**：位于 `.file/config` 目录

***

## 📚 相关链接

- 前台页面：<https://gitee.com/YXXHYH/yh-fe>
- 后台页面：<https://gitee.com/YXXHYH/yh-admin-fe>

