package com.xiaomu.labservice.module.feedback.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 创建反馈 DTO
 */
@Data
public class FeedbackCreateDTO {
    @NotBlank(message = "反馈类型不能为空")
    private String type;
    
    private Long targetId;
    
    @NotBlank(message = "反馈内容不能为空")
    private String content;
    
    @Min(value = 1, message = "评分最小为1")
    @Max(value = 5, message = "评分最大为5")
    private Integer rating;
}

