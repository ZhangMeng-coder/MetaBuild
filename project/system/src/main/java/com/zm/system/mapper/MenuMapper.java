package com.zm.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zm.system.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MenuMapper extends BaseMapper<SysMenu> {

    List<SysMenu> selectMenusByRoleIds(List<Long> roleIds);

    List<SysMenu> selectAllMenus();
}
