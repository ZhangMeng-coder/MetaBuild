package com.zm.server.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.zm.server.entity.MetaFormConfig;

import java.util.List;

/**
 * 元数据表单配置 Service 接口
 */
public interface MetaFormConfigService extends IService<MetaFormConfig> {

    /**
     * 批量保存表单配置（根据ID判断新增或修改）
     *
     * @param tableId 元数据表ID
     * @param configs 表单配置列表
     */
    void batchSave(Long tableId, List<MetaFormConfig> configs);
}