package com.xiaomu.labservice.common.exception;

import com.xiaomu.labservice.common.response.ResultCode;

/**
 * 未授权异常
 */
public class UnauthorizedException extends BusinessException {

    public UnauthorizedException() {
        super(ResultCode.UNAUTHORIZED);
    }

    public UnauthorizedException(String message) {
        super(ResultCode.UNAUTHORIZED.getCode(), message);
    }
}
