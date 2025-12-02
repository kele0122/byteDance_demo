package com.example.bytedancedemo1.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/*AppDatabase.java - 数据库类

**作用**: Room 数据库的主类，负责数据库的创建、管理和初始化

**关键注解**:
  - `@Database(entities = {User.class}, version = 1, exportSchema = false)`:
  - `entities`: 指定数据库包含的实体类
  - `version`: 数据库版本号
  - `exportSchema`: 是否导出数据库架构（用于版本迁移）

**核心功能**:

#### 单例模式
```java
private static AppDatabase instance;
public static synchronized AppDatabase getInstance(Context context)
```
- 使用单例模式确保整个应用只有一个数据库实例
- `synchronized` 关键字保证线程安全

#### 数据库初始化
```java
public static void initDatabase(Context context)
```
- 在应用首次启动时调用
- 检查数据库是否为空（通过 `getUserCount()`）
- 如果为空，自动插入预埋的测试账号
- 确保应用始终有可用的测试账号

#### 配置说明
- `allowMainThreadQueries()`: 允许在主线程执行数据库查询（仅用于演示，生产环境应使用后台线程）
- 数据库名称: `"app_database"`
- 数据库文件位置: `/data/data/com.example.bytedancedemo1/databases/app_database`

*/

@Database(entities = {User.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;
    
    public abstract UserDao userDao();
    
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "app_database")
                    .allowMainThreadQueries() // 允许在主线程查询
                    .build();
        }
        return instance;
    }
    
    // 初始化数据库，预埋账号
    public static void initDatabase(Context context) {
        AppDatabase db = getInstance(context);
        UserDao userDao = db.userDao();
        
        // 如果数据库为空，则插入预埋账号
        if (userDao.getUserCount() == 0) {
            // 预埋账号：可以使用邮箱或用户名
//            User defaultUser1 = new User("admin@example.com", "123456");
//            userDao.insertUser(defaultUser1);
            // 也可以添加一个使用用户名的账号
            User defaultUser2 = new User("admin", "123456");
            userDao.insertUser(defaultUser2);
        }
    }
}

