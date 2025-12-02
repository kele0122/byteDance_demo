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

public class LoginActivity extends AppCompatActivity {
    
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private Button btnLogin;
    private Button btnWechatLogin;
    private Button btnAppleLogin;
    private TextView tvForgotPassword;
    private TextView tvRegister;
    
    private AppDatabase database;
    private UserDao userDao;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        // 初始化数据库
        AppDatabase.initDatabase(this);
        database = AppDatabase.getInstance(this);
        userDao = database.userDao();
        
        initViews();
        setupListeners();
    }
    
    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnWechatLogin = findViewById(R.id.btnWechatLogin);
        btnAppleLogin = findViewById(R.id.btnAppleLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvRegister = findViewById(R.id.tvRegister);
    }
    
    private void setupListeners() {
        // 登录按钮点击事件
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            
            if (validateInput(email, password)) {
                login(email, password);
            }
        });
        
        // 微信登录点击事件
        btnWechatLogin.setOnClickListener(v -> {
            Toast.makeText(LoginActivity.this, "微信登录功能暂未开放", Toast.LENGTH_SHORT).show();
        });
        
        // Apple登录点击事件
        btnAppleLogin.setOnClickListener(v -> {
            Toast.makeText(LoginActivity.this, "Apple登录功能暂未开放", Toast.LENGTH_SHORT).show();
        });
        
        // 忘记密码点击事件
        tvForgotPassword.setOnClickListener(v -> {
            Toast.makeText(LoginActivity.this, "忘记密码功能暂未开放", Toast.LENGTH_SHORT).show();
        });
        
        // 注册点击事件
        tvRegister.setOnClickListener(v -> {
            // 跳转到注册页面
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
    
    private boolean validateInput(String email, String password) {
        if (email.isEmpty()) {
            Toast.makeText(this, "请输入用户名或邮箱", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        // 如果输入包含@符号，验证邮箱格式；否则认为是用户名
        if (email.contains("@")) {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "请输入有效的邮箱地址", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        // 如果不包含@符号，认为是用户名，直接通过验证
        
        if (password.isEmpty()) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        return true;
    }
    
    private void login(String email, String password) {
        // 从Room数据库查询用户（登录验证）
        User user = userDao.getUserByUsernameAndPassword(email, password);
        
        if (user != null) {
            // 登录成功，从Room数据库读取用户名
            // 注意：SharedPreferences只用于存储用户名和签名，不在登录时存储
            // 用户名和签名应该在个人中心页面设置时存储
            
            // 跳转到个人中心页面，传递用户名
            Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
            intent.putExtra("username", email);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "邮箱或密码错误", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // 如果从注册页面返回，填充用户名
        String username = getIntent().getStringExtra("username");
        if (username != null && !username.isEmpty()) {
            etEmail.setText(username);
        }
    }
}

