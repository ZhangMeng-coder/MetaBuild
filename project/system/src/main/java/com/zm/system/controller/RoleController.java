package com.zm.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zm.common.vo.RespData;
import com.zm.system.annotation.LogInfo;
import com.zm.system.dto.RoleQuery;
import com.zm.system.entity.SysRole;
import com.zm.system.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 角色管理控制器
 * <p>提供角色管理的 CRUD 接口，包括分页查询、新增、修改、删除、状态设置和菜单权限分配。</p>
 */
@RestController
@RequestMapping("/api/system/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * 分页查询角色列表
     *
     * @param query 查询条件，支持按角色名称模糊搜索和按状态筛选
     * @return 分页角色列表
     */
    @PostMapping("/page")
    @LogInfo("分页查询角色列表")
    @PreAuthorize("hasAuthority('system:role:list')")
    public RespData<Page<SysRole>> page(@RequestBody RoleQuery query) {
        Page<SysRole> page = roleService.page(query);
        return RespData.success(page);
    }

    /**
     * 新增角色
     *
     * @param role 角色信息
     * @return 操作结果
     */
    @PostMapping
    @LogInfo("新增角色")
    @PreAuthorize("hasAuthority('system:role:add')")
    public RespData<Void> add(@Valid @RequestBody SysRole role) {
        roleService.add(role);
        return RespData.success();
    }

    /**
     * 修改角色信息
     *
     * @param role 角色信息，需包含角色ID
     * @return 操作结果
     */
    @PutMapping
    @LogInfo("修改角色")
    @PreAuthorize("hasAuthority('system:role:edit')")
    public RespData<Void> update(@RequestBody SysRole role) {
        roleService.update(role);
        return RespData.success();
    }

    /**
     * 删除角色
     *
     * @param id 角色ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @LogInfo("删除角色")
    @PreAuthorize("hasAuthority('system:role:delete')")
    public RespData<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return RespData.success();
    }

    /**
     * 设置角色状态（启用/禁用）
     *
     * @param params 包含角色ID和状态值的 Map
     * @return 操作结果
     */
    @PutMapping("/status")
    @LogInfo("设置角色状态")
    @PreAuthorize("hasAuthority('system:role:edit')")
    public RespData<Void> setStatus(@RequestBody Map<String, Object> params) {
        Long id = Long.valueOf(params.get("id").toString());
        Integer status = Integer.valueOf(params.get("status").toString());
        roleService.setStatus(id, status);
        return RespData.success();
    }

    /**
     * 获取角色已分配的菜单ID列表
     *
     * @param id 角色ID
     * @return 菜单ID列表
     */
    @GetMapping("/{id}/menus")
    @LogInfo("获取角色菜单")
    @PreAuthorize("hasAuthority('system:role:menu:assign')")
    public RespData<List<Long>> getRoleMenus(@PathVariable Long id) {
        List<Long> menuIds = roleService.getRoleMenus(id);
        return RespData.success(menuIds);
    }

    /**
     * 设置角色的菜单权限分配
     *
     * @param id      角色ID
     * @param menuIds 菜单ID列表
     * @return 操作结果
     */
    @PutMapping("/{id}/menus")
    @LogInfo("分配角色菜单")
    @PreAuthorize("hasAuthority('system:role:menu:assign')")
    public RespData<Void> setRoleMenus(@PathVariable Long id, @RequestBody List<Long> menuIds) {
        roleService.setRoleMenus(id, menuIds);
        return RespData.success();
    }
}
