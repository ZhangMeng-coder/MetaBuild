package com.zm.common.aspect;

import com.alibaba.fastjson2.JSON;
import com.zm.common.vo.RespData;
import com.zm.common.annotation.LogInfo;
import com.zm.common.entity.SysUserLog;
import com.zm.common.mapper.UserLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

/**
 * 操作日志切面
 * <p>拦截所有带有 @LogInfo 注解的方法，自动记录请求和响应日志。</p>
 */
@Aspect
@Component
public class LogAspect {

    @Autowired
    private UserLogMapper userLogMapper;

    /**
     * 环绕通知，拦截 @LogInfo 注解的方法
     * <p>在方法执行前记录请求信息，执行后记录响应结果，保存到数据库。</p>
     *
     * @param joinPoint 切入点
     * @param logInfo   日志注解
     * @return 方法执行结果
     * @throws Throwable 方法执行异常
     */
    @Around("@annotation(logInfo)")
    public Object around(ProceedingJoinPoint joinPoint, LogInfo logInfo) throws Throwable {
        // 获取当前请求
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        SysUserLog userLog = new SysUserLog();
        // 记录操作名称（@LogInfo 的 value 值）
        userLog.setOperationName(logInfo.value());
        userLog.setOperationTime(LocalDateTime.now());

        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            // 记录请求方式（GET/POST/PUT/DELETE）
            userLog.setHttpMethod(request.getMethod());
            // 记录请求URL
            userLog.setUrl(request.getRequestURI());
        }

        // 记录请求参数（JSON格式），过滤掉敏感字段
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0) {
            // 过滤掉 HttpServletRequest/HttpServletResponse 等非业务参数
            Object[] bizArgs = filterArgs(args);
            if (bizArgs.length > 0) {
                userLog.setParams(JSON.toJSONString(bizArgs));
            }
        }

        // 执行目标方法
        Object result = joinPoint.proceed();

        // 记录响应结果（只记录 code 和 msg，不记录 data）
        if (result instanceof RespData) {
            RespData<?> respData = (RespData<?>) result;
            userLog.setResultCode(respData.getCode());
            userLog.setResultMsg(respData.getMsg());
        }

        // 异步保存日志到数据库
        try {
            userLogMapper.insert(userLog);
        } catch (Exception e) {
            // 日志记录失败不影响主业务
        }

        return result;
    }

    /**
     * 过滤非业务参数（如 HttpServletRequest、HttpServletResponse 等）
     */
    private Object[] filterArgs(Object[] args) {
        return java.util.Arrays.stream(args)
                .filter(arg -> !(arg instanceof jakarta.servlet.ServletRequest
                        || arg instanceof jakarta.servlet.ServletResponse
                        || arg instanceof java.io.InputStream
                        || arg instanceof java.io.OutputStream))
                .toArray();
    }
}
