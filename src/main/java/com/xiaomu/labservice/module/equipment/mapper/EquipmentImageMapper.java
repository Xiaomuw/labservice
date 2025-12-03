package com.xiaomu.labservice.module.equipment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaomu.labservice.module.equipment.entity.EquipmentImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 设备图片 Mapper
 */
@Mapper
public interface EquipmentImageMapper extends BaseMapper<EquipmentImage> {

    @Select("SELECT * FROM equipment_image WHERE equipment_id = #{equipmentId} ORDER BY sort_order ASC")
    List<EquipmentImage> selectByEquipmentId(Long equipmentId);
}

