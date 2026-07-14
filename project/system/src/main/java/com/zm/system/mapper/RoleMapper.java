package com.zm.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zm.system.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<SysRole> {

    List<String> selectRoleCodesByUserId(Long userId);

    List<String> selectRoleNamesByUserId(Long userId);
}
