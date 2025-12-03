package com.xiaomu.labservice.module.repair.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建报修 DTO
 */
@Data
public class RepairCreateDTO {
    @NotNull(message = "设备ID不能为空")
    private Long equipmentId;
    
    @NotBlank(message = "报修标题不能为空")
    private String title;
    
    private String description;
    
    @Min(value = 1, message = "紧急程度最小为1")
    @Max(value = 3, message = "紧急程度最大为3")
    private Integer urgency = 1;
}

