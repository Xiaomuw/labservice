package com.xiaomu.labservice.module.reservation.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaomu.labservice.common.response.PageResult;
import com.xiaomu.labservice.common.response.Result;
import com.xiaomu.labservice.module.reservation.dto.ReservationCreateDTO;
import com.xiaomu.labservice.module.reservation.dto.ReservationVO;
import com.xiaomu.labservice.module.reservation.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 预约控制器
 */
@Tag(name = "预约管理", description = "预约相关接口")
@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class ReservationController {

    private final ReservationService reservationService;

    @Operation(summary = "查询我的预约列表")
    @GetMapping("/me")
    public Result<PageResult<ReservationVO>> getMyReservations(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String status) {
        Page<ReservationVO> reservationPage = reservationService.getMyReservations(page, size, status);
        return Result.success(PageResult.of(reservationPage));
    }

    @Operation(summary = "查询预约详情")
    @GetMapping("/{id}")
    public Result<ReservationVO> getReservationById(@PathVariable Long id) {
        ReservationVO reservation = reservationService.getReservationById(id);
        return Result.success(reservation);
    }

    @Operation(summary = "创建预约申请")
    @PostMapping
    public Result<ReservationVO> createReservation(@Valid @RequestBody ReservationCreateDTO createDTO) {
        ReservationVO reservation = reservationService.createReservation(createDTO);
        return Result.success("预约申请创建成功", reservation);
    }

    @Operation(summary = "编辑预约申请")
    @PutMapping("/{id}")
    public Result<ReservationVO> updateReservation(@PathVariable Long id, @Valid @RequestBody ReservationCreateDTO updateDTO) {
        ReservationVO reservation = reservationService.updateReservation(id, updateDTO);
        return Result.success("预约申请更新成功", reservation);
    }

    @Operation(summary = "取消预约申请")
    @DeleteMapping("/{id}")
    public Result<Void> cancelReservation(@PathVariable Long id) {
        reservationService.cancelReservation(id);
        return Result.success();
    }

    @Operation(summary = "查询所有预约列表（管理员）")
    @GetMapping("/admin/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'LAB_ADMIN')")
    public Result<PageResult<ReservationVO>> getAllReservations(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long labId) {
        Page<ReservationVO> reservationPage = reservationService.getAllReservations(page, size, status, labId);
        return Result.success(PageResult.of(reservationPage));
    }

    @Operation(summary = "批准预约")
    @PutMapping("/admin/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'LAB_ADMIN')")
    public Result<Void> approveReservation(@PathVariable Long id) {
        reservationService.approveReservation(id);
        return Result.success();
    }

    @Operation(summary = "拒绝预约")
    @PutMapping("/admin/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'LAB_ADMIN')")
    public Result<Void> rejectReservation(@PathVariable Long id, @RequestParam String reason) {
        reservationService.rejectReservation(id, reason);
        return Result.success();
    }

    @Operation(summary = "标记完成")
    @PutMapping("/admin/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'LAB_ADMIN')")
    public Result<Void> completeReservation(@PathVariable Long id) {
        reservationService.completeReservation(id);
        return Result.success();
    }
}

