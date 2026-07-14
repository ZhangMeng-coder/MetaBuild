package com.zm.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zm.common.annotation.LogInfo;
import com.zm.common.vo.RespData;
import com.zm.system.entity.SysDictData;
import com.zm.system.service.DictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典数据控制器
 * <p>提供字典数据的分页查询、新增、修改、删除功能。</p>
 */
@RestController
@RequestMapping("/api/system/dict/data")
public class DictDataController {

    @Autowired
    private DictDataService dictDataService;

    /**
     * 分页查询字典数据
     */
    @PostMapping("/page")
    @LogInfo("查询字典数据")
    @PreAuthorize("hasAuthority('system:dict:data:list')")
    public RespData<Page<SysDictData>> page(@RequestBody Page<SysDictData> page,
                                            @RequestParam(required = false) String dictType) {
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<SysDictData>()
                .eq(dictType != null && !dictType.isEmpty(), SysDictData::getDictType, dictType)
                .orderByAsc(SysDictData::getDictSort);
        return RespData.success(dictDataService.page(page, wrapper));
    }

    /**
     * 查询字典数据详情
     */
    @GetMapping("/{id}")
    @LogInfo("查询字典数据详情")
    @PreAuthorize("hasAuthority('system:dict:data:list')")
    public RespData<SysDictData> get(@PathVariable Long id) {
        return RespData.success(dictDataService.getById(id));
    }

    /**
     * 新增字典数据
     */
    @PostMapping
    @LogInfo("新增字典数据")
    @PreAuthorize("hasAuthority('system:dict:data:add')")
    public RespData<Void> add(@RequestBody SysDictData dictData) {
        dictDataService.add(dictData);
        return RespData.success();
    }

    /**
     * 修改字典数据
     */
    @PutMapping
    @LogInfo("修改字典数据")
    @PreAuthorize("hasAuthority('system:dict:data:edit')")
    public RespData<Void> edit(@RequestBody SysDictData dictData) {
        dictDataService.edit(dictData);
        return RespData.success();
    }

    /**
     * 删除字典数据
     */
    @DeleteMapping("/{id}")
    @LogInfo("删除字典数据")
    @PreAuthorize("hasAuthority('system:dict:data:delete')")
    public RespData<Void> delete(@PathVariable Long id) {
        dictDataService.delete(id);
        return RespData.success();
    }

    /**
     * 根据字典类型获取启用的字典数据列表（供前端下拉框等使用，无需登录也可调用）
     */
    @GetMapping("/type/{dictType}")
    public RespData<List<SysDictData>> getByType(@PathVariable String dictType) {
        return RespData.success(dictDataService.getDictDataByType(dictType));
    }
}
