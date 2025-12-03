package com.xiaomu.labservice.module.feedback.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaomu.labservice.module.feedback.dto.FeedbackCreateDTO;
import com.xiaomu.labservice.module.feedback.dto.FeedbackVO;

/**
 * 反馈服务接口
 */
public interface FeedbackService {
    Page<FeedbackVO> getMyFeedbacks(Long page, Long size);
    FeedbackVO getFeedbackById(Long id);
    FeedbackVO createFeedback(FeedbackCreateDTO createDTO);
    Page<FeedbackVO> getAllFeedbacks(Long page, Long size, String type, String status);
    void replyFeedback(Long id, String replyContent);
    void closeFeedback(Long id);
}

