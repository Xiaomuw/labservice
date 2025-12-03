package com.xiaomu.labservice.module.file.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * 文件服务接口
 */
public interface FileService {
    String uploadFile(String bucket, String objectName, MultipartFile file);
    void deleteFile(String bucket, String objectName);
    InputStream downloadFile(String bucket, String objectName);
    String getFileUrl(String bucket, String objectName);
}

