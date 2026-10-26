package com.zm.server.controller;

import com.zm.common.annotation.LogInfo;
import com.zm.common.util.UserContext;
import com.zm.common.vo.RespData;
import com.zm.server.entity.MetaColumn;
import com.zm.server.entity.MetaTable;
import com.zm.server.mapper.MetaColumnMapper;
import com.zm.server.mapper.MetaTableMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 元数据动态数据控制器
 * <p>根据元数据配置，动态生成对应数据库表的分页查询、新增、修改、删除、导出和导入功能。</p>
 */
@RestController
@RequestMapping("/api/server/meta/dynamic")
public class MetaDynamicController {

    @Autowired
    private MetaTableMapper metaTableMapper;

    @Autowired
    private MetaColumnMapper metaColumnMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 分页查询动态数据
     *
     * @param tableId 元数据表ID
     * @param params  请求参数，包含 current, size, 以及各字段的搜索条件
     */
    @PostMapping("/{tableId}/page")
    @PreAuthorize("hasAnyAuthority('server:meta:table:list', 'meta:dynamic:'+#tableId+':list')")
    public RespData<Map<String, Object>> page(@PathVariable Long tableId, @RequestBody Map<String, Object> params) {
        MetaTable metaTable = metaTableMapper.selectById(tableId);
        if (metaTable == null) {
            return RespData.error(500,"元数据表不存在");
        }

        // 获取元数据列信息（用于构建查询列名和搜索条件）
        List<MetaColumn> columns = metaColumnMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<MetaColumn>()
                        .eq(MetaColumn::getTableId, tableId)
        );

        String tableName = metaTable.getTableName();

        // 构建列名列表（排除 is_delete）
        String columnNames = columns.stream()
                .map(MetaColumn::getColumnName)
                .filter(name -> !"is_delete".equals(name))
                .collect(Collectors.joining(", "));

        // 列名 -> MetaColumn 映射（用于快速查找列信息和展示类型）
        Map<String, MetaColumn> columnMap = columns.stream()
                .collect(Collectors.toMap(MetaColumn::getColumnName, c -> c));

        // 分页参数
        int current = params.get("current") instanceof Number ? ((Number) params.get("current")).intValue() : 1;
        int size = params.get("size") instanceof Number ? ((Number) params.get("size")).intValue() : 10;

        // 搜索参数
        @SuppressWarnings("unchecked")
        Map<String, Object> searchParams = (Map<String, Object>) params.getOrDefault("params", new HashMap<>());

        // 构建 SQL
        StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM ").append(tableName).append(" WHERE is_delete = 0");
        StringBuilder dataSql = new StringBuilder("SELECT ").append(columnNames).append(" FROM ").append(tableName).append(" WHERE is_delete = 0");

        List<Object> args = new ArrayList<>();
        for (Map.Entry<String, Object> entry : searchParams.entrySet()) {
            if (entry.getValue() == null || entry.getValue().toString().isEmpty()) {
                continue;
            }
            String paramKey = entry.getKey();
            Object paramValue = entry.getValue();
            String operator = " LIKE ?";
            String colName = paramKey;

            // 处理范围查询参数（_start / _end 后缀）
            boolean isRangeStart = paramKey.endsWith("_start");
            boolean isRangeEnd = paramKey.endsWith("_end");
            if (isRangeStart || isRangeEnd) {
                colName = paramKey.substring(0, paramKey.length() - 6);
                operator = isRangeStart ? " >= ?" : " <= ?";
            }

            // 安全过滤：只允许在元数据列中定义的列名
            MetaColumn metaCol = columnMap.get(colName);
            if (metaCol == null) {
                continue;
            }

            // 根据展示类型选择查询方式
            if (!isRangeStart && !isRangeEnd) {
                String showType = metaCol.getShowType();
                if ("select".equals(showType) || "radio".equals(showType)) {
                    operator = " = ?";
                }
                // checkbox/text/textarea 仍使用 LIKE
            }

            countSql.append(" AND ").append(colName).append(operator);
            dataSql.append(" AND ").append(colName).append(operator);

            if (" LIKE ?".equals(operator)) {
                args.add("%" + paramValue.toString() + "%");
            } else {
                args.add(paramValue);
            }
        }

        // 排序和分页
        dataSql.append(" ORDER BY id DESC LIMIT ? OFFSET ?");
        List<Object> countArgs = new ArrayList<>(args);
        List<Object> dataArgs = new ArrayList<>(args);
        dataArgs.add(size);
        dataArgs.add((current - 1) * size);

        // 执行查询
        try {
            Integer total = jdbcTemplate.queryForObject(countSql.toString(), Integer.class, countArgs.toArray());
            List<Map<String, Object>> records = jdbcTemplate.queryForList(dataSql.toString(), dataArgs.toArray());

            Map<String, Object> result = new HashMap<>();
            result.put("total", total != null ? total : 0);
            result.put("records", records);
            return RespData.success(result);
        } catch (Exception e) {
            return RespData.error(500,"查询失败: " + e.getMessage());
        }
    }

    /**
     * 查询单条数据
     */
    @GetMapping("/{tableId}/{id}")
    @PreAuthorize("hasAnyAuthority('server:meta:table:list', 'meta:dynamic:'+#tableId+':list')")
    public RespData<Map<String, Object>> get(@PathVariable Long tableId, @PathVariable Long id) {
        MetaTable metaTable = metaTableMapper.selectById(tableId);
        if (metaTable == null) {
            return RespData.error(500,"元数据表不存在");
        }
        try {
            String sql = "SELECT * FROM " + metaTable.getTableName() + " WHERE id = ? AND is_delete = 0";
            List<Map<String, Object>> records = jdbcTemplate.queryForList(sql, id);
            if (records.isEmpty()) {
                return RespData.error(500,"数据不存在");
            }
            return RespData.success(records.get(0));
        } catch (Exception e) {
            return RespData.error(500,"查询失败: " + e.getMessage());
        }
    }

    /**
     * 新增数据
     */
    @PostMapping("/{tableId}")
    @LogInfo("动态数据新增")
    @PreAuthorize("hasAnyAuthority('server:meta:table:add', 'meta:dynamic:'+#tableId+':add')")
    public RespData<Void> add(@PathVariable Long tableId, @RequestBody Map<String, Object> data) {
        MetaTable metaTable = metaTableMapper.selectById(tableId);
        if (metaTable == null) {
            return RespData.error(500,"元数据表不存在");
        }
        // 获取元数据列
        List<MetaColumn> columns = metaColumnMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<MetaColumn>()
                        .eq(MetaColumn::getTableId, tableId)
        );

        // 过滤出有效的列名（排除主键和审计字段）
        Set<String> validColumns = columns.stream()
                .filter(c -> c.getIsPk() != 1 && !"is_delete".equals(c.getColumnName())
                        && !"create_time".equals(c.getColumnName()) && !"update_time".equals(c.getColumnName())
                        && !"create_user".equals(c.getColumnName()) && !"update_user".equals(c.getColumnName()))
                .map(MetaColumn::getColumnName)
                .collect(Collectors.toSet());

        // 构建 INSERT SQL
        StringBuilder sql = new StringBuilder("INSERT INTO ").append(metaTable.getTableName()).append(" (");
        StringBuilder values = new StringBuilder(" VALUES (");
        List<Object> args = new ArrayList<>();

        // 添加用户提交的字段
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (validColumns.contains(entry.getKey())) {
                sql.append(entry.getKey()).append(", ");
                values.append("?, ");
                args.add(entry.getValue());
            }
        }

        // 添加审计字段
        sql.append("create_user, create_time, is_delete");
        values.append("?, NOW(), 0");
        args.add(UserContext.getUserId() != null ? UserContext.getUserId().intValue() : null);

        sql.append(")").append(values).append(")");

        try {
            jdbcTemplate.update(sql.toString(), args.toArray());
            return RespData.success();
        } catch (Exception e) {
            return RespData.error(500,"新增失败: " + e.getMessage());
        }
    }

    /**
     * 修改数据
     */
    @PutMapping("/{tableId}")
    @LogInfo("动态数据修改")
    @PreAuthorize("hasAnyAuthority('server:meta:table:edit', 'meta:dynamic:'+#tableId+':edit')")
    public RespData<Void> edit(@PathVariable Long tableId, @RequestBody Map<String, Object> data) {
        MetaTable metaTable = metaTableMapper.selectById(tableId);
        if (metaTable == null) {
            return RespData.error(500,"元数据表不存在");
        }
        Object idValue = data.get("id");
        if (idValue == null) {
            return RespData.error(500,"缺少主键 id");
        }

        // 获取元数据列
        List<MetaColumn> columns = metaColumnMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<MetaColumn>()
                        .eq(MetaColumn::getTableId, tableId)
        );

        // 过滤出有效列名
        Set<String> validColumns = columns.stream()
                .filter(c -> c.getIsPk() != 1 && !"is_delete".equals(c.getColumnName())
                        && !"create_time".equals(c.getColumnName()) && !"create_user".equals(c.getColumnName()))
                .map(MetaColumn::getColumnName)
                .collect(Collectors.toSet());

        // 构建 UPDATE SQL
        StringBuilder sql = new StringBuilder("UPDATE ").append(metaTable.getTableName()).append(" SET ");
        List<Object> args = new ArrayList<>();

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (validColumns.contains(entry.getKey())) {
                sql.append(entry.getKey()).append(" = ?, ");
                args.add(entry.getValue());
            }
        }

        // 添加更新审计字段
        sql.append("update_user = ?, update_time = NOW()");
        args.add(UserContext.getUserId() != null ? UserContext.getUserId().intValue() : null);

        sql.append(" WHERE id = ?");
        args.add(idValue);

        try {
            jdbcTemplate.update(sql.toString(), args.toArray());
            return RespData.success();
        } catch (Exception e) {
            return RespData.error(500,"修改失败: " + e.getMessage());
        }
    }

    /**
     * 删除数据（逻辑删除）
     */
    @DeleteMapping("/{tableId}/{id}")
    @LogInfo("动态数据删除")
    @PreAuthorize("hasAnyAuthority('server:meta:table:delete', 'meta:dynamic:'+#tableId+':delete')")
    public RespData<Void> delete(@PathVariable Long tableId, @PathVariable Long id) {
        MetaTable metaTable = metaTableMapper.selectById(tableId);
        if (metaTable == null) {
            return RespData.error(500 ,"元数据表不存在");
        }
        try {
            String sql = "UPDATE " + metaTable.getTableName() + " SET is_delete = 1, update_user = ?, update_time = NOW() WHERE id = ?";
            jdbcTemplate.update(sql, UserContext.getUserId() != null ? UserContext.getUserId().intValue() : null, id);
            return RespData.success();
        } catch (Exception e) {
            return RespData.error(500 ,"删除失败: " + e.getMessage());
        }
    }

    /**
     * 导出数据（CSV 格式）
     */
    @PostMapping("/{tableId}/export")
    @PreAuthorize("hasAnyAuthority('server:meta:table:export', 'meta:dynamic:'+#tableId+':export')")
    public RespData<List<Map<String, Object>>> export(@PathVariable Long tableId, @RequestBody(required = false) Map<String, Object> params) {
        MetaTable metaTable = metaTableMapper.selectById(tableId);
        if (metaTable == null) {
            return RespData.error(500 ,"元数据表不存在");
        }
        String tableName = metaTable.getTableName();
        try {
            String sql = "SELECT * FROM " + tableName + " WHERE is_delete = 0 ORDER BY id DESC";
            List<Map<String, Object>> records = jdbcTemplate.queryForList(sql);
            return RespData.success(records);
        } catch (Exception e) {
            return RespData.error(500 ,"导出失败: " + e.getMessage());
        }
    }

    /**
     * 导入数据（CSV 格式）
     */
    @PostMapping("/{tableId}/import")
    @LogInfo("动态数据导入")
    @PreAuthorize("hasAnyAuthority('server:meta:table:import', 'meta:dynamic:'+#tableId+':import')")
    public RespData<Void> importData(@PathVariable Long tableId, @RequestParam("file") MultipartFile file) {
        MetaTable metaTable = metaTableMapper.selectById(tableId);
        if (metaTable == null) {
            return RespData.error(500 ,"元数据表不存在");
        }
        // 获取元数据列
        List<MetaColumn> columns = metaColumnMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<MetaColumn>()
                        .eq(MetaColumn::getTableId, tableId)
        );
        Set<String> validColumns = columns.stream()
                .filter(c -> c.getIsPk() != 1 && !"is_delete".equals(c.getColumnName())
                        && !"create_time".equals(c.getColumnName()) && !"update_time".equals(c.getColumnName())
                        && !"create_user".equals(c.getColumnName()) && !"update_user".equals(c.getColumnName()))
                .map(MetaColumn::getColumnName)
                .collect(Collectors.toSet());

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
            String headerLine = reader.readLine();
            if (headerLine == null) {
                return RespData.error(500,"文件为空");
            }
            String[] headers = headerLine.split(",");
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] values = line.split(",");
                StringBuilder sql = new StringBuilder("INSERT INTO ").append(metaTable.getTableName()).append(" (");
                StringBuilder placeholders = new StringBuilder(" VALUES (");
                List<Object> args = new ArrayList<>();

                for (int i = 0; i < headers.length && i < values.length; i++) {
                    String colName = headers[i].trim();
                    if (validColumns.contains(colName)) {
                        sql.append(colName).append(", ");
                        placeholders.append("?, ");
                        args.add(values[i].trim());
                    }
                }
                sql.append("create_user, create_time, is_delete");
                placeholders.append("?, NOW(), 0");
                args.add(UserContext.getUserId() != null ? UserContext.getUserId().intValue() : null);

                sql.append(")").append(placeholders).append(")");
                jdbcTemplate.update(sql.toString(), args.toArray());
            }
            reader.close();
            return RespData.success();
        } catch (Exception e) {
            return RespData.error(500 ,"导入失败: " + e.getMessage());
        }
    }
}