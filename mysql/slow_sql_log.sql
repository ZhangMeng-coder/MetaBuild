-- ========================================
-- 慢SQL日志表（slow_sql_log）
-- 记录执行耗时超过阈值（默认2秒）的慢SQL，用于数据库性能分析
-- ========================================
CREATE TABLE `slow_sql_log` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `sql_content` TEXT         NOT NULL                 COMMENT '慢SQL内容（可执行SQL）',
    `cost_ms`     BIGINT       NOT NULL DEFAULT 0       COMMENT '耗时(毫秒)',
    `method_name` VARCHAR(500) NOT NULL DEFAULT ''       COMMENT 'Mapper方法全限定名',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_cost_ms` (`cost_ms`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='慢SQL日志表';
