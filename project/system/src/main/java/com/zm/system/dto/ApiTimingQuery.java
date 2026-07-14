package com.zm.system.dto;

import com.zm.common.vo.BasePageVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ApiTimingQuery extends BasePageVO {
    private Long minCostMs;  // 最小耗时，用于筛选慢接口
}
