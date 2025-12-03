package com.example.monitoringsdk;

import android.app.Application;

/**
 * 配置类，控制是否开启启动监控、帧率监控、ANR 监控。
 * 使用 Builder 模式
 */
public class PerformanceMonitoringConfig {
    // final 字段确保对象不可变（线程安全）
    public final boolean enableStartup;
    public final boolean enableFrameMetrics;
    public final boolean enableAnrWatchdog;

    private PerformanceMonitoringConfig(Builder builder) {
        this.enableStartup = builder.enableStartup;
        this.enableFrameMetrics = builder.enableFrameMetrics;
        this.enableAnrWatchdog = builder.enableAnrWatchdog;
    }

    public static class Builder {
        private boolean enableStartup = true;
        private boolean enableFrameMetrics = true;
        private boolean enableAnrWatchdog = true;
        // 链式调用方法
        public Builder enableStartup(boolean enable) {
            this.enableStartup = enable;
            return this;    // 返回 this 实现链式调用
        }

        public Builder enableFrameMetrics(boolean enable) {
            this.enableFrameMetrics = enable;
            return this;
        }

        public Builder enableAnrWatchdog(boolean enable) {
            this.enableAnrWatchdog = enable;
            return this;
        }
        // build 方法创建最终对象
        public PerformanceMonitoringConfig build() {
            return new PerformanceMonitoringConfig(this);
        }
    }
}



