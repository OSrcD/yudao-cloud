-- VIP 管理后台菜单与权限（挂在会员中心 parent_id=2262）
-- 执行后需给角色重新分配菜单权限，或给超管刷新

INSERT INTO system_menu(
  id, name, permission, type, sort, parent_id, path, icon, component, component_name, status,
  visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted
) VALUES
(6100, 'VIP套餐', '', 2, 12, 2262, 'vip-package', 'ep:ticket', 'member/vip/package/index', 'MemberVipPackage', 0,
 1, 1, 1, '1', NOW(), '1', NOW(), b'0'),
(6101, 'VIP套餐查询', 'member:vip-package:query', 3, 1, 6100, '', '', '', '', 0,
 1, 1, 1, '1', NOW(), '1', NOW(), b'0'),
(6102, 'VIP套餐创建', 'member:vip-package:create', 3, 2, 6100, '', '', '', '', 0,
 1, 1, 1, '1', NOW(), '1', NOW(), b'0'),
(6103, 'VIP套餐更新', 'member:vip-package:update', 3, 3, 6100, '', '', '', '', 0,
 1, 1, 1, '1', NOW(), '1', NOW(), b'0'),
(6104, 'VIP套餐删除', 'member:vip-package:delete', 3, 4, 6100, '', '', '', '', 0,
 1, 1, 1, '1', NOW(), '1', NOW(), b'0'),
(6110, 'VIP订单', '', 2, 13, 2262, 'vip-order', 'ep:document', 'member/vip/order/index', 'MemberVipOrder', 0,
 1, 1, 1, '1', NOW(), '1', NOW(), b'0'),
(6111, 'VIP订单查询', 'member:vip-order:query', 3, 1, 6110, '', '', '', '', 0,
 1, 1, 1, '1', NOW(), '1', NOW(), b'0'),
(6120, '用户VIP修改', 'member:user:update-vip', 3, 7, 2317, '', '', '', '', 0,
 1, 1, 1, '1', NOW(), '1', NOW(), b'0');

-- 给超级管理员角色(通常 id=1)授权
INSERT INTO system_role_menu (role_id, menu_id, creator, create_time, updater, update_time, deleted, tenant_id)
VALUES
(1, 6100, '1', NOW(), '1', NOW(), b'0', 1),
(1, 6101, '1', NOW(), '1', NOW(), b'0', 1),
(1, 6102, '1', NOW(), '1', NOW(), b'0', 1),
(1, 6103, '1', NOW(), '1', NOW(), b'0', 1),
(1, 6104, '1', NOW(), '1', NOW(), b'0', 1),
(1, 6110, '1', NOW(), '1', NOW(), b'0', 1),
(1, 6111, '1', NOW(), '1', NOW(), b'0', 1),
(1, 6120, '1', NOW(), '1', NOW(), b'0', 1);
