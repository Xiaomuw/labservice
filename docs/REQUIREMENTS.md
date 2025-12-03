# 實驗室共享平台 (Lab Service) 需求文檔

## 📋 項目概述

### 項目名稱
Lab Service - 實驗室共享平台後端服務

### 項目描述
一個基於 Spring Boot 構建的實驗室資源共享平台，提供實驗室管理、設備管理、預約管理、報修管理等功能，旨在提高實驗室資源的利用率和管理效率。

### 技術棧
| 類別 | 技術 |
|------|------|
| 語言 | Java 21 |
| 框架 | Spring Boot 4.0.0 |
| 資料庫 | MySQL 8.0 |
| 快取 | Redis |
| 消息隊列 | Kafka |
| 文件存儲 | MinIO |
| 安全認證 | Spring Security + JWT |
| API 文檔 | Springdoc OpenAPI (Swagger) |
| ORM | MyBatis-Plus |
| 構建工具 | Maven |

---

## 🏗️ 系統架構

### 模塊結構
```
com.xiaomu.labservice
├── common                    # 通用模塊
│   ├── config               # 配置類
│   ├── constant             # 常量定義
│   ├── exception            # 自定義異常
│   ├── response             # 統一響應封裝
│   └── util                 # 工具類
├── security                  # 安全模塊
│   ├── config               # 安全配置
│   ├── filter               # JWT 過濾器
│   └── service              # 安全服務
├── module                    # 業務模塊
│   ├── user                 # 用戶模塊
│   ├── lab                  # 實驗室模塊
│   ├── equipment            # 設備模塊
│   ├── reservation          # 預約模塊
│   ├── repair               # 報修模塊
│   ├── feedback             # 反饋模塊
│   └── file                 # 文件服務模塊
├── mq                        # 消息隊列模塊
│   ├── producer             # 消息生產者
│   └── consumer             # 消息消費者
└── LabserviceApplication.java
```

---

## 🔐 一、用戶認證與管理模塊

### 1.1 功能概述
提供完整的用戶認證和管理功能，支持多角色權限控制。

### 1.2 用戶角色定義
| 角色 | 角色編碼 | 描述 |
|------|----------|------|
| 普通用戶 | `ROLE_USER` | 可預約實驗室、提交報修、查看設備信息 |
| 實驗室管理員 | `ROLE_LAB_ADMIN` | 管理特定實驗室及其設備 |
| 系統管理員 | `ROLE_ADMIN` | 擁有所有權限，管理用戶和系統配置 |

### 1.3 API 接口設計

#### 1.3.1 用戶認證接口
| 方法 | 路徑 | 描述 | 權限 |
|------|------|------|------|
| POST | `/api/v1/auth/register` | 用戶註冊 | 公開 |
| POST | `/api/v1/auth/login` | 用戶登錄 | 公開 |
| POST | `/api/v1/auth/logout` | 用戶登出 | 已認證 |
| POST | `/api/v1/auth/refresh-token` | 刷新 Token | 已認證 |
| POST | `/api/v1/auth/send-code` | 發送驗證碼 | 公開 |
| POST | `/api/v1/auth/reset-password` | 重置密碼 | 公開 |

#### 1.3.2 用戶信息接口
| 方法 | 路徑 | 描述 | 權限 |
|------|------|------|------|
| GET | `/api/v1/users/me` | 獲取當前用戶信息 | 已認證 |
| PUT | `/api/v1/users/me` | 更新當前用戶信息 | 已認證 |
| PUT | `/api/v1/users/me/password` | 修改密碼 | 已認證 |
| POST | `/api/v1/users/me/avatar` | 上傳頭像 | 已認證 |
| DELETE | `/api/v1/users/me` | 注銷賬戶 | 已認證 |

#### 1.3.3 用戶管理接口 (管理員)
| 方法 | 路徑 | 描述 | 權限 |
|------|------|------|------|
| GET | `/api/v1/admin/users` | 查詢用戶列表 (分頁) | ADMIN |
| GET | `/api/v1/admin/users/{id}` | 查詢用戶詳情 | ADMIN |
| POST | `/api/v1/admin/users` | 新增用戶 | ADMIN |
| PUT | `/api/v1/admin/users/{id}` | 編輯用戶 | ADMIN |
| DELETE | `/api/v1/admin/users/{id}` | 刪除用戶 | ADMIN |
| PUT | `/api/v1/admin/users/{id}/status` | 禁用/啟用用戶 | ADMIN |

### 1.4 數據模型

#### User 用戶表
| 字段 | 類型 | 說明 |
|------|------|------|
| id | BIGINT | 主鍵 |
| username | VARCHAR(50) | 用戶名，唯一 |
| password | VARCHAR(255) | 密碼 (BCrypt 加密) |
| email | VARCHAR(100) | 郵箱，唯一 |
| phone | VARCHAR(20) | 手機號 |
| nickname | VARCHAR(50) | 暱稱 |
| avatar | VARCHAR(255) | 頭像 URL |
| role | VARCHAR(20) | 角色 |
| status | TINYINT | 狀態：0-禁用，1-正常 |
| created_at | DATETIME | 創建時間 |
| updated_at | DATETIME | 更新時間 |

---

## 🏢 二、實驗室管理模塊

### 2.1 功能概述
管理實驗室基本信息，支持增刪改查操作。

### 2.2 API 接口設計

| 方法 | 路徑 | 描述 | 權限 |
|------|------|------|------|
| GET | `/api/v1/labs` | 查詢實驗室列表 (分頁) | 已認證 |
| GET | `/api/v1/labs/{id}` | 查詢實驗室詳情 | 已認證 |
| POST | `/api/v1/labs` | 新增實驗室 | ADMIN, LAB_ADMIN |
| PUT | `/api/v1/labs/{id}` | 編輯實驗室 | ADMIN, LAB_ADMIN |
| DELETE | `/api/v1/labs/{id}` | 刪除實驗室 | ADMIN |
| GET | `/api/v1/labs/{id}/equipments` | 查詢實驗室下的設備列表 | 已認證 |
| GET | `/api/v1/labs/{id}/reservations` | 查詢實驗室的預約記錄 | 已認證 |

### 2.3 數據模型

#### Lab 實驗室表
| 字段 | 類型 | 說明 |
|------|------|------|
| id | BIGINT | 主鍵 |
| name | VARCHAR(100) | 實驗室名稱 |
| location | VARCHAR(200) | 位置 |
| description | TEXT | 描述 |
| capacity | INT | 容納人數 |
| manager_id | BIGINT | 負責人 ID |
| status | TINYINT | 狀態：0-關閉，1-開放 |
| open_time | TIME | 開放時間 |
| close_time | TIME | 關閉時間 |
| created_at | DATETIME | 創建時間 |
| updated_at | DATETIME | 更新時間 |

---

## 🔧 三、設備管理模塊

### 3.1 功能概述
管理實驗室設備信息，支持設備圖片上傳。

### 3.2 API 接口設計

| 方法 | 路徑 | 描述 | 權限 |
|------|------|------|------|
| GET | `/api/v1/equipments` | 查詢設備列表 (分頁) | 已認證 |
| GET | `/api/v1/equipments/{id}` | 查詢設備詳情 | 已認證 |
| POST | `/api/v1/equipments` | 新增設備 | ADMIN, LAB_ADMIN |
| PUT | `/api/v1/equipments/{id}` | 編輯設備 | ADMIN, LAB_ADMIN |
| DELETE | `/api/v1/equipments/{id}` | 刪除設備 | ADMIN, LAB_ADMIN |
| POST | `/api/v1/equipments/{id}/images` | 上傳設備圖片 | ADMIN, LAB_ADMIN |
| DELETE | `/api/v1/equipments/{id}/images/{imageId}` | 刪除設備圖片 | ADMIN, LAB_ADMIN |
| GET | `/api/v1/equipments/available` | 查詢可用設備 | 已認證 |

### 3.3 數據模型

#### Equipment 設備表
| 字段 | 類型 | 說明 |
|------|------|------|
| id | BIGINT | 主鍵 |
| lab_id | BIGINT | 所屬實驗室 ID |
| name | VARCHAR(100) | 設備名稱 |
| model | VARCHAR(100) | 型號 |
| serial_number | VARCHAR(100) | 序列號 |
| description | TEXT | 描述 |
| status | TINYINT | 狀態：0-維修中，1-正常，2-報廢 |
| purchase_date | DATE | 購置日期 |
| warranty_date | DATE | 保修截止日期 |
| created_at | DATETIME | 創建時間 |
| updated_at | DATETIME | 更新時間 |

#### EquipmentImage 設備圖片表
| 字段 | 類型 | 說明 |
|------|------|------|
| id | BIGINT | 主鍵 |
| equipment_id | BIGINT | 設備 ID |
| image_url | VARCHAR(255) | 圖片 URL |
| sort_order | INT | 排序 |
| created_at | DATETIME | 創建時間 |

---

## 📅 四、預約管理模塊

### 4.1 功能概述
用戶可預約實驗室或設備，管理員可審批預約申請。

### 4.2 預約狀態流轉
```
PENDING(待審批) -> APPROVED(已批准) -> IN_USE(使用中) -> COMPLETED(已完成)
                -> REJECTED(已拒絕)
                -> CANCELLED(已取消)
```

### 4.3 API 接口設計

#### 4.3.1 用戶預約接口
| 方法 | 路徑 | 描述 | 權限 |
|------|------|------|------|
| GET | `/api/v1/reservations/me` | 查詢我的預約列表 | 已認證 |
| GET | `/api/v1/reservations/{id}` | 查詢預約詳情 | 已認證 |
| POST | `/api/v1/reservations` | 創建預約申請 | 已認證 |
| PUT | `/api/v1/reservations/{id}` | 編輯預約申請 | 已認證 |
| DELETE | `/api/v1/reservations/{id}` | 取消預約申請 | 已認證 |

#### 4.3.2 預約管理接口 (管理員)
| 方法 | 路徑 | 描述 | 權限 |
|------|------|------|------|
| GET | `/api/v1/admin/reservations` | 查詢所有預約列表 | ADMIN, LAB_ADMIN |
| PUT | `/api/v1/admin/reservations/{id}/approve` | 批准預約 | ADMIN, LAB_ADMIN |
| PUT | `/api/v1/admin/reservations/{id}/reject` | 拒絕預約 | ADMIN, LAB_ADMIN |
| PUT | `/api/v1/admin/reservations/{id}/complete` | 標記完成 | ADMIN, LAB_ADMIN |

### 4.4 數據模型

#### Reservation 預約表
| 字段 | 類型 | 說明 |
|------|------|------|
| id | BIGINT | 主鍵 |
| user_id | BIGINT | 申請人 ID |
| lab_id | BIGINT | 實驗室 ID |
| equipment_id | BIGINT | 設備 ID (可選) |
| title | VARCHAR(200) | 預約標題 |
| purpose | TEXT | 使用目的 |
| start_time | DATETIME | 開始時間 |
| end_time | DATETIME | 結束時間 |
| status | VARCHAR(20) | 狀態 |
| approver_id | BIGINT | 審批人 ID |
| approve_time | DATETIME | 審批時間 |
| reject_reason | VARCHAR(500) | 拒絕原因 |
| created_at | DATETIME | 創建時間 |
| updated_at | DATETIME | 更新時間 |

---

## 🛠️ 五、報修管理模塊

### 5.1 功能概述
用戶可提交設備故障報修申請，管理員可處理報修工單。

### 5.2 報修狀態流轉
```
PENDING(待處理) -> PROCESSING(處理中) -> RESOLVED(已解決)
                -> CLOSED(已關閉)
```

### 5.3 API 接口設計

#### 5.3.1 用戶報修接口
| 方法 | 路徑 | 描述 | 權限 |
|------|------|------|------|
| GET | `/api/v1/repairs/me` | 查詢我的報修列表 | 已認證 |
| GET | `/api/v1/repairs/{id}` | 查詢報修詳情 | 已認證 |
| POST | `/api/v1/repairs` | 創建報修申請 | 已認證 |
| PUT | `/api/v1/repairs/{id}` | 編輯報修申請 | 已認證 |
| DELETE | `/api/v1/repairs/{id}` | 取消報修申請 | 已認證 |
| POST | `/api/v1/repairs/{id}/images` | 上傳報修圖片 | 已認證 |

#### 5.3.2 報修管理接口 (管理員)
| 方法 | 路徑 | 描述 | 權限 |
|------|------|------|------|
| GET | `/api/v1/admin/repairs` | 查詢所有報修列表 | ADMIN, LAB_ADMIN |
| PUT | `/api/v1/admin/repairs/{id}/process` | 開始處理報修 | ADMIN, LAB_ADMIN |
| PUT | `/api/v1/admin/repairs/{id}/resolve` | 標記已解決 | ADMIN, LAB_ADMIN |
| PUT | `/api/v1/admin/repairs/{id}/close` | 關閉報修 | ADMIN, LAB_ADMIN |

### 5.4 數據模型

#### Repair 報修表
| 字段 | 類型 | 說明 |
|------|------|------|
| id | BIGINT | 主鍵 |
| user_id | BIGINT | 報修人 ID |
| equipment_id | BIGINT | 設備 ID |
| title | VARCHAR(200) | 報修標題 |
| description | TEXT | 故障描述 |
| urgency | TINYINT | 緊急程度：1-一般，2-緊急，3-非常緊急 |
| status | VARCHAR(20) | 狀態 |
| handler_id | BIGINT | 處理人 ID |
| handle_time | DATETIME | 處理時間 |
| resolve_time | DATETIME | 解決時間 |
| resolve_note | TEXT | 處理記錄 |
| created_at | DATETIME | 創建時間 |
| updated_at | DATETIME | 更新時間 |

#### RepairImage 報修圖片表
| 字段 | 類型 | 說明 |
|------|------|------|
| id | BIGINT | 主鍵 |
| repair_id | BIGINT | 報修 ID |
| image_url | VARCHAR(255) | 圖片 URL |
| created_at | DATETIME | 創建時間 |

---

## 💬 六、反饋管理模塊

### 6.1 功能概述
用戶可對預約和報修過程提交反饋，管理員可查看和處理反饋。

### 6.2 API 接口設計

#### 6.2.1 用戶反饋接口
| 方法 | 路徑 | 描述 | 權限 |
|------|------|------|------|
| GET | `/api/v1/feedbacks/me` | 查詢我的反饋列表 | 已認證 |
| GET | `/api/v1/feedbacks/{id}` | 查詢反饋詳情 | 已認證 |
| POST | `/api/v1/feedbacks` | 提交反饋 | 已認證 |

#### 6.2.2 反饋管理接口 (管理員)
| 方法 | 路徑 | 描述 | 權限 |
|------|------|------|------|
| GET | `/api/v1/admin/feedbacks` | 查詢所有反饋列表 | ADMIN, LAB_ADMIN |
| PUT | `/api/v1/admin/feedbacks/{id}/reply` | 回覆反饋 | ADMIN, LAB_ADMIN |
| PUT | `/api/v1/admin/feedbacks/{id}/close` | 關閉反饋 | ADMIN, LAB_ADMIN |

### 6.3 數據模型

#### Feedback 反饋表
| 字段 | 類型 | 說明 |
|------|------|------|
| id | BIGINT | 主鍵 |
| user_id | BIGINT | 用戶 ID |
| type | VARCHAR(20) | 類型：RESERVATION/REPAIR/OTHER |
| target_id | BIGINT | 關聯 ID (預約或報修 ID) |
| content | TEXT | 反饋內容 |
| rating | TINYINT | 評分：1-5 |
| status | VARCHAR(20) | 狀態：PENDING/REPLIED/CLOSED |
| reply_content | TEXT | 回覆內容 |
| reply_time | DATETIME | 回覆時間 |
| replier_id | BIGINT | 回覆人 ID |
| created_at | DATETIME | 創建時間 |
| updated_at | DATETIME | 更新時間 |

---

## 📁 七、文件服務模塊

### 7.1 功能概述
使用 MinIO 對象存儲服務管理系統中的靜態資源文件。

### 7.2 存儲桶設計
| 存儲桶名稱 | 用途 |
|------------|------|
| `avatars` | 用戶頭像 |
| `equipment-images` | 設備圖片 |
| `repair-images` | 報修圖片 |

### 7.3 API 接口設計

| 方法 | 路徑 | 描述 | 權限 |
|------|------|------|------|
| POST | `/api/v1/files/upload` | 上傳文件 | 已認證 |
| GET | `/api/v1/files/{bucket}/{filename}` | 獲取文件 | 公開/已認證 |
| DELETE | `/api/v1/files/{bucket}/{filename}` | 刪除文件 | 已認證 |

### 7.4 文件上傳規則
| 類型 | 允許格式 | 最大大小 |
|------|----------|----------|
| 頭像 | jpg, jpeg, png, gif | 5MB |
| 設備圖片 | jpg, jpeg, png, gif | 10MB |
| 報修圖片 | jpg, jpeg, png, gif | 10MB |

---

## 🔒 八、權限控制模塊

### 8.1 JWT 認證流程
```
1. 用戶登錄 -> 驗證憑證 -> 生成 Access Token + Refresh Token
2. 請求 API -> 攜帶 Access Token -> JWT Filter 驗證
3. Token 過期 -> 使用 Refresh Token 刷新 -> 獲取新 Token
```

### 8.2 Token 配置
| 配置項 | 值 | 說明 |
|--------|-----|------|
| Access Token 有效期 | 2 小時 | 短期令牌 |
| Refresh Token 有效期 | 7 天 | 長期令牌 |
| Token 簽名算法 | HS512 | HMAC-SHA512 |

### 8.3 權限註解
```java
// 需要登錄
@PreAuthorize("isAuthenticated()")

// 需要管理員角色
@PreAuthorize("hasRole('ADMIN')")

// 需要管理員或實驗室管理員角色
@PreAuthorize("hasAnyRole('ADMIN', 'LAB_ADMIN')")
```

---

## 🚀 九、數據緩存模塊 (Redis)

### 9.1 緩存策略
| 緩存 Key 模式 | 用途 | TTL |
|---------------|------|-----|
| `user:{id}` | 用戶信息緩存 | 30 分鐘 |
| `lab:list` | 實驗室列表緩存 | 1 小時 |
| `lab:{id}` | 實驗室詳情緩存 | 30 分鐘 |
| `equipment:list:{labId}` | 設備列表緩存 | 30 分鐘 |
| `equipment:{id}` | 設備詳情緩存 | 30 分鐘 |
| `verify_code:{email}` | 驗證碼緩存 | 5 分鐘 |
| `token:blacklist:{token}` | Token 黑名單 | Token 剩餘有效期 |

### 9.2 緩存更新機制
- 寫入更新：數據修改時主動更新緩存
- 失效刪除：數據刪除時刪除對應緩存
- 延遲雙刪：防止緩存不一致問題

---

## 📨 十、消息隊列模塊 (Kafka)

### 10.1 Topic 設計
| Topic 名稱 | 用途 | 消費者 |
|------------|------|--------|
| `email-notification` | 郵件通知消息 | EmailConsumer |
| `reservation-event` | 預約事件消息 | ReservationEventConsumer |
| `repair-event` | 報修事件消息 | RepairEventConsumer |
| `system-log` | 系統日誌消息 | LogConsumer |

### 10.2 消息格式
```json
{
  "messageId": "uuid",
  "type": "EMAIL_VERIFICATION",
  "payload": {
    "to": "user@example.com",
    "subject": "驗證碼",
    "content": "您的驗證碼是：123456"
  },
  "timestamp": "2024-01-01T12:00:00Z"
}
```

### 10.3 應用場景
1. **解耦**：預約審批後異步發送通知郵件
2. **異步**：報修創建後異步記錄日誌
3. **削峰**：高並發驗證碼發送請求削峰處理

---

## 📧 十一、郵件服務模塊

### 11.1 郵件類型
| 類型 | 模板名稱 | 觸發場景 |
|------|----------|----------|
| 驗證碼 | `verification-code` | 註冊、重置密碼 |
| 預約審批通知 | `reservation-approved` | 預約被批准 |
| 預約拒絕通知 | `reservation-rejected` | 預約被拒絕 |
| 報修處理通知 | `repair-processed` | 報修開始處理 |
| 報修解決通知 | `repair-resolved` | 報修已解決 |
| 反饋回覆通知 | `feedback-replied` | 反饋已回覆 |

### 11.2 郵件配置
```yaml
spring:
  mail:
    host: smtp.example.com
    port: 587
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
```

---

## 📊 統一響應格式

### 成功響應
```json
{
  "code": 200,
  "message": "success",
  "data": {
    // 返回數據
  },
  "timestamp": "2024-01-01T12:00:00Z"
}
```

### 分頁響應
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [],
    "total": 100,
    "page": 1,
    "size": 10,
    "pages": 10
  },
  "timestamp": "2024-01-01T12:00:00Z"
}
```

### 錯誤響應
```json
{
  "code": 400,
  "message": "請求參數錯誤",
  "errors": [
    {
      "field": "email",
      "message": "郵箱格式不正確"
    }
  ],
  "timestamp": "2024-01-01T12:00:00Z"
}
```

---

## 🔢 錯誤碼定義

| 錯誤碼 | 說明 |
|--------|------|
| 200 | 成功 |
| 400 | 請求參數錯誤 |
| 401 | 未認證 |
| 403 | 權限不足 |
| 404 | 資源不存在 |
| 409 | 資源衝突 |
| 500 | 服務器內部錯誤 |
| 1001 | 用戶名已存在 |
| 1002 | 郵箱已註冊 |
| 1003 | 驗證碼錯誤 |
| 1004 | 驗證碼已過期 |
| 2001 | 實驗室不存在 |
| 2002 | 實驗室已關閉 |
| 3001 | 設備不存在 |
| 3002 | 設備維修中 |
| 4001 | 預約時間衝突 |
| 4002 | 預約已過期 |
| 5001 | 報修已處理 |

---

## 🗃️ 數據庫設計 SQL

```sql
-- 用戶表
CREATE TABLE `user` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL,
  `email` VARCHAR(100) NOT NULL UNIQUE,
  `phone` VARCHAR(20),
  `nickname` VARCHAR(50),
  `avatar` VARCHAR(255),
  `role` VARCHAR(20) NOT NULL DEFAULT 'ROLE_USER',
  `status` TINYINT NOT NULL DEFAULT 1,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX `idx_email` (`email`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 實驗室表
CREATE TABLE `lab` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `location` VARCHAR(200),
  `description` TEXT,
  `capacity` INT,
  `manager_id` BIGINT,
  `status` TINYINT NOT NULL DEFAULT 1,
  `open_time` TIME,
  `close_time` TIME,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX `idx_status` (`status`),
  INDEX `idx_manager` (`manager_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 設備表
CREATE TABLE `equipment` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `lab_id` BIGINT NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `model` VARCHAR(100),
  `serial_number` VARCHAR(100),
  `description` TEXT,
  `status` TINYINT NOT NULL DEFAULT 1,
  `purchase_date` DATE,
  `warranty_date` DATE,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX `idx_lab` (`lab_id`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 設備圖片表
CREATE TABLE `equipment_image` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `equipment_id` BIGINT NOT NULL,
  `image_url` VARCHAR(255) NOT NULL,
  `sort_order` INT DEFAULT 0,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX `idx_equipment` (`equipment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 預約表
CREATE TABLE `reservation` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `lab_id` BIGINT NOT NULL,
  `equipment_id` BIGINT,
  `title` VARCHAR(200) NOT NULL,
  `purpose` TEXT,
  `start_time` DATETIME NOT NULL,
  `end_time` DATETIME NOT NULL,
  `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  `approver_id` BIGINT,
  `approve_time` DATETIME,
  `reject_reason` VARCHAR(500),
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX `idx_user` (`user_id`),
  INDEX `idx_lab` (`lab_id`),
  INDEX `idx_status` (`status`),
  INDEX `idx_time` (`start_time`, `end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 報修表
CREATE TABLE `repair` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `equipment_id` BIGINT NOT NULL,
  `title` VARCHAR(200) NOT NULL,
  `description` TEXT,
  `urgency` TINYINT NOT NULL DEFAULT 1,
  `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  `handler_id` BIGINT,
  `handle_time` DATETIME,
  `resolve_time` DATETIME,
  `resolve_note` TEXT,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX `idx_user` (`user_id`),
  INDEX `idx_equipment` (`equipment_id`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 報修圖片表
CREATE TABLE `repair_image` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `repair_id` BIGINT NOT NULL,
  `image_url` VARCHAR(255) NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX `idx_repair` (`repair_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 反饋表
CREATE TABLE `feedback` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `type` VARCHAR(20) NOT NULL,
  `target_id` BIGINT,
  `content` TEXT NOT NULL,
  `rating` TINYINT,
  `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  `reply_content` TEXT,
  `reply_time` DATETIME,
  `replier_id` BIGINT,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX `idx_user` (`user_id`),
  INDEX `idx_type` (`type`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

---

## 🔧 環境配置

### application.yml 示例
```yaml
server:
  port: 8080

spring:
  application:
    name: labservice
  
  datasource:
    url: jdbc:mysql://localhost:3306/labservice?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:123456}
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD:}
  
  kafka:
    bootstrap-servers: ${KAFKA_SERVERS:localhost:9092}
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
    consumer:
      group-id: labservice-group
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
  
  mail:
    host: ${MAIL_HOST:smtp.gmail.com}
    port: ${MAIL_PORT:587}
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}

# MinIO 配置
minio:
  endpoint: ${MINIO_ENDPOINT:http://localhost:9000}
  access-key: ${MINIO_ACCESS_KEY:minioadmin}
  secret-key: ${MINIO_SECRET_KEY:minioadmin}

# JWT 配置
jwt:
  secret: ${JWT_SECRET:your-secret-key}
  access-token-expiration: 7200000  # 2 小時
  refresh-token-expiration: 604800000  # 7 天

# MyBatis-Plus 配置
mybatis-plus:
  mapper-locations: classpath:/mapper/**/*.xml
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

---

## 📝 版本記錄

| 版本 | 日期 | 描述 |
|------|------|------|
| v1.0.0 | 2024-XX-XX | 初始版本，包含基礎功能 |

---

## 👨‍💻 開發團隊

- **項目負責人**: xiaomu
- **後端開發**: xiaomu
- **文檔維護**: xiaomu

---

*本文檔參考 [vibe-music-server](https://github.com/Alex-LiSun/vibe-music-server) 項目的代碼風格和架構設計。*

