package com.zm.server.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zm.common.annotation.LogInfo;
import com.zm.common.vo.RespData;
import com.zm.server.entity.MetaColumn;
import com.zm.server.entity.MetaFormConfig;
import com.zm.server.entity.MetaListConfig;
import com.zm.server.mapper.MetaColumnMapper;
import com.zm.server.service.MetaFormConfigService;
import com.zm.server.service.MetaListConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 元数据配置控制器
 * <p>提供列表展示配置和表单配置的 CRUD 功能，用于拖拽式配置页面。</p>
 */
@RestController
@RequestMapping("/api/server/meta/config")
public class MetaConfigController {

    @Autowired
    private MetaListConfigService metaListConfigService;

    @Autowired
    private MetaFormConfigService metaFormConfigService;

    @Autowired
    private MetaColumnMapper metaColumnMapper;

    // ==================== 列表配置 ====================

    /**
     * 获取指定元数据表的列表配置
     * 如果尚未配置，则根据元数据列自动生成默认配置
     */
    @GetMapping("/{tableId}/list")
    @PreAuthorize("hasAuthority('server:meta:table:list')")
    public RespData<List<MetaListConfig>> getListConfig(@PathVariable Long tableId) {
        LambdaQueryWrapper<MetaListConfig> wrapper = new LambdaQueryWrapper<MetaListConfig>()
                .eq(MetaListConfig::getTableId, tableId)
                .orderByAsc(MetaListConfig::getSort);
        List<MetaListConfig> configs = metaListConfigService.list(wrapper);

        // 如果没有配置，自动生成默认配置
        if (configs == null || configs.isEmpty()) {
            configs = generateDefaultListConfig(tableId);
        }
        return RespData.success(configs);
    }

    /**
     * 批量保存列表配置
     */
    @PostMapping("/{tableId}/list")
    @LogInfo("保存列表配置")
    @PreAuthorize("hasAuthority('server:meta:table:edit')")
    public RespData<Void> saveListConfig(@PathVariable Long tableId, @RequestBody List<MetaListConfig> configs) {
        metaListConfigService.batchSave(tableId, configs);
        return RespData.success();
    }

    // ==================== 表单配置 ====================

    /**
     * 获取指定元数据表的表单配置
     * 如果尚未配置，则根据元数据列自动生成默认配置
     */
    @GetMapping("/{tableId}/form")
    @PreAuthorize("hasAuthority('server:meta:table:list')")
    public RespData<List<MetaFormConfig>> getFormConfig(@PathVariable Long tableId) {
        LambdaQueryWrapper<MetaFormConfig> wrapper = new LambdaQueryWrapper<MetaFormConfig>()
                .eq(MetaFormConfig::getTableId, tableId)
                .orderByAsc(MetaFormConfig::getSort);
        List<MetaFormConfig> configs = metaFormConfigService.list(wrapper);

        // 如果没有配置，自动生成默认配置
        if (configs == null || configs.isEmpty()) {
            configs = generateDefaultFormConfig(tableId);
        }
        return RespData.success(configs);
    }

    /**
     * 批量保存表单配置
     */
    @PostMapping("/{tableId}/form")
    @LogInfo("保存表单配置")
    @PreAuthorize("hasAuthority('server:meta:table:edit')")
    public RespData<Void> saveFormConfig(@PathVariable Long tableId, @RequestBody List<MetaFormConfig> configs) {
        metaFormConfigService.batchSave(tableId, configs);
        return RespData.success();
    }

    // ==================== 初始化默认配置 ====================

    /**
     * 初始化默认列表配置
     * 从元数据列中取所有非主键、非逻辑删除的列作为列表列
     */
    private List<MetaListConfig> generateDefaultListConfig(Long tableId) {
        List<MetaColumn> columns = metaColumnMapper.selectList(
                new LambdaQueryWrapper<MetaColumn>()
                        .eq(MetaColumn::getTableId, tableId)
                        .orderByAsc(MetaColumn::getSort)
        );
        List<MetaListConfig> configs = new ArrayList<>();
        int sort = 0;
        for (MetaColumn col : columns) {
            // 跳过 is_delete 等审计字段
            if ("is_delete".equals(col.getColumnName()) || "create_user".equals(col.getColumnName())
                    || "update_user".equals(col.getColumnName())) {
                continue;
            }
            MetaListConfig config = new MetaListConfig();
            config.setTableId(tableId);
            config.setColumnId(col.getId());
            config.setColumnLabel(col.getColumnComment());
            config.setAlign("left");
            config.setSortable(0);
            config.setVisible(1);
            config.setIsSearch(0);
            config.setSort(sort++);
            configs.add(config);
        }
        return configs;
    }

    /**
     * 初始化默认表单配置
     * 从元数据列中取所有非主键、非逻辑删除的列作为表单字段
     */
    private List<MetaFormConfig> generateDefaultFormConfig(Long tableId) {
        List<MetaColumn> columns = metaColumnMapper.selectList(
                new LambdaQueryWrapper<MetaColumn>()
                        .eq(MetaColumn::getTableId, tableId)
                        .orderByAsc(MetaColumn::getSort)
        );
        List<MetaFormConfig> configs = new ArrayList<>();
        int sort = 0;
        for (MetaColumn col : columns) {
            // 跳过主键、审计字段
            if (col.getIsPk() == 1 || "is_delete".equals(col.getColumnName())
                    || "create_user".equals(col.getColumnName()) || "create_time".equals(col.getColumnName())
                    || "update_user".equals(col.getColumnName()) || "update_time".equals(col.getColumnName())) {
                continue;
            }
            MetaFormConfig config = new MetaFormConfig();
            config.setTableId(tableId);
            config.setColumnId(col.getId());
            config.setFieldLabel(col.getColumnComment());
            config.setIsRequired(col.getIsRequired());
            config.setSpan(12);
            config.setSort(sort++);
            configs.add(config);
        }
        return configs;
    }
}