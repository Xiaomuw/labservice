package com.xiaomu.labservice.module.feedback.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 反馈视图对象
 */
@Data
public class FeedbackVO {
    private Long id;
    private Long userId;
    private String userName;
    private String type;
    private Long targetId;
    private String content;
    private Integer rating;
    private String status;
    private String replyContent;
    private LocalDateTime replyTime;
    private Long replierId;
    private String replierName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

