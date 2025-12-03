plugins {
    alias(libs.plugins.android.application) apply false
}
buildscript {
    repositories {
        // 优先使用阿里云镜像
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
        maven { url = uri("https://maven.aliyun.com/repository/central") }
        // 备用官方源
        google()
        mavenCentral()
    }
}
