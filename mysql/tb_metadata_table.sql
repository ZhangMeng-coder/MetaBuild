-- ========================================
-- 元数据表定义表（tb_metadata_table）
-- 存储元数据表的定义信息，表名以 tb_ 开头
-- ========================================
CREATE TABLE `tb_metadata_table` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
    `table_name` varchar(100) NOT NULL COMMENT '表名（对应数据库实际表名）',
    `table_comment` varchar(500) DEFAULT NULL COMMENT '表备注/显示名称',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态(0=停用,1=启用)',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    `create_user` int DEFAULT NULL COMMENT '创建人',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_user` int DEFAULT NULL COMMENT '更新人',
    `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除(0=未删除,1=已删除)',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_table_name` (`table_name`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='元数据表定义表';