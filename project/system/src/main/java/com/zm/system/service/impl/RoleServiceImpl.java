package com.zm.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.zm.common.enums.ServiceExceptionEnums;
import com.zm.common.exception.ServiceException;
import com.zm.system.dto.RoleQuery;
import com.zm.system.entity.SysRole;
import com.zm.system.entity.SysRoleMenu;
import com.zm.system.mapper.RoleMapper;
import com.zm.system.mapper.RoleMenuMapper;
import com.zm.system.service.RoleService;
import com.zm.common.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoleServiceImpl extends ServiceImpl<RoleMapper, SysRole> implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Override
    public Page<SysRole> page(RoleQuery query) {
        Page<SysRole> page = query.toPage(SysRole.class);
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getRoleName())) {
            wrapper.like(SysRole::getRoleName, query.getRoleName());
        }
        if (query.getStatus() != null) {
            wrapper.eq(SysRole::getStatus, query.getStatus());
        }
        wrapper.orderByAsc(SysRole::getSort);
        return roleMapper.selectPage(page, wrapper);
    }

    @Override
    public void add(SysRole role) {
        // 检查角色编码是否已存在
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleCode, role.getRoleCode());
        if (roleMapper.selectCount(wrapper) > 0) {
            throw new ServiceException(ServiceExceptionEnums.DATA_EXIST);
        }
        if (role.getStatus() == null) {
            role.setStatus(1);
        }
        // 设置创建人
        if (UserContext.getUserId() != null) {
            role.setCreateUser(UserContext.getUserId().intValue());
        }
        roleMapper.insert(role);
    }

    @Override
    public void update(SysRole role) {
        // 检查角色编码是否已存在（排除自身）
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleCode, role.getRoleCode());
        wrapper.ne(SysRole::getId, role.getId());
        if (roleMapper.selectCount(wrapper) > 0) {
            throw new ServiceException(ServiceExceptionEnums.DATA_EXIST);
        }
        // 设置更新人
        if (UserContext.getUserId() != null) {
            role.setUpdateUser(UserContext.getUserId().intValue());
        }
        roleMapper.updateById(role);
    }

    @Override
    public void delete(Long id) {
        SysRole role = new SysRole();
        role.setId(id);
        // 设置更新人（逻辑删除需要更新人）
        if (UserContext.getUserId() != null) {
            role.setUpdateUser(UserContext.getUserId().intValue());
        }
        roleMapper.deleteById(role);
    }

    @Override
    public void setStatus(Long id, Integer status) {
        SysRole role = roleMapper.selectById(id);
        if (role == null) {
            throw new ServiceException(ServiceExceptionEnums.DATA_NOT_EXIST);
        }
        role.setStatus(status);
        // 设置更新人
        if (UserContext.getUserId() != null) {
            role.setUpdateUser(UserContext.getUserId().intValue());
        }
        roleMapper.updateById(role);
    }

    @Override
    public List<Long> getRoleMenus(Long roleId) {
        return roleMenuMapper.selectMenuIdsByRoleId(roleId);
    }

    @Override
    @Transactional
    public void setRoleMenus(Long roleId, List<Long> menuIds) {
        // 删除原有菜单关联
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId, roleId);
        roleMenuMapper.delete(wrapper);

        // 新增菜单关联
        if (menuIds != null && !menuIds.isEmpty()) {
            List<SysRoleMenu> list = menuIds.stream().map(menuId -> {
                SysRoleMenu rm = new SysRoleMenu();
                rm.setRoleId(roleId);
                rm.setMenuId(menuId);
                return rm;
            }).collect(Collectors.toList());
            for (SysRoleMenu rm : list) {
                roleMenuMapper.insert(rm);
            }
        }
    }
}
