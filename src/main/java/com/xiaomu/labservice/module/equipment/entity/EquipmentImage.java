package com.xiaomu.labservice.module.equipment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 设备图片实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("equipment_image")
public class EquipmentImage {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long equipmentId;
    private String imageUrl;
    private Integer sortOrder;
    private LocalDateTime createdAt;
}

