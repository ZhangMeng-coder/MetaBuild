-- ========================================
-- 用户操作日志表（sys_user_log）
-- 记录用户的操作请求和响应结果，用于审计和问题排查
-- ========================================
CREATE TABLE `sys_user_log` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `operation_name` varchar(50) DEFAULT NULL COMMENT '操作名称（如：新增用户、删除角色）',
    `operation_time` datetime NOT NULL COMMENT '操作时间',
    `http_method` varchar(10) NOT NULL COMMENT '请求方式(GET/POST/PUT/DELETE)',
    `url` varchar(255) NOT NULL COMMENT '请求URL',
    `params` text COMMENT '请求参数(JSON格式)',
    `result_code` int DEFAULT NULL COMMENT '响应码',
    `result_msg` varchar(255) DEFAULT NULL COMMENT '响应信息',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_operation_time` (`operation_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户操作日志表';