package com.zm.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zm.server.entity.MetaTable;
import org.apache.ibatis.annotations.Mapper;

/**
 * 元数据表 Mapper 接口
 */
@Mapper
public interface MetaTableMapper extends BaseMapper<MetaTable> {
}