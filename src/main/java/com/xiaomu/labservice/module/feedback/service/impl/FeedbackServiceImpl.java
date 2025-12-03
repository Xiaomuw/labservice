package com.xiaomu.labservice.module.feedback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaomu.labservice.common.constant.StatusConstant;
import com.xiaomu.labservice.common.exception.BusinessException;
import com.xiaomu.labservice.common.response.ResultCode;
import com.xiaomu.labservice.module.feedback.dto.FeedbackCreateDTO;
import com.xiaomu.labservice.module.feedback.dto.FeedbackVO;
import com.xiaomu.labservice.module.feedback.entity.Feedback;
import com.xiaomu.labservice.module.feedback.mapper.FeedbackMapper;
import com.xiaomu.labservice.module.feedback.service.FeedbackService;
import com.xiaomu.labservice.module.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 反馈服务实现
 */
@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackMapper feedbackMapper;
    private final UserMapper userMapper;

    @Override
    public Page<FeedbackVO> getMyFeedbacks(Long page, Long size) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        com.xiaomu.labservice.module.user.entity.User user = userMapper.selectByUsername(username);
        
        Page<Feedback> feedbackPage = new Page<>(page, size);
        LambdaQueryWrapper<Feedback> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Feedback::getUserId, user.getId());
        wrapper.orderByDesc(Feedback::getCreatedAt);
        
        Page<Feedback> result = feedbackMapper.selectPage(feedbackPage, wrapper);
        Page<FeedbackVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        List<FeedbackVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return voPage;
    }

    @Override
    public FeedbackVO getFeedbackById(Long id) {
        Feedback feedback = feedbackMapper.selectById(id);
        if (feedback == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        return convertToVO(feedback);
    }

    @Override
    @Transactional
    public FeedbackVO createFeedback(FeedbackCreateDTO createDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        com.xiaomu.labservice.module.user.entity.User user = userMapper.selectByUsername(username);
        
        Feedback feedback = new Feedback();
        feedback.setUserId(user.getId());
        feedback.setType(createDTO.getType());
        feedback.setTargetId(createDTO.getTargetId());
        feedback.setContent(createDTO.getContent());
        feedback.setRating(createDTO.getRating());
        feedback.setStatus(StatusConstant.FeedbackStatus.PENDING);
        
        feedbackMapper.insert(feedback);
        return convertToVO(feedback);
    }

    @Override
    public Page<FeedbackVO> getAllFeedbacks(Long page, Long size, String type, String status) {
        Page<Feedback> feedbackPage = new Page<>(page, size);
        LambdaQueryWrapper<Feedback> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(type)) {
            wrapper.eq(Feedback::getType, type);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(Feedback::getStatus, status);
        }
        
        wrapper.orderByDesc(Feedback::getCreatedAt);
        Page<Feedback> result = feedbackMapper.selectPage(feedbackPage, wrapper);
        
        Page<FeedbackVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        List<FeedbackVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return voPage;
    }

    @Override
    @Transactional
    public void replyFeedback(Long id, String replyContent) {
        Feedback feedback = feedbackMapper.selectById(id);
        if (feedback == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        com.xiaomu.labservice.module.user.entity.User replier = userMapper.selectByUsername(username);
        
        feedback.setStatus(StatusConstant.FeedbackStatus.REPLIED);
        feedback.setReplyContent(replyContent);
        feedback.setReplierId(replier.getId());
        feedback.setReplyTime(LocalDateTime.now());
        
        feedbackMapper.updateById(feedback);
        
        // TODO: 发送通知消息
    }

    @Override
    @Transactional
    public void closeFeedback(Long id) {
        Feedback feedback = feedbackMapper.selectById(id);
        if (feedback == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        
        feedback.setStatus(StatusConstant.FeedbackStatus.CLOSED);
        feedbackMapper.updateById(feedback);
    }

    private FeedbackVO convertToVO(Feedback feedback) {
        FeedbackVO vo = new FeedbackVO();
        BeanUtils.copyProperties(feedback, vo);
        
        if (feedback.getUserId() != null) {
            com.xiaomu.labservice.module.user.entity.User user = userMapper.selectById(feedback.getUserId());
            if (user != null) {
                vo.setUserName(user.getNickname() != null ? user.getNickname() : user.getUsername());
            }
        }
        
        if (feedback.getReplierId() != null) {
            com.xiaomu.labservice.module.user.entity.User replier = userMapper.selectById(feedback.getReplierId());
            if (replier != null) {
                vo.setReplierName(replier.getNickname() != null ? replier.getNickname() : replier.getUsername());
            }
        }
        
        return vo;
    }
}

