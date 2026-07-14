package com.zm.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.IService;
import com.zm.system.dto.RoleQuery;
import com.zm.system.entity.SysRole;

import java.util.List;

public interface RoleService extends IService<SysRole> {

    Page<SysRole> page(RoleQuery query);

    void add(SysRole role);

    void update(SysRole role);

    void delete(Long id);

    void setStatus(Long id, Integer status);

    List<Long> getRoleMenus(Long roleId);

    void setRoleMenus(Long roleId, List<Long> menuIds);
}
