package com.zm.server.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.zm.server.entity.MetaTable;

import java.util.List;
import java.util.Map;

/**
 * 元数据表 Service 接口
 */
public interface MetaTableService extends IService<MetaTable> {

    /**
     * 新增元数据表（含列信息）
     *
     * @param metaTable  元数据表信息
     * @param columns    列定义列表
     */
    void add(MetaTable metaTable, List<Map<String, Object>> columns);

    /**
     * 修改元数据表
     *
     * @param metaTable 元数据表信息
     */
    void edit(MetaTable metaTable);

    /**
     * 删除元数据表（逻辑删除，连带删除其列定义）
     *
     * @param id 元数据表ID
     */
    void delete(Long id);
}