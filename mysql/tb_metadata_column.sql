-- ========================================
-- 元数据列定义表（tb_metadata_column）
-- 存储元数据表的列定义信息，包括展示类型、字典项等
-- ========================================
CREATE TABLE `tb_metadata_column` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
    `table_id` int NOT NULL COMMENT '所属元数据表ID',
    `column_name` varchar(100) NOT NULL COMMENT '列名（对应数据库实际列名）',
    `column_comment` varchar(500) DEFAULT NULL COMMENT '列备注/显示名称',
    `data_type` varchar(50) NOT NULL COMMENT '数据类型（如 varchar, int, datetime）',
    `is_required` tinyint NOT NULL DEFAULT '0' COMMENT '是否必填(0=否,1=是)',
    `is_pk` tinyint NOT NULL DEFAULT '0' COMMENT '是否主键(0=否,1=是)',
    `show_type` varchar(50) NOT NULL DEFAULT 'text' COMMENT '展示类型(text/textarea/select/radio/checkbox/date/time/datetime)',
    `dict_type` varchar(100) DEFAULT NULL COMMENT '字典类型（关联 sys_dict_type.dict_type）',
    `dict_sql` varchar(1000) DEFAULT NULL COMMENT 'SQL 动态字典项（需返回 id 和 name 字段）',
    `sort` int NOT NULL DEFAULT '0' COMMENT '排序号',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态(0=停用,1=启用)',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    `create_user` int DEFAULT NULL COMMENT '创建人',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_user` int DEFAULT NULL COMMENT '更新人',
    `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除(0=未删除,1=已删除)',
    PRIMARY KEY (`id`),
    KEY `idx_table_id` (`table_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='元数据列定义表';