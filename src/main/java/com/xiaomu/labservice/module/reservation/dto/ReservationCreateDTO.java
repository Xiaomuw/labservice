package com.xiaomu.labservice.module.reservation.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 创建预约 DTO
 */
@Data
public class ReservationCreateDTO {
    @NotNull(message = "实验室ID不能为空")
    private Long labId;
    
    private Long equipmentId;
    
    @NotBlank(message = "预约标题不能为空")
    private String title;
    
    private String purpose;
    
    @NotNull(message = "开始时间不能为空")
    @Future(message = "开始时间必须是未来时间")
    private LocalDateTime startTime;
    
    @NotNull(message = "结束时间不能为空")
    @Future(message = "结束时间必须是未来时间")
    private LocalDateTime endTime;
}

