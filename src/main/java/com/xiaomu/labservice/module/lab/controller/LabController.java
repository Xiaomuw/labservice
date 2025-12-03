package com.xiaomu.labservice.module.lab.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaomu.labservice.common.response.PageResult;
import com.xiaomu.labservice.common.response.Result;
import com.xiaomu.labservice.module.lab.dto.LabVO;
import com.xiaomu.labservice.module.lab.service.LabService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 实验室控制器
 */
@Tag(name = "实验室管理", description = "实验室相关接口")
@RestController
@RequestMapping("/api/v1/labs")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class LabController {

    private final LabService labService;

    @Operation(summary = "查询实验室列表")
    @GetMapping
    public Result<PageResult<LabVO>> getLabList(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String keyword) {
        Page<LabVO> labPage = labService.getLabList(page, size, keyword);
        return Result.success(PageResult.of(labPage));
    }

    @Operation(summary = "查询实验室详情")
    @GetMapping("/{id}")
    public Result<LabVO> getLabById(@PathVariable Long id) {
        LabVO lab = labService.getLabById(id);
        return Result.success(lab);
    }

    @Operation(summary = "新增实验室")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LAB_ADMIN')")
    public Result<LabVO> createLab(@RequestBody LabVO labVO) {
        LabVO lab = labService.createLab(labVO);
        return Result.success("实验室创建成功", lab);
    }

    @Operation(summary = "编辑实验室")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LAB_ADMIN')")
    public Result<LabVO> updateLab(@PathVariable Long id, @RequestBody LabVO labVO) {
        LabVO lab = labService.updateLab(id, labVO);
        return Result.success("实验室更新成功", lab);
    }

    @Operation(summary = "删除实验室")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteLab(@PathVariable Long id) {
        labService.deleteLab(id);
        return Result.success();
    }
}

