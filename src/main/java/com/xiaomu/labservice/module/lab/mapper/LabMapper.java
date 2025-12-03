package com.xiaomu.labservice.module.lab.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaomu.labservice.module.lab.entity.Lab;
import org.apache.ibatis.annotations.Mapper;

/**
 * 实验室 Mapper
 */
@Mapper
public interface LabMapper extends BaseMapper<Lab> {
}

