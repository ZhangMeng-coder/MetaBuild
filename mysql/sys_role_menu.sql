-- ========================================
-- 角色-菜单关联表（sys_role_menu）
-- 多对多关系：一个角色可访问多个菜单/按钮，一个菜单/按钮可被多个角色访问
-- 此表也控制按钮级别的权限（通过 sys_menu.perms 字段）
-- ========================================
CREATE TABLE `sys_role_menu` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `role_id` int NOT NULL COMMENT '角色ID',
    `menu_id` int NOT NULL COMMENT '菜单ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_menu` (`role_id`,`menu_id`),
    KEY `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色菜单关联表(含按钮权限)';
