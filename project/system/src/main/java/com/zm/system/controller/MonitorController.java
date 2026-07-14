package com.zm.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zm.common.entity.ApiTimingLog;
import com.zm.common.entity.SlowSqlLog;
import com.zm.common.mapper.ApiTimingLogMapper;
import com.zm.common.mapper.SlowSqlLogMapper;
import com.zm.common.vo.RespData;
import com.zm.system.dto.ApiTimingQuery;
import com.zm.system.dto.SlowSqlQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/system/monitor")
public class MonitorController {

    @Autowired
    private SlowSqlLogMapper slowSqlLogMapper;

    @Autowired
    private ApiTimingLogMapper apiTimingLogMapper;

    /** 慢SQL分页查询 */
    @PostMapping("/slow-sql/page")
    public RespData<Page<SlowSqlLog>> slowSqlPage(@RequestBody SlowSqlQuery query) {
        Page<SlowSqlLog> page = query.toPage(SlowSqlLog.class);
        LambdaQueryWrapper<SlowSqlLog> wrapper = new LambdaQueryWrapper<>();
        if (query.getMinCostMs() != null && query.getMinCostMs() > 0) {
            wrapper.ge(SlowSqlLog::getCostMs, query.getMinCostMs());
        }
        wrapper.orderByDesc(SlowSqlLog::getCreateTime);
        return RespData.success(slowSqlLogMapper.selectPage(page, wrapper));
    }

    /** 接口耗时分页查询 */
    @PostMapping("/api-timing/page")
    public RespData<Page<ApiTimingLog>> apiTimingPage(@RequestBody ApiTimingQuery query) {
        Page<ApiTimingLog> page = query.toPage(ApiTimingLog.class);
        LambdaQueryWrapper<ApiTimingLog> wrapper = new LambdaQueryWrapper<>();
        if (query.getMinCostMs() != null && query.getMinCostMs() > 0) {
            wrapper.ge(ApiTimingLog::getCostMs, query.getMinCostMs());
        }
        wrapper.orderByDesc(ApiTimingLog::getCreateTime);
        return RespData.success(apiTimingLogMapper.selectPage(page, wrapper));
    }
}
