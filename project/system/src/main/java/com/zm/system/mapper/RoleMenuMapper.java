package com.zm.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zm.system.entity.SysRoleMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleMenuMapper extends BaseMapper<SysRoleMenu> {

    List<Long> selectMenuIdsByRoleId(Long roleId);

    List<Long> selectMenuIdsByRoleIds(List<Long> roleIds);
}
