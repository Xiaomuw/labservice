package com.xiaomu.labservice.mq.producer;

import com.xiaomu.labservice.common.constant.KafkaTopicConstant;
import com.xiaomu.labservice.mq.message.EmailMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * 邮件消息生产者
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * 发送验证码邮件
     */
    public void sendVerificationCode(String email, String code) {
        EmailMessage message = EmailMessage.builder()
                .messageId(UUID.randomUUID().toString())
                .type("VERIFICATION_CODE")
                .to(email)
                .subject("验证码")
                .content("您的验证码是：" + code + "，有效期5分钟。")
                .templateName("verification-code")
                .build();

        sendMessage(message);
    }

    /**
     * 发送预约通知邮件
     */
    public void sendReservationNotification(String email, String title, String status) {
        EmailMessage message = EmailMessage.builder()
                .messageId(UUID.randomUUID().toString())
                .type("RESERVATION_NOTIFICATION")
                .to(email)
                .subject("预约状态更新")
                .content("您的预约「" + title + "」状态已更新为：" + status)
                .templateName("reservation-notification")
                .build();

        sendMessage(message);
    }

    /**
     * 发送报修通知邮件
     */
    public void sendRepairNotification(String email, String title, String status) {
        EmailMessage message = EmailMessage.builder()
                .messageId(UUID.randomUUID().toString())
                .type("REPAIR_NOTIFICATION")
                .to(email)
                .subject("报修状态更新")
                .content("您的报修「" + title + "」状态已更新为：" + status)
                .templateName("repair-notification")
                .build();

        sendMessage(message);
    }

    /**
     * 发送反馈回复通知邮件
     */
    public void sendFeedbackReplyNotification(String email, String replyContent) {
        EmailMessage message = EmailMessage.builder()
                .messageId(UUID.randomUUID().toString())
                .type("FEEDBACK_REPLY")
                .to(email)
                .subject("反馈回复")
                .content("您的反馈已收到回复：" + replyContent)
                .templateName("feedback-reply")
                .build();

        sendMessage(message);
    }

    /**
     * 发送消息
     */
    private void sendMessage(EmailMessage message) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                KafkaTopicConstant.EMAIL_NOTIFICATION, message);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("邮件消息发送成功: {}", message.getMessageId());
            } else {
                log.error("邮件消息发送失败: {}", message.getMessageId(), ex);
            }
        });
    }
}

