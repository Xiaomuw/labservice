package com.xiaomu.labservice.module.repair.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 报修实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("repair")
public class Repair {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long equipmentId;
    private String title;
    private String description;
    private Integer urgency;
    private String status;
    private Long handlerId;
    private LocalDateTime handleTime;
    private LocalDateTime resolveTime;
    private String resolveNote;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

