package com.example.monitoringsdk;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * SDK 入口，负责初始化、启动时间记录、子能力装配。
 */
public final class PerformanceMonitor {

    private static final String TAG = "PerformanceMonitor";

    private static volatile boolean sInited = false;
    private static Application sApplication;
    private static PerformanceMonitoringConfig sConfig;

    private static final AtomicBoolean sStartupRecorded = new AtomicBoolean(false);
    private static long sAppStartTime = SystemClock.elapsedRealtime();
    private static final Object sInitLock = new Object();

    private PerformanceMonitor() {
    }
    // volatile + 双重检查锁保证线程安全
    public static void init(@NonNull Application application,
                            @NonNull PerformanceMonitoringConfig config) {
        // 第一次无锁检查（性能优化）
        if (sInited) {
            return;
        }

        synchronized (sInitLock) {
            // 第二次有锁检查（线程安全）
            if (sInited) {
                return;
            }

            // 赋值操作
            sApplication = application;
            sConfig = config;

            // 初始化模块
            try {
                if (sConfig.enableStartup) {
                    hookStartup();  // 可能抛出异常
                }
                if (sConfig.enableFrameMetrics) {
                    hookFrameMetrics();
                }
                if (sConfig.enableAnrWatchdog) {
                    AnrMonitor.install();
                }

                // 所有初始化成功后，最后设置标志
                // volatile 写，确保前面的操作对其他线程可见
                sInited = true;

            } catch (Exception e) {
                // 初始化失败，清理状态
                sApplication = null;
                sConfig = null;
                throw new RuntimeException("SDK initialization failed", e);
            }
        }
    }

    /**
     * 从Application.attachBaseContext调用
     */
    public static void markProcessStart() {
        sAppStartTime = SystemClock.elapsedRealtime();
        PageRenderTracer.setAppLaunchStart(sAppStartTime);
    }

    /**
     * 标记用户触发的页面导航的开始。
     */
    public static void beginPageLoad(@NonNull String pageName) {
        if (!isStartupFeatureEnabled()) {
            return;
        }
        PageRenderTracer.beginInteraction(pageName);
    }

    /**
     * 观察给定的活动何时完成渲染。回调在decorview.post之后运行，
     * *因此测量范围从记录的开始到帧调度时刻。
     */
    public static void trackPageRender(@NonNull Activity activity,
                                       @NonNull String pageName) {
        trackPageRender(activity, pageName, false);
    }

    public static void trackPageRender(@NonNull Activity activity,
                                       @NonNull String pageName,
                                       boolean isColdStartPage) {
        if (!isStartupFeatureEnabled()) {
            return;
        }
        PageRenderTracer.observePageRender(activity, pageName, isColdStartPage);
    }

    private static void hookStartup() {
        sApplication.registerActivityLifecycleCallbacks(
                new Application.ActivityLifecycleCallbacks() {
                    @Override
                    public void onActivityCreated(@NonNull Activity activity, Bundle savedInstanceState) {
                        // no-op
                    }
                    // 在第一个 Activity 的 onActivityStarted 时计算启动耗时
                    @Override
                    public void onActivityStarted(@NonNull Activity activity) {
                        if (sStartupRecorded.compareAndSet(false, true)) {
                            long now = SystemClock.elapsedRealtime();
                            long cost = now - sAppStartTime;
                            Log.i(TAG, "Cold start cost = " + cost + " ms");
                        }
                    }

                    @Override
                    public void onActivityResumed(@NonNull Activity activity) {
                    }

                    @Override
                    public void onActivityPaused(@NonNull Activity activity) {
                    }

                    @Override
                    public void onActivityStopped(@NonNull Activity activity) {
                    }

                    @Override
                    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
                    }

                    @Override
                    public void onActivityDestroyed(@NonNull Activity activity) {
                    }
                }
        );
    }

    private static void hookFrameMetrics() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FrameMetricsCollector.install(sApplication);
        } else {
            Log.w(TAG, "FrameMetrics not supported below API 24");
        }
    }

    private static boolean isStartupFeatureEnabled() {
        return sConfig != null && sConfig.enableStartup;
    }
}



