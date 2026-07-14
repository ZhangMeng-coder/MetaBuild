-- ========================================
-- 接口耗时日志表（api_timing_log）
-- 记录所有 RestController 接口的请求耗时，用于接口性能监控
-- ========================================
CREATE TABLE `api_timing_log` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `http_method` VARCHAR(10)  NOT NULL DEFAULT ''       COMMENT '请求方式(GET/POST/PUT/DELETE)',
    `url`         VARCHAR(500) NOT NULL DEFAULT ''       COMMENT '请求路径',
    `method_name` VARCHAR(500) NOT NULL DEFAULT ''       COMMENT 'Controller方法名',
    `cost_ms`     BIGINT       NOT NULL DEFAULT 0       COMMENT '耗时(毫秒)',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_cost_ms` (`cost_ms`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='接口耗时日志表';
