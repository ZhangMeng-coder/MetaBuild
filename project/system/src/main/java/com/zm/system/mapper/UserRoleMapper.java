package com.zm.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zm.system.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserRoleMapper extends BaseMapper<SysUserRole> {

    List<Long> selectRoleIdsByUserId(Long userId);
}
