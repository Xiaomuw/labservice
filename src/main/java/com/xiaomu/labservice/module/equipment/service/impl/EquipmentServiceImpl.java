package com.xiaomu.labservice.module.equipment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaomu.labservice.common.constant.StatusConstant;
import com.xiaomu.labservice.common.exception.BusinessException;
import com.xiaomu.labservice.common.response.ResultCode;
import com.xiaomu.labservice.module.equipment.dto.EquipmentVO;
import com.xiaomu.labservice.module.equipment.entity.Equipment;
import com.xiaomu.labservice.module.equipment.entity.EquipmentImage;
import com.xiaomu.labservice.module.equipment.mapper.EquipmentImageMapper;
import com.xiaomu.labservice.module.equipment.mapper.EquipmentMapper;
import com.xiaomu.labservice.module.equipment.service.EquipmentService;
import com.xiaomu.labservice.module.lab.mapper.LabMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 设备服务实现
 */
@Service
@RequiredArgsConstructor
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentMapper equipmentMapper;
    private final EquipmentImageMapper equipmentImageMapper;
    private final LabMapper labMapper;

    @Override
    public Page<EquipmentVO> getEquipmentList(Long page, Long size, Long labId, String keyword) {
        Page<Equipment> equipmentPage = new Page<>(page, size);
        LambdaQueryWrapper<Equipment> wrapper = new LambdaQueryWrapper<>();
        
        if (labId != null) {
            wrapper.eq(Equipment::getLabId, labId);
        }
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Equipment::getName, keyword)
                    .or().like(Equipment::getModel, keyword)
                    .or().like(Equipment::getSerialNumber, keyword));
        }
        
        wrapper.orderByDesc(Equipment::getCreatedAt);
        Page<Equipment> result = equipmentMapper.selectPage(equipmentPage, wrapper);
        
        Page<EquipmentVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        List<EquipmentVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return voPage;
    }

    @Override
    public EquipmentVO getEquipmentById(Long id) {
        Equipment equipment = equipmentMapper.selectById(id);
        if (equipment == null) {
            throw new BusinessException(ResultCode.EQUIPMENT_NOT_FOUND);
        }
        return convertToVO(equipment);
    }

    @Override
    @Transactional
    public EquipmentVO createEquipment(EquipmentVO equipmentVO) {
        Equipment equipment = new Equipment();
        BeanUtils.copyProperties(equipmentVO, equipment);
        equipmentMapper.insert(equipment);
        return convertToVO(equipment);
    }

    @Override
    @Transactional
    public EquipmentVO updateEquipment(Long id, EquipmentVO equipmentVO) {
        Equipment equipment = equipmentMapper.selectById(id);
        if (equipment == null) {
            throw new BusinessException(ResultCode.EQUIPMENT_NOT_FOUND);
        }
        BeanUtils.copyProperties(equipmentVO, equipment);
        equipment.setId(id);
        equipmentMapper.updateById(equipment);
        return convertToVO(equipment);
    }

    @Override
    @Transactional
    public void deleteEquipment(Long id) {
        Equipment equipment = equipmentMapper.selectById(id);
        if (equipment == null) {
            throw new BusinessException(ResultCode.EQUIPMENT_NOT_FOUND);
        }
        equipmentMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void uploadImage(Long equipmentId, String imageUrl) {
        Equipment equipment = equipmentMapper.selectById(equipmentId);
        if (equipment == null) {
            throw new BusinessException(ResultCode.EQUIPMENT_NOT_FOUND);
        }
        
        List<EquipmentImage> existingImages = equipmentImageMapper.selectByEquipmentId(equipmentId);
        int sortOrder = existingImages.size();
        
        EquipmentImage image = new EquipmentImage();
        image.setEquipmentId(equipmentId);
        image.setImageUrl(imageUrl);
        image.setSortOrder(sortOrder);
        equipmentImageMapper.insert(image);
    }

    @Override
    @Transactional
    public void deleteImage(Long equipmentId, Long imageId) {
        EquipmentImage image = equipmentImageMapper.selectById(imageId);
        if (image == null || !image.getEquipmentId().equals(equipmentId)) {
            throw new BusinessException(ResultCode.FILE_NOT_FOUND);
        }
        equipmentImageMapper.deleteById(imageId);
    }

    @Override
    public Page<EquipmentVO> getAvailableEquipment(Long page, Long size, Long labId) {
        Page<Equipment> equipmentPage = new Page<>(page, size);
        LambdaQueryWrapper<Equipment> wrapper = new LambdaQueryWrapper<>();
        
        if (labId != null) {
            wrapper.eq(Equipment::getLabId, labId);
        }
        wrapper.eq(Equipment::getStatus, StatusConstant.EquipmentStatus.NORMAL);
        wrapper.orderByDesc(Equipment::getCreatedAt);
        
        Page<Equipment> result = equipmentMapper.selectPage(equipmentPage, wrapper);
        Page<EquipmentVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        List<EquipmentVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return voPage;
    }

    private EquipmentVO convertToVO(Equipment equipment) {
        EquipmentVO vo = new EquipmentVO();
        BeanUtils.copyProperties(equipment, vo);
        
        // 查询图片
        List<EquipmentImage> images = equipmentImageMapper.selectByEquipmentId(equipment.getId());
        vo.setImageUrls(images.stream()
                .map(EquipmentImage::getImageUrl)
                .collect(Collectors.toList()));
        
        // 查询实验室名称
        if (equipment.getLabId() != null) {
            com.xiaomu.labservice.module.lab.entity.Lab lab = labMapper.selectById(equipment.getLabId());
            if (lab != null) {
                vo.setLabName(lab.getName());
            }
        }
        
        return vo;
    }
}

