package com.xiaomu.labservice.module.repair.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 报修图片实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("repair_image")
public class RepairImage {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long repairId;
    private String imageUrl;
    private LocalDateTime createdAt;
}

