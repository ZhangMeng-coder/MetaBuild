-- ========================================
-- 用户-角色关联表（sys_user_role）
-- 多对多关系：一个用户可拥有多个角色，一个角色可被多个用户拥有
-- ========================================
CREATE TABLE `sys_user_role` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` int NOT NULL COMMENT '用户ID',
    `role_id` int NOT NULL COMMENT '角色ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`,`role_id`),
    KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';
