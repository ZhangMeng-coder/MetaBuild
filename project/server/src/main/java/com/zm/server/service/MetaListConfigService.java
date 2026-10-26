package com.zm.server.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.zm.server.entity.MetaListConfig;

import java.util.List;

/**
 * 元数据列表配置 Service 接口
 */
public interface MetaListConfigService extends IService<MetaListConfig> {

    /**
     * 批量保存列表配置（根据ID判断新增或修改）
     *
     * @param tableId 元数据表ID
     * @param configs 列表配置列表
     */
    void batchSave(Long tableId, List<MetaListConfig> configs);
}