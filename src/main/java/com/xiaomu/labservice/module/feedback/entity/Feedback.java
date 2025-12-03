package com.xiaomu.labservice.module.feedback.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 反馈实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("feedback")
public class Feedback {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String type;
    private Long targetId;
    private String content;
    private Integer rating;
    private String status;
    private String replyContent;
    private LocalDateTime replyTime;
    private Long replierId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

