package com.zm.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zm.common.annotation.LogInfo;
import com.zm.common.vo.RespData;
import com.zm.system.entity.SysDictType;
import com.zm.system.service.DictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 字典类型控制器
 * <p>提供字典类型的分页查询、新增、修改、删除功能。</p>
 */
@RestController
@RequestMapping("/api/system/dict/type")
public class DictTypeController {

    @Autowired
    private DictTypeService dictTypeService;

    /**
     * 分页查询字典类型
     */
    @PostMapping("/page")
    @PreAuthorize("hasAuthority('system:dict:list')")
    public RespData<Page<SysDictType>> page(@RequestBody Page<SysDictType> page) {
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<SysDictType>()
                .orderByAsc(SysDictType::getSort);
        return RespData.success(dictTypeService.page(page, wrapper));
    }

    /**
     * 查询字典类型详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:dict:list')")
    public RespData<SysDictType> get(@PathVariable Long id) {
        return RespData.success(dictTypeService.getById(id));
    }

    /**
     * 新增字典类型
     */
    @PostMapping
    @LogInfo("新增字典类型")
    @PreAuthorize("hasAuthority('system:dict:add')")
    public RespData<Void> add(@RequestBody SysDictType dictType) {
        dictTypeService.add(dictType);
        return RespData.success();
    }

    /**
     * 修改字典类型
     */
    @PutMapping
    @LogInfo("修改字典类型")
    @PreAuthorize("hasAuthority('system:dict:edit')")
    public RespData<Void> edit(@RequestBody SysDictType dictType) {
        dictTypeService.edit(dictType);
        return RespData.success();
    }

    /**
     * 删除字典类型
     */
    @DeleteMapping("/{id}")
    @LogInfo("删除字典类型")
    @PreAuthorize("hasAuthority('system:dict:delete')")
    public RespData<Void> delete(@PathVariable Long id) {
        dictTypeService.delete(id);
        return RespData.success();
    }
}
