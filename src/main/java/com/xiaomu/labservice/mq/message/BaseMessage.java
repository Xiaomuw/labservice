package com.xiaomu.labservice.mq.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * 基础消息类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BaseMessage {
    private String messageId;
    private String type;
    private LocalDateTime timestamp;

}

