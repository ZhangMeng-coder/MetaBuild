-- ========================================
-- 元数据列表配置表（tb_metadata_list_config）
-- 配置元数据表在列表页中的列展示方式（排序、宽度、可见性等）
-- ========================================
CREATE TABLE `tb_metadata_list_config` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
    `table_id` int NOT NULL COMMENT '所属元数据表ID',
    `column_id` int NOT NULL COMMENT '关联的元数据列ID',
    `column_label` varchar(100) DEFAULT NULL COMMENT '列显示名称（覆盖默认）',
    `width` int DEFAULT NULL COMMENT '列宽（px）',
    `align` varchar(10) DEFAULT 'left' COMMENT '对齐方式(left/center/right)',
    `sortable` tinyint NOT NULL DEFAULT '0' COMMENT '是否可排序(0=否,1=是)',
    `visible` tinyint NOT NULL DEFAULT '1' COMMENT '是否可见(0=否,1=是)',
    `frozen` varchar(10) DEFAULT NULL COMMENT '冻结位置(left/right)',
    `is_search` tinyint NOT NULL DEFAULT '0' COMMENT '是否作为搜索条件(0=否,1=是)',
    `sort` int NOT NULL DEFAULT '0' COMMENT '排序号',
    `create_user` int DEFAULT NULL COMMENT '创建人',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_user` int DEFAULT NULL COMMENT '更新人',
    `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除(0=未删除,1=已删除)',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_table_column` (`table_id`, `column_id`),
    KEY `idx_table_id` (`table_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='元数据列表配置表';