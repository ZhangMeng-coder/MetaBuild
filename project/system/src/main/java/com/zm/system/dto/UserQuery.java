package com.zm.system.dto;

import com.zm.common.vo.BasePageVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserQuery extends BasePageVO {

    private String username;

    private Integer status;
}
