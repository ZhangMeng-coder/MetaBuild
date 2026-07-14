package com.zm.common.aspect;

import com.zm.common.entity.ApiTimingLog;
import com.zm.common.mapper.ApiTimingLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

/**
 * 接口耗时统计切面
 * <p>拦截所有 RestController 方法，自动记录每个接口的请求方式和执行耗时。
 * 超过阈值（默认 3 秒）的接口会以 WARN 级别输出，方便发现性能瓶颈。</p>
 */
@Slf4j
@Aspect
@Component
public class TimingAspect {

    /** 接口耗时告警阈值，单位毫秒 */
    private static final long WARN_THRESHOLD_MS = 3000;

    @Autowired
    private ApiTimingLogMapper apiTimingLogMapper;

    /** 慢接口日志格式 */
    private static final String SLOW_LOG_FORMAT = "\n================== 慢接口告警 ==================\n" +
            "请求方式: {}\n请求路径: {}\nController: {}\n耗时: {} ms\n" +
            "============================================";

    /** 正常接口日志格式 */
    private static final String NORMAL_LOG_FORMAT = "[TIMING] {} {} - {} ms - {}";

    /**
     * 切点：所有标注了 @RestController 的类中的方法
     */
    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void restController() {
    }

    @Around("restController()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();

        try {
            return joinPoint.proceed();
        } finally {
            long cost = System.currentTimeMillis() - start;
            HttpServletRequest request = getRequest();

            if (cost > WARN_THRESHOLD_MS) {
                log.warn(SLOW_LOG_FORMAT,
                        request != null ? request.getMethod() : "UNKNOWN",
                        request != null ? request.getRequestURI() : "",
                        methodName,
                        cost);
            } else {
                log.info(NORMAL_LOG_FORMAT,
                        request != null ? request.getMethod() : "UNKNOWN",
                        request != null ? request.getRequestURI() : "",
                        cost,
                        methodName);
            }

            // 持久化接口耗时日志
            try {
                ApiTimingLog apiTimingLog = new ApiTimingLog();
                apiTimingLog.setHttpMethod(request != null ? request.getMethod() : "UNKNOWN");
                apiTimingLog.setUrl(request != null ? request.getRequestURI() : "");
                apiTimingLog.setMethodName(methodName);
                apiTimingLog.setCostMs(cost);
                apiTimingLog.setCreateTime(LocalDateTime.now());
                apiTimingLogMapper.insert(apiTimingLog);
            } catch (Exception e) {
                log.error("保存接口耗时日志失败", e);
            }
        }
    }

    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            return attributes.getRequest();
        }
        return null;
    }
}
