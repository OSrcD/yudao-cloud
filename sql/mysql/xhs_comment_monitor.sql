-- ============================================================
-- 评论监控相关 DDL（已通过 MySQL MCP 执行，此文件仅供存档参考）
-- ============================================================

-- 1. 评论监控关键词配置表
CREATE TABLE IF NOT EXISTS `xhs_comment_monitor_keyword` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tenant_id` bigint NOT NULL DEFAULT 1 COMMENT '多租户编号',
  `keyword` varchar(255) NOT NULL COMMENT '监控关键词',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='小红书评论监控关键词配置';

-- 2. 评论监控推送去重日志表
CREATE TABLE IF NOT EXISTS `xhs_comment_monitor_notify_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tenant_id` bigint NOT NULL DEFAULT 1 COMMENT '多租户编号',
  `comment_id` varchar(255) NOT NULL COMMENT '评论 ID',
  `keyword` varchar(255) NOT NULL COMMENT '命中关键词',
  `notify_type` varchar(64) DEFAULT NULL COMMENT '推送类型: webhook/wecom/feishu',
  `notify_url` varchar(500) DEFAULT NULL COMMENT '推送地址快照',
  `notify_status` tinyint NOT NULL DEFAULT 0 COMMENT '0-成功 1-失败',
  `notify_resp` text DEFAULT NULL COMMENT '第三方响应',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_comment_keyword_tenant` (`comment_id`, `keyword`, `tenant_id`),
  KEY `idx_tenant_create_time` (`tenant_id`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='小红书评论监控推送去重日志';

-- 3. 评论监控推送配置表
CREATE TABLE IF NOT EXISTS `xhs_comment_monitor_notify_config` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint NOT NULL DEFAULT 1 COMMENT '多租户编号',
  `enabled` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否开启推送',
  `notify_type` varchar(64) DEFAULT 'webhook' COMMENT '推送类型: webhook/wecom/feishu',
  `notify_url` varchar(500) DEFAULT '' COMMENT 'Webhook 地址',
  `secret` varchar(255) DEFAULT '' COMMENT '签名密钥（预留）',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='小红书评论监控推送配置';

-- 4. 菜单（id=6070）及按钮权限（6071/6072/6073）
-- 已通过 MySQL MCP 插入，此处仅供参考
INSERT INTO system_menu(id, name, permission, type, sort, parent_id, path, icon, component, status, creator, create_time, updater, update_time)
VALUES (6070, '评论监控', '', 2, 13, 6000, 'xhs-comment-monitor', 'ep:bell', 'business/xhsCommentMonitor/index', 0, '1', NOW(), '1', NOW());

INSERT INTO system_menu(id, name, permission, type, sort, parent_id, path, icon, component, status, creator, create_time, updater, update_time)
VALUES
  (6071, '评论监控查询', 'business:xhs-note-comment:query', 3, 1, 6070, '', '', '', 0, '1', NOW(), '1', NOW()),
  (6072, '关键词管理', 'business:xhs-comment-monitor:keyword', 3, 2, 6070, '', '', '', 0, '1', NOW(), '1', NOW()),
  (6073, '推送配置', 'business:xhs-comment-monitor:notify', 3, 3, 6070, '', '', '', 0, '1', NOW(), '1', NOW());
