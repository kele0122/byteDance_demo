// Top-level build file where you can add configuration options common to all sub-projects/modules.
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

// 禁用源码转换 - 解决 TransformException: Failed to transform gradle-8.13-src.zip
subprojects {
    afterEvaluate {
        configurations.all {
            // 禁用 src-directory 属性请求，强制使用 jar
            val artifactType = Attribute.of("artifactType", String::class.java)
            // 直接设置 artifactType 为 jar，避免 src-directory 转换
            attributes {
                attribute(artifactType, "jar")
            }
        }
    }
}
