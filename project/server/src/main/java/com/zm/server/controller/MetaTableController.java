package com.zm.server.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zm.common.annotation.LogInfo;
import com.zm.common.vo.RespData;
import com.zm.server.entity.MetaColumn;
import com.zm.server.entity.MetaTable;
import com.zm.server.mapper.MetaColumnMapper;
import com.zm.server.mapper.MetaDbMapper;
import com.zm.server.service.MetaColumnService;
import com.zm.server.service.MetaTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 元数据表控制器
 * <p>提供元数据表的 CRUD 功能，以及通过 information_schema 查询数据库表和列信息。</p>
 */
@RestController
@RequestMapping("/api/server/meta/table")
public class MetaTableController {

    @Autowired
    private MetaTableService metaTableService;

    @Autowired
    private MetaColumnMapper metaColumnMapper;

    @Autowired
    private MetaColumnService metaColumnService;

    @Autowired
    private MetaDbMapper metaDbMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // ==================== 数据库元数据查询 ====================

    /**
     * 查询当前数据库中所有表（排除 sys_ 和 tb_ 开头的表）
     */
    @GetMapping("/db-tables")
    @PreAuthorize("hasAuthority('server:meta:table:list')")
    public RespData<List<Map<String, Object>>> getDbTables() {
        return RespData.success(metaDbMapper.selectDbTables());
    }

    /**
     * 查询指定表的列信息
     */
    @GetMapping("/db-columns/{tableName}")
    @PreAuthorize("hasAuthority('server:meta:table:list')")
    public RespData<List<Map<String, Object>>> getDbColumns(@PathVariable String tableName) {
        return RespData.success(metaDbMapper.selectDbColumns(tableName));
    }

    // ==================== 元数据表 CRUD ====================

    /**
     * 分页查询已保存的元数据表
     */
    @PostMapping("/page")
    @PreAuthorize("hasAuthority('server:meta:table:list')")
    public RespData<Page<MetaTable>> page(@RequestBody Page<MetaTable> page) {
        LambdaQueryWrapper<MetaTable> wrapper = new LambdaQueryWrapper<MetaTable>()
                .orderByDesc(MetaTable::getCreateTime);
        return RespData.success(metaTableService.page(page, wrapper));
    }

    /**
     * 查询元数据表详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('server:meta:table:list')")
    public RespData<MetaTable> get(@PathVariable Long id) {
        return RespData.success(metaTableService.getById(id));
    }

    /**
     * 新增元数据表（从 information_schema 中选择表，保存表定义和列定义）
     *
     * @param request 请求体，包含 tableName（数据库表名）、tableComment（备注名称）和 columns（列定义列表）
     */
    @PostMapping
    @LogInfo("新增元数据表")
    @PreAuthorize("hasAuthority('server:meta:table:add')")
    public RespData<Void> add(@RequestBody Map<String, Object> request) {
        String tableName = (String) request.get("tableName");
        String tableComment = (String) request.get("tableComment");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> columns = (List<Map<String, Object>>) request.get("columns");

        MetaTable metaTable = new MetaTable();
        metaTable.setTableName(tableName);
        metaTable.setTableComment(tableComment);
        metaTableService.add(metaTable, columns);
        return RespData.success();
    }

    /**
     * 修改元数据表
     */
    @PutMapping
    @LogInfo("修改元数据表")
    @PreAuthorize("hasAuthority('server:meta:table:edit')")
    public RespData<Void> edit(@RequestBody MetaTable metaTable) {
        metaTableService.edit(metaTable);
        return RespData.success();
    }

    /**
     * 删除元数据表（逻辑删除，连带删除其列定义）
     */
    @DeleteMapping("/{id}")
    @LogInfo("删除元数据表")
    @PreAuthorize("hasAuthority('server:meta:table:delete')")
    public RespData<Void> delete(@PathVariable Long id) {
        metaTableService.delete(id);
        return RespData.success();
    }

    // ==================== 元数据列管理 ====================

    /**
     * 查询指定元数据表的所有列
     */
    @GetMapping("/{id}/columns")
    @PreAuthorize("hasAuthority('server:meta:table:list')")
    public RespData<List<MetaColumn>> getColumns(@PathVariable Long id) {
        LambdaQueryWrapper<MetaColumn> wrapper = new LambdaQueryWrapper<MetaColumn>()
                .eq(MetaColumn::getTableId, id)
                .orderByAsc(MetaColumn::getSort);
        return RespData.success(metaColumnMapper.selectList(wrapper));
    }

    /**
     * 修改元数据列
     */
    @PutMapping("/column")
    @LogInfo("修改元数据列")
    @PreAuthorize("hasAuthority('server:meta:table:edit')")
    public RespData<Void> editColumn(@RequestBody MetaColumn column) {
        metaColumnService.edit(column);
        return RespData.success();
    }

    /**
     * 执行字典 SQL 查询，返回 id 和 name 字段列表
     * <p>用于元数据列配置中，通过 SQL 动态获取字典项。</p>
     *
     * @param request 请求体，包含 sql 字段
     * @return 查询结果，每行包含 id 和 name
     */
    @PostMapping("/column/dict-sql")
    @PreAuthorize("hasAuthority('server:meta:table:list')")
    public RespData<List<Map<String, Object>>> executeDictSql(@RequestBody Map<String, String> request) {
        String sql = request.get("sql");
        if (sql == null || sql.trim().isEmpty()) {
            return RespData.error(500,"SQL 不能为空");
        }
        // 限制只能执行 SELECT 查询，防止 SQL 注入篡改
        String trimmedSql = sql.trim().toUpperCase();
        if (!trimmedSql.startsWith("SELECT")) {
            return RespData.error(500,"只允许执行 SELECT 查询");
        }
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
        return RespData.success(result);
    }
}