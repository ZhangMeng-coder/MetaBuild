package com.zm.common.exception;

import lombok.Getter;
import com.zm.common.enums.ServiceExceptionEnums;

@Getter
public class ServiceException extends RuntimeException{

    private Integer code;
    private String message;

    public ServiceException(ServiceExceptionEnums serviceExceptionEnums) {
        super(serviceExceptionEnums.getMessage());
        this.code = serviceExceptionEnums.getCode();
        this.message = serviceExceptionEnums.getMessage();
    }
}
