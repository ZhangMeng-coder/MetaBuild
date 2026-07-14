-- ========================================
-- 字典类型表（sys_dict_type）
-- 存储字典类型的定义，如：用户状态、性别等
-- ========================================
CREATE TABLE `sys_dict_type` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '字典主键',
    `dict_name` varchar(100) NOT NULL COMMENT '字典名称',
    `dict_type` varchar(100) NOT NULL COMMENT '字典类型',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态(0=停用,1=启用)',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
    `create_user` int DEFAULT NULL COMMENT '创建人',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_user` int DEFAULT NULL COMMENT '更新人',
    `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除(0=未删除,1=已删除)',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dict_type` (`dict_type`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典类型表';
