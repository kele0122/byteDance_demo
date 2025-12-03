package com.example.monitoringsdk;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

/**
 * ANR 监控入口，使用 watch dog方案。
 */
final class AnrMonitor {

    private AnrMonitor() {
    }

    static void install() {
        Watchdog.install();
    }

    /**
     * 使用“主线程心跳 + 后台定时检查”的方式检测 ANR，
     * 避免和用户点击的时序刚好错开而漏检。
     */
    private static final class Watchdog {

        private static final String TAG = "AnrWatchdog";
        // 超过该时间未收到主线程心跳，则认为可能发生 ANR
        private static final long ANR_TIMEOUT_MS = 5_000L;
        // 心跳间隔（越小越敏感）
        private static final long HEARTBEAT_INTERVAL_MS = 500L;

        private static HandlerThread sThread;
        private static Handler sBgHandler;
        private static Handler sMainHandler;
        // 主线程最近一次正常响应的时间
        private static volatile long sLastResponsiveTime = 0L;

        private static final Runnable sHeartbeatRunnable = new Runnable() {
            @Override
            public void run() {
                sLastResponsiveTime = SystemClock.uptimeMillis();
                Log.d(TAG, "heartbeat on main, t=" + sLastResponsiveTime);
                if (sMainHandler != null) {
                    sMainHandler.postDelayed(this, HEARTBEAT_INTERVAL_MS);
                }
            }
        };

        private static final Runnable sCheckRunnable = new Runnable() {
            @Override
            public void run() {
                long now = SystemClock.uptimeMillis();
                long last = sLastResponsiveTime;
                long diff = now - last;
                Log.d(TAG, "watchdog check, diff=" + diff);
                if (last > 0 && diff > ANR_TIMEOUT_MS) {
                    Log.e(TAG, "检测到主线程卡顿，超过阈值 " + diff + " ms");
                    dumpMainThreadStack(TAG);
                }
                if (sBgHandler != null) {
                    sBgHandler.postDelayed(this, HEARTBEAT_INTERVAL_MS);
                }
            }
        };

        static void install() {
            if (sThread != null) {
                return;
            }
            Log.i(TAG, "install watchdog");
            sLastResponsiveTime = SystemClock.uptimeMillis();
            sThread = new HandlerThread("monitoringsdk-anr-watchdog");
            sThread.start();
            sBgHandler = new Handler(sThread.getLooper());
            sMainHandler = new Handler(Looper.getMainLooper());
            // 主线程持续发送心跳
            sMainHandler.post(sHeartbeatRunnable);
            // 后台线程持续检查心跳是否超时
            sBgHandler.postDelayed(sCheckRunnable, HEARTBEAT_INTERVAL_MS);
        }
    }

    private static void dumpMainThreadStack(String tag) {
        Thread mainThread = Looper.getMainLooper().getThread();
        if (mainThread == null) {
            Log.e(tag, "无法获取主线程引用");
            return;
        }
        Log.e(tag, "----- 主线程调用栈开始 -----");
        for (StackTraceElement element : mainThread.getStackTrace()) {
            Log.e(tag, "  at " + element);
        }
        Log.e(tag, "----- 主线程调用栈结束 -----");
    }
}