-- ========================================
-- 系统初始化数据
-- 用于首次部署时填充基础数据
-- 包含：默认管理员用户、角色、菜单及关联关系
-- ========================================

-- 初始用户 (密码: admin123, BCrypt加密)
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', 1);

-- 初始角色
INSERT INTO `sys_role` (`role_name`, `role_code`, `description`, `sort`) VALUES
('超级管理员', 'ROLE_ADMIN', '拥有系统所有权限', 1),
('普通用户', 'ROLE_USER', '普通用户角色', 2);

-- 初始菜单（含按钮级权限）
-- 目录/菜单: id 固定便于关联，按钮: 挂在对应菜单下
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `path`, `component`, `perms`, `icon`, `sort`) VALUES
-- 目录
(1, 0, '系统管理', 0, '/system', NULL, NULL, 'Setting', 1),
-- 菜单（一级菜单，挂在系统管理下）
(2, 1, '用户管理', 1, '/system/user', 'views/system/user/index.vue', 'system:user:list', 'User', 1),
(3, 1, '角色管理', 1, '/system/role', 'views/system/role/index.vue', 'system:role:list', 'UserFilled', 2),
(4, 1, '菜单管理', 1, '/system/menu', 'views/system/menu/index.vue', 'system:menu:list', 'Menu', 3),
-- 按钮（挂在用户管理下）
(5, 2, '新增用户', 2, NULL, NULL, 'system:user:add', NULL, 1),
(6, 2, '编辑用户', 2, NULL, NULL, 'system:user:edit', NULL, 2),
(7, 2, '删除用户', 2, NULL, NULL, 'system:user:delete', NULL, 3),
(8, 2, '分配角色', 2, NULL, NULL, 'system:user:role:assign', NULL, 4),
-- 按钮（挂在角色管理下）
(9, 3, '新增角色', 2, NULL, NULL, 'system:role:add', NULL, 1),
(10, 3, '编辑角色', 2, NULL, NULL, 'system:role:edit', NULL, 2),
(11, 3, '删除角色', 2, NULL, NULL, 'system:role:delete', NULL, 3),
(12, 3, '分配菜单', 2, NULL, NULL, 'system:role:menu:assign', NULL, 4),
-- 菜单（挂在系统管理下）
(16, 1, '操作日志', 1, '/system/log', 'views/system/log/index.vue', 'system:log:list', 'Document', 4),
-- 目录：功能配置
(32, 0, '功能配置', 0, '/func', NULL, NULL, 'Setting', 2),
-- 菜单：元数据管理（挂在功能配置下）
(27, 32, '元数据管理', 1, '/metadata/table', 'views/metadata/table/index.vue', 'server:meta:table:list', 'Collection', 1),
-- 按钮（挂在菜单管理下）
(13, 4, '新增菜单', 2, NULL, NULL, 'system:menu:add', NULL, 1),
(14, 4, '编辑菜单', 2, NULL, NULL, 'system:menu:edit', NULL, 2),
(15, 4, '删除菜单', 2, NULL, NULL, 'system:menu:delete', NULL, 3),
-- 目录：监控管理
(17, 0, '监控管理', 0, '/monitor', NULL, NULL, 'Monitor', 5),
-- 菜单（挂在监控管理下）
(18, 17, '慢SQL监控', 1, '/system/monitor/slow-sql', 'views/system/monitor/slow-sql.vue', 'system:monitor:slow-sql', 'DataBoard', 1),
(19, 17, '接口耗时监控', 1, '/system/monitor/api-timing', 'views/system/monitor/api-timing.vue', 'system:monitor:api-timing', 'Timer', 2),
-- 目录：字典管理（挂在系统管理下）
(20, 1, '字典管理', 1, '/system/dict/type', 'views/system/dict/type/index.vue', 'system:dict:list', 'Notebook', 5),
-- 按钮（挂在字典管理下）
(21, 20, '新增字典', 2, NULL, NULL, 'system:dict:add', NULL, 1),
(22, 20, '编辑字典', 2, NULL, NULL, 'system:dict:edit', NULL, 2),
(23, 20, '删除字典', 2, NULL, NULL, 'system:dict:delete', NULL, 3),
(24, 20, '新增字典数据', 2, NULL, NULL, 'system:dict:data:add', NULL, 4),
(25, 20, '编辑字典数据', 2, NULL, NULL, 'system:dict:data:edit', NULL, 5),
(26, 20, '删除字典数据', 2, NULL, NULL, 'system:dict:data:delete', NULL, 6),
(31, 20, '字典数据查询', 2, NULL, NULL, 'system:dict:data:list', NULL, 7),
-- 按钮（挂在元数据管理下）
(28, 27, '新增元数据表', 2, NULL, NULL, 'server:meta:table:add', NULL, 1),
(29, 27, '编辑元数据表', 2, NULL, NULL, 'server:meta:table:edit', NULL, 2),
(30, 27, '删除元数据表', 2, NULL, NULL, 'server:meta:table:delete', NULL, 3),
(33, 27, '导出元数据', 2, NULL, NULL, 'server:meta:table:export', NULL, 4),
(34, 27, '导入元数据', 2, NULL, NULL, 'server:meta:table:import', NULL, 5);

-- 用户-角色关联
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (1, 1);

-- 角色-菜单关联 (超级管理员拥有所有菜单和按钮)
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 16),
(1, 5), (1, 6), (1, 7), (1, 8),
(1, 9), (1, 10), (1, 11), (1, 12),
(1, 13), (1, 14), (1, 15),
(1, 17), (1, 18), (1, 19),
(1, 20), (1, 21), (1, 22), (1, 23),
(1, 24), (1, 25), (1, 26),
(1, 31),
(1, 32),
(1, 27), (1, 28), (1, 29), (1, 30), (1, 33), (1, 34);
