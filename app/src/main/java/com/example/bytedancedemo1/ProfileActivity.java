package com.example.bytedancedemo1;

import android.os.Build;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bytedancedemo1.utils.SharedPreferencesHelper;

public class ProfileActivity extends AppCompatActivity {
    
    private ImageView ivAvatar;
    private TextView tvUsername;
    private TextView tvWelcome;
    private LinearLayout llPersonalInfo;
    private LinearLayout llFavorites;
    private LinearLayout llHistory;
    private LinearLayout llSettings;
    private LinearLayout llAbout;
    private LinearLayout llFeedback;
    
    private SharedPreferencesHelper prefsHelper;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        
        prefsHelper = new SharedPreferencesHelper(this);
        
        initViews();
        loadUserInfo();
        setupListeners();
    }
    
    private void initViews() {
        ivAvatar = findViewById(R.id.ivAvatar);
        tvUsername = findViewById(R.id.tvUsername);
        tvWelcome = findViewById(R.id.tvWelcome);
        llPersonalInfo = findViewById(R.id.llPersonalInfo);
        llFavorites = findViewById(R.id.llFavorites);
        llHistory = findViewById(R.id.llHistory);
        llSettings = findViewById(R.id.llSettings);
        llAbout = findViewById(R.id.llAbout);
        llFeedback = findViewById(R.id.llFeedback);
        
        // 设置圆形头像 - 使用 ViewOutlineProvider (API 21+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ivAvatar.setClipToOutline(true);
            ivAvatar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    ivAvatar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    // 创建圆形outline provider
                    ivAvatar.setOutlineProvider(new android.view.ViewOutlineProvider() {
                        @Override
                        public void getOutline(android.view.View view, android.graphics.Outline outline) {
                            outline.setOval(0, 0, view.getWidth(), view.getHeight());
                        }
                    });
                }
            });
        }
    }
    
    private void loadUserInfo() {
        // 从Intent获取用户名（登录时传递）
        String username = getIntent().getStringExtra("username");
        
        // 如果Intent中没有，则从SharedPreferences读取（可能是之前保存的）
        if (username == null || username.isEmpty()) {
            username = prefsHelper.getUsername();
        } else {
            // 如果从登录页面传递了用户名，保存到SharedPreferences（用于显示）
            // 注意：这里只保存用户名用于显示，签名应该在用户设置时保存
            prefsHelper.saveUsername(username);
        }
        
        // 设置用户头像
        ivAvatar.setImageResource(R.drawable.touxiang);
        
        // 更新UI
        tvUsername.setText(username);
        tvWelcome.setText("欢迎来到信息App");
    }
    
    private void setupListeners() {
        // 个人信息条目点击事件
        llPersonalInfo.setOnClickListener(v -> {
            Toast.makeText(ProfileActivity.this, "点击了个人信息", Toast.LENGTH_SHORT).show();
        });
        
        // 我的收藏条目点击事件
        llFavorites.setOnClickListener(v -> {
            Toast.makeText(ProfileActivity.this, "点击了我的收藏", Toast.LENGTH_SHORT).show();
        });
        
        // 浏览历史条目点击事件
        llHistory.setOnClickListener(v -> {
            Toast.makeText(ProfileActivity.this, "点击了浏览历史", Toast.LENGTH_SHORT).show();
        });
        
        // 设置条目点击事件
        llSettings.setOnClickListener(v -> {
            Toast.makeText(ProfileActivity.this, "点击了设置", Toast.LENGTH_SHORT).show();
        });
        
        // 关于我们条目点击事件
        llAbout.setOnClickListener(v -> {
            Toast.makeText(ProfileActivity.this, "点击了关于我们", Toast.LENGTH_SHORT).show();
        });
        
        // 意见反馈条目点击事件
        llFeedback.setOnClickListener(v -> {
            Toast.makeText(ProfileActivity.this, "点击了意见反馈", Toast.LENGTH_SHORT).show();
        });
    }
}

