package com.example.bytedancedemo1;

import android.app.Application;
import android.content.Context;

import com.example.monitoringsdk.PerformanceMonitor;
import com.example.monitoringsdk.PerformanceMonitoringConfig;

/**
 * 自定义 Application，作为监控 SDK 的初始化入口。
 */
public class App extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // 标记进程启动时间（用于冷启动耗时统计）
        PerformanceMonitor.markProcessStart();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // 构建监控配置：根据需要开关各项能力
        PerformanceMonitoringConfig config = new PerformanceMonitoringConfig.Builder()
                .enableStartup(true)        // 冷启动耗时
                .enableFrameMetrics(true)   // FPS / 丢帧监控
                .enableAnrWatchdog(true)    // ANR 监控
                .build();

        // 初始化性能监控 SDK（只需调用一次）
        PerformanceMonitor.init(this, config);
    }
}


