package com.zm.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zm.common.enums.ServiceExceptionEnums;
import com.zm.common.exception.ServiceException;
import com.zm.system.dto.UserQuery;
import com.zm.system.entity.SysUser;
import com.zm.system.entity.SysUserRole;
import com.zm.system.mapper.RoleMapper;
import com.zm.system.mapper.UserMapper;
import com.zm.system.mapper.UserRoleMapper;
import com.zm.system.service.UserService;
import com.zm.common.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl extends ServiceImpl<UserMapper, SysUser> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Page<SysUser> page(UserQuery query) {
        Page<SysUser> page = query.toPage(SysUser.class);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getUsername())) {
            wrapper.like(SysUser::getUsername, query.getUsername());
        }
        if (query.getStatus() != null) {
            wrapper.eq(SysUser::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(SysUser::getCreateTime);
        page = userMapper.selectPage(page, wrapper);
        // 联查每个用户对应的角色名称
        for (SysUser user : page.getRecords()) {
            List<String> roleNames = roleMapper.selectRoleNamesByUserId(user.getId());
            if (roleNames != null && !roleNames.isEmpty()) {
                user.setRoleNames(String.join(", ", roleNames));
            }
        }
        return page;
    }

    @Override
    public void add(SysUser user) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, user.getUsername());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new ServiceException(ServiceExceptionEnums.DATA_EXIST);
        }
        // 密码加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        // 设置创建人
        if (UserContext.getUserId() != null) {
            user.setCreateUser(UserContext.getUserId().intValue());
        }
        userMapper.insert(user);
    }

    @Override
    public void update(SysUser user) {
        // 检查用户名是否已存在（排除自身）
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, user.getUsername());
        wrapper.ne(SysUser::getId, user.getId());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new ServiceException(ServiceExceptionEnums.DATA_EXIST);
        }
        // 密码字段不更新（除非有值）
        user.setPassword(null);
        // 设置更新人
        if (UserContext.getUserId() != null) {
            user.setUpdateUser(UserContext.getUserId().intValue());
        }
        userMapper.updateById(user);
    }

    @Override
    public void delete(Long id) {
        SysUser user = new SysUser();
        user.setId(id);
        // 设置更新人（逻辑删除需要更新人）
        if (UserContext.getUserId() != null) {
            user.setUpdateUser(UserContext.getUserId().intValue());
        }
        userMapper.deleteById(user);
    }

    @Override
    public void resetPassword(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new ServiceException(ServiceExceptionEnums.DATA_NOT_EXIST);
        }
        user.setPassword(passwordEncoder.encode("123456"));
        // 设置更新人
        if (UserContext.getUserId() != null) {
            user.setUpdateUser(UserContext.getUserId().intValue());
        }
        userMapper.updateById(user);
    }

    @Override
    public void setStatus(Long id, Integer status) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new ServiceException(ServiceExceptionEnums.DATA_NOT_EXIST);
        }
        user.setStatus(status);
        // 设置更新人
        if (UserContext.getUserId() != null) {
            user.setUpdateUser(UserContext.getUserId().intValue());
        }
        userMapper.updateById(user);
    }

    @Override
    public List<Long> getUserRoles(Long userId) {
        return userRoleMapper.selectRoleIdsByUserId(userId);
    }

    @Override
    @Transactional
    public void setUserRoles(Long userId, List<Long> roleIds) {
        // 删除原有角色关联
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userId);
        userRoleMapper.delete(wrapper);

        // 新增角色关联
        if (roleIds != null && !roleIds.isEmpty()) {
            List<SysUserRole> list = roleIds.stream().map(roleId -> {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                return ur;
            }).collect(Collectors.toList());
            for (SysUserRole ur : list) {
                userRoleMapper.insert(ur);
            }
        }
    }
}
