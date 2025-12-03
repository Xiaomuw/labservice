package com.xiaomu.labservice.module.equipment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 设备实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("equipment")
public class Equipment {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long labId;
    private String name;
    private String model;
    private String serialNumber;
    private String description;
    private Integer status;
    private LocalDate purchaseDate;
    private LocalDate warrantyDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

