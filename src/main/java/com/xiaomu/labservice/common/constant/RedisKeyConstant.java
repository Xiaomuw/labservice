package com.xiaomu.labservice.common.constant;

/**
 * Redis Key 常量
 */
public class RedisKeyConstant {

    /**
     * 用户信息缓存
     */
    public static final String USER_INFO = "user:info:";

    /**
     * 用户缓存
     */
    public static final String USER = "user:";

    /**
     * 实验室列表缓存
     */
    public static final String LAB_LIST = "lab:list";

    /**
     * 实验室详情缓存
     */
    public static final String LAB = "lab:";

    /**
     * 设备列表缓存
     */
    public static final String EQUIPMENT_LIST = "equipment:list:";

    /**
     * 设备详情缓存
     */
    public static final String EQUIPMENT = "equipment:";

    /**
     * 验证码缓存
     */
    public static final String VERIFY_CODE = "verify_code:";

    /**
     * Token 黑名单
     */
    public static final String TOKEN_BLACKLIST = "token:blacklist:";

    /**
     * 限流 Key
     */
    public static final String RATE_LIMIT = "rate:limit:";

    private RedisKeyConstant() {
    }
}
