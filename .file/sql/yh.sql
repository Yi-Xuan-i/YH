create database yh;
use yh;


create table admin
(
    id               bigint unsigned                    not null
        primary key,
    name             varchar(20)                        not null,
    encoded_password varchar(60)                        not null,
    created_time     datetime default CURRENT_TIMESTAMP null,
    constraint admin_pk_2
        unique (name)
)
    charset = utf8mb3;

create table admin_permission
(
    admin_id        bigint unsigned not null,
    permission_code varchar(255)    not null,
    primary key (permission_code, admin_id)
)
    charset = utf8mb3;

create table audit
(
    id             bigint auto_increment
        primary key,
    admin_id       bigint unsigned                    not null,
    request_method varchar(10)                        not null,
    request_path   varchar(200)                       not null,
    request_body   text                               null,
    created_time   datetime default CURRENT_TIMESTAMP null
)
    charset = utf8mb3;

create table cart_item
(
    cart_item_id bigint auto_increment comment 'ID'
        primary key,
    user_id      bigint                   not null comment '用户ID',
    product_id   bigint                   not null comment '商品ID',
    sku_id       bigint                   not null comment 'SKU ID',
    quantity     int unsigned default '1' not null comment '数量',
    price        decimal(10, 2)           not null comment '加入购物车时的价格快照',
    selected_sku json                     null comment '选中规格（JSON冗余，避免SKU失效时无法展示）',
    updated_at   datetime                 null comment '更新时间',
    created_at   datetime                 null comment '创建时间'
)
    comment '购物车条目表' charset = utf8mb3;

create table category
(
    category_id int auto_increment
        primary key,
    name        varchar(50)          not null,
    parent_id   int                  null comment '父分类ID',
    level       tinyint    default 1 null comment '分类层级',
    is_leaf     tinyint(1) default 0 null comment '是否末级分类'
)
    comment '商品分类表' charset = utf8mb3;

create table chat_conversation
(
    id                 bigint unsigned               not null comment '会话id'
        primary key,
    user1_id           bigint unsigned               not null comment '用户1id',
    user2_id           bigint unsigned               not null comment '用户2id',
    user1_unread_count smallint unsigned default '0' not null comment '用户1未读聊天数',
    user2_unread_count smallint unsigned             not null comment '用户2未读聊天数',
    updated_time       datetime                      not null comment '更新时间',
    created_time       datetime                      not null comment '创建时间',
    constraint chat_conversation_pk
        unique (user1_id, user2_id)
);

create index chat_conversation_user1_id_updated_time_index
    on chat_conversation (user1_id asc, updated_time desc);

create index chat_conversation_user2_id_updated_time_index
    on chat_conversation (user2_id asc, updated_time desc);

create table chat_message
(
    id              bigint unsigned not null comment '消息id'
        primary key,
    conversation_id bigint unsigned not null comment '会话id',
    sender_id       bigint unsigned not null comment '发送者id',
    message_type    tinyint         not null comment '消息类型',
    content         text            not null comment '普通文本内容',
    created_time    datetime        not null comment '创建时间'
);

create index chat_message_conversation_id_id_index
    on chat_message (conversation_id asc, id desc);

create table chat_message_media
(
    id         bigint       not null comment '媒体id'
        primary key,
    message_id bigint       null comment '所属消息id',
    media_type tinyint      not null comment '媒体类型',
    url        varchar(256) null comment '视频url',
    cover_url  varchar(256) null comment '视频封面url',
    created_at datetime     not null comment '创建时间'
);

create index idx_message_id
    on chat_message_media (message_id);

create table conversation
(
    conversation_id bigint unsigned auto_increment comment '会话id'
        primary key,
    user_id         bigint                  not null comment '会话所属用户id',
    title           varchar(255) default '' null comment '会话标题',
    updated_at      datetime                null comment '更新时间',
    created_at      datetime                null comment '创建时间'
)
    comment 'ai聊天会话表' charset = utf8mb3;

create index idx_user
    on conversation (user_id asc, conversation_id desc);

create table conversation_message
(
    message_id      bigint unsigned auto_increment comment '消息id'
        primary key,
    conversation_id bigint unsigned                                                     not null comment '关联会话id',
    role            enum ('user', 'assistant', 'system') charset utf8mb3 default 'user' not null comment '消息角色',
    content         longtext                                                            not null comment '消息内容',
    updated_at      datetime                                                            null comment '更新时间',
    created_at      datetime                                                            null comment '创建时间'
)
    comment 'ai聊天会话消息表';

create index idx_conversation
    on conversation_message (conversation_id asc, message_id desc);

create table intent
(
    id       bigint      not null
        primary key,
    intent   varchar(30) not null,
    response text        not null
);

create index intent_intent_index
    on intent (intent);

create table live
(
    room_id   bigint unsigned not null comment '房间id'
        primary key,
    anchor_id bigint unsigned not null comment '直播id',
    client_id varchar(25)     null comment '客户端id（SRS 给每一个物理连接分配的唯一自增编号）',
    title     varchar(25)     null comment '直播标题',
    cover_url varchar(256)    null comment '直播封面url'
)
    charset = utf8mb3;

create table live_product
(
    id         bigint unsigned not null
        primary key,
    room_id    bigint unsigned not null,
    product_id bigint unsigned not null,
    name       varchar(256)    null,
    price      decimal(10, 2)  null,
    stock      int unsigned    null,
    image_url  varchar(256)    null
)
    charset = utf8mb3;

create index live_product_room_id_index
    on live_product (room_id);

create table merchant
(
    merchant_id          bigint                                                                 not null comment '商家ID'
        primary key,
    name                 varchar(100)                                                           not null comment '店铺名称',
    contact_phone        varchar(20)                                                            null comment '联系电话',
    avatar_url           varchar(255)                                                           null comment '店铺头像',
    certification_status enum ('UNCERTIFIED', 'PENDING', 'CERTIFIED') default 'UNCERTIFIED'     null comment '认证状态',
    created_at           datetime                                     default CURRENT_TIMESTAMP null
)
    comment '商家信息表' charset = utf8mb3;

create table message_outbox
(
    id              bigint unsigned  not null comment '消息id'
        primary key,
    business_id     bigint unsigned  null comment '业务id',
    exchange_name   varchar(64)      null comment '交换机名称',
    routing_key     varchar(64)      null comment '路由键',
    message_body    text             null comment '消息体',
    status          tinyint unsigned null comment '状态',
    retry_count     tinyint unsigned null comment '重试次数',
    next_retry_time datetime         null comment '下次重试时间',
    updated_at      datetime         null comment '更新时间',
    created_at      datetime         null comment '创建时间'
);

create table `order`
(
    order_id         bigint unsigned                                                                                                not null comment '订单号'
        primary key,
    user_id          bigint unsigned                                                                                                not null comment '用户ID',
    merchant_id      bigint unsigned                                                                                                not null comment '商家ID',
    payment_amount   decimal(10, 2)                                                                                                 not null comment '实付金额',
    order_status     enum ('UNPAID', 'PAID', 'SHIPPED', 'COMPLETED', 'CANCELLED', 'REFUND_PROCESSING', 'REFUNDED') default 'UNPAID' null comment '状态',
    delivery_address json                                                                                                           not null comment '收货地址（JSON结构存储）',
    updated_at       datetime                                                                                                       null comment '更新时间',
    created_at       datetime                                                                                                       null comment '创建时间'
)
    comment '订单主表' charset = utf8mb3;

create table order_item
(
    order_item_id bigint unsigned not null comment '订单明细id'
        primary key,
    order_id      bigint unsigned not null comment '订单id',
    product_id    bigint unsigned not null comment '商品id',
    sku_id        bigint unsigned not null comment 'SKU id',
    sku           json            not null comment 'sku快照',
    product_name  varchar(256)    not null comment '商品名称（快照）',
    quantity      int unsigned    not null comment '购买数量',
    price         decimal(10, 2)  not null comment '购买时单价',
    pay_time      datetime        null
)
    comment '订单商品明细' charset = utf8mb3;

create table product
(
    product_id     bigint unsigned                        not null comment '商品id'
        primary key,
    merchant_id    bigint unsigned                        not null comment '商家id',
    category_id    bigint unsigned                        null comment '分类id',
    default_sku_id bigint unsigned                        null comment '主规格（商品卡片展示时展示该sku的图片和价格）',
    title          varchar(255)                           not null comment '商品标题',
    cover_url      varchar(255)                           not null comment '封面url',
    description    text charset utf8mb4                   null comment '商品描述',
    status         tinyint unsigned                       null comment '状态',
    is_hot         tinyint(1)   default 0                 null comment '是否热门推荐',
    sales_volume   int unsigned default '0'               null comment '销量',
    rating         float        default 0                 null comment '平均评分',
    updated_at     datetime     default CURRENT_TIMESTAMP null comment '更新时间',
    created_at     datetime     default CURRENT_TIMESTAMP null comment '创建时间'
)
    comment '商品主表' charset = utf8mb3;

create table product_carousel
(
    id     bigint          not null
        primary key,
    sku_id bigint unsigned not null,
    url    varchar(256)    not null
)
    comment '商品SKU轮播图表' charset = utf8mb3;

create index product_carousel_product_id_sku_id_index
    on product_carousel (sku_id);

create table product_sku
(
    sku_id     bigint unsigned             not null
        primary key,
    product_id bigint unsigned             not null,
    price      decimal(10, 2) default 0.00 null comment 'SKU价格',
    stock      int unsigned   default '0'  null comment 'SKU库存'
)
    comment '商品SKU表' charset = utf8mb3;

create index product_id
    on product_sku (product_id);

create table sku_spec
(
    sku_id   bigint not null,
    key_id   bigint not null,
    value_id bigint not null,
    primary key (sku_id, key_id)
)
    charset = utf8mb3;

create table spec_key
(
    key_id   bigint      not null
        primary key,
    key_name varchar(50) not null comment '规格名称（如“颜色”）'
)
    charset = utf8mb3;

create table spec_value
(
    value_id   bigint       not null
        primary key,
    value_name varchar(50)  not null comment '规格值（如“红色”）',
    image_url  varchar(255) null comment '规格值图片（如颜色色块）'
)
    charset = utf8mb3;

create table undo_log
(
    id            bigint auto_increment
        primary key,
    branch_id     bigint       not null,
    xid           varchar(100) not null,
    context       varchar(128) not null,
    rollback_info longblob     not null,
    log_status    int          not null,
    log_created   datetime     not null,
    log_modified  datetime     not null,
    ext           varchar(100) null,
    constraint ux_undo_log
        unique (xid, branch_id)
)
    charset = utf8mb3;

create table user
(
    id               bigint unsigned                    not null comment '用户id'
        primary key,
    phone_number     varchar(20)                        not null comment '手机号',
    name             varchar(20)                        not null comment '用户名',
    encoded_password char(60)                           not null comment '加密后的密码',
    avatar_url       varchar(256)                       null comment '用户头像',
    bio              varchar(256)                       null comment '用户简介',
    updated_at       datetime                           null comment '更新时间',
    created_at       datetime default CURRENT_TIMESTAMP null comment '创建时间',
    constraint uer_pk_2
        unique (phone_number)
)
    charset = utf8mb3;

create table user_address
(
    address_id     bigint unsigned auto_increment comment '地址id'
        primary key,
    user_id        bigint unsigned      not null comment '用户id',
    is_default     tinyint(1) default 0 null comment '是否默认地址',
    receiver_name  varchar(50)          not null comment '收货人姓名',
    receiver_phone varchar(20)          not null comment '收货人手机',
    province       varchar(50)          not null comment '省份',
    city           varchar(50)          not null comment '城市',
    district       varchar(50)          not null comment '区县',
    detail_address varchar(255)         not null comment '详细地址（楼号/门牌）'
)
    comment '用户收货地址表' charset = utf8mb3;

create table user_follow
(
    id           bigint unsigned not null
        primary key,
    follower_id  bigint unsigned not null,
    followee_id  bigint unsigned not null,
    created_time datetime        not null,
    constraint user_follow_pk_2
        unique (followee_id, follower_id)
);

create index user_follow_followee_id_index
    on user_follow (followee_id);

create index user_follow_follower_id_index
    on user_follow (follower_id);

create table user_friend
(
    id         bigint unsigned not null
        primary key,
    user_id    bigint unsigned not null,
    friend_id  bigint unsigned not null,
    created_at datetime        not null
);

create table user_preferences
(
    id                bigint not null
        primary key,
    user_id           bigint not null,
    video_pref_vector blob   not null,
    constraint user_preferences_pk
        unique (user_id)
);

create table user_video_interaction
(
    id         bigint unsigned not null
        primary key,
    user_id    bigint unsigned not null,
    video_id   bigint unsigned not null,
    type       tinyint         not null comment '交互类型',
    created_at datetime        null
)
    comment '用户对视频交互操作记录（用于统计获取用户偏好标签）';

create index user_video_interaction_user_id_index
    on user_video_interaction (user_id);

create table video
(
    id          bigint unsigned          not null comment '视频id'
        primary key,
    creator_id  bigint unsigned          null comment '视频创作者id',
    url         varchar(256)             not null comment '视频url',
    cover_url   varchar(256)             null comment '视频封面url',
    description varchar(256)             null comment '文案',
    likes       int unsigned default '0' null comment '点赞数',
    comments    int unsigned default '0' null comment '评论数',
    favorites   int unsigned default '0' null comment '收藏数',
    status      tinyint      default 0   not null comment '状态',
    updated_at  datetime                 null comment '更新时间',
    created_at  datetime                 null comment '创建时间'
)
    charset = utf8mb3;

create index video_creator_id_status_id_index
    on video (creator_id asc, status asc, id desc);

create table video_tag
(
    id           bigint                             not null
        primary key,
    name         varchar(25)                        not null,
    created_time datetime default CURRENT_TIMESTAMP null,
    constraint video_tag_pk_2
        unique (name)
)
    charset = utf8mb3;

create table video_tag_mp
(
    video_id bigint unsigned not null,
    tag_id   bigint unsigned not null,
    primary key (video_id, tag_id)
)
    charset = utf8mb3;

create table video_upload_task
(
    id           bigint unsigned                            not null comment '上传id'
        primary key,
    upload_id    varchar(200)                               null comment '上传id（一般指分片上传id）',
    user_id      bigint unsigned                            not null comment '用户id',
    `key`        varchar(100)                               not null comment '对象key',
    total_chunks int unsigned                               not null comment '总分块数',
    upload_type  tinyint unsigned                           null comment '上传类型',
    status       tinyint unsigned default '0'               null comment '状态',
    expire_at    datetime                                   null comment '上传过期时间',
    updated_at   datetime         default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    created_at   datetime         default CURRENT_TIMESTAMP null comment '创建时间'
);

create index video_upload_task_upload_id_index
    on video_upload_task (upload_id);

create table video_user_collections
(
    id         bigint unsigned              not null comment '收藏夹id'
        primary key,
    user_id    bigint unsigned              not null comment '用户id',
    name       varchar(25)                  not null comment '收藏夹名',
    item_count int unsigned     default '0' not null comment '收藏视频项数',
    is_public  tinyint unsigned default '0' not null comment '是否公开',
    updated_at datetime                     null comment '更新时间',
    created_at datetime                     null comment '创建时间'
);

create index video_user_collections_user_id_id_index
    on video_user_collections (user_id asc, id desc);

create table video_user_collections_item
(
    id             bigint unsigned not null comment '收藏夹项id'
        primary key,
    collections_id bigint unsigned not null comment '所属收藏夹id',
    user_id        bigint unsigned not null comment '所属用户id',
    video_id       bigint unsigned not null comment '收藏视频id',
    created_at     datetime        null comment '创建时间'
);

create index video_user_collections_item_collections_id_index
    on video_user_collections_item (collections_id);

create index video_user_collections_item_user_id_video_id_index
    on video_user_collections_item (user_id, video_id);

create table video_user_comment
(
    id          bigint unsigned                        not null comment '评论id'
        primary key,
    video_id    bigint unsigned                        not null comment '关联视频id',
    content     text                                   not null comment '评论内容',
    user_id     bigint unsigned                        not null comment '评论者id',
    root_id     bigint unsigned                        not null comment '根评论id（根评论则为其本身）',
    parent_id   bigint unsigned                        null comment '直接父评论id',
    reply_count int unsigned default '0'               not null comment '回复数',
    like_count  int unsigned default '0'               not null comment '点赞数',
    updated_at  timestamp    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    created_at  timestamp    default CURRENT_TIMESTAMP null comment '创建时间'
)
    comment '视频评论表';

create index idx_user_id
    on video_user_comment (user_id);

create index video_user_comment_parent_id_video_id_id_index
    on video_user_comment (parent_id asc, video_id asc, id desc);

create index video_user_comment_root_id_id_index
    on video_user_comment (root_id, id);

create table video_user_like
(
    id       bigint                 not null
        primary key,
    user_id  bigint                 not null,
    video_id bigint                 not null,
    status   enum ('FRONT', 'BACK') not null,
    constraint video_id_user_id_pk
        unique (user_id, video_id)
);

create index video_user_like_pk_2
    on video_user_like (user_id asc, id desc);

