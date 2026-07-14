package com.zm.common.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 * <p>标注在 Controller 方法上，记录用户的请求和响应日志。
 * 通过 AOP 切面自动采集：操作时间、请求方式、URL、参数、响应结果。</p>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogInfo {

    /** 操作描述，如"新增用户"、"删除角色"等 */
    String value() default "";
}
