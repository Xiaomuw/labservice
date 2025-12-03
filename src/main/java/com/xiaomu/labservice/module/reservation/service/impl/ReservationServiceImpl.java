package com.xiaomu.labservice.module.reservation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaomu.labservice.common.constant.StatusConstant;
import com.xiaomu.labservice.common.exception.BusinessException;
import com.xiaomu.labservice.common.response.ResultCode;
import com.xiaomu.labservice.module.equipment.mapper.EquipmentMapper;
import com.xiaomu.labservice.module.lab.mapper.LabMapper;
import com.xiaomu.labservice.module.reservation.dto.ReservationCreateDTO;
import com.xiaomu.labservice.module.reservation.dto.ReservationVO;
import com.xiaomu.labservice.module.reservation.entity.Reservation;
import com.xiaomu.labservice.module.reservation.mapper.ReservationMapper;
import com.xiaomu.labservice.module.reservation.service.ReservationService;
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
 * 预约服务实现
 */
@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationMapper reservationMapper;
    private final UserMapper userMapper;
    private final LabMapper labMapper;
    private final EquipmentMapper equipmentMapper;

    @Override
    public Page<ReservationVO> getMyReservations(Long page, Long size, String status) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        com.xiaomu.labservice.module.user.entity.User user = userMapper.selectByUsername(username);
        
        Page<Reservation> reservationPage = new Page<>(page, size);
        LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Reservation::getUserId, user.getId());
        
        if (StringUtils.hasText(status)) {
            wrapper.eq(Reservation::getStatus, status);
        }
        
        wrapper.orderByDesc(Reservation::getCreatedAt);
        Page<Reservation> result = reservationMapper.selectPage(reservationPage, wrapper);
        
        Page<ReservationVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        List<ReservationVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return voPage;
    }

    @Override
    public ReservationVO getReservationById(Long id) {
        Reservation reservation = reservationMapper.selectById(id);
        if (reservation == null) {
            throw new BusinessException(ResultCode.RESERVATION_NOT_FOUND);
        }
        return convertToVO(reservation);
    }

    @Override
    @Transactional
    public ReservationVO createReservation(ReservationCreateDTO createDTO) {
        // 验证时间
        if (createDTO.getEndTime().isBefore(createDTO.getStartTime())) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "结束时间必须晚于开始时间");
        }
        
        // 检查时间冲突
        List<Reservation> conflicts = reservationMapper.checkTimeConflict(
                createDTO.getLabId(), createDTO.getStartTime(), createDTO.getEndTime());
        if (!conflicts.isEmpty()) {
            throw new BusinessException(ResultCode.RESERVATION_TIME_CONFLICT);
        }
        
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        com.xiaomu.labservice.module.user.entity.User user = userMapper.selectByUsername(username);
        
        Reservation reservation = new Reservation();
        reservation.setUserId(user.getId());
        reservation.setLabId(createDTO.getLabId());
        reservation.setEquipmentId(createDTO.getEquipmentId());
        reservation.setTitle(createDTO.getTitle());
        reservation.setPurpose(createDTO.getPurpose());
        reservation.setStartTime(createDTO.getStartTime());
        reservation.setEndTime(createDTO.getEndTime());
        reservation.setStatus(StatusConstant.ReservationStatus.PENDING);
        
        reservationMapper.insert(reservation);
        return convertToVO(reservation);
    }

    @Override
    @Transactional
    public ReservationVO updateReservation(Long id, ReservationCreateDTO updateDTO) {
        Reservation reservation = reservationMapper.selectById(id);
        if (reservation == null) {
            throw new BusinessException(ResultCode.RESERVATION_NOT_FOUND);
        }
        
        if (!StatusConstant.ReservationStatus.PENDING.equals(reservation.getStatus())) {
            throw new BusinessException(ResultCode.RESERVATION_STATUS_ERROR);
        }
        
        // 检查时间冲突（排除自己）
        List<Reservation> conflicts = reservationMapper.checkTimeConflict(
                updateDTO.getLabId(), updateDTO.getStartTime(), updateDTO.getEndTime());
        conflicts = conflicts.stream()
                .filter(r -> !r.getId().equals(id))
                .collect(Collectors.toList());
        if (!conflicts.isEmpty()) {
            throw new BusinessException(ResultCode.RESERVATION_TIME_CONFLICT);
        }
        
        reservation.setLabId(updateDTO.getLabId());
        reservation.setEquipmentId(updateDTO.getEquipmentId());
        reservation.setTitle(updateDTO.getTitle());
        reservation.setPurpose(updateDTO.getPurpose());
        reservation.setStartTime(updateDTO.getStartTime());
        reservation.setEndTime(updateDTO.getEndTime());
        
        reservationMapper.updateById(reservation);
        return convertToVO(reservation);
    }

    @Override
    @Transactional
    public void cancelReservation(Long id) {
        Reservation reservation = reservationMapper.selectById(id);
        if (reservation == null) {
            throw new BusinessException(ResultCode.RESERVATION_NOT_FOUND);
        }
        
        if (StatusConstant.ReservationStatus.COMPLETED.equals(reservation.getStatus()) ||
            StatusConstant.ReservationStatus.CANCELLED.equals(reservation.getStatus())) {
            throw new BusinessException(ResultCode.RESERVATION_STATUS_ERROR);
        }
        
        reservation.setStatus(StatusConstant.ReservationStatus.CANCELLED);
        reservationMapper.updateById(reservation);
    }

    @Override
    public Page<ReservationVO> getAllReservations(Long page, Long size, String status, Long labId) {
        Page<Reservation> reservationPage = new Page<>(page, size);
        LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(status)) {
            wrapper.eq(Reservation::getStatus, status);
        }
        if (labId != null) {
            wrapper.eq(Reservation::getLabId, labId);
        }
        
        wrapper.orderByDesc(Reservation::getCreatedAt);
        Page<Reservation> result = reservationMapper.selectPage(reservationPage, wrapper);
        
        Page<ReservationVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        List<ReservationVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return voPage;
    }

    @Override
    @Transactional
    public void approveReservation(Long id) {
        Reservation reservation = reservationMapper.selectById(id);
        if (reservation == null) {
            throw new BusinessException(ResultCode.RESERVATION_NOT_FOUND);
        }
        
        if (!StatusConstant.ReservationStatus.PENDING.equals(reservation.getStatus())) {
            throw new BusinessException(ResultCode.RESERVATION_STATUS_ERROR);
        }
        
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        var approver = userMapper.selectByUsername(username);
        
        reservation.setStatus(StatusConstant.ReservationStatus.APPROVED);
        reservation.setApproverId(approver.getId());
        reservation.setApproveTime(LocalDateTime.now());
        
        reservationMapper.updateById(reservation);
        
        // TODO: 发送通知消息
    }

    @Override
    @Transactional
    public void rejectReservation(Long id, String reason) {
        Reservation reservation = reservationMapper.selectById(id);
        if (reservation == null) {
            throw new BusinessException(ResultCode.RESERVATION_NOT_FOUND);
        }
        
        if (!StatusConstant.ReservationStatus.PENDING.equals(reservation.getStatus())) {
            throw new BusinessException(ResultCode.RESERVATION_STATUS_ERROR);
        }
        
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        var approver = userMapper.selectByUsername(username);
        
        reservation.setStatus(StatusConstant.ReservationStatus.REJECTED);
        reservation.setApproverId(approver.getId());
        reservation.setApproveTime(LocalDateTime.now());
        reservation.setRejectReason(reason);
        
        reservationMapper.updateById(reservation);
        
        // TODO: 发送通知消息
    }

    @Override
    @Transactional
    public void completeReservation(Long id) {
        Reservation reservation = reservationMapper.selectById(id);
        if (reservation == null) {
            throw new BusinessException(ResultCode.RESERVATION_NOT_FOUND);
        }
        
        if (!StatusConstant.ReservationStatus.APPROVED.equals(reservation.getStatus()) &&
            !StatusConstant.ReservationStatus.IN_USE.equals(reservation.getStatus())) {
            throw new BusinessException(ResultCode.RESERVATION_STATUS_ERROR);
        }
        
        reservation.setStatus(StatusConstant.ReservationStatus.COMPLETED);
        reservationMapper.updateById(reservation);
    }

    private ReservationVO convertToVO(Reservation reservation) {
        ReservationVO vo = new ReservationVO();
        BeanUtils.copyProperties(reservation, vo);
        
        if (reservation.getUserId() != null) {
            com.xiaomu.labservice.module.user.entity.User user = userMapper.selectById(reservation.getUserId());
            if (user != null) {
                vo.setUserName(user.getNickname() != null ? user.getNickname() : user.getUsername());
            }
        }
        
        if (reservation.getLabId() != null) {
            com.xiaomu.labservice.module.lab.entity.Lab lab = labMapper.selectById(reservation.getLabId());
            if (lab != null) {
                vo.setLabName(lab.getName());
            }
        }
        
        if (reservation.getEquipmentId() != null) {
            com.xiaomu.labservice.module.equipment.entity.Equipment equipment = equipmentMapper.selectById(reservation.getEquipmentId());
            if (equipment != null) {
                vo.setEquipmentName(equipment.getName());
            }
        }
        
        if (reservation.getApproverId() != null) {
            com.xiaomu.labservice.module.user.entity.User approver = userMapper.selectById(reservation.getApproverId());
            if (approver != null) {
                vo.setApproverName(approver.getNickname() != null ? approver.getNickname() : approver.getUsername());
            }
        }
        
        return vo;
    }
}

