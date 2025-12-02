package com.example.bytedancedemo1.database;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/* User.java - 用户实体类

**作用**: 定义用户数据表的结构和字段

**关键注解**:
        - `@Entity(tableName = "users")`: 标记为 Room 实体类，表名为 "users"
        - `@PrimaryKey(autoGenerate = true)`: 主键，自动生成
- `@Ignore`: 标记带参构造函数，Room 不使用此构造函数

**字段说明**:
        ```java
- id: int 类型，主键，自动递增
- username: String 类型，用户名或邮箱
- password: String 类型，用户密码
```

**特点**:
- 提供无参构造函数（Room 必需）
- 提供带参构造函数用于快速创建对象（使用 `@Ignore` 注解）
- 标准的 Getter/Setter 方法*/

@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private String username;
    private String password;
    
    public User() {
    }
    
    @Ignore
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}

