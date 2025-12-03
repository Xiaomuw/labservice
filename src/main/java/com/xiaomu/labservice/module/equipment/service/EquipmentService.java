package com.xiaomu.labservice.module.equipment.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaomu.labservice.module.equipment.dto.EquipmentVO;

/**
 * 设备服务接口
 */
public interface EquipmentService {
    Page<EquipmentVO> getEquipmentList(Long page, Long size, Long labId, String keyword);
    EquipmentVO getEquipmentById(Long id);
    EquipmentVO createEquipment(EquipmentVO equipmentVO);
    EquipmentVO updateEquipment(Long id, EquipmentVO equipmentVO);
    void deleteEquipment(Long id);
    void uploadImage(Long equipmentId, String imageUrl);
    void deleteImage(Long equipmentId, Long imageId);
    Page<EquipmentVO> getAvailableEquipment(Long page, Long size, Long labId);
}

