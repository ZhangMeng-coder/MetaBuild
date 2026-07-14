package com.zm.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户操作日志实体类
 * <p>对应数据库表 sys_user_log，记录用户的操作请求和响应结果。</p>
 */
@Data
@TableName("sys_user_log")
public class SysUserLog {

    /** 日志ID，主键，自增 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 操作名称（@LogInfo 注解的 value 值） */
    private String operationName;

    /** 操作时间 */
    private LocalDateTime operationTime;

    /** 请求方式（GET/POST/PUT/DELETE） */
    private String httpMethod;

    /** 请求URL */
    private String url;

    /** 请求参数（JSON格式） */
    private String params;

    /** 响应码 */
    private Integer resultCode;

    /** 响应信息 */
    private String resultMsg;

    /** 创建时间 */
    private LocalDateTime createTime;
}
