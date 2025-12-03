package com.xiaomu.labservice.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件工具类
 */
@Slf4j
public class FileUtil {

    /**
     * 允许的图片类型
     */
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif"
    );

    /**
     * 允许的图片扩展名
     */
    private static final List<String> ALLOWED_IMAGE_EXTENSIONS = Arrays.asList(
            ".jpg", ".jpeg", ".png", ".gif"
    );

    /**
     * 头像最大大小 (5MB)
     */
    public static final long AVATAR_MAX_SIZE = 5 * 1024 * 1024;

    /**
     * 设备/报修图片最大大小 (10MB)
     */
    public static final long IMAGE_MAX_SIZE = 10 * 1024 * 1024;

    /**
     * 验证文件类型是否为图片
     */
    public static boolean isImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }
        String contentType = file.getContentType();
        return contentType != null && ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase());
    }

    /**
     * 验证文件扩展名
     */
    public static boolean isValidImageExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return false;
        }
        String extension = getFileExtension(filename).toLowerCase();
        return ALLOWED_IMAGE_EXTENSIONS.contains(extension);
    }

    /**
     * 获取文件扩展名
     */
    public static String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(lastDotIndex);
    }

    /**
     * 生成唯一文件名
     */
    public static String generateFileName(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        return UUID.randomUUID().toString() + extension;
    }

    /**
     * 生成带前缀的文件名
     */
    public static String generateFileName(String prefix, String originalFilename) {
        String extension = getFileExtension(originalFilename);
        return prefix + "/" + UUID.randomUUID().toString() + extension;
    }

    /**
     * 验证文件大小
     */
    public static boolean isValidSize(MultipartFile file, long maxSize) {
        return file != null && file.getSize() <= maxSize;
    }
}
