package com.example.bytedancedemo1;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.monitoringsdk.PerformanceMonitor;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000; // 开屏显示2秒

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 冷启动页面渲染打点（用于统计从进程启动到第一个页面可见的耗时）
        PerformanceMonitor.trackPageRender(this, "SplashActivity", true);

        // 延迟跳转到登录页面
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // 跳转到登录页面
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // 关闭开屏页面
            }
        }, SPLASH_DELAY);
    }
}
