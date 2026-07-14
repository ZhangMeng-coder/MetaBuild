package com.zm.system.dto;

import com.zm.common.vo.BasePageVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 操作日志分页查询参数
 * <p>继承 BasePageVO 获取分页参数，支持按操作名称模糊搜索。</p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LogQuery extends BasePageVO {

    /** 操作名称（模糊搜索） */
    private String operationName;
}
