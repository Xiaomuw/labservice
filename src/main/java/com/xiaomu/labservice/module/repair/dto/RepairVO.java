package com.xiaomu.labservice.module.repair.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 报修视图对象
 */
@Data
public class RepairVO {
    private Long id;
    private Long userId;
    private String userName;
    private Long equipmentId;
    private String equipmentName;
    private String title;
    private String description;
    private Integer urgency;
    private String status;
    private Long handlerId;
    private String handlerName;
    private LocalDateTime handleTime;
    private LocalDateTime resolveTime;
    private String resolveNote;
    private List<String> imageUrls;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

