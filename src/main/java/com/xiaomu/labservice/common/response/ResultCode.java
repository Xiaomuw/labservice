package com.xiaomu.labservice.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应码枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    // 成功
    SUCCESS(200, "success"),

    // 客户端错误 4xx
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未认证"),
    FORBIDDEN(403, "权限不足"),
    NOT_FOUND(404, "资源不存在"),
    CONFLICT(409, "资源冲突"),

    // 服务器错误 5xx
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),

    // 业务错误 1xxx
    BUSINESS_ERROR(1000, "业务处理失败"),

    // 用户相关错误 1001-1099
    USERNAME_EXISTS(1001, "用户名已存在"),
    EMAIL_EXISTS(1002, "邮箱已注册"),
    VERIFY_CODE_ERROR(1003, "验证码错误"),
    VERIFY_CODE_EXPIRED(1004, "验证码已过期"),
    USER_NOT_FOUND(1005, "用户不存在"),
    PASSWORD_ERROR(1006, "密码错误"),
    USER_DISABLED(1007, "用户已被禁用"),

    // 实验室相关错误 2001-2099
    LAB_NOT_FOUND(2001, "实验室不存在"),
    LAB_CLOSED(2002, "实验室已关闭"),

    // 设备相关错误 3001-3099
    EQUIPMENT_NOT_FOUND(3001, "设备不存在"),
    EQUIPMENT_REPAIRING(3002, "设备维修中"),
    EQUIPMENT_SCRAPPED(3003, "设备已报废"),

    // 预约相关错误 4001-4099
    RESERVATION_TIME_CONFLICT(4001, "预约时间冲突"),
    RESERVATION_EXPIRED(4002, "预约已过期"),
    RESERVATION_NOT_FOUND(4003, "预约不存在"),
    RESERVATION_STATUS_ERROR(4004, "预约状态错误"),

    // 报修相关错误 5001-5099
    REPAIR_PROCESSED(5001, "报修已处理"),
    REPAIR_NOT_FOUND(5002, "报修不存在"),

    // 文件相关错误 6001-6099
    FILE_UPLOAD_ERROR(6001, "文件上传失败"),
    FILE_DELETE_ERROR(6002, "文件删除失败"),
    FILE_NOT_FOUND(6003, "文件不存在"),
    FILE_TYPE_NOT_SUPPORTED(6004, "文件类型不支持"),
    FILE_SIZE_EXCEEDED(6005, "文件大小超出限制");

    private final Integer code;
    private final String message;
}
