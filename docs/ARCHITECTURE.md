# 实验室共享平台 (Lab Service) 技术架构文档

## 1. 架构概述

### 1.1 系统定位
Lab Service 是一个基于微服务理念设计的单体应用，采用分层架构模式，为实验室资源管理提供完整的后端服务支持。

### 1.2 设计原则
| 原则 | 说明 |
|------|------|
| 分层架构 | Controller → Service → Mapper，职责清晰 |
| 模块化设计 | 按业务领域划分模块，高内聚低耦合 |
| RESTful API | 遵循 REST 设计规范，统一接口风格 |
| 安全优先 | JWT 认证 + RBAC 权限控制 |
| 可扩展性 | 预留消息队列，支持未来拆分微服务 |

### 1.3 技术选型
```
┌─────────────────────────────────────────────────────────────────┐
│                        客户端 (Web/App)                          │
└─────────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                      Nginx (反向代理/负载均衡)                    │
└─────────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                     Lab Service (Spring Boot 4)                  │
│  ┌───────────┬───────────┬───────────┬───────────┬───────────┐  │
│  │   User    │    Lab    │ Equipment │Reservation│  Repair   │  │
│  │  Module   │  Module   │  Module   │  Module   │  Module   │  │
│  └───────────┴───────────┴───────────┴───────────┴───────────┘  │
└─────────────────────────────────────────────────────────────────┘
          │              │              │              │
          ▼              ▼              ▼              ▼
┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐
│   MySQL     │  │    Redis    │  │    Kafka    │  │    MinIO    │
│  (数据存储)  │  │   (缓存)    │  │  (消息队列)  │  │  (文件存储)  │
└─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘
```

---

## 2. 系统架构

### 2.1 整体架构图
```
┌────────────────────────────────────────────────────────────────────────────┐
│                              表现层 (Presentation Layer)                    │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │                         REST Controllers                              │  │
│  │   AuthController | UserController | LabController | EquipmentController│  │
│  │   ReservationController | RepairController | FeedbackController       │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
├────────────────────────────────────────────────────────────────────────────┤
│                              业务层 (Business Layer)                        │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │                           Services                                    │  │
│  │   AuthService | UserService | LabService | EquipmentService          │  │
│  │   ReservationService | RepairService | FeedbackService | FileService │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
├────────────────────────────────────────────────────────────────────────────┤
│                              数据访问层 (Data Access Layer)                  │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │                      MyBatis-Plus Mappers                             │  │
│  │   UserMapper | LabMapper | EquipmentMapper | ReservationMapper       │  │
│  │   RepairMapper | FeedbackMapper | EquipmentImageMapper               │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
├────────────────────────────────────────────────────────────────────────────┤
│                              基础设施层 (Infrastructure Layer)               │
│  ┌────────────┐  ┌────────────┐  ┌────────────┐  ┌────────────┐          │
│  │   MySQL    │  │   Redis    │  │   Kafka    │  │   MinIO    │          │
│  └────────────┘  └────────────┘  └────────────┘  └────────────┘          │
└────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 模块依赖关系
```
                    ┌─────────────┐
                    │   common    │
                    │  (通用模块)  │
                    └──────┬──────┘
                           │
          ┌────────────────┼────────────────┐
          │                │                │
          ▼                ▼                ▼
   ┌─────────────┐  ┌─────────────┐  ┌─────────────┐
   │  security   │  │     mq      │  │   module    │
   │  (安全模块)  │  │ (消息队列)   │  │  (业务模块)  │
   └──────┬──────┘  └──────┬──────┘  └──────┬──────┘
          │                │                │
          └────────────────┼────────────────┘
                           │
                           ▼
                    ┌─────────────┐
                    │    main     │
                    │  (启动入口)  │
                    └─────────────┘
```

---

## 3. 目录结构

### 3.1 项目结构
```
labservice/
├── docs/                                    # 文档目录
│   ├── REQUIREMENTS.md                      # 需求文档
│   └── ARCHITECTURE.md                      # 架构文档
├── sql/                                     # SQL 脚本目录
│   ├── schema.sql                           # 建表语句
│   └── data.sql                             # 初始数据
├── src/
│   ├── main/
│   │   ├── java/com/xiaomu/labservice/
│   │   │   ├── common/                      # 通用模块
│   │   │   │   ├── config/                  # 配置类
│   │   │   │   │   ├── CorsConfig.java      # 跨域配置
│   │   │   │   │   ├── RedisConfig.java     # Redis 配置
│   │   │   │   │   ├── KafkaConfig.java     # Kafka 配置
│   │   │   │   │   ├── MinioConfig.java     # MinIO 配置
│   │   │   │   │   ├── DruidConfig.java     # Druid 配置
│   │   │   │   │   └── SwaggerConfig.java   # API 文档配置
│   │   │   │   ├── constant/                # 常量
│   │   │   │   │   ├── RedisKeyConstant.java
│   │   │   │   │   ├── KafkaTopicConstant.java
│   │   │   │   │   └── StatusConstant.java
│   │   │   │   ├── exception/               # 异常处理
│   │   │   │   │   ├── BusinessException.java
│   │   │   │   │   ├── UnauthorizedException.java
│   │   │   │   │   └── GlobalExceptionHandler.java
│   │   │   │   ├── response/                # 响应封装
│   │   │   │   │   ├── Result.java
│   │   │   │   │   ├── PageResult.java
│   │   │   │   │   └── ResultCode.java
│   │   │   │   └── util/                    # 工具类
│   │   │   │       ├── JwtUtil.java
│   │   │   │       ├── RedisUtil.java
│   │   │   │       └── FileUtil.java
│   │   │   │
│   │   │   ├── security/                    # 安全模块
│   │   │   │   ├── config/
│   │   │   │   │   └── SecurityConfig.java
│   │   │   │   ├── filter/
│   │   │   │   │   └── JwtAuthenticationFilter.java
│   │   │   │   └── service/
│   │   │   │       └── UserDetailsServiceImpl.java
│   │   │   │
│   │   │   ├── module/                      # 业务模块
│   │   │   │   ├── user/                    # 用户模块
│   │   │   │   │   ├── controller/
│   │   │   │   │   │   ├── AuthController.java
│   │   │   │   │   │   ├── UserController.java
│   │   │   │   │   │   └── AdminUserController.java
│   │   │   │   │   ├── service/
│   │   │   │   │   │   ├── UserService.java
│   │   │   │   │   │   └── impl/UserServiceImpl.java
│   │   │   │   │   ├── mapper/
│   │   │   │   │   │   └── UserMapper.java
│   │   │   │   │   ├── entity/
│   │   │   │   │   │   └── User.java
│   │   │   │   │   └── dto/
│   │   │   │   │       ├── LoginDTO.java
│   │   │   │   │       ├── RegisterDTO.java
│   │   │   │   │       └── UserVO.java
│   │   │   │   │
│   │   │   │   ├── lab/                     # 实验室模块
│   │   │   │   │   ├── controller/
│   │   │   │   │   ├── service/
│   │   │   │   │   ├── mapper/
│   │   │   │   │   ├── entity/
│   │   │   │   │   └── dto/
│   │   │   │   │
│   │   │   │   ├── equipment/               # 设备模块
│   │   │   │   │   ├── controller/
│   │   │   │   │   ├── service/
│   │   │   │   │   ├── mapper/
│   │   │   │   │   ├── entity/
│   │   │   │   │   └── dto/
│   │   │   │   │
│   │   │   │   ├── reservation/             # 预约模块
│   │   │   │   │   ├── controller/
│   │   │   │   │   ├── service/
│   │   │   │   │   ├── mapper/
│   │   │   │   │   ├── entity/
│   │   │   │   │   └── dto/
│   │   │   │   │
│   │   │   │   ├── repair/                  # 报修模块
│   │   │   │   │   ├── controller/
│   │   │   │   │   ├── service/
│   │   │   │   │   ├── mapper/
│   │   │   │   │   ├── entity/
│   │   │   │   │   └── dto/
│   │   │   │   │
│   │   │   │   ├── feedback/                # 反馈模块
│   │   │   │   │   ├── controller/
│   │   │   │   │   ├── service/
│   │   │   │   │   ├── mapper/
│   │   │   │   │   ├── entity/
│   │   │   │   │   └── dto/
│   │   │   │   │
│   │   │   │   └── file/                    # 文件模块
│   │   │   │       ├── controller/
│   │   │   │       │   └── FileController.java
│   │   │   │       └── service/
│   │   │   │           ├── FileService.java
│   │   │   │           └── impl/MinioFileServiceImpl.java
│   │   │   │
│   │   │   ├── mq/                          # 消息队列模块
│   │   │   │   ├── producer/
│   │   │   │   │   ├── EmailProducer.java
│   │   │   │   │   └── EventProducer.java
│   │   │   │   ├── consumer/
│   │   │   │   │   ├── EmailConsumer.java
│   │   │   │   │   ├── ReservationEventConsumer.java
│   │   │   │   │   └── RepairEventConsumer.java
│   │   │   │   └── message/
│   │   │   │       ├── BaseMessage.java
│   │   │   │       └── EmailMessage.java
│   │   │   │
│   │   │   └── LabserviceApplication.java   # 启动类
│   │   │
│   │   └── resources/
│   │       ├── application.yml              # 主配置文件
│   │       ├── application-dev.yml          # 开发环境配置
│   │       ├── application-prod.yml         # 生产环境配置
│   │       ├── mapper/                      # MyBatis XML 映射文件
│   │       │   ├── UserMapper.xml
│   │       │   ├── LabMapper.xml
│   │       │   └── ...
│   │       └── templates/                   # 邮件模板
│   │           ├── verification-code.html
│   │           └── ...
│   │
│   └── test/                                # 测试目录
│       └── java/com/xiaomu/labservice/
│           ├── module/
│           │   ├── user/UserServiceTest.java
│           │   └── ...
│           └── LabserviceApplicationTests.java
│
├── pom.xml                                  # Maven 配置
├── mvnw                                     # Maven Wrapper (Linux/Mac)
├── mvnw.cmd                                 # Maven Wrapper (Windows)
└── README.md                                # 项目说明
```

### 3.2 各层职责

| 层级 | 包名 | 职责 |
|------|------|------|
| Controller | `*.controller` | 处理 HTTP 请求，参数校验，调用 Service |
| Service | `*.service` | 业务逻辑处理，事务管理 |
| Mapper | `*.mapper` | 数据库访问，MyBatis-Plus 接口 |
| Entity | `*.entity` | 数据库实体类，与表结构对应 |
| DTO | `*.dto` | 数据传输对象，包含请求/响应 VO |

---

## 4. 核心组件设计

### 4.1 统一响应封装

```java
/**
 * 统一响应结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private Integer code;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data, LocalDateTime.now());
    }

    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null, LocalDateTime.now());
    }
}
```

### 4.2 全局异常处理

```java
/**
 * 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<List<FieldError>> handleValidationException(MethodArgumentNotValidException e) {
        List<FieldError> errors = e.getBindingResult().getFieldErrors().stream()
            .map(error -> new FieldError(error.getField(), error.getDefaultMessage()))
            .toList();
        return Result.error(400, "参数校验失败", errors);
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error(500, "服务器内部错误");
    }
}
```

### 4.3 JWT 认证过滤器

```java
/**
 * JWT 认证过滤器
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final RedisUtil redisUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 1. 从请求头获取 Token
        String token = extractToken(request);
        
        if (token != null && !isTokenBlacklisted(token)) {
            // 2. 验证 Token 并获取用户信息
            String username = jwtUtil.extractUsername(token);
            
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                if (jwtUtil.validateToken(token, userDetails)) {
                    // 3. 设置认证信息
                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private boolean isTokenBlacklisted(String token) {
        return redisUtil.hasKey(RedisKeyConstant.TOKEN_BLACKLIST + token);
    }
}
```

### 4.4 MyBatis-Plus 基础配置

```java
/**
 * MyBatis-Plus 配置
 */
@Configuration
@MapperScan("com.xiaomu.labservice.module.*.mapper")
public class MybatisPlusConfig {

    /**
     * 分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    /**
     * 自动填充处理器
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                this.strictInsertFill(metaObject, "createdAt", LocalDateTime.class, LocalDateTime.now());
                this.strictInsertFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                this.strictUpdateFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
            }
        };
    }
}
```

---

## 5. 安全架构

### 5.1 认证流程

```
┌─────────┐      ┌─────────────┐      ┌─────────────┐      ┌─────────┐
│  Client │      │  Controller │      │   Service   │      │  Redis  │
└────┬────┘      └──────┬──────┘      └──────┬──────┘      └────┬────┘
     │                  │                    │                  │
     │  POST /login     │                    │                  │
     │─────────────────>│                    │                  │
     │                  │  authenticate()    │                  │
     │                  │───────────────────>│                  │
     │                  │                    │                  │
     │                  │  generate tokens   │                  │
     │                  │<───────────────────│                  │
     │                  │                    │                  │
     │                  │                    │  store refresh   │
     │                  │                    │─────────────────>│
     │                  │                    │                  │
     │  access_token    │                    │                  │
     │  refresh_token   │                    │                  │
     │<─────────────────│                    │                  │
     │                  │                    │                  │
```

### 5.2 权限控制矩阵

| 资源 | USER | LAB_ADMIN | ADMIN |
|------|:----:|:---------:|:-----:|
| 查看实验室列表 | ✓ | ✓ | ✓ |
| 创建实验室 | ✗ | ✓ | ✓ |
| 删除实验室 | ✗ | ✗ | ✓ |
| 查看设备列表 | ✓ | ✓ | ✓ |
| 管理设备 | ✗ | ✓ | ✓ |
| 创建预约 | ✓ | ✓ | ✓ |
| 审批预约 | ✗ | ✓ | ✓ |
| 提交报修 | ✓ | ✓ | ✓ |
| 处理报修 | ✗ | ✓ | ✓ |
| 用户管理 | ✗ | ✗ | ✓ |

### 5.3 Spring Security 配置

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 公开接口
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/api/v1/files/*/public/**").permitAll()
                // 管理员接口
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                // 其他接口需要认证
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) 
            throws Exception {
        return config.getAuthenticationManager();
    }
}
```

---

## 6. 缓存架构

### 6.1 Redis 缓存设计

```
┌──────────────────────────────────────────────────────────────┐
│                       Redis 缓存层                            │
├──────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌─────────────────┐  ┌─────────────────┐  ┌──────────────┐  │
│  │   用户缓存       │  │   业务缓存       │  │  临时缓存    │  │
│  ├─────────────────┤  ├─────────────────┤  ├──────────────┤  │
│  │ user:{id}       │  │ lab:list        │  │ verify:{email}│ │
│  │ user:info:{id}  │  │ lab:{id}        │  │ token:black:* │ │
│  │                 │  │ equipment:*     │  │ rate:limit:*  │ │
│  └─────────────────┘  └─────────────────┘  └──────────────┘  │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

### 6.2 缓存操作工具类

```java
@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 设置缓存（带过期时间）
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 获取缓存
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        Object value = redisTemplate.opsForValue().get(key);
        return clazz.cast(value);
    }

    /**
     * 删除缓存
     */
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 批量删除缓存
     */
    public void deleteByPattern(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 判断 key 是否存在
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 设置过期时间
     */
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }
}
```

### 6.3 缓存更新策略

```java
@Service
@RequiredArgsConstructor
public class LabServiceImpl implements LabService {

    private final LabMapper labMapper;
    private final RedisUtil redisUtil;

    private static final String LAB_CACHE_KEY = "lab:";
    private static final String LAB_LIST_CACHE_KEY = "lab:list";

    @Override
    @Cacheable(value = "lab", key = "#id")
    public Lab getById(Long id) {
        return labMapper.selectById(id);
    }

    @Override
    @CacheEvict(value = "lab", key = "#lab.id")
    @Transactional
    public void update(Lab lab) {
        labMapper.updateById(lab);
        // 延迟双删，防止缓存不一致
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(500);
                redisUtil.delete(LAB_CACHE_KEY + lab.getId());
                redisUtil.delete(LAB_LIST_CACHE_KEY);
            } catch (InterruptedException ignored) {}
        });
    }

    @Override
    @CacheEvict(value = "lab", allEntries = true)
    @Transactional
    public void delete(Long id) {
        labMapper.deleteById(id);
    }
}
```

---

## 7. 消息队列架构

### 7.1 Kafka Topic 设计

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           Kafka Cluster                                  │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ┌─────────────────────┐  ┌─────────────────────┐  ┌─────────────────┐  │
│  │ email-notification  │  │ reservation-event   │  │  repair-event   │  │
│  │   Partitions: 3     │  │   Partitions: 3     │  │  Partitions: 3  │  │
│  │   Replication: 2    │  │   Replication: 2    │  │  Replication: 2 │  │
│  └──────────┬──────────┘  └──────────┬──────────┘  └────────┬────────┘  │
│             │                        │                      │           │
│             ▼                        ▼                      ▼           │
│  ┌─────────────────────┐  ┌─────────────────────┐  ┌─────────────────┐  │
│  │   EmailConsumer     │  │ ReservationConsumer │  │ RepairConsumer  │  │
│  │   (Consumer Group)  │  │   (Consumer Group)  │  │ (Consumer Group)│  │
│  └─────────────────────┘  └─────────────────────┘  └─────────────────┘  │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### 7.2 消息生产者

```java
@Component
@RequiredArgsConstructor
@Slf4j
public class EmailProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendVerificationCode(String email, String code) {
        EmailMessage message = EmailMessage.builder()
            .messageId(UUID.randomUUID().toString())
            .type("VERIFICATION_CODE")
            .to(email)
            .subject("验证码")
            .content("您的验证码是：" + code + "，有效期5分钟。")
            .timestamp(LocalDateTime.now())
            .build();

        kafkaTemplate.send(KafkaTopicConstant.EMAIL_NOTIFICATION, message)
            .whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("邮件消息发送成功: {}", message.getMessageId());
                } else {
                    log.error("邮件消息发送失败: {}", message.getMessageId(), ex);
                }
            });
    }

    public void sendReservationNotification(String email, String title, String status) {
        EmailMessage message = EmailMessage.builder()
            .messageId(UUID.randomUUID().toString())
            .type("RESERVATION_NOTIFICATION")
            .to(email)
            .subject("预约状态更新")
            .content("您的预约「" + title + "」状态已更新为：" + status)
            .timestamp(LocalDateTime.now())
            .build();

        kafkaTemplate.send(KafkaTopicConstant.EMAIL_NOTIFICATION, message);
    }
}
```

### 7.3 消息消费者

```java
@Component
@RequiredArgsConstructor
@Slf4j
public class EmailConsumer {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @KafkaListener(topics = KafkaTopicConstant.EMAIL_NOTIFICATION, groupId = "email-group")
    public void consume(EmailMessage message) {
        log.info("收到邮件消息: {}", message.getMessageId());

        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(fromEmail);
            mailMessage.setTo(message.getTo());
            mailMessage.setSubject(message.getSubject());
            mailMessage.setText(message.getContent());

            mailSender.send(mailMessage);
            log.info("邮件发送成功: {}", message.getTo());
        } catch (Exception e) {
            log.error("邮件发送失败: {}", message.getTo(), e);
            // 可以在这里实现重试逻辑
        }
    }
}
```

---

## 8. 文件存储架构

### 8.1 MinIO 存储设计

```
MinIO Server
├── avatars/                    # 用户头像桶
│   ├── {userId}/
│   │   └── avatar.jpg
│   └── ...
├── equipment-images/           # 设备图片桶
│   ├── {equipmentId}/
│   │   ├── 001.jpg
│   │   ├── 002.jpg
│   │   └── ...
│   └── ...
└── repair-images/              # 报修图片桶
    ├── {repairId}/
    │   ├── 001.jpg
    │   └── ...
    └── ...
```

### 8.2 文件服务实现

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class MinioFileServiceImpl implements FileService {

    private final MinioClient minioClient;

    @Value("${minio.endpoint}")
    private String endpoint;

    @Override
    public String uploadFile(String bucket, String objectName, MultipartFile file) {
        try {
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
            return String.format("%s/%s/%s", endpoint, bucket, fileName);

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
        String extension = originalName.substring(originalName.lastIndexOf("."));
        return prefix + "/" + UUID.randomUUID() + extension;
    }
}
```

---

## 9. 数据库设计

### 9.1 ER 图

```
┌─────────────┐       ┌─────────────┐       ┌─────────────────┐
│    User     │       │     Lab     │       │   Equipment     │
├─────────────┤       ├─────────────┤       ├─────────────────┤
│ id (PK)     │       │ id (PK)     │       │ id (PK)         │
│ username    │       │ name        │       │ lab_id (FK)     │──┐
│ password    │       │ location    │       │ name            │  │
│ email       │   ┌──>│ manager_id  │       │ model           │  │
│ phone       │   │   │ status      │       │ serial_number   │  │
│ nickname    │   │   │ capacity    │       │ status          │  │
│ avatar      │   │   │ open_time   │       │ purchase_date   │  │
│ role        │───┘   │ close_time  │<──────│ warranty_date   │  │
│ status      │       └─────────────┘       └─────────────────┘  │
└──────┬──────┘                                      │           │
       │                                             │           │
       │       ┌─────────────────┐                   │           │
       │       │  Reservation    │                   │           │
       │       ├─────────────────┤                   │           │
       │       │ id (PK)         │                   │           │
       └──────>│ user_id (FK)    │                   │           │
               │ lab_id (FK)     │───────────────────┘           │
               │ equipment_id    │<──────────────────────────────┘
               │ title           │
               │ purpose         │
               │ start_time      │
               │ end_time        │
               │ status          │
               │ approver_id     │
               └─────────────────┘

┌─────────────┐       ┌─────────────────┐       ┌─────────────────┐
│   Repair    │       │  RepairImage    │       │EquipmentImage   │
├─────────────┤       ├─────────────────┤       ├─────────────────┤
│ id (PK)     │<──────│ repair_id (FK)  │       │ id (PK)         │
│ user_id     │       │ image_url       │       │ equipment_id(FK)│
│ equipment_id│       │ created_at      │       │ image_url       │
│ title       │       └─────────────────┘       │ sort_order      │
│ description │                                 └─────────────────┘
│ urgency     │
│ status      │       ┌─────────────────┐
│ handler_id  │       │    Feedback     │
│ handle_time │       ├─────────────────┤
│ resolve_time│       │ id (PK)         │
│ resolve_note│       │ user_id (FK)    │
└─────────────┘       │ type            │
                      │ target_id       │
                      │ content         │
                      │ rating          │
                      │ status          │
                      │ reply_content   │
                      │ replier_id      │
                      └─────────────────┘
```

### 9.2 索引设计原则

| 表名 | 索引 | 类型 | 说明 |
|------|------|------|------|
| user | idx_email | UNIQUE | 邮箱唯一索引，用于登录 |
| user | idx_username | UNIQUE | 用户名唯一索引 |
| lab | idx_status | NORMAL | 按状态筛选实验室 |
| equipment | idx_lab_status | COMPOSITE | 复合索引，查询实验室设备 |
| reservation | idx_user_status | COMPOSITE | 用户预约列表查询 |
| reservation | idx_time_range | COMPOSITE | 时间范围查询，冲突检测 |
| repair | idx_equipment | NORMAL | 设备报修记录查询 |

---

## 10. API 设计规范

### 10.1 URL 命名规范

```
# 资源命名：使用名词复数形式
GET    /api/v1/labs              # 获取实验室列表
GET    /api/v1/labs/{id}         # 获取单个实验室
POST   /api/v1/labs              # 创建实验室
PUT    /api/v1/labs/{id}         # 更新实验室
DELETE /api/v1/labs/{id}         # 删除实验室

# 子资源
GET    /api/v1/labs/{id}/equipments    # 获取实验室下的设备

# 操作类接口
POST   /api/v1/reservations/{id}/approve   # 审批预约
POST   /api/v1/reservations/{id}/reject    # 拒绝预约

# 当前用户资源
GET    /api/v1/users/me                    # 获取当前用户信息
GET    /api/v1/reservations/me             # 获取我的预约列表
```

### 10.2 请求/响应示例

#### 创建预约请求
```http
POST /api/v1/reservations HTTP/1.1
Host: api.example.com
Authorization: Bearer <token>
Content-Type: application/json

{
  "labId": 1,
  "equipmentId": 10,
  "title": "毕业设计实验",
  "purpose": "进行机器学习模型训练",
  "startTime": "2024-03-15T09:00:00",
  "endTime": "2024-03-15T12:00:00"
}
```

#### 成功响应
```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "code": 200,
  "message": "success",
  "data": {
    "id": 100,
    "userId": 1,
    "labId": 1,
    "equipmentId": 10,
    "title": "毕业设计实验",
    "purpose": "进行机器学习模型训练",
    "startTime": "2024-03-15T09:00:00",
    "endTime": "2024-03-15T12:00:00",
    "status": "PENDING",
    "createdAt": "2024-03-10T10:30:00"
  },
  "timestamp": "2024-03-10T10:30:00"
}
```

#### 错误响应
```http
HTTP/1.1 409 Conflict
Content-Type: application/json

{
  "code": 4001,
  "message": "预约时间冲突，该时段已被预约",
  "data": null,
  "timestamp": "2024-03-10T10:30:00"
}
```

---

## 11. 部署架构

### 11.1 开发环境

```
┌─────────────────────────────────────────────────────────────────┐
│                      开发者本机 (localhost)                      │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐             │
│  │ Lab Service │  │   MySQL     │  │   Redis     │             │
│  │  (8080)     │  │  (3306)     │  │  (6379)     │             │
│  └─────────────┘  └─────────────┘  └─────────────┘             │
│                                                                 │
│  ┌─────────────┐  ┌─────────────┐                              │
│  │   Kafka     │  │   MinIO     │                              │
│  │  (9092)     │  │  (9000)     │                              │
│  └─────────────┘  └─────────────┘                              │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 11.2 生产环境

```
                        ┌─────────────┐
                        │   客户端     │
                        └──────┬──────┘
                               │
                               ▼
                        ┌─────────────┐
                        │   Nginx     │
                        │ (负载均衡)   │
                        └──────┬──────┘
                               │
              ┌────────────────┼────────────────┐
              │                │                │
              ▼                ▼                ▼
       ┌─────────────┐  ┌─────────────┐  ┌─────────────┐
       │ Lab Service │  │ Lab Service │  │ Lab Service │
       │  Node 1     │  │  Node 2     │  │  Node 3     │
       └──────┬──────┘  └──────┬──────┘  └──────┬──────┘
              │                │                │
              └────────────────┼────────────────┘
                               │
         ┌─────────────────────┼─────────────────────┐
         │                     │                     │
         ▼                     ▼                     ▼
  ┌─────────────┐       ┌─────────────┐       ┌─────────────┐
  │ MySQL Master│       │Redis Cluster│       │ Kafka Cluster│
  │ MySQL Slave │       │  (3 nodes)  │       │  (3 brokers) │
  └─────────────┘       └─────────────┘       └─────────────┘
                               │
                               ▼
                        ┌─────────────┐
                        │ MinIO Cluster│
                        │  (分布式存储) │
                        └─────────────┘
```

### 11.3 Docker Compose 配置示例

```yaml
version: '3.8'

services:
  labservice:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DB_HOST=mysql
      - REDIS_HOST=redis
      - KAFKA_SERVERS=kafka:9092
      - MINIO_ENDPOINT=http://minio:9000
    depends_on:
      - mysql
      - redis
      - kafka
      - minio

  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: ${DB_PASSWORD}
      MYSQL_DATABASE: labservice
    volumes:
      - mysql_data:/var/lib/mysql
      - ./sql:/docker-entrypoint-initdb.d
    ports:
      - "3306:3306"

  redis:
    image: redis:7-alpine
    command: redis-server --appendonly yes
    volumes:
      - redis_data:/data
    ports:
      - "6379:6379"

  kafka:
    image: confluentinc/cp-kafka:7.5.0
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"

  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
    ports:
      - "2181:2181"

  minio:
    image: minio/minio
    command: server /data --console-address ":9001"
    environment:
      MINIO_ROOT_USER: ${MINIO_ACCESS_KEY}
      MINIO_ROOT_PASSWORD: ${MINIO_SECRET_KEY}
    volumes:
      - minio_data:/data
    ports:
      - "9000:9000"
      - "9001:9001"

volumes:
  mysql_data:
  redis_data:
  minio_data:
```

---

## 12. 监控与日志

### 12.1 日志配置

```yaml
# logback-spring.xml 配置要点
logging:
  level:
    root: INFO
    com.xiaomu.labservice: DEBUG
    org.springframework.security: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/labservice.log
    max-size: 100MB
    max-history: 30
```

### 12.2 健康检查端点

```yaml
# Spring Boot Actuator 配置
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: when_authorized
```

### 12.3 关键指标监控

| 指标类型 | 监控项 | 告警阈值 |
|----------|--------|----------|
| 系统指标 | CPU 使用率 | > 80% |
| 系统指标 | 内存使用率 | > 85% |
| 应用指标 | 请求响应时间 | P99 > 1s |
| 应用指标 | 错误率 | > 1% |
| 数据库 | 连接池使用率 | > 80% |
| 缓存 | Redis 命中率 | < 90% |
| 消息队列 | 消费延迟 | > 5min |

---

## 13. 开发规范

### 13.1 代码规范

| 项目 | 规范 |
|------|------|
| 命名规范 | 遵循阿里巴巴 Java 开发手册 |
| 代码格式 | 使用 Google Java Style |
| 注释规范 | 类、方法必须有 Javadoc 注释 |
| 日志规范 | 使用 Slf4j + Logback |

### 13.2 Git 提交规范

```
<type>(<scope>): <subject>

# type 类型
feat:     新功能
fix:      Bug 修复
docs:     文档更新
style:    代码格式调整
refactor: 重构
test:     测试相关
chore:    构建/工具相关

# 示例
feat(user): 添加用户注册功能
fix(reservation): 修复预约时间冲突判断逻辑
docs(readme): 更新部署文档
```

### 13.3 分支管理

```
main          # 主分支，保护分支，仅通过 PR 合并
├── develop   # 开发分支
├── feature/* # 功能分支 (feature/user-auth)
├── bugfix/*  # Bug 修复分支
└── release/* # 发布分支
```

---

## 14. 版本历史

| 版本 | 日期 | 作者 | 描述 |
|------|------|------|------|
| v1.0.0 | 2024-XX-XX | xiaomu | 初始版本 |

---

*本文档参考 [vibe-music-server](https://github.com/Alex-LiSun/vibe-music-server) 项目架构设计。*

