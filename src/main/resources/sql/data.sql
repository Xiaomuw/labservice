-- 实验室共享平台初始数据脚本

-- 插入默认管理员用户 (密码: admin123)
-- 密码使用 BCrypt 加密，对应明文为 admin123
INSERT INTO `user` (`username`, `password`, `email`, `nickname`, `role`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJ/2', 'admin@labservice.com', '系统管理员', 'ROLE_ADMIN', 1),
('labadmin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJ/2', 'labadmin@labservice.com', '实验室管理员', 'ROLE_LAB_ADMIN', 1),
('user1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJ/2', 'user1@labservice.com', '测试用户1', 'ROLE_USER', 1);

-- 插入示例实验室
INSERT INTO `lab` (`name`, `location`, `description`, `capacity`, `manager_id`, `status`, `open_time`, `close_time`) VALUES
('计算机实验室A', '教学楼101', '配备高性能计算机，用于软件开发和算法实验', 30, 2, 1, '08:00:00', '22:00:00'),
('物理实验室B', '实验楼201', '配备各类物理实验设备，用于物理实验教学', 20, 2, 1, '08:00:00', '18:00:00'),
('化学实验室C', '实验楼301', '配备化学实验设备，用于化学实验教学', 25, 2, 1, '08:00:00', '18:00:00');

-- 插入示例设备
INSERT INTO `equipment` (`lab_id`, `name`, `model`, `serial_number`, `description`, `status`, `purchase_date`, `warranty_date`) VALUES
(1, '高性能工作站', 'Dell Precision 5820', 'SN001', 'Intel Xeon处理器，32GB内存，用于高性能计算', 1, '2023-01-15', '2026-01-15'),
(1, '服务器', 'HP ProLiant DL380', 'SN002', '双路服务器，用于服务器集群实验', 1, '2023-02-20', '2026-02-20'),
(2, '示波器', 'Tektronix TBS1000', 'SN003', '数字示波器，用于信号分析', 1, '2023-03-10', '2026-03-10'),
(2, '万用表', 'Fluke 87V', 'SN004', '数字万用表，用于电路测量', 1, '2023-04-05', '2026-04-05'),
(3, '分析天平', 'Sartorius BSA224S', 'SN005', '精密分析天平，用于化学实验', 1, '2023-05-12', '2026-05-12');

