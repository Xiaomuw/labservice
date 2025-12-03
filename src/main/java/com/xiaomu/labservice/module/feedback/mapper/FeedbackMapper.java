package com.xiaomu.labservice.module.feedback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaomu.labservice.module.feedback.entity.Feedback;
import org.apache.ibatis.annotations.Mapper;

/**
 * 反馈 Mapper
 */
@Mapper
public interface FeedbackMapper extends BaseMapper<Feedback> {
}

