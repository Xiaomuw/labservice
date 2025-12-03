package com.xiaomu.labservice.module.lab.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 实验室实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("lab")
public class Lab {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String location;
    private String description;
    private Integer capacity;
    private Long managerId;
    private Integer status;
    private LocalTime openTime;
    private LocalTime closeTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

