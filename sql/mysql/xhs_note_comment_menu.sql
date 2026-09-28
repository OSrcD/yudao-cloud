-- 菜单 SQL
INSERT INTO system_menu(
    id, name, permission, type, sort, parent_id,
    path, icon, component, status, creator, create_time, updater, update_time
)
VALUES (
    6050, '评论采集', '', 2, 11, 6000,
    'xhs-note-comment', 'ep:chat-dot-round', 'business/xhsNoteComment/index', 0, '1', NOW(), '1', NOW()
);

-- 按钮 SQL
INSERT INTO system_menu(
    id, name, permission, type, sort, parent_id,
    path, icon, component, status, creator, create_time, updater, update_time
)
VALUES (
    6051, '评论查询', 'business:xhs-note-comment:query', 3, 1, 6050,
    '', '', '', 0, '1', NOW(), '1', NOW()
);

INSERT INTO system_menu(
    id, name, permission, type, sort, parent_id,
    path, icon, component, status, creator, create_time, updater, update_time
)
VALUES (
    6052, '评论创建', 'business:xhs-note-comment:create', 3, 2, 6050,
    '', '', '', 0, '1', NOW(), '1', NOW()
);

INSERT INTO system_menu(
    id, name, permission, type, sort, parent_id,
    path, icon, component, status, creator, create_time, updater, update_time
)
VALUES (
    6053, '评论更新', 'business:xhs-note-comment:update', 3, 3, 6050,
    '', '', '', 0, '1', NOW(), '1', NOW()
);

INSERT INTO system_menu(
    id, name, permission, type, sort, parent_id,
    path, icon, component, status, creator, create_time, updater, update_time
)
VALUES (
    6054, '评论删除', 'business:xhs-note-comment:delete', 3, 4, 6050,
    '', '', '', 0, '1', NOW(), '1', NOW()
);
