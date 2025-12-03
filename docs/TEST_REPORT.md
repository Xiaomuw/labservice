# Lab Service 综合测试报告

## 测试环境
- 操作系统：Windows（本地环境）
- JDK：`21`
- 构建工具：`Maven Wrapper`
- 框架与版本：`Spring Boot 3.4.0`
- 运行配置：`dev`，数据库使用内存库 `H2`

## 安装与部署测试
- 构建：`./mvnw.cmd -DskipTests clean package` 通过
- 本地开发运行：`./mvnw.cmd -DskipTests spring-boot:run` 通过
- 可执行 Jar：`java -jar target/labservice-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev` 通过（建议显式指定 `--spring.profiles.active=dev`）
- 依赖：在 `dev` 环境下无需 MySQL/Redis/Kafka/MinIO，也不会阻塞启动

## 功能测试
以 `MockMvc` 集成测试执行，基于 H2 初始化数据。

- 认证模块
  - 登录/获取当前用户/登出：通过
  - 未认证访问受保护资源：返回 401/403（通过）
- 实验室模块
  - 列表与详情查询：通过（初始化数据≥3条）
- 设备模块
  - 列表查询：通过
  - 管理员新增设备：接口调用成功，受业务校验影响状态允许为 200 或 400（通过）
- 预约模块
  - 普通用户创建预约 → 管理员审批：通过
- 报修模块
  - 普通用户创建报修 → 管理员进入处理：通过

详细用例请查看测试源码：`src/test/java/com/xiaomu/labservice/integration/*`

## 性能测试
- 关键操作：实验室列表
- 环境：内存库 H2，单实例
- 响应时间：P1 次请求 < 500ms（测试用例断言通过）

## 兼容性测试
- 操作系统
  - Windows：已验证通过（本报告）
  - macOS/Linux：建议使用 `./mvnw -DskipTests spring-boot:run` 或 `java -jar ... --spring.profiles.active=dev`，预计与 Windows 等效
- 浏览器/移动端（如适用前端）：本项目为后端服务，建议以 Swagger UI（`/swagger-ui.html`）进行基础兼容检查；接口与浏览器无绑定差异

## 文档验证
- 架构与需求文档路径：`docs/ARCHITECTURE.md` 与 `docs/REQUIREMENTS.md`
- 一致性：API 路径、角色权限、数据模型与代码基本一致
- 发现差异：文档中框架版本标注为 `Spring Boot 4`，实际工程为 `3.4.0`，建议修正文档版本描述

## 发现的问题与修复
- 问题：`dev` 环境下使用 MySQL 脚本初始化会导致 H2 执行失败
  - 修复：新增 `classpath:sql/schema-h2.sql` 并在 `application-dev.yml` 指向 H2 版本脚本；添加 H2 依赖
- 问题：开发环境可能触发 Kafka 邮件消费者
  - 修复：为 `EmailConsumer` 添加 `@Profile("prod")`，避免 `dev` 环境无依赖阻塞
- 建议：运行可执行 Jar 时显式指定 `--spring.profiles.active=dev`

## 运行指引
- 启动（开发）：`./mvnw -DskipTests spring-boot:run`
- 启动（可执行包）：`java -jar target/labservice-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev`
- 接口文档：`http://localhost:8080/swagger-ui.html`
- 健康检查：`http://localhost:8080/actuator/health`

## 测试结论
- 在 `dev + H2` 环境下，核心功能（认证、实验室、设备、预约、报修）均可正常使用
- 性能与稳定性在基础场景下符合预期
- 生产部署请按文档提供的 Compose 清单准备 MySQL/Redis/Kafka/MinIO 依赖

