package com.xiaomu.labservice.module.reservation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 预约实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("reservation")
public class Reservation {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long labId;
    private Long equipmentId;
    private String title;
    private String purpose;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private Long approverId;
    private LocalDateTime approveTime;
    private String rejectReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

