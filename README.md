# ByteDance Demo1
一个基于 Android 的登录、注册和个人中心应用示例项目，集成了性能监控 SDK。

## 📱 项目简介

本项目实现了用户注册、登录、个人中心等基础功能。应用采用 Material Design 设计规范，使用 Room 数据库存储用户账号信息，SharedPreferences 存储用户偏好设置（用户名、签名等）。

**核心亮点**：项目集成了 **性能监控 SDK (monitoringsdk)**，提供冷启动耗时、页面渲染耗时、FPS/卡顿监控、ANR 监控等完整的性能监控能力。

## ✨ 功能特性

### 📊 性能监控 SDK (monitoringsdk)

项目集成了完整的性能监控 SDK，提供以下监控能力：

#### 1. 冷启动耗时监控
- **功能**：自动统计应用从进程启动到第一个 Activity 可见的耗时
- **实现**：基于 `Application.attachBaseContext` 和 `ActivityLifecycleCallbacks`
- **输出**：Logcat 中输出 `Cold start cost = xxx ms`

#### 2. 页面渲染耗时监控
- **功能**：统计冷启动页面和普通页面的渲染耗时
- **冷启动页面**：从进程启动到页面首次渲染完成
- **普通页面**：从用户点击跳转到页面渲染完成
- **实现**：基于 `DecorView.post()` 回调
- **输出**：
  - `Cold start render page=xxx cost=xxx ms`
  - `Page render page=xxx cost=xxx ms`

#### 3. FPS / 卡顿监控
- **功能**：实时监控每个 Activity 的帧率和卡顿情况
- **实现**：基于 `Window.addOnFrameMetricsAvailableListener` (API 24+)
- **指标**：
  - FPS（每秒帧数）
  - Jank Rate（卡顿率，超过 16.67ms 的帧占比）
- **输出**：每 1 秒输出一次统计，格式：`Activity=xxx fps=59.8 jankRate=0.05`

#### 4. ANR 监控
- **功能**：检测主线程卡顿，超过阈值时输出调用栈
- **实现**：主线程心跳 + 后台线程定时检查机制
- **阈值**：5 秒（超过 5 秒未收到主线程心跳则判定为 ANR）
- **心跳间隔**：500ms
- **输出**：
  - `检测到主线程卡顿，超过阈值 xxx ms`
  - 完整的主线程调用栈信息



#### 查看监控日志
在 Logcat 中过滤以下 Tag：
- `PerformanceMonitor` - 冷启动耗时
- `PageRenderTracer` - 页面渲染耗时
- `FrameMetricsCollector` - FPS / 卡顿统计
- `AnrWatchdog` - ANR 检测和调用栈

---

### 📱 应用功能

### 1. 开屏页面 (SplashActivity)
- 应用启动时显示开屏图片
- 2秒后自动跳转到登录页面

### 2. 注册页面 (RegisterActivity)
- 支持用户名或邮箱注册
- 密码输入和确认密码验证
- 密码长度验证（至少6位）
- 用户名唯一性检查（从 Room 数据库查询）
- 输入格式验证和错误提示
- 注册成功后自动跳转到登录页面

### 3. 登录页面 (LoginActivity)
- 支持邮箱或用户名登录
- 密码显示/隐藏切换
- 微信登录和 Apple 登录按钮（UI实现）
- 忘记密码和注册链接
- 输入验证和错误提示
- 使用 Room 数据库验证用户信息
- 登录成功后跳转到个人中心页面

### 4. 个人中心页面 (ProfileActivity)
- 圆形头像显示
- 用户信息展示（用户名、欢迎语）
- 功能菜单列表：
  - 个人信息
  - 我的收藏
  - 浏览历史
  - 设置
  - 关于我们
  - 意见反馈
- 所有菜单项支持点击交互

## 🏗️ 项目结构

```
bytedancedemo1/
├── app/                                        # 主应用模块
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/bytedancedemo1/
│   │   │   │   ├── App.java                   # 自定义Application（SDK初始化）
│   │   │   │   ├── SplashActivity.java        # 开屏页面（冷启动监控）
│   │   │   │   ├── RegisterActivity.java      # 注册页面
│   │   │   │   ├── LoginActivity.java         # 登录页面
│   │   │   │   ├── ProfileActivity.java       # 个人中心页面（ANR测试）
│   │   │   │   ├── MainActivity.java          # 主页面
│   │   │   │   ├── database/                  # 数据库相关
│   │   │   │   │   ├── AppDatabase.java       # Room数据库
│   │   │   │   │   ├── User.java              # 用户实体类
│   │   │   │   │   └── UserDao.java           # 数据访问对象
│   │   │   │   └── utils/                     # 工具类
│   │   │   │       └── SharedPreferencesHelper.java
│   │   │   ├── res/                           # 资源文件
│   │   │   └── AndroidManifest.xml
│   │   └── test/                              # 测试代码
│   └── build.gradle.kts
├── monitoringsdk/                             # 性能监控 SDK 模块
│   ├── src/main/java/com/example/monitoringsdk/
│   │   ├── PerformanceMonitor.java           # SDK 入口类
│   │   ├── PerformanceMonitoringConfig.java  # 配置类
│   │   ├── PageRenderTracer.java             # 页面渲染追踪
│   │   ├── FrameMetricsCollector.java        # FPS/卡顿监控
│   │   └── AnrMonitor.java                   # ANR 监控
│   └── build.gradle.kts
├── gradle/
│   ├── libs.versions.toml                    # 依赖版本管理
│   └── wrapper/
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

## 🛠️ 技术栈

### 核心框架
- **Android SDK**: 24-36
- **Java**: 11
- **Gradle**: 8.13.0
- **Kotlin DSL**: 用于构建脚本

### 主要依赖库

#### UI 组件
- **AndroidX AppCompat**: 1.6.1
  - 提供向后兼容的 Activity 和 Fragment
- **Material Design Components**: 1.10.0
  - MaterialButton
  - TextInputLayout
  - TextInputEditText
- **ConstraintLayout**: 2.1.4
  - 灵活的布局管理器

#### 数据存储
- **Room Database**: 2.6.1
  - 用于存储用户账号信息（用户名、密码）
  - 用户注册、登录验证
- **SharedPreferences**
  - 存储用户偏好设置（用户名、签名等）
  - 轻量级键值对存储

#### 性能监控
- **monitoringsdk** (本地模块)
  - 冷启动耗时监控
  - 页面渲染耗时监控
  - FPS / 卡顿监控（基于 FrameMetrics API）
  - ANR 监控（主线程心跳机制）


## 📦 依赖管理

项目使用 `libs.versions.toml` 进行版本管理，所有依赖版本集中管理在 `gradle/libs.versions.toml` 文件中。

## 🗄️ 数据存储设计

### Room 数据库（用户账号信息）

#### User 表结构
```java
@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private String username;  // 用户名或邮箱
    private String password;   // 密码
}
```

#### 预埋账号
应用首次启动时会自动创建以下测试账号：
- 用户名: `admin`，密码: `123456`
- 邮箱: `admin@example.com`，密码: `123456`


## 🎨 UI 设计

### 设计规范
- 遵循 Material Design 3 设计规范
- 使用圆角设计（8dp）
- 统一的颜色主题
- 响应式布局

### 颜色方案
- 主色调: `#2196F3` (蓝色)
- 微信绿: `#07C160`
- Apple黑: `#000000`
- 输入框背景: `#F5F5F5`
- 文本颜色: `#333333` (深色), `#999999` (灰色)

## 🚀 快速开始

### 环境要求
- Android Studio Hedgehog | 2023.1.1 或更高版本
- JDK 11 或更高版本
- Android SDK 24 或更高版本

### 构建步骤

1. **克隆项目**
   ```bash
   git clone <repository-url>
   cd bytedancedemo1
   ```

2. **打开项目**
   - 使用 Android Studio 打开项目
   - 等待 Gradle 同步完成

3. **运行应用**
   - 连接 Android 设备或启动模拟器
   - 点击运行按钮或使用快捷键 `Shift + F10`

### 配置说明

#### 数据库初始化
数据库会在应用首次启动时自动初始化，预埋测试账号。

#### 镜像源配置
项目已配置阿里云 Maven 镜像源，加快依赖下载速度：
- 阿里云公共仓库
- 阿里云 Google 仓库
- 阿里云 Gradle 插件仓库

## 📱 使用说明

### 注册流程
1. 启动应用，显示开屏页面（2秒）
2. 进入登录页面，点击"还没有账户?立即注册"
3. 进入注册页面，输入用户名/邮箱、密码、确认密码
4. 系统验证输入格式和密码一致性
5. 系统检查用户名是否已存在（从 Room 数据库查询）
6. 注册成功后自动跳转到登录页面，用户名自动填充

### 登录流程
1. 在登录页面输入用户名/邮箱和密码
2. 点击登录按钮
3. 系统从 Room 数据库验证用户信息
4. 验证成功后跳转到个人中心页面

### 测试账号
- **方式1**: 用户名 `admin`，密码 `123456`
- **方式2**: 邮箱 `admin@example.com`，密码 `123456`

### SDK 功能验证

#### 1. 冷启动耗时验证
- 启动应用后，在 Logcat 中查看 `PerformanceMonitor` 标签
- 预期输出：`Cold start cost = xxx ms`
- 预期输出：`Cold start render page=SplashActivity cost=xxx ms`

#### 2. 页面渲染耗时验证
- 登录后进入个人中心页面
- 在 Logcat 中查看 `PageRenderTracer` 标签
- 预期输出：`Page render page=ProfileActivity cost=xxx ms`

#### 3. FPS / 卡顿监控验证
- 在任意 Activity 中滑动或操作
- 在 Logcat 中查看 `FrameMetricsCollector` 标签
- 预期输出：`Activity=ProfileActivity fps=59.8 jankRate=0.05`
- 每秒输出一次统计

#### 4. ANR 监控验证
- 进入个人中心页面（ProfileActivity）
- 点击底部的蓝色按钮："测试 ANR 监控（点击后主线程将阻塞6秒）"
- 等待 6 秒（UI 会卡住，这是预期行为）
- 在 Logcat 中查看 `AnrWatchdog` 标签
- 预期输出：
  ```
  检测到主线程卡顿，超过阈值 xxx ms
  ----- 主线程调用栈开始 -----
  at com.example.bytedancedemo1.ProfileActivity.lambda$setupListeners$...
  ...
  ----- 主线程调用栈结束 -----
  ```



### 网络优化
- 禁用源码下载，加快构建速度
- 增加超时时间配置
- 使用国内镜像源

## 📝 代码说明

### 数据存储逻辑

#### Room 数据库
- **用途**: 存储用户账号信息（用户名、密码）
- **注册**: 检查用户名唯一性，存储新用户
- **登录**: 验证用户账号和密码
- **位置**: `app/src/main/java/com/example/bytedancedemo1/database/`

#### SharedPreferences
- **用途**: 存储用户偏好设置（用户名、签名）
- **时机**: 在个人中心页面设置时保存
- **不用于**: 注册和登录时的数据存储
- **位置**: `app/src/main/java/com/example/bytedancedemo1/utils/SharedPreferencesHelper.java`

### 关键类说明

#### RegisterActivity
- 处理用户注册逻辑
- 验证输入格式和密码一致性
- 从 Room 数据库检查用户名唯一性
- 存储新用户到 Room 数据库

#### LoginActivity
- 处理用户登录逻辑
- 从 Room 数据库验证用户信息
- 登录成功后通过 Intent 传递用户名到个人中心

#### ProfileActivity
- 显示用户信息
- 从 Intent 或 SharedPreferences 读取用户名
- 管理用户偏好设置
- **ANR 测试按钮**：点击后阻塞主线程 6 秒，用于验证 ANR 监控功能

### SDK 关键类说明

#### PerformanceMonitor
- SDK 入口类，提供初始化和打点方法
- `init()`: 初始化 SDK，配置各项监控能力
- `markProcessStart()`: 标记进程启动时间
- `trackPageRender()`: 页面渲染打点
- `beginPageLoad()`: 页面跳转开始打点

#### PerformanceMonitoringConfig
- SDK 配置类，使用 Builder 模式
- 可配置是否启用启动监控、帧率监控、ANR 监控

#### PageRenderTracer
- 页面渲染追踪器
- 统计冷启动和普通页面的渲染耗时

#### FrameMetricsCollector
- 帧率监控器（API 24+）
- 基于 `Window.addOnFrameMetricsAvailableListener`
- 统计 FPS 和卡顿率

#### AnrMonitor
- ANR 监控器
- 主线程心跳 + 后台线程检查机制
- 检测到卡顿时输出调用栈

## 🔍 SDK 技术实现细节

### 冷启动耗时监控
- **起点**：`Application.attachBaseContext()` 调用 `markProcessStart()`
- **终点**：第一个 Activity 的 `onActivityStarted()` 回调
- **实现**：`ActivityLifecycleCallbacks` + `AtomicBoolean` 保证只记录一次

### 页面渲染耗时监控
- **冷启动页面**：从进程启动到 `DecorView.post()` 回调
- **普通页面**：从 `beginPageLoad()` 到 `DecorView.post()` 回调
- **实现**：`ConcurrentHashMap` 存储页面开始时间，`DecorView.post()` 计算耗时

### FPS / 卡顿监控
- **API 要求**：Android 7.0 (API 24) 及以上
- **实现**：`Window.addOnFrameMetricsAvailableListener`
- **指标计算**：
  - FPS = 帧数 / 时间（秒）
  - Jank = 超过 16.67ms（60fps 单帧时间）的帧数
  - Jank Rate = Jank / 总帧数

### ANR 监控
- **机制**：主线程每 500ms 发送心跳，后台线程每 500ms 检查心跳
- **阈值**：5 秒未收到心跳则判定为 ANR
- **实现**：
  - 主线程：`Handler.postDelayed()` 持续发送心跳
  - 后台线程：`HandlerThread` + `Handler` 定时检查
  - 检测到 ANR 时：`Thread.getStackTrace()` 获取调用栈

### 线程安全设计
- **双重检查锁**：SDK 初始化使用 `volatile` + `synchronized`
- **原子操作**：使用 `AtomicBoolean`、`AtomicLong` 保证线程安全
- **并发集合**：使用 `ConcurrentHashMap` 存储页面开始时间

## 📊 性能监控最佳实践

### 1. 初始化时机
- 在 `Application.onCreate()` 中尽早初始化
- 避免在 `attachBaseContext()` 中初始化（可能影响启动耗时）


