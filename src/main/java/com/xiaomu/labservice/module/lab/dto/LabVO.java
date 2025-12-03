package com.xiaomu.labservice.module.lab.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 实验室视图对象
 */
@Data
public class LabVO {
    private Long id;
    private String name;
    private String location;
    private String description;
    private Integer capacity;
    private Long managerId;
    private String managerName;
    private Integer status;
    private LocalTime openTime;
    private LocalTime closeTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

