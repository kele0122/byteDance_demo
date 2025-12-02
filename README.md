# ByteDance Demo1

一个基于 Android 的登录、注册和个人中心应用示例项目。

## 📱 项目简介

本项目实现了用户注册、登录、个人中心等基础功能。应用采用 Material Design 设计规范，使用 Room 数据库存储用户账号信息，SharedPreferences 存储用户偏好设置（用户名、签名等）。

## ✨ 功能特性

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
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/bytedancedemo1/
│   │   │   │   ├── SplashActivity.java          # 开屏页面
│   │   │   │   ├── RegisterActivity.java        # 注册页面
│   │   │   │   ├── LoginActivity.java           # 登录页面
│   │   │   │   ├── ProfileActivity.java         # 个人中心页面
│   │   │   │   ├── MainActivity.java            # 主页面
│   │   │   │   ├── database/                    # 数据库相关
│   │   │   │   │   ├── AppDatabase.java        # Room数据库
│   │   │   │   │   ├── User.java               # 用户实体类
│   │   │   │   │   └── UserDao.java            # 数据访问对象
│   │   │   │   └── utils/                      # 工具类
│   │   │   │       └── SharedPreferencesHelper.java  # SharedPreferences工具
│   │   │   ├── res/
│   │   │   │   ├── layout/                     # 布局文件
│   │   │   │   │   ├── activity_splash.xml
│   │   │   │   │   ├── activity_register.xml
│   │   │   │   │   ├── activity_login.xml
│   │   │   │   │   └── activity_profile.xml
│   │   │   │   ├── drawable/                   # 图片和drawable资源
│   │   │   │   ├── values/                     # 资源值
│   │   │   │   │   ├── colors.xml
│   │   │   │   │   ├── strings.xml
│   │   │   │   │   └── themes.xml
│   │   │   │   └── icon/                       # 图标资源
│   │   │   └── AndroidManifest.xml
│   │   └── test/                               # 测试代码
│   └── build.gradle.kts                        # 应用构建配置
├── gradle/
│   ├── libs.versions.toml                      # 依赖版本管理
│   └── wrapper/                                # Gradle包装器
├── build.gradle.kts                            # 项目构建配置
├── settings.gradle.kts                         # 项目设置
└── gradle.properties                           # Gradle属性配置
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

#### 其他
- **AndroidX Activity**: 1.8.0
- **JUnit**: 4.13.2 (测试)
- **Espresso**: 3.5.1 (UI测试)

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

#### 使用场景
- **注册时**: 检查用户名是否已存在，存储新用户到 Room 数据库
- **登录时**: 从 Room 数据库验证用户账号和密码
- **不存储**: 不在注册或登录时存储到 SharedPreferences

#### 预埋账号
应用首次启动时会自动创建以下测试账号：
- 用户名: `admin`，密码: `123456`
- 邮箱: `admin@example.com`，密码: `123456`

### SharedPreferences（用户偏好设置）

#### 存储内容
- **用户名**: 用于个人中心页面显示
- **签名**: 用户个性签名

#### 使用场景
- **不在注册时存储**: 注册时只存储到 Room 数据库
- **不在登录时存储**: 登录时通过 Intent 传递用户名
- **在个人中心**: 用于显示和保存用户偏好设置（用户名、签名）

#### 数据位置
- 文件路径: `/data/data/com.example.bytedancedemo1/shared_prefs/user_prefs.xml`
- 查看方式: 使用 Android Studio 的 Device File Explorer 或 adb 命令

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

## 🔧 配置优化

### Gradle 性能优化
项目已配置以下优化选项：
- 并行构建: `org.gradle.parallel=true`
- 构建缓存: `org.gradle.caching=true`
- 按需配置: `org.gradle.configureondemand=true`
- 守护进程: `org.gradle.daemon=true`
- JVM 内存: `-Xmx4096m`

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

## 📄 许可证

本项目仅供学习和参考使用。

---

**最后更新**: 2024年
