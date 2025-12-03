package com.xiaomu.labservice.module.repair.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaomu.labservice.module.repair.entity.RepairImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 报修图片 Mapper
 */
@Mapper
public interface RepairImageMapper extends BaseMapper<RepairImage> {

    @Select("SELECT * FROM repair_image WHERE repair_id = #{repairId}")
    List<RepairImage> selectByRepairId(Long repairId);
}

