package com.zm.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("api_timing_log")
public class ApiTimingLog {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String httpMethod;      // 请求方式
    private String url;             // 请求路径
    private String methodName;      // Controller方法名
    private Long costMs;            // 耗时(毫秒)
    private LocalDateTime createTime; // 创建时间
}
