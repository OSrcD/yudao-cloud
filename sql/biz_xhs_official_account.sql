-- ----------------------------
-- 小红书不评论官方号名单表 (数据库初始化脚本)
-- 用于 App 截流遇到企业号/官方号自动跳过不评论，以及管理后台统一 CRUD 管理与批量导入
-- 支持按照 App 用户的手机号及用户编号 (user_id) 隔离与管理
-- ----------------------------

CREATE TABLE IF NOT EXISTS `biz_xhs_official_account` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint DEFAULT NULL COMMENT 'App用户编号 (关联 member_user.id)',
  `mobile` varchar(20) DEFAULT '' COMMENT 'App用户手机号码/账号',
  `account_id` varchar(100) NOT NULL COMMENT '作者小红书号',
  `nickname` varchar(100) DEFAULT '' COMMENT '作者昵称',
  `remark` varchar(255) DEFAULT '' COMMENT '备注说明',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态: 0开启过滤(生效), 1关闭过滤(禁用)',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除(0未删除 1已删除)',
  `tenant_id` bigint NOT NULL DEFAULT '1' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_user_account` (`user_id`, `account_id`, `deleted`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_mobile` (`mobile`),
  KEY `idx_account_id` (`account_id`, `deleted`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='小红书不评论官方号名单表';

-- 兼容已建表环境平滑增加用户编号与手机号字段
-- ALTER TABLE `biz_xhs_official_account` ADD COLUMN IF NOT EXISTS `user_id` bigint DEFAULT NULL COMMENT 'App用户编号 (关联 member_user.id)' AFTER `id`;
-- ALTER TABLE `biz_xhs_official_account` ADD COLUMN IF NOT EXISTS `mobile` varchar(20) DEFAULT '' COMMENT 'App用户手机号码/账号' AFTER `user_id`;

-- 菜单配置 (父菜单 parent_id=6000 小红书业务)
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `updater`)
VALUES (6090, '官方号名单', 'business:xhs-official-account:query', 2, 15, 6000, 'xhs-official-account', 'ep:shield-check', 'business/xhsOfficialAccount/index', 0, b'1', b'1', b'1', '1', '1')
ON DUPLICATE KEY UPDATE `name`=VALUES(`name`), `path`=VALUES(`path`), `component`=VALUES(`component`);

-- 按钮权限配置 (查询/创建/更新/删除/导出)
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `updater`) VALUES
(6091, '官方号查询', 'business:xhs-official-account:query', 3, 1, 6090, '', '', '', 0, b'1', b'1', b'1', '1', '1'),
(6092, '官方号创建', 'business:xhs-official-account:create', 3, 2, 6090, '', '', '', 0, b'1', b'1', b'1', '1', '1'),
(6093, '官方号更新', 'business:xhs-official-account:update', 3, 3, 6090, '', '', '', 0, b'1', b'1', b'1', '1', '1'),
(6094, '官方号删除', 'business:xhs-official-account:delete', 3, 4, 6090, '', '', '', 0, b'1', b'1', b'1', '1', '1'),
(6095, '官方号导出', 'business:xhs-official-account:export', 3, 5, 6090, '', '', '', 0, b'1', b'1', b'1', '1', '1')
ON DUPLICATE KEY UPDATE `name`=VALUES(`name`), `permission`=VALUES(`permission`);
