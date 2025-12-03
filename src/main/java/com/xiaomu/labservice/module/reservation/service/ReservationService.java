package com.xiaomu.labservice.module.reservation.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaomu.labservice.module.reservation.dto.ReservationCreateDTO;
import com.xiaomu.labservice.module.reservation.dto.ReservationVO;

/**
 * 预约服务接口
 */
public interface ReservationService {
    Page<ReservationVO> getMyReservations(Long page, Long size, String status);
    ReservationVO getReservationById(Long id);
    ReservationVO createReservation(ReservationCreateDTO createDTO);
    ReservationVO updateReservation(Long id, ReservationCreateDTO updateDTO);
    void cancelReservation(Long id);
    Page<ReservationVO> getAllReservations(Long page, Long size, String status, Long labId);
    void approveReservation(Long id);
    void rejectReservation(Long id, String reason);
    void completeReservation(Long id);
}

