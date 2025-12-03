-- 实验室共享平台数据库建表脚本

-- 创建数据库（H2 内存模式无需显式创建，保留以兼容 MySQL 语法）
-- CREATE DATABASE IF NOT EXISTS `labservice` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '密码',
  `email` VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
  `phone` VARCHAR(20) COMMENT '手机号',
  `nickname` VARCHAR(50) COMMENT '昵称',
  `avatar` VARCHAR(255) COMMENT '头像 URL',
  `role` VARCHAR(20) NOT NULL DEFAULT 'ROLE_USER' COMMENT '角色',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_email` (`email`),
  INDEX `idx_username` (`username`),
  INDEX `idx_status` (`status`)
) COMMENT='用户表';

-- 实验室表
CREATE TABLE IF NOT EXISTS `lab` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  `name` VARCHAR(100) NOT NULL COMMENT '实验室名称',
  `location` VARCHAR(200) COMMENT '位置',
  `description` TEXT COMMENT '描述',
  `capacity` INT COMMENT '容纳人数',
  `manager_id` BIGINT COMMENT '负责人 ID',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-关闭，1-开放',
  `open_time` TIME COMMENT '开放时间',
  `close_time` TIME COMMENT '关闭时间',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_status` (`status`),
  INDEX `idx_manager` (`manager_id`)
) COMMENT='实验室表';

-- 设备表
CREATE TABLE IF NOT EXISTS `equipment` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  `lab_id` BIGINT NOT NULL COMMENT '所属实验室 ID',
  `name` VARCHAR(100) NOT NULL COMMENT '设备名称',
  `model` VARCHAR(100) COMMENT '型号',
  `serial_number` VARCHAR(100) COMMENT '序列号',
  `description` TEXT COMMENT '描述',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-维修中，1-正常，2-报废',
  `purchase_date` DATE COMMENT '购置日期',
  `warranty_date` DATE COMMENT '保修截止日期',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_lab` (`lab_id`),
  INDEX `idx_status` (`status`),
  INDEX `idx_lab_status` (`lab_id`, `status`)
) COMMENT='设备表';

-- 设备图片表
CREATE TABLE IF NOT EXISTS `equipment_image` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  `equipment_id` BIGINT NOT NULL COMMENT '设备 ID',
  `image_url` VARCHAR(255) NOT NULL COMMENT '图片 URL',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX `idx_equipment` (`equipment_id`)
) COMMENT='设备图片表';

-- 预约表
CREATE TABLE IF NOT EXISTS `reservation` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '申请人 ID',
  `lab_id` BIGINT NOT NULL COMMENT '实验室 ID',
  `equipment_id` BIGINT COMMENT '设备 ID (可选)',
  `title` VARCHAR(200) NOT NULL COMMENT '预约标题',
  `purpose` TEXT COMMENT '使用目的',
  `start_time` DATETIME NOT NULL COMMENT '开始时间',
  `end_time` DATETIME NOT NULL COMMENT '结束时间',
  `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING-待审批，APPROVED-已批准，REJECTED-已拒绝，IN_USE-使用中，COMPLETED-已完成，CANCELLED-已取消',
  `approver_id` BIGINT COMMENT '审批人 ID',
  `approve_time` DATETIME COMMENT '审批时间',
  `reject_reason` VARCHAR(500) COMMENT '拒绝原因',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_user` (`user_id`),
  INDEX `idx_lab` (`lab_id`),
  INDEX `idx_equipment` (`equipment_id`),
  INDEX `idx_status` (`status`),
  INDEX `idx_time` (`start_time`, `end_time`),
  INDEX `idx_user_status` (`user_id`, `status`)
) COMMENT='预约表';

-- 报修表
CREATE TABLE IF NOT EXISTS `repair` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '报修人 ID',
  `equipment_id` BIGINT NOT NULL COMMENT '设备 ID',
  `title` VARCHAR(200) NOT NULL COMMENT '报修标题',
  `description` TEXT COMMENT '故障描述',
  `urgency` TINYINT NOT NULL DEFAULT 1 COMMENT '紧急程度：1-一般，2-紧急，3-非常紧急',
  `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING-待处理，PROCESSING-处理中，RESOLVED-已解决，CLOSED-已关闭',
  `handler_id` BIGINT COMMENT '处理人 ID',
  `handle_time` DATETIME COMMENT '处理时间',
  `resolve_time` DATETIME COMMENT '解决时间',
  `resolve_note` TEXT COMMENT '处理记录',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_user` (`user_id`),
  INDEX `idx_equipment` (`equipment_id`),
  INDEX `idx_status` (`status`),
  INDEX `idx_user_status` (`user_id`, `status`)
) COMMENT='报修表';

-- 报修图片表
CREATE TABLE IF NOT EXISTS `repair_image` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  `repair_id` BIGINT NOT NULL COMMENT '报修 ID',
  `image_url` VARCHAR(255) NOT NULL COMMENT '图片 URL',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX `idx_repair` (`repair_id`)
) COMMENT='报修图片表';

-- 反馈表
CREATE TABLE IF NOT EXISTS `feedback` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `type` VARCHAR(20) NOT NULL COMMENT '类型：RESERVATION-预约反馈，REPAIR-报修反馈，OTHER-其他',
  `target_id` BIGINT COMMENT '关联 ID (预约或报修 ID)',
  `content` TEXT NOT NULL COMMENT '反馈内容',
  `rating` TINYINT COMMENT '评分：1-5',
  `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING-待处理，REPLIED-已回复，CLOSED-已关闭',
  `reply_content` TEXT COMMENT '回复内容',
  `reply_time` DATETIME COMMENT '回复时间',
  `replier_id` BIGINT COMMENT '回复人 ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_user` (`user_id`),
  INDEX `idx_type` (`type`),
  INDEX `idx_status` (`status`),
  INDEX `idx_user_status` (`user_id`, `status`)
) COMMENT='反馈表';

