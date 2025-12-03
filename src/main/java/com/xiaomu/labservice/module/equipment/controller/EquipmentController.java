package com.xiaomu.labservice.module.equipment.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaomu.labservice.common.response.PageResult;
import com.xiaomu.labservice.common.response.Result;
import com.xiaomu.labservice.module.equipment.dto.EquipmentVO;
import com.xiaomu.labservice.module.equipment.service.EquipmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 设备控制器
 */
@Tag(name = "设备管理", description = "设备相关接口")
@RestController
@RequestMapping("/api/v1/equipments")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class EquipmentController {

    private final EquipmentService equipmentService;

    @Operation(summary = "查询设备列表")
    @GetMapping
    public Result<PageResult<EquipmentVO>> getEquipmentList(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Long labId,
            @RequestParam(required = false) String keyword) {
        Page<EquipmentVO> equipmentPage = equipmentService.getEquipmentList(page, size, labId, keyword);
        return Result.success(PageResult.of(equipmentPage));
    }

    @Operation(summary = "查询设备详情")
    @GetMapping("/{id}")
    public Result<EquipmentVO> getEquipmentById(@PathVariable Long id) {
        EquipmentVO equipment = equipmentService.getEquipmentById(id);
        return Result.success(equipment);
    }

    @Operation(summary = "查询可用设备")
    @GetMapping("/available")
    public Result<PageResult<EquipmentVO>> getAvailableEquipment(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Long labId) {
        Page<EquipmentVO> equipmentPage = equipmentService.getAvailableEquipment(page, size, labId);
        return Result.success(PageResult.of(equipmentPage));
    }

    @Operation(summary = "新增设备")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LAB_ADMIN')")
    public Result<EquipmentVO> createEquipment(@RequestBody EquipmentVO equipmentVO) {
        EquipmentVO equipment = equipmentService.createEquipment(equipmentVO);
        return Result.success("设备创建成功", equipment);
    }

    @Operation(summary = "编辑设备")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LAB_ADMIN')")
    public Result<EquipmentVO> updateEquipment(@PathVariable Long id, @RequestBody EquipmentVO equipmentVO) {
        EquipmentVO equipment = equipmentService.updateEquipment(id, equipmentVO);
        return Result.success("设备更新成功", equipment);
    }

    @Operation(summary = "删除设备")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LAB_ADMIN')")
    public Result<Void> deleteEquipment(@PathVariable Long id) {
        equipmentService.deleteEquipment(id);
        return Result.success();
    }

    @Operation(summary = "上传设备图片")
    @PostMapping("/{id}/images")
    @PreAuthorize("hasAnyRole('ADMIN', 'LAB_ADMIN')")
    public Result<Void> uploadImage(@PathVariable Long id, @RequestParam String imageUrl) {
        equipmentService.uploadImage(id, imageUrl);
        return Result.success();
    }

    @Operation(summary = "删除设备图片")
    @DeleteMapping("/{id}/images/{imageId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LAB_ADMIN')")
    public Result<Void> deleteImage(@PathVariable Long id, @PathVariable Long imageId) {
        equipmentService.deleteImage(id, imageId);
        return Result.success();
    }
}

