package com.xiaomu.labservice.mq.message;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * 邮件消息
 */
@Data
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class EmailMessage extends BaseMessage {
    private String to;
    private String subject;
    private String content;
    private String templateName;
    private Object templateData;

    public EmailMessage() {
        super();
        this.setTimestamp(LocalDateTime.now());
    }
}
