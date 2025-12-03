package com.xiaomu.labservice.module.equipment.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 设备视图对象
 */
@Data
public class EquipmentVO {
    private Long id;
    private Long labId;
    private String labName;
    private String name;
    private String model;
    private String serialNumber;
    private String description;
    private Integer status;
    private LocalDate purchaseDate;
    private LocalDate warrantyDate;
    private List<String> imageUrls;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

