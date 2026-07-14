package com.zm.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zm.common.vo.RespData;
import com.zm.common.annotation.LogInfo;
import com.zm.system.dto.LogQuery;
import com.zm.common.entity.SysUserLog;
import com.zm.common.mapper.UserLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 操作日志控制器
 * <p>提供操作日志的分页查询接口，支持按操作名称模糊搜索。</p>
 */
@RestController
@RequestMapping("/api/system/log")
public class LogController {

    @Autowired
    private UserLogMapper userLogMapper;

    /**
     * 分页查询操作日志列表
     *
     * @param query 查询条件，支持按操作名称模糊搜索
     * @return 分页日志列表
     */
    @PostMapping("/page")
    @LogInfo("查询操作日志")
    @PreAuthorize("hasAuthority('system:log:list')")
    public RespData<Page<SysUserLog>> page(@RequestBody LogQuery query) {
        Page<SysUserLog> page = query.toPage(SysUserLog.class);
        LambdaQueryWrapper<SysUserLog> wrapper = new LambdaQueryWrapper<>();
        // 按操作名称模糊搜索
        if (query.getOperationName() != null && !query.getOperationName().isEmpty()) {
            wrapper.like(SysUserLog::getOperationName, query.getOperationName());
        }
        // 按操作时间倒序排列
        wrapper.orderByDesc(SysUserLog::getOperationTime);
        return RespData.success(userLogMapper.selectPage(page, wrapper));
    }
}
