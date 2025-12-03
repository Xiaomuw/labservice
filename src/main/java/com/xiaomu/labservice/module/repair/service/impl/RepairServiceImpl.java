package com.xiaomu.labservice.module.repair.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaomu.labservice.common.constant.StatusConstant;
import com.xiaomu.labservice.common.exception.BusinessException;
import com.xiaomu.labservice.common.response.ResultCode;
import com.xiaomu.labservice.module.equipment.mapper.EquipmentMapper;
import com.xiaomu.labservice.module.repair.dto.RepairCreateDTO;
import com.xiaomu.labservice.module.repair.dto.RepairVO;
import com.xiaomu.labservice.module.repair.entity.Repair;
import com.xiaomu.labservice.module.repair.entity.RepairImage;
import com.xiaomu.labservice.module.repair.mapper.RepairImageMapper;
import com.xiaomu.labservice.module.repair.mapper.RepairMapper;
import com.xiaomu.labservice.module.repair.service.RepairService;
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
 * 报修服务实现
 */
@Service
@RequiredArgsConstructor
public class RepairServiceImpl implements RepairService {

    private final RepairMapper repairMapper;
    private final RepairImageMapper repairImageMapper;
    private final UserMapper userMapper;
    private final EquipmentMapper equipmentMapper;

    @Override
    public Page<RepairVO> getMyRepairs(Long page, Long size, String status) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        com.xiaomu.labservice.module.user.entity.User user = userMapper.selectByUsername(username);
        
        Page<Repair> repairPage = new Page<>(page, size);
        LambdaQueryWrapper<Repair> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Repair::getUserId, user.getId());
        
        if (StringUtils.hasText(status)) {
            wrapper.eq(Repair::getStatus, status);
        }
        
        wrapper.orderByDesc(Repair::getCreatedAt);
        Page<Repair> result = repairMapper.selectPage(repairPage, wrapper);
        
        Page<RepairVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        List<RepairVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return voPage;
    }

    @Override
    public RepairVO getRepairById(Long id) {
        Repair repair = repairMapper.selectById(id);
        if (repair == null) {
            throw new BusinessException(ResultCode.REPAIR_NOT_FOUND);
        }
        return convertToVO(repair);
    }

    @Override
    @Transactional
    public RepairVO createRepair(RepairCreateDTO createDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        com.xiaomu.labservice.module.user.entity.User user = userMapper.selectByUsername(username);
        
        Repair repair = new Repair();
        repair.setUserId(user.getId());
        repair.setEquipmentId(createDTO.getEquipmentId());
        repair.setTitle(createDTO.getTitle());
        repair.setDescription(createDTO.getDescription());
        repair.setUrgency(createDTO.getUrgency() != null ? createDTO.getUrgency() : 1);
        repair.setStatus(StatusConstant.RepairStatus.PENDING);
        
        repairMapper.insert(repair);
        return convertToVO(repair);
    }

    @Override
    @Transactional
    public RepairVO updateRepair(Long id, RepairCreateDTO updateDTO) {
        Repair repair = repairMapper.selectById(id);
        if (repair == null) {
            throw new BusinessException(ResultCode.REPAIR_NOT_FOUND);
        }
        
        if (!StatusConstant.RepairStatus.PENDING.equals(repair.getStatus())) {
            throw new BusinessException(ResultCode.REPAIR_PROCESSED);
        }
        
        repair.setTitle(updateDTO.getTitle());
        repair.setDescription(updateDTO.getDescription());
        repair.setUrgency(updateDTO.getUrgency());
        
        repairMapper.updateById(repair);
        return convertToVO(repair);
    }

    @Override
    @Transactional
    public void cancelRepair(Long id) {
        Repair repair = repairMapper.selectById(id);
        if (repair == null) {
            throw new BusinessException(ResultCode.REPAIR_NOT_FOUND);
        }
        
        if (!StatusConstant.RepairStatus.PENDING.equals(repair.getStatus())) {
            throw new BusinessException(ResultCode.REPAIR_PROCESSED);
        }
        
        repair.setStatus(StatusConstant.RepairStatus.CLOSED);
        repairMapper.updateById(repair);
    }

    @Override
    @Transactional
    public void uploadImage(Long repairId, String imageUrl) {
        Repair repair = repairMapper.selectById(repairId);
        if (repair == null) {
            throw new BusinessException(ResultCode.REPAIR_NOT_FOUND);
        }
        
        RepairImage image = new RepairImage();
        image.setRepairId(repairId);
        image.setImageUrl(imageUrl);
        repairImageMapper.insert(image);
    }

    @Override
    public Page<RepairVO> getAllRepairs(Long page, Long size, String status) {
        Page<Repair> repairPage = new Page<>(page, size);
        LambdaQueryWrapper<Repair> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(status)) {
            wrapper.eq(Repair::getStatus, status);
        }
        
        wrapper.orderByDesc(Repair::getCreatedAt);
        Page<Repair> result = repairMapper.selectPage(repairPage, wrapper);
        
        Page<RepairVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        List<RepairVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return voPage;
    }

    @Override
    @Transactional
    public void processRepair(Long id) {
        Repair repair = repairMapper.selectById(id);
        if (repair == null) {
            throw new BusinessException(ResultCode.REPAIR_NOT_FOUND);
        }
        
        if (!StatusConstant.RepairStatus.PENDING.equals(repair.getStatus())) {
            throw new BusinessException(ResultCode.REPAIR_PROCESSED);
        }
        
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        com.xiaomu.labservice.module.user.entity.User handler = userMapper.selectByUsername(username);
        
        repair.setStatus(StatusConstant.RepairStatus.PROCESSING);
        repair.setHandlerId(handler.getId());
        repair.setHandleTime(LocalDateTime.now());
        
        repairMapper.updateById(repair);
        
        // TODO: 发送通知消息
    }

    @Override
    @Transactional
    public void resolveRepair(Long id, String resolveNote) {
        Repair repair = repairMapper.selectById(id);
        if (repair == null) {
            throw new BusinessException(ResultCode.REPAIR_NOT_FOUND);
        }
        
        if (!StatusConstant.RepairStatus.PROCESSING.equals(repair.getStatus())) {
            throw new BusinessException(ResultCode.REPAIR_PROCESSED);
        }
        
        repair.setStatus(StatusConstant.RepairStatus.RESOLVED);
        repair.setResolveTime(LocalDateTime.now());
        repair.setResolveNote(resolveNote);
        
        repairMapper.updateById(repair);
        
        // TODO: 发送通知消息
    }

    @Override
    @Transactional
    public void closeRepair(Long id) {
        Repair repair = repairMapper.selectById(id);
        if (repair == null) {
            throw new BusinessException(ResultCode.REPAIR_NOT_FOUND);
        }
        
        repair.setStatus(StatusConstant.RepairStatus.CLOSED);
        repairMapper.updateById(repair);
    }

    private RepairVO convertToVO(Repair repair) {
        RepairVO vo = new RepairVO();
        BeanUtils.copyProperties(repair, vo);
        
        if (repair.getUserId() != null) {
            com.xiaomu.labservice.module.user.entity.User user = userMapper.selectById(repair.getUserId());
            if (user != null) {
                vo.setUserName(user.getNickname() != null ? user.getNickname() : user.getUsername());
            }
        }
        
        if (repair.getEquipmentId() != null) {
            com.xiaomu.labservice.module.equipment.entity.Equipment equipment = equipmentMapper.selectById(repair.getEquipmentId());
            if (equipment != null) {
                vo.setEquipmentName(equipment.getName());
            }
        }
        
        if (repair.getHandlerId() != null) {
            com.xiaomu.labservice.module.user.entity.User handler = userMapper.selectById(repair.getHandlerId());
            if (handler != null) {
                vo.setHandlerName(handler.getNickname() != null ? handler.getNickname() : handler.getUsername());
            }
        }
        
        List<RepairImage> images = repairImageMapper.selectByRepairId(repair.getId());
        vo.setImageUrls(images.stream()
                .map(RepairImage::getImageUrl)
                .collect(Collectors.toList()));
        
        return vo;
    }
}

