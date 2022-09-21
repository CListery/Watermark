import com.clistery.gradle.AppDependencies

plugins {
    id("app")
}

dependencies {
    AppDependencies.baseLibs.forEach { implementation(it) }
    
    implementation(AppDependencies.clistery.appbasic)
    implementation(project(mapOf("path" to ":lib_watermark")))
    
    // https://mvnrepository.com/artifact/org.permissionsdispatcher/permissionsdispatcher
    implementation(AppDependencies.org.permissionsdispatcher)
    // https://mvnrepository.com/artifact/org.permissionsdispatcher/permissionsdispatcher-processor
    kapt(AppDependencies.org.permissionsdispatcher_processor)
}
