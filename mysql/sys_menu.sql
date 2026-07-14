-- ========================================
-- 系统菜单表（sys_menu）
-- 存储菜单/目录/按钮的定义，支持三级树形结构
-- 权限标识（perms）用于接口和按钮级别的权限控制
-- ========================================
CREATE TABLE `sys_menu` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
    `parent_id` int DEFAULT '0' COMMENT '父菜单ID',
    `menu_name` varchar(50) NOT NULL COMMENT '菜单名称',
    `menu_type` tinyint NOT NULL DEFAULT 0 COMMENT '菜单类型(0=目录,1=菜单,2=按钮)',
    `path` varchar(255) DEFAULT NULL COMMENT '路由路径',
    `component` varchar(255) DEFAULT NULL COMMENT '组件路径',
    `perms` varchar(100) DEFAULT NULL COMMENT '权限标识(如 system:user:list)',
    `icon` varchar(50) DEFAULT NULL COMMENT '菜单图标',
    `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态(0=禁用,1=启用)',
    `create_user` int DEFAULT NULL COMMENT '创建人',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_user` int DEFAULT NULL COMMENT '更新人',
    `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除(0=未删除,1=已删除)',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统菜单表';
