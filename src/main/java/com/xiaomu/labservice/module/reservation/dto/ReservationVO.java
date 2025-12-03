package com.xiaomu.labservice.module.reservation.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 预约视图对象
 */
@Data
public class ReservationVO {
    private Long id;
    private Long userId;
    private String userName;
    private Long labId;
    private String labName;
    private Long equipmentId;
    private String equipmentName;
    private String title;
    private String purpose;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private Long approverId;
    private String approverName;
    private LocalDateTime approveTime;
    private String rejectReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

