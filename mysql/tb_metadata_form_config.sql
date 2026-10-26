-- ========================================
-- 元数据表单配置表（tb_metadata_form_config）
-- 配置元数据表在新增/编辑表单中的字段展示方式（排序、布局、占位提示等）
-- ========================================
CREATE TABLE `tb_metadata_form_config` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
    `table_id` int NOT NULL COMMENT '所属元数据表ID',
    `column_id` int NOT NULL COMMENT '关联的元数据列ID',
    `field_label` varchar(100) DEFAULT NULL COMMENT '字段显示名称（覆盖默认）',
    `is_required` tinyint DEFAULT NULL COMMENT '是否必填（覆盖默认，null表示使用列默认）',
    `span` int NOT NULL DEFAULT '12' COMMENT '栅格宽度(1-24, 默认12即半行)',
    `placeholder` varchar(200) DEFAULT NULL COMMENT '占位提示文字',
    `default_value` varchar(500) DEFAULT NULL COMMENT '默认值',
    `sort` int NOT NULL DEFAULT '0' COMMENT '排序号',
    `create_user` int DEFAULT NULL COMMENT '创建人',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_user` int DEFAULT NULL COMMENT '更新人',
    `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除(0=未删除,1=已删除)',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_table_column` (`table_id`, `column_id`),
    KEY `idx_table_id` (`table_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='元数据表单配置表';