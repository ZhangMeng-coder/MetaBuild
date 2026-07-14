package com.zm.system.controller;

import com.zm.common.vo.RespData;
import com.zm.system.annotation.LogInfo;
import com.zm.system.dto.UserQuery;
import com.zm.system.entity.SysUser;
import com.zm.system.service.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户管理控制器
 * <p>提供用户管理的 CRUD 接口，包括分页查询、新增、修改、删除、密码重置、状态设置和角色分配。</p>
 */
@RestController
@RequestMapping("/api/system/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 分页查询用户列表
     *
     * @param query 查询条件，支持按用户名模糊搜索和按状态筛选
     * @return 分页用户列表
     */
    @PostMapping("/page")
    @LogInfo("分页查询用户列表")
    @PreAuthorize("hasAuthority('system:user:list')")
    public RespData<Page<SysUser>> page(@RequestBody UserQuery query) {
        Page<SysUser> page = userService.page(query);
        return RespData.success(page);
    }

    /**
     * 新增用户
     *
     * @param user 用户信息，需包含用户名和密码
     * @return 操作结果
     */
    @PostMapping
    @LogInfo("新增用户")
    @PreAuthorize("hasAuthority('system:user:add')")
    public RespData<Void> add(@Valid @RequestBody SysUser user) {
        userService.add(user);
        return RespData.success();
    }

    /**
     * 修改用户信息
     *
     * @param user 用户信息，需包含用户ID
     * @return 操作结果
     */
    @PutMapping
    @LogInfo("修改用户")
    @PreAuthorize("hasAuthority('system:user:edit')")
    public RespData<Void> update(@RequestBody SysUser user) {
        userService.update(user);
        return RespData.success();
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @LogInfo("删除用户")
    @PreAuthorize("hasAuthority('system:user:delete')")
    public RespData<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return RespData.success();
    }

    /**
     * 重置密码为默认密码（123456）
     *
     * @param id 用户ID
     * @return 操作结果
     */
    @PutMapping("/reset-password/{id}")
    @LogInfo("重置密码")
    @PreAuthorize("hasAuthority('system:user:edit')")
    public RespData<Void> resetPassword(@PathVariable Long id) {
        userService.resetPassword(id);
        return RespData.success();
    }

    /**
     * 设置用户状态（启用/禁用）
     *
     * @param params 包含用户ID和状态值的 Map
     * @return 操作结果
     */
    @PutMapping("/status")
    @LogInfo("设置用户状态")
    @PreAuthorize("hasAuthority('system:user:edit')")
    public RespData<Void> setStatus(@RequestBody Map<String, Object> params) {
        Long id = Long.valueOf(params.get("id").toString());
        Integer status = Integer.valueOf(params.get("status").toString());
        userService.setStatus(id, status);
        return RespData.success();
    }

    /**
     * 获取用户已分配的角色ID列表
     *
     * @param id 用户ID
     * @return 角色ID列表
     */
    @GetMapping("/{id}/roles")
    @LogInfo("获取用户角色")
    @PreAuthorize("hasAuthority('system:user:role:assign')")
    public RespData<List<Long>> getUserRoles(@PathVariable Long id) {
        List<Long> roleIds = userService.getUserRoles(id);
        return RespData.success(roleIds);
    }

    /**
     * 设置用户的角色分配
     *
     * @param id      用户ID
     * @param roleIds 角色ID列表
     * @return 操作结果
     */
    @PutMapping("/{id}/roles")
    @LogInfo("分配用户角色")
    @PreAuthorize("hasAuthority('system:user:role:assign')")
    public RespData<Void> setUserRoles(@PathVariable Long id, @RequestBody List<Long> roleIds) {
        userService.setUserRoles(id, roleIds);
        return RespData.success();
    }
}
