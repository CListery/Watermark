import com.clistery.gradle.AppConfig
import com.clistery.gradle.AppDependencies
import com.clistery.gradle.implementation

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
}

android {
    compileSdkVersion(AppConfig.compileSdk)
    buildToolsVersion(AppConfig.buildToolsVersion)
    
    buildFeatures {
        viewBinding = true
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    defaultConfig {
        applicationId = "com.yh.demo"
        minSdkVersion(AppConfig.minSdk)
        targetSdkVersion(AppConfig.targetSdk)
        versionCode(AppConfig.versionCode)
        versionName(AppConfig.versionName)
    
        testInstrumentationRunner("androidx.test.runner.AndroidJUnitRunner")
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            // debuggable true
        }
    }
}

dependencies {
    implementation(AppDependencies.baseLibs)
    
    implementation(AppDependencies.clistery.appinject)
    implementation(project(mapOf("path" to ":lib_watermark")))
    
    // https://mvnrepository.com/artifact/org.permissionsdispatcher/permissionsdispatcher
    implementation(AppDependencies.org.permissionsdispatcher)
    // https://mvnrepository.com/artifact/org.permissionsdispatcher/permissionsdispatcher-processor
    kapt(AppDependencies.org.permissionsdispatcher_processor)
}
