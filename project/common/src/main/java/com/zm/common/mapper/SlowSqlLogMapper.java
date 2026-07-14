package com.zm.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zm.common.entity.SlowSqlLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SlowSqlLogMapper extends BaseMapper<SlowSqlLog> {
}
