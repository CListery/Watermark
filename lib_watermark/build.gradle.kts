import com.clistery.gradle.AppDependencies

plugins {
    id("kre-publish")
}

dependencies {
    AppDependencies.baseLibs.forEach { implementation(it) }
    implementation(AppDependencies.clistery.appbasic)
}
