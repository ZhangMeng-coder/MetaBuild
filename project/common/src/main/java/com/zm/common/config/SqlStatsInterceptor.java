package com.zm.common.config;

import com.zm.common.entity.SlowSqlLog;
import com.zm.common.mapper.SlowSqlLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

/**
 * SQL 性能统计拦截器
 * <p>拦截所有 MyBatis 的查询和更新操作，记录执行耗时。
 * 超过阈值（默认 2 秒）的慢 SQL 会以 WARN 级别输出可执行 SQL 语句，方便排查性能问题。</p>
 */
@Slf4j
@Component
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class SqlStatsInterceptor implements Interceptor {

    /** 慢 SQL 阈值，单位毫秒 */
    private static final long SLOW_THRESHOLD_MS = 2000;

    @Autowired
    @Lazy
    private SlowSqlLogMapper slowSqlLogMapper;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return invocation.proceed();
        } finally {
            long cost = System.currentTimeMillis() - start;
            if (cost > SLOW_THRESHOLD_MS) {
                // 慢 SQL：输出可执行 SQL 语句
                MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
                Object parameter = invocation.getArgs().length > 1 ? invocation.getArgs()[1] : null;
                BoundSql boundSql = ms.getBoundSql(parameter);
                String sql = getExecutableSql(ms, boundSql);
                log.warn("\n================== 慢SQL告警 ==================\n" +
                        "方法: {}\n耗时: {} ms\nSQL: {}\n============================================",
                        ms.getId(), cost, sql);

                // 持久化慢SQL日志
                try {
                    SlowSqlLog slowSqlLog = new SlowSqlLog();
                    slowSqlLog.setSqlContent(sql);
                    slowSqlLog.setCostMs(cost);
                    slowSqlLog.setMethodName(ms.getId());
                    slowSqlLog.setCreateTime(LocalDateTime.now());
                    slowSqlLogMapper.insert(slowSqlLog);
                } catch (Exception e) {
                    log.error("保存慢SQL日志失败", e);
                }
            }
        }
    }

    /**
     * 获取可执行 SQL（将参数拼入 SQL，方便直接复制执行）
     */
    private String getExecutableSql(MappedStatement ms, BoundSql boundSql) {
        Configuration configuration = ms.getConfiguration();
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");

        if (parameterMappings == null || parameterMappings.isEmpty() || parameterObject == null) {
            return sql;
        }

        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        String[] parts = sql.split("\\?");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            result.append(parts[i]);
            if (i < parameterMappings.size()) {
                Object value;
                try {
                    if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                        value = parameterObject;
                    } else {
                        MetaObject metaObject = configuration.newMetaObject(parameterObject);
                        value = metaObject.getValue(parameterMappings.get(i).getProperty());
                    }
                    result.append(formatParam(value));
                } catch (Exception e) {
                    result.append("?");
                }
            }
        }
        return result.toString();
    }

    private String formatParam(Object value) {
        if (value == null) {
            return "NULL";
        }
        if (value instanceof String) {
            return "'" + value + "'";
        }
        if (value instanceof Date) {
            DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, Locale.CHINA);
            return "'" + df.format(value) + "'";
        }
        if (value instanceof Boolean) {
            return (Boolean) value ? "1" : "0";
        }
        return value.toString();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }
}
