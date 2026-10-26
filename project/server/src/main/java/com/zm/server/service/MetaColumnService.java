package com.zm.server.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.zm.server.entity.MetaColumn;

/**
 * 元数据列 Service 接口
 */
public interface MetaColumnService extends IService<MetaColumn> {

    /**
     * 修改元数据列
     *
     * @param column 元数据列信息
     */
    void edit(MetaColumn column);
}