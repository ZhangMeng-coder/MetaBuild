package com.zm.system.controller;

import com.zm.common.vo.RespData;
import com.zm.common.annotation.LogInfo;
import com.zm.system.entity.SysMenu;
import com.zm.system.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理控制器
 * <p>提供菜单管理的 CRUD 接口，包括树形查询、新增、修改、删除。</p>
 */
@RestController
@RequestMapping("/api/system/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    /**
     * 获取菜单树
     * <p>查询所有菜单并以树形结构返回，便于前端渲染导航菜单。</p>
     *
     * @return 树形菜单列表
     */
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('system:menu:list')")
    public RespData<List<SysMenu>> tree() {
        List<SysMenu> tree = menuService.tree();
        return RespData.success(tree);
    }

    /**
     * 新增菜单
     *
     * @param menu 菜单信息
     * @return 操作结果
     */
    @PostMapping
    @LogInfo("新增菜单")
    @PreAuthorize("hasAuthority('system:menu:add')")
    public RespData<Long> add(@RequestBody SysMenu menu) {
        Long menuId = menuService.add(menu);
        return RespData.success(menuId);
    }

    /**
     * 修改菜单信息
     *
     * @param menu 菜单信息，需包含菜单ID
     * @return 操作结果
     */
    @PutMapping
    @LogInfo("修改菜单")
    @PreAuthorize("hasAuthority('system:menu:edit')")
    public RespData<Void> update(@RequestBody SysMenu menu) {
        menuService.update(menu);
        return RespData.success();
    }

    /**
     * 删除菜单
     * <p>删除前会检查是否有子菜单，若有子菜单则不允许删除。</p>
     *
     * @param id 菜单ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @LogInfo("删除菜单")
    @PreAuthorize("hasAuthority('system:menu:delete')")
    public RespData<Void> delete(@PathVariable Long id) {
        menuService.delete(id);
        return RespData.success();
    }
}
