-- ----------------------------
-- 小红书评论记录与去重分析表 (本地数据库初始化脚本)
-- 用于 App 端进作品页点击分享链接查重、去重评论、评论成功自动入库以及后续吞评/折叠数据分析
-- ----------------------------

CREATE TABLE IF NOT EXISTS `biz_xhs_comment_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `app_account_id` bigint NOT NULL COMMENT '当前App登录账号ID',
  `app_mobile` varchar(20) DEFAULT '' COMMENT '当前App登录手机号',
  `xhs_user_id` varchar(64) DEFAULT '' COMMENT '评论人小红书账号ID',
  `xhs_user_name` varchar(128) DEFAULT '' COMMENT '评论人小红书名称/昵称',
  `share_link` varchar(1000) NOT NULL COMMENT '小红书作品分享链接(纯URL)',
  `share_content` text DEFAULT NULL COMMENT '完整分享文本内容',
  `note_id` varchar(64) DEFAULT '' COMMENT '小红书笔记ID(从链接解析，用于高精度去重)',
  `note_title` varchar(500) DEFAULT '' COMMENT '小红书作品标题',
  `comment_content` text NOT NULL COMMENT '发表的评论内容',
  `comment_status` tinyint DEFAULT 0 COMMENT '评论状态: 0未知, 1正常, 2被吞/消失, 3被折叠, 4其他异常',
  `check_status` tinyint DEFAULT 0 COMMENT '检测状态: 0未检测, 1已检测, 2检测失败',
  `check_time` datetime DEFAULT NULL COMMENT '最近一次检测状态时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注说明',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除(0未删除 1已删除)',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_app_account_note` (`app_account_id`, `note_id`, `deleted`),
  KEY `idx_app_account_link` (`app_account_id`, `share_link`(255), `deleted`),
  KEY `idx_app_account_title` (`app_account_id`, `note_title`(191), `deleted`),
  KEY `idx_app_mobile` (`app_mobile`),
  KEY `idx_xhs_user_id` (`xhs_user_id`),
  KEY `idx_comment_status` (`comment_status`),
  KEY `idx_check_status` (`check_status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='小红书评论记录与去重分析表';

-- 菜单配置 (可选，用于本地管理后台查看)
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `updater`)
VALUES (6080, '评论去重分析', 'business:xhs-comment-record:query', 2, 14, 6000, 'xhs-comment-record', 'ep:data-analysis', 'business/xhsCommentRecord/index', 0, b'1', b'1', b'1', '1', '1')
ON DUPLICATE KEY UPDATE `name`=VALUES(`name`), `path`=VALUES(`path`), `component`=VALUES(`component`);

-- 按钮权限配置 (查询/创建/更新/删除)
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `updater`) VALUES
(6081, '评论记录查询', 'business:xhs-comment-record:query', 3, 1, 6080, '', '', '', 0, b'1', b'1', b'1', '1', '1'),
(6082, '评论记录创建', 'business:xhs-comment-record:create', 3, 2, 6080, '', '', '', 0, b'1', b'1', b'1', '1', '1'),
(6083, '评论记录更新', 'business:xhs-comment-record:update', 3, 3, 6080, '', '', '', 0, b'1', b'1', b'1', '1', '1'),
(6084, '评论记录删除', 'business:xhs-comment-record:delete', 3, 4, 6080, '', '', '', 0, b'1', b'1', b'1', '1', '1')
ON DUPLICATE KEY UPDATE `name`=VALUES(`name`), `permission`=VALUES(`permission`);

