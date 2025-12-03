package com.xiaomu.labservice.module.file.controller;

import com.xiaomu.labservice.common.response.Result;
import com.xiaomu.labservice.module.file.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件控制器
 */
@Tag(name = "文件管理", description = "文件上传下载相关接口")
@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class FileController {

    private final FileService fileService;

    @Operation(summary = "上传文件")
    @PostMapping("/upload")
    public Result<String> uploadFile(
            @RequestParam String bucket,
            @RequestParam String objectName,
            @RequestParam("file") MultipartFile file) {
        String url = fileService.uploadFile(bucket, objectName, file);
        return Result.success("文件上传成功", url);
    }

    @Operation(summary = "删除文件")
    @DeleteMapping("/{bucket}/{objectName}")
    public Result<Void> deleteFile(
            @PathVariable String bucket,
            @PathVariable String objectName) {
        fileService.deleteFile(bucket, objectName);
        return Result.success();
    }
}

