package com.xiaomu.labservice.module.file.service.impl;

import com.xiaomu.labservice.common.config.MinioConfig;
import com.xiaomu.labservice.common.exception.BusinessException;
import com.xiaomu.labservice.common.response.ResultCode;
import com.xiaomu.labservice.common.util.FileUtil;
import com.xiaomu.labservice.module.file.service.FileService;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

/**
 * MinIO 文件服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MinioFileServiceImpl implements FileService {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    @Value("${minio.buckets.avatar}")
    private String avatarBucket;

    @Value("${minio.buckets.equipment}")
    private String equipmentBucket;

    @Value("${minio.buckets.repair}")
    private String repairBucket;

    @Override
    public String uploadFile(String bucket, String objectName, MultipartFile file) {
        try {
            // 验证文件
            if (!FileUtil.isImage(file)) {
                throw new BusinessException(ResultCode.FILE_TYPE_NOT_SUPPORTED);
            }

            // 确保桶存在
            ensureBucketExists(bucket);

            // 生成唯一文件名
            String fileName = generateFileName(objectName, file.getOriginalFilename());

            // 上传文件
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(fileName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());

            // 返回访问 URL
            return getFileUrl(bucket, fileName);

        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new BusinessException(ResultCode.FILE_UPLOAD_ERROR);
        }
    }

    @Override
    public void deleteFile(String bucket, String objectName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            log.error("文件删除失败", e);
            throw new BusinessException(ResultCode.FILE_DELETE_ERROR);
        }
    }

    @Override
    public InputStream downloadFile(String bucket, String objectName) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            log.error("文件下载失败", e);
            throw new BusinessException(ResultCode.FILE_NOT_FOUND);
        }
    }

    @Override
    public String getFileUrl(String bucket, String objectName) {
        return String.format("%s/%s/%s", minioConfig.getEndpoint(), bucket, objectName);
    }

    private void ensureBucketExists(String bucket) throws Exception {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(bucket)
                .build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucket)
                    .build());
        }
    }

    private String generateFileName(String prefix, String originalName) {
        String extension = FileUtil.getFileExtension(originalName);
        return prefix + "/" + UUID.randomUUID() + extension;
    }
}

