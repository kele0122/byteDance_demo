package com.example.bytedancedemo1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bytedancedemo1.database.AppDatabase;
import com.example.bytedancedemo1.database.User;
import com.example.bytedancedemo1.database.UserDao;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {
    
    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmPassword;
    private Button btnRegister;
    private TextView tvLogin;
    
    private AppDatabase database;
    private UserDao userDao;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        // 初始化数据库
        AppDatabase.initDatabase(this);
        database = AppDatabase.getInstance(this);
        userDao = database.userDao();
        
        initViews();
        setupListeners();
    }
    
    private void initViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
    }
    
    private void setupListeners() {
        // 注册按钮点击事件
        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();
            
            if (validateInput(username, password, confirmPassword)) {
                register(username, password);
            }
        });
        
        // 已有账号链接点击事件
        tvLogin.setOnClickListener(v -> {
            // 跳转到登录页面
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
    
    private boolean validateInput(String username, String password, String confirmPassword) {
        if (username.isEmpty()) {
            Toast.makeText(this, "请输入用户名或邮箱", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        // 如果输入包含@符号，验证邮箱格式；否则认为是用户名
        if (username.contains("@")) {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
                Toast.makeText(this, "请输入有效的邮箱地址", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        
        if (password.isEmpty()) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (password.length() < 6) {
            Toast.makeText(this, "密码长度至少6位", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (confirmPassword.isEmpty()) {
            Toast.makeText(this, "请再次输入密码", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        return true;
    }
    
    private void register(String username, String password) {
        // 检查用户名是否已存在（从Room数据库查询）
        User existingUser = userDao.getUserByUsername(username);
        
        if (existingUser != null) {
            Toast.makeText(this, "该用户名或邮箱已被注册", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 创建新用户并存储到Room数据库
        User newUser = new User(username, password);
        userDao.insertUser(newUser);
        
        Toast.makeText(this, "注册成功！", Toast.LENGTH_SHORT).show();
        
        // 注册成功后跳转到登录页面
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.putExtra("username", username); // 传递用户名到登录页面
        startActivity(intent);
        finish();
    }
}

