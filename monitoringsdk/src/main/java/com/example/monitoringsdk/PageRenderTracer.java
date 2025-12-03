package com.example.monitoringsdk;

import android.app.Activity;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 跟踪冷启动与后续页面的渲染耗时。
 *
 * 起点：
 * - 冷启动：Application.attachBaseContext 中的 markProcessStart 打点
 * - 页面跳转：用户点击按钮触发 beginInteraction
 *
 * 终点：
 * - DecorView.post(...) 回调触发，代表布局/绘制已经进入下一帧队列
 */
final class PageRenderTracer {

    private static final String TAG = "PageRenderTracer";

    private static volatile long sAppLaunchStart = -1L;
    private static final AtomicBoolean sAppLaunchReported = new AtomicBoolean(false);
    private static final ConcurrentHashMap<String, Long> sInteractionStart = new ConcurrentHashMap<>();

    private PageRenderTracer() {
    }

    // 记录 APP 冷启动的最早起点，供后续计算冷启动渲染耗时
    static void setAppLaunchStart(long timestamp) {
        sAppLaunchStart = timestamp;
        sAppLaunchReported.set(false);
    }

    // 记录用户点击二级页面的时刻，作为页面加载起点
    static void beginInteraction(@NonNull String pageName) {
        sInteractionStart.put(pageName, SystemClock.elapsedRealtime());
    }

    // 在 DecorView.post 回调中统计对应页面的渲染时间
    static void observePageRender(@NonNull Activity activity,
                                  @NonNull String pageName,
                                  boolean isColdStartPage) {
        Window window = activity.getWindow();
        if (window == null) {
            Log.w(TAG, "Window is null for page=" + pageName);
            return;
        }
        View decorView = window.getDecorView();
        if (decorView == null) {
            Log.w(TAG, "DecorView is null for page=" + pageName);
            return;
        }

        decorView.post(() -> {
            long end = SystemClock.elapsedRealtime();
            if (isColdStartPage) {
                if (sAppLaunchStart <= 0) {
                    Log.w(TAG, "Cold start timestamp not set");
                    return;
                }
                if (!sAppLaunchReported.compareAndSet(false, true)) {
                    Log.d(TAG, "Cold start already reported, ignore duplicate for " + pageName);
                    return;
                }
                long cost = end - sAppLaunchStart;
                Log.i(TAG, "Cold start render page=" + pageName + " cost=" + cost + " ms");
            } else {
                Long start = sInteractionStart.remove(pageName);
                if (start == null) {
                    Log.w(TAG, "No interaction start recorded for page=" + pageName);
                    return;
                }
                long cost = end - start;
                Log.i(TAG, "Page render page=" + pageName + " cost=" + cost + " ms");
            }
        });
    }
}


