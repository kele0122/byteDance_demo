package com.example.bytedancedemo1.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

/*UserDao.java - 数据访问对象

**作用**: 定义数据库操作的接口，包含所有对用户表的增删改查方法

**关键注解**:
        - `@Dao`: 标记为 Room 的数据访问对象接口

**提供的方法**:

        | 方法 | 说明 | SQL 操作 |
        |------|------|----------|
        | `insertUser(User user)` | 插入新用户 | INSERT |
        | `getUserByUsernameAndPassword(String username, String password)` | 根据用户名和密码查询用户（用于登录验证） | SELECT ... WHERE |
        | `getUserByUsername(String username)` | 根据用户名查询用户 | SELECT ... WHERE |
        | `getUserCount()` | 获取用户总数（用于判断数据库是否为空） | SELECT COUNT(*) |

*/
@Dao
public interface UserDao {
    @Insert
    void insertUser(User user);
    
    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    User getUserByUsernameAndPassword(String username, String password);
    
    @Query("SELECT * FROM users WHERE username = :username")
    User getUserByUsername(String username);
    
    @Query("SELECT COUNT(*) FROM users")
    int getUserCount();
}

