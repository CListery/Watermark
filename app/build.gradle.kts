import com.clistery.gradle.AppDependencies

plugins {
    id("app")
}

dependencies {
    AppDependencies.baseLibs.forEach { implementation(it) }
    
    implementation(project(mapOf("path" to ":lib_watermark")))
    implementation("io.github.clistery:watermark:1.3.1")

    // https://mvnrepository.com/artifact/org.permissionsdispatcher/permissionsdispatcher
    implementation(AppDependencies.org.permissionsdispatcher)
    // https://mvnrepository.com/artifact/org.permissionsdispatcher/permissionsdispatcher-processor
    kapt(AppDependencies.org.permissionsdispatcher_processor)
}
