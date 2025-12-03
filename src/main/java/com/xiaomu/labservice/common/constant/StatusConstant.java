package com.xiaomu.labservice.common.constant;

/**
 * 状态常量
 */
public class StatusConstant {

    /**
     * 用户状态
     */
    public static class UserStatus {
        public static final int DISABLED = 0;  // 禁用
        public static final int ENABLED = 1;   // 正常
    }

    /**
     * 实验室状态
     */
    public static class LabStatus {
        public static final int CLOSED = 0;    // 关闭
        public static final int OPEN = 1;       // 开放
    }

    /**
     * 设备状态
     */
    public static class EquipmentStatus {
        public static final int REPAIRING = 0;  // 维修中
        public static final int NORMAL = 1;     // 正常
        public static final int SCRAPPED = 2;   // 报废
    }

    /**
     * 预约状态
     */
    public static class ReservationStatus {
        public static final String PENDING = "PENDING";       // 待审批
        public static final String APPROVED = "APPROVED";     // 已批准
        public static final String REJECTED = "REJECTED";      // 已拒绝
        public static final String IN_USE = "IN_USE";         // 使用中
        public static final String COMPLETED = "COMPLETED";   // 已完成
        public static final String CANCELLED = "CANCELLED";   // 已取消
    }

    /**
     * 报修状态
     */
    public static class RepairStatus {
        public static final String PENDING = "PENDING";       // 待处理
        public static final String PROCESSING = "PROCESSING"; // 处理中
        public static final String RESOLVED = "RESOLVED";     // 已解决
        public static final String CLOSED = "CLOSED";        // 已关闭
    }

    /**
     * 反馈状态
     */
    public static class FeedbackStatus {
        public static final String PENDING = "PENDING";       // 待处理
        public static final String REPLIED = "REPLIED";       // 已回复
        public static final String CLOSED = "CLOSED";         // 已关闭
    }

    /**
     * 反馈类型
     */
    public static class FeedbackType {
        public static final String RESERVATION = "RESERVATION"; // 预约反馈
        public static final String REPAIR = "REPAIR";           // 报修反馈
        public static final String OTHER = "OTHER";             // 其他
    }

    private StatusConstant() {
    }
}
