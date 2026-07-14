package com.zm.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("slow_sql_log")
public class SlowSqlLog {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String sqlContent;      // 慢SQL内容（可执行SQL）
    private Long costMs;            // 耗时(毫秒)
    private String methodName;      // Mapper方法全限定名
    private LocalDateTime createTime; // 创建时间
}
