package com.xiaomu.labservice.common.constant;

/**
 * Kafka Topic 常量
 */
public class KafkaTopicConstant {

    /**
     * 邮件通知 Topic
     */
    public static final String EMAIL_NOTIFICATION = "email-notification";

    /**
     * 预约事件 Topic
     */
    public static final String RESERVATION_EVENT = "reservation-event";

    /**
     * 报修事件 Topic
     */
    public static final String REPAIR_EVENT = "repair-event";

    /**
     * 系统日志 Topic
     */
    public static final String SYSTEM_LOG = "system-log";

    private KafkaTopicConstant() {
    }
}
