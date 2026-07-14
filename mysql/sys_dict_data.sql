-- ========================================
-- 字典数据表（sys_dict_data）
-- 存储字典的具体键值对数据，通过 dict_type 关联字典类型
-- ========================================
CREATE TABLE `sys_dict_data` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '字典编码',
    `dict_type` varchar(100) NOT NULL COMMENT '字典类型',
    `dict_label` varchar(100) NOT NULL COMMENT '字典标签',
    `dict_value` varchar(100) NOT NULL COMMENT '字典键值',
    `dict_sort` int NOT NULL DEFAULT '0' COMMENT '字典排序',
    `css_class` varchar(100) DEFAULT NULL COMMENT '样式属性（如：primary, success, info, warning, danger）',
    `list_class` varchar(100) DEFAULT NULL COMMENT '表格回显样式',
    `is_default` tinyint NOT NULL DEFAULT '0' COMMENT '是否默认(0=否,1=是)',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态(0=停用,1=启用)',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    `create_user` int DEFAULT NULL COMMENT '创建人',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_user` int DEFAULT NULL COMMENT '更新人',
    `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除(0=未删除,1=已删除)',
    PRIMARY KEY (`id`),
    KEY `idx_dict_type` (`dict_type`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典数据表';
