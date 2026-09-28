-- ============================================================
-- 笔记监控相关变更
-- ============================================================

-- 1. 笔记采集表新增监控字段
ALTER TABLE `xhs_note_collect`
  ADD COLUMN `is_monitored` tinyint(1) DEFAULT 0 COMMENT '是否监控中：0-否，1-是' AFTER `last_comment_collect_time`;

-- 2. 菜单 SQL：笔记监控（父菜单 parent_id=6000，与笔记采集、评论同级）
INSERT INTO system_menu(
    id, name, permission, type, sort, parent_id,
    path, icon, component, status, creator, create_time, updater, update_time
)
VALUES (
    6060, '笔记监控', '', 2, 12, 6000,
    'xhs-note-monitor', 'ep:view', 'business/xhsNoteMonitor/index', 0, '1', NOW(), '1', NOW()
);

-- 3. 按钮 SQL
INSERT INTO system_menu(
    id, name, permission, type, sort, parent_id,
    path, icon, component, status, creator, create_time, updater, update_time
)
VALUES (
    6061, '笔记监控查询', 'business:xhs-note-collect:query', 3, 1, 6060,
    '', '', '', 0, '1', NOW(), '1', NOW()
);

INSERT INTO system_menu(
    id, name, permission, type, sort, parent_id,
    path, icon, component, status, creator, create_time, updater, update_time
)
VALUES (
    6062, '笔记监控更新', 'business:xhs-note-collect:update', 3, 2, 6060,
    '', '', '', 0, '1', NOW(), '1', NOW()
);
