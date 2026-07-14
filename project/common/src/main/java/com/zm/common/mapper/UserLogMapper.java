package com.zm.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zm.common.entity.SysUserLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户操作日志 Mapper
 */
@Mapper
public interface UserLogMapper extends BaseMapper<SysUserLog> {
}
