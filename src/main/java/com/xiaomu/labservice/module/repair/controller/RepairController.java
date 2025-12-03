package com.xiaomu.labservice.module.repair.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaomu.labservice.common.response.PageResult;
import com.xiaomu.labservice.common.response.Result;
import com.xiaomu.labservice.module.repair.dto.RepairCreateDTO;
import com.xiaomu.labservice.module.repair.dto.RepairVO;
import com.xiaomu.labservice.module.repair.service.RepairService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 报修控制器
 */
@Tag(name = "报修管理", description = "报修相关接口")
@RestController
@RequestMapping("/api/v1/repairs")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class RepairController {

    private final RepairService repairService;

    @Operation(summary = "查询我的报修列表")
    @GetMapping("/me")
    public Result<PageResult<RepairVO>> getMyRepairs(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String status) {
        Page<RepairVO> repairPage = repairService.getMyRepairs(page, size, status);
        return Result.success(PageResult.of(repairPage));
    }

    @Operation(summary = "查询报修详情")
    @GetMapping("/{id}")
    public Result<RepairVO> getRepairById(@PathVariable Long id) {
        RepairVO repair = repairService.getRepairById(id);
        return Result.success(repair);
    }

    @Operation(summary = "创建报修申请")
    @PostMapping
    public Result<RepairVO> createRepair(@Valid @RequestBody RepairCreateDTO createDTO) {
        RepairVO repair = repairService.createRepair(createDTO);
        return Result.success("报修申请创建成功", repair);
    }

    @Operation(summary = "编辑报修申请")
    @PutMapping("/{id}")
    public Result<RepairVO> updateRepair(@PathVariable Long id, @Valid @RequestBody RepairCreateDTO updateDTO) {
        RepairVO repair = repairService.updateRepair(id, updateDTO);
        return Result.success("报修申请更新成功", repair);
    }

    @Operation(summary = "取消报修申请")
    @DeleteMapping("/{id}")
    public Result<Void> cancelRepair(@PathVariable Long id) {
        repairService.cancelRepair(id);
        return Result.success();
    }

    @Operation(summary = "上传报修图片")
    @PostMapping("/{id}/images")
    public Result<Void> uploadImage(@PathVariable Long id, @RequestParam String imageUrl) {
        repairService.uploadImage(id, imageUrl);
        return Result.success();
    }

    @Operation(summary = "查询所有报修列表（管理员）")
    @GetMapping("/admin/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'LAB_ADMIN')")
    public Result<PageResult<RepairVO>> getAllRepairs(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String status) {
        Page<RepairVO> repairPage = repairService.getAllRepairs(page, size, status);
        return Result.success(PageResult.of(repairPage));
    }

    @Operation(summary = "开始处理报修")
    @PutMapping("/admin/{id}/process")
    @PreAuthorize("hasAnyRole('ADMIN', 'LAB_ADMIN')")
    public Result<Void> processRepair(@PathVariable Long id) {
        repairService.processRepair(id);
        return Result.success();
    }

    @Operation(summary = "标记已解决")
    @PutMapping("/admin/{id}/resolve")
    @PreAuthorize("hasAnyRole('ADMIN', 'LAB_ADMIN')")
    public Result<Void> resolveRepair(@PathVariable Long id, @RequestParam String resolveNote) {
        repairService.resolveRepair(id, resolveNote);
        return Result.success();
    }

    @Operation(summary = "关闭报修")
    @PutMapping("/admin/{id}/close")
    @PreAuthorize("hasAnyRole('ADMIN', 'LAB_ADMIN')")
    public Result<Void> closeRepair(@PathVariable Long id) {
        repairService.closeRepair(id);
        return Result.success();
    }
}

