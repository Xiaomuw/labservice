package com.xiaomu.labservice.mq.consumer;

import com.xiaomu.labservice.common.constant.KafkaTopicConstant;
import com.xiaomu.labservice.mq.message.EmailMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * 邮件消息消费者
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailConsumer {

    private final JavaMailSender mailSender;

    @KafkaListener(topics = KafkaTopicConstant.EMAIL_NOTIFICATION, groupId = "email-group")
    public void consume(EmailMessage message) {
        log.info("收到邮件消息: {}", message.getMessageId());

        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(message.getTo());
            mailMessage.setSubject(message.getSubject());
            mailMessage.setText(message.getContent());

            mailSender.send(mailMessage);
            log.info("邮件发送成功: {}", message.getTo());
        } catch (Exception e) {
            log.error("邮件发送失败: {}", message.getTo(), e);
            // TODO: 实现重试逻辑
        }
    }
}
