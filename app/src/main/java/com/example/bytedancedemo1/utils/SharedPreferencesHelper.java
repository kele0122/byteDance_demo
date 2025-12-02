package com.example.bytedancedemo1.utils;

import android.content.Context;
import android.content.SharedPreferences;



/*##

**作用**: 封装 SharedPreferences 操作，提供简洁的 API 用于存储和读取用户偏好设置

**核心功能**:

#### 1. 存储键值定义
private static final String PREF_NAME = "user_prefs";        // SharedPreferences 文件名
private static final String KEY_USERNAME = "username";       // 用户名的键
private static final String KEY_SIGNATURE = "signature";     // 签名的键

- 集中管理所有键名，避免硬编码
- 统一修改，降低出错风险

#### 2. 初始化
public SharedPreferencesHelper(Context context) {
    sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
}
- 使用 `MODE_PRIVATE` 模式，确保数据只能被当前应用访问
- 文件名: `"user_prefs"`

#### 3. SharedPreferences 的特点

**优点**:
- ✅ 轻量级，适合存储简单的键值对
- ✅ 使用方便，无需 SQL 语句
- ✅ 自动持久化，应用重启后数据仍然存在
- ✅ 线程安全

**适用场景**:
- ✅ 用户偏好设置（如用户名、签名）
- ✅ 应用配置信息
- ✅ 简单的标志位（如首次启动标记）
- ✅ 小量数据（建议不超过 1MB）

**不适用场景**:
- ❌ 大量结构化数据（应使用 Room/SQLite）
- ❌ 复杂查询需求
- ❌ 需要关系型数据存储

*/

public class SharedPreferencesHelper {
    private static final String PREF_NAME = "user_prefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_SIGNATURE = "signature";
    
    private SharedPreferences sharedPreferences;


    public SharedPreferencesHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    /*
    | 方法 | 说明 | 参数 | 返回值 |
    |------|------|------|--------|
    | `saveUsername(String username)` | 保存用户名 | 用户名字符串 | 无 |
    | `getUsername()` | 获取用户名 | 无 | 用户名（默认值："用户"） |
    | `saveSignature(String signature)` | 保存个性签名 | 签名字符串 | 无 |
    | `getSignature()` | 获取个性签名 | 无 | 签名（默认值："这个人很懒，什么都没有留下"） |
    | `clear()` | 清空所有数据 | 无 | 无 |*/
    
    public void saveUsername(String username) {
        sharedPreferences.edit().putString(KEY_USERNAME, username).apply();
    }
    
    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, "用户");
    }
    
    public void saveSignature(String signature) {
        sharedPreferences.edit().putString(KEY_SIGNATURE, signature).apply();
    }
    
    public String getSignature() {
        return sharedPreferences.getString(KEY_SIGNATURE, "这个人很懒，什么都没有留下");
    }
    /* `apply()`: 异步提交，不会阻塞主线程，性能更好
    `commit()`: 同步提交，会阻塞线程直到写入完成*/
    public void clear() {
        sharedPreferences.edit().clear().apply();
    }
}

