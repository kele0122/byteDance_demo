package com.example.monitoringsdk;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.FrameMetrics;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 基于 Window.addOnFrameMetricsAvailableListener 的 FPS / 丢帧监控。
 */
final class FrameMetricsCollector {

    private static final String TAG = "FrameMetricsCollector";
    // 用于 FPS / jank 计算
    private static final long NANOS_IN_SECOND = 1_000_000_000L;
    private static final long FRAME_DURATION_60HZ_NS = 16_666_667L;
    //记录每个 Activity 的监听器避免重复注册
    private static final ConcurrentHashMap<Activity, Window.OnFrameMetricsAvailableListener> sListeners =
            new ConcurrentHashMap<>();
    // 用于在主线程之外收集帧数据的线程和处理器
    private static final HandlerThread sCollectorThread;
    private static final Handler sCollectorHandler;
    // 静态代码块：类加载时自动执行，JVM保证只执行一次，保证线程安全
    static {
        sCollectorThread = new HandlerThread("monitoringsdk-frame-metrics");
        sCollectorThread.start();
        sCollectorHandler = new Handler(sCollectorThread.getLooper());
    }

    static void install(@NonNull Application app) {
        // 注册 Activity 生命周期回调，收集每个 Activity 的帧数据
        // 统一管理：集中处理所有 Activity 的生命周期事件
        // 避免重复代码：无需在每个 Activity 中编写相同的逻辑
        app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                attach(activity);
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                detach(activity);
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
            }
        });
    }

    private static void attach(@NonNull Activity activity) {
        // 1. Window 是 Android 视图系统中的顶级容器， 使用 Window 能获取到系统的帧数据
        final Window window = activity.getWindow();
        if (window == null) {
            return;
        }

        // 2. 检查是否已经为该 Activity 注册过监听器
        if (sListeners.containsKey(activity)) {
            return;
        }

        // 3. 创建统计对象
        final Stats stats = new Stats();

        // 4. 创建帧率监听器
        /*w：触发回调的 Window（与注册时相同）
        metrics：FrameMetrics 对象，包含帧性能数据
        dropCountSinceLastInvocation：自上次回调以来丢弃的帧数（Android 8.0+）*/
        Window.OnFrameMetricsAvailableListener listener =
                (w, metrics, dropCountSinceLastInvocation) -> {

                    // 5. 获取帧总耗时（纳秒）
                   /* FrameMetrics.TOTAL_DURATION：帧从开始到显示的总耗时（纳秒）
                    包含 CPU 处理、GPU 渲染、系统合成等所有时间*/
                    long totalDurationNs = metrics.getMetric(FrameMetrics.TOTAL_DURATION);

                    // 6. 更新统计信息
                    stats.frameCount.incrementAndGet();
                    stats.totalDurationNs.addAndGet(totalDurationNs);

                    // 7. 计算卡顿（Jank）
                    long jankForThisFrame = Math.max(0,
                            (totalDurationNs - FRAME_DURATION_60HZ_NS) / FRAME_DURATION_60HZ_NS);
                    if (jankForThisFrame > 0) {
                        stats.jankFrameCount.incrementAndGet();
                    }

                    // 8. 获取当前时间
                    long nowNs = System.nanoTime();

                    // 9. 如果是第一次记录，初始化报告时间
                    if (stats.lastReportNs == 0) {
                        stats.lastReportNs = nowNs;
                    }

                    // 10. 计算距离上次报告的时间差
                    long elapsed = nowNs - stats.lastReportNs;

                    // 11. 如果超过1秒，输出报告
                    if (elapsed >= NANOS_IN_SECOND) {
                        // 11.1 获取并重置计数器
                        long frames = stats.frameCount.getAndSet(0);
                        long jank = stats.jankFrameCount.getAndSet(0);
                        stats.totalDurationNs.set(0);
                        stats.lastReportNs = nowNs;

                        // 11.2 计算 FPS 和卡顿率
                        float fps = (float) frames * NANOS_IN_SECOND / elapsed;
                        float jankRate = frames > 0 ? (float) jank / frames : 0f;

                        // 11.3 输出日志
                        Log.i(TAG, "Activity=" + activity.getClass().getSimpleName()
                                + " fps=" + fps
                                + " jankRate=" + jankRate);
                    }
                };

        // 12. 将监听器注册到 Window
        window.addOnFrameMetricsAvailableListener(listener, sCollectorHandler);

        // 13. 保存监听器引用到 Map
        sListeners.put(activity, listener);
    }

    private static void detach(@NonNull Activity activity) {
        Window.OnFrameMetricsAvailableListener listener = sListeners.remove(activity);
        if (listener != null) {
            Window window = activity.getWindow();
            if (window != null) {
                window.removeOnFrameMetricsAvailableListener(listener);
            }
        }
    }
    // 在多线程环境下的读写场景
    private static final class Stats {
        // 这三个字段使用 AtomicLong，内部已经是 volatile + CAS
        final AtomicLong frameCount = new AtomicLong();      // 帧计数器，原子操作
        final AtomicLong jankFrameCount = new AtomicLong(); // 卡顿帧计数器，原子操作
        final AtomicLong totalDurationNs = new AtomicLong(); // 总耗时，原子操作
        // lastReportNs 需要单独读写，不需要原子操作
        volatile long lastReportNs;                         // 上次报告时间，只需保证可见性
    }
}



