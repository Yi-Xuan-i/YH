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
    cart_item_id bigint auto_increment
        primary key,
    user_id      bigint                                 not null comment '用户ID',
    product_id   bigint                                 not null comment '商品ID',
    sku_id       bigint                                 not null comment 'SKU ID',
    quantity     int unsigned default '1'               not null comment '数量',
    price        decimal(10, 2)                         not null comment '加入购物车时的价格快照',
    selected_sku json                                   null comment '选中规格（JSON冗余，避免SKU失效时无法展示）',
    created_time datetime     default CURRENT_TIMESTAMP null comment '加入时间'
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
    id                 bigint unsigned               not null
        primary key,
    user1_id           bigint unsigned               not null,
    user2_id           bigint unsigned               not null,
    user1_unread_count smallint unsigned default '0' not null,
    user2_unread_count smallint unsigned             not null,
    updated_time       datetime                      not null,
    created_time       datetime                      not null,
    constraint chat_conversation_pk
        unique (user1_id, user2_id)
);

create index chat_conversation_user1_id_updated_time_index
    on chat_conversation (user1_id asc, updated_time desc);

create index chat_conversation_user2_id_updated_time_index
    on chat_conversation (user2_id asc, updated_time desc);

create table chat_message
(
    id              bigint unsigned        not null
        primary key,
    conversation_id varchar(40)            not null,
    sender_id       bigint unsigned        not null,
    content         text                   not null,
    content_type    enum ('TEXT', 'IMAGE') not null,
    created_time    datetime               not null
);

create index chat_message_conversation_id_id_index
    on chat_message (conversation_id asc, id desc);

create table conversation
(
    conversation_id bigint unsigned auto_increment comment '对话主键'
        primary key,
    user_id         bigint                  not null comment '用户标识',
    title           varchar(255) default '' null comment '对话标题',
    created_at      datetime                null comment '创建时间',
    updated_at      datetime                null comment '最后更新时间'
)
    charset = utf8mb3;

create index idx_user
    on conversation (user_id asc, conversation_id desc);

create table conversation_message
(
    message_id      bigint unsigned auto_increment comment '消息主键'
        primary key,
    conversation_id bigint unsigned                                                     not null comment '关联对话ID',
    role            enum ('user', 'assistant', 'system') charset utf8mb3 default 'user' not null comment '消息角色',
    content         longtext                                                            not null comment '消息内容'
);

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
    room_id   bigint      not null
        primary key,
    anchor_id bigint      not null,
    client_id varchar(25) null comment '推流时携带的client_id（用于推流结束时鉴权）'
)
    charset = utf8mb3;

create table live_product
(
    id        bigint unsigned not null
        primary key,
    room_id   bigint unsigned not null,
    name      varchar(256)    not null,
    price     decimal(10, 2)  not null,
    stock     int unsigned    not null,
    image_url varchar(256)    null
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

create table `order`
(
    order_id         bigint unsigned                                                                                                not null comment '订单号'
        primary key,
    user_id          bigint unsigned                                                                                                not null comment '用户ID',
    merchant_id      bigint unsigned                                                                                                not null comment '商家ID',
    payment_amount   decimal(10, 2)                                                                                                 not null comment '实付金额',
    order_status     enum ('UNPAID', 'PAID', 'SHIPPED', 'COMPLETED', 'CANCELLED', 'REFUND_PROCESSING', 'REFUNDED') default 'UNPAID' null,
    delivery_address json                                                                                                           not null comment '收货地址（JSON结构存储）',
    created_time     datetime                                                                                                       not null
)
    comment '订单主表' charset = utf8mb3;

create table order_item
(
    order_item_id bigint         not null
        primary key,
    order_id      bigint         not null,
    product_id    bigint         not null,
    sku_id        bigint         not null comment 'SKU ID',
    sku           json           not null comment '快照',
    product_name  varchar(256)   null,
    quantity      int unsigned   not null comment '购买数量',
    price         decimal(10, 2) not null comment '购买时单价'
)
    comment '订单商品明细' charset = utf8mb3;

create table product
(
    product_id   bigint                                                                                     not null comment '商品ID'
        primary key,
    merchant_id  bigint                                                                                     not null comment '商家ID',
    category_id  int                                                                                        null comment '分类ID',
    title        varchar(255)                                                                               not null comment '商品标题',
    cover_url    varchar(255)                                                                               not null comment '封面路径',
    description  text                                                                                       null comment '商品描述',
    price        decimal(10, 2)                                                                             not null comment '基础价格',
    stock        int unsigned                                                     default '0'               null comment '总库存',
    status       enum ('PENDING', 'APPROVED', 'REJECTED', 'ON_SALE', 'OFF_SHELF') default 'PENDING'         null comment '状态',
    is_hot       tinyint(1)                                                       default 0                 null comment '是否热门推荐',
    sales_volume int unsigned                                                     default '0'               null comment '销量',
    rating       float                                                            default 0                 null comment '平均评分',
    created_at   datetime                                                         default CURRENT_TIMESTAMP null,
    updated_at   datetime                                                         default CURRENT_TIMESTAMP null
)
    comment '商品主表' charset = utf8mb3;

create table product_carousel
(
    id         bigint       not null
        primary key,
    url        varchar(256) not null,
    product_id bigint       not null
)
    charset = utf8mb3;

create index product_carousel_product_id_index
    on product_carousel (product_id);

create table product_sku
(
    sku_id     bigint                      not null
        primary key,
    product_id bigint                      not null,
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

create table student
(
    id    bigint unsigned auto_increment
        primary key,
    score double(4, 1) not null,
    age   int          not null
);

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
    id               bigint unsigned                    not null
        primary key,
    phone_number     varchar(20)                        not null,
    name             varchar(20)                        not null,
    encoded_password char(60)                           not null,
    avatar_url       varchar(256)                       null,
    bio              varchar(256)                       null,
    created_time     datetime default CURRENT_TIMESTAMP null,
    constraint uer_pk_2
        unique (phone_number)
)
    charset = utf8mb3;

create table user_address
(
    address_id     bigint unsigned auto_increment comment '地址ID'
        primary key,
    user_id        bigint unsigned      not null comment '用户ID',
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
    id           bigint unsigned not null
        primary key,
    user_id      bigint unsigned not null,
    friend_id    bigint unsigned not null,
    created_time datetime        not null
);

create table user_new_friend_message
(
    id        bigint unsigned not null
        primary key,
    user_id   bigint unsigned not null,
    friend_id bigint unsigned not null
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

create table video
(
    id           bigint unsigned                                                                    not null
        primary key,
    creator_id   bigint unsigned                                                                    null,
    url          varchar(256)                                                                       not null,
    cover_url    varchar(256)                                                                       not null,
    description  varchar(256)                                                                       not null,
    likes        int unsigned                                             default '0'               null,
    comments     int unsigned                                             default '0'               null,
    favorites    int unsigned                                             default '0'               null,
    status       enum ('UPLOADED', 'PROCESSING', 'REJECTED', 'PUBLISHED') default 'UPLOADED'        not null,
    created_time datetime                                                 default CURRENT_TIMESTAMP null
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
    video_id bigint not null,
    tag_id   bigint not null,
    primary key (video_id, tag_id)
)
    charset = utf8mb3;

create table video_upload_task
(
    id           bigint unsigned                            not null comment '上传id'
        primary key,
    user_id      bigint unsigned                            not null,
    file_size    bigint unsigned                            not null comment '文件总大小(字节)',
    total_chunks int unsigned                               not null comment '总分块数',
    chunk_bitmap blob                                       not null comment '分块位图',
    status       tinyint unsigned default '0'               null comment '0=上传中,1=已完成,2=已过期',
    created_at   datetime         default CURRENT_TIMESTAMP null,
    updated_at   datetime         default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
);

create table video_user_collections
(
    id         bigint unsigned              not null
        primary key,
    user_id    bigint unsigned              not null,
    name       varchar(25)                  not null,
    item_count int unsigned     default '0' not null,
    is_public  tinyint unsigned default '0' not null comment '0-私密、1-公开',
    update_at  datetime                     not null,
    created_at datetime                     not null
);

create index video_user_collections_user_id_id_index
    on video_user_collections (user_id asc, id desc);

create table video_user_collections_item
(
    id             bigint unsigned not null
        primary key,
    collections_id bigint unsigned not null,
    video_id       bigint unsigned not null,
    created_at     datetime        not null
);

create index video_user_collections_item_collections_id_index
    on video_user_collections_item (collections_id);

create table video_user_comment
(
    id          bigint unsigned                        not null comment '评论唯一ID'
        primary key,
    video_id    bigint unsigned                        not null comment '关联视频ID',
    content     text                                   not null comment '评论内容',
    user_id     bigint unsigned                        not null comment '评论者用户ID',
    root_id     bigint unsigned                        not null comment '根评论ID（根评论则为其本身）',
    parent_id   bigint unsigned                        null comment '直接父评论ID',
    reply_count int unsigned default '0'               not null,
    like_count  int unsigned default '0'               not null comment '点赞数',
    created_at  timestamp    default CURRENT_TIMESTAMP not null comment '创建时间',
    updated_at  timestamp    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '视频评论表';

create index idx_user_id
    on video_user_comment (user_id);

create index video_user_comment_parent_id_video_id_id_index
    on video_user_comment (parent_id asc, video_id asc, id desc);

create index video_user_comment_root_id_id_index
    on video_user_comment (root_id, id);

create table video_user_favorite
(
    id       bigint                 not null
        primary key,
    user_id  bigint                 not null,
    video_id bigint                 not null,
    status   enum ('FRONT', 'BACK') not null,
    constraint video_user_favorite_pk_2
        unique (video_id, user_id)
);

create index video_user_favorite_user_id_id_index
    on video_user_favorite (user_id asc, id desc);

create table video_user_like
(
    id       bigint                 not null
        primary key,
    user_id  bigint                 not null,
    video_id bigint                 not null,
    status   enum ('FRONT', 'BACK') not null,
    constraint video_user_like_pk_2
        unique (user_id asc, id desc)
);

create index video_user_like_video_id_user_id_index
    on video_user_like (video_id, user_id);