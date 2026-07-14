package com.zm.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zm.server.entity.MetaColumn;
import org.apache.ibatis.annotations.Mapper;

/**
 * 元数据列 Mapper 接口
 */
@Mapper
public interface MetaColumnMapper extends BaseMapper<MetaColumn> {
}