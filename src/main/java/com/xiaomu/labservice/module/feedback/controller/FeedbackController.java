package com.xiaomu.labservice.module.feedback.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaomu.labservice.common.response.PageResult;
import com.xiaomu.labservice.common.response.Result;
import com.xiaomu.labservice.module.feedback.dto.FeedbackCreateDTO;
import com.xiaomu.labservice.module.feedback.dto.FeedbackVO;
import com.xiaomu.labservice.module.feedback.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 反馈控制器
 */
@Tag(name = "反馈管理", description = "反馈相关接口")
@RestController
@RequestMapping("/api/v1/feedbacks")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @Operation(summary = "查询我的反馈列表")
    @GetMapping("/me")
    public Result<PageResult<FeedbackVO>> getMyFeedbacks(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size) {
        Page<FeedbackVO> feedbackPage = feedbackService.getMyFeedbacks(page, size);
        return Result.success(PageResult.of(feedbackPage));
    }

    @Operation(summary = "查询反馈详情")
    @GetMapping("/{id}")
    public Result<FeedbackVO> getFeedbackById(@PathVariable Long id) {
        FeedbackVO feedback = feedbackService.getFeedbackById(id);
        return Result.success(feedback);
    }

    @Operation(summary = "提交反馈")
    @PostMapping
    public Result<FeedbackVO> createFeedback(@Valid @RequestBody FeedbackCreateDTO createDTO) {
        FeedbackVO feedback = feedbackService.createFeedback(createDTO);
        return Result.success("反馈提交成功", feedback);
    }

    @Operation(summary = "查询所有反馈列表（管理员）")
    @GetMapping("/admin/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'LAB_ADMIN')")
    public Result<PageResult<FeedbackVO>> getAllFeedbacks(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status) {
        Page<FeedbackVO> feedbackPage = feedbackService.getAllFeedbacks(page, size, type, status);
        return Result.success(PageResult.of(feedbackPage));
    }

    @Operation(summary = "回复反馈")
    @PutMapping("/admin/{id}/reply")
    @PreAuthorize("hasAnyRole('ADMIN', 'LAB_ADMIN')")
    public Result<Void> replyFeedback(@PathVariable Long id, @RequestParam String replyContent) {
        feedbackService.replyFeedback(id, replyContent);
        return Result.success();
    }

    @Operation(summary = "关闭反馈")
    @PutMapping("/admin/{id}/close")
    @PreAuthorize("hasAnyRole('ADMIN', 'LAB_ADMIN')")
    public Result<Void> closeFeedback(@PathVariable Long id) {
        feedbackService.closeFeedback(id);
        return Result.success();
    }
}

