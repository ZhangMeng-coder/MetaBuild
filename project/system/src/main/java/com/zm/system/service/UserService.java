package com.zm.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zm.system.dto.UserQuery;
import com.zm.system.entity.SysUser;

import java.util.List;

public interface UserService extends IService<SysUser> {

    Page<SysUser> page(UserQuery query);

    void add(SysUser user);

    void update(SysUser user);

    void delete(Long id);

    void resetPassword(Long id);

    void setStatus(Long id, Integer status);

    List<Long> getUserRoles(Long userId);

    void setUserRoles(Long userId, List<Long> roleIds);
}
