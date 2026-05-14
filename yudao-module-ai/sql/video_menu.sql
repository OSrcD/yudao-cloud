CREATE TABLE `biz_ai_video` (
                                `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
                                `user_id` bigint NOT NULL COMMENT '用户编号',
                                `prompt` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '提示词',
                                `platform` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '平台',
                                `model_id` bigint NOT NULL COMMENT '模型编号',
                                `model` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模型',
                                `width` int DEFAULT NULL COMMENT '宽度',
                                `height` int DEFAULT NULL COMMENT '高度',
                                `duration` int DEFAULT NULL COMMENT '时长（秒）',
                                `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态',
                                `finish_time` datetime DEFAULT NULL COMMENT '完成时间',
                                `error_message` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '错误信息',
                                `video_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '视频地址',
                                `preview_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '预览图地址',
                                `options` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '配置选项',
                                `task_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '任务编号',
                                `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                                `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                                `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 视频表';


-- 1. 插入菜单：视频管理
INSERT INTO `system_menu` (
    `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `component`, `icon`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`
) VALUES (
             '视频管理', 'ai:video:query', 2, 10, 2760, 'video', 'ai/video/index', 'ep:video-camera', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0
         );

-- 2. 获取刚才插入的菜单 ID
SET @menu_id = LAST_INSERT_ID();

-- 3. 插入按钮权限：查询
INSERT INTO `system_menu` (
    `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `component`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`
) VALUES (
             '视频查询', 'ai:video:query', 3, 1, @menu_id, '', '', 0, '1', NOW(), '1', NOW(), 0
         );

-- 4. 插入按钮权限：删除
INSERT INTO `system_menu` (
    `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `component`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`
) VALUES (
             '视频删除', 'ai:video:delete', 3, 2, @menu_id, '', '', 0, '1', NOW(), '1', NOW(), 0
         );
