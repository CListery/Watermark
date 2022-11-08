import com.clistery.gradle.AppDependencies

plugins {
    id("kre-publish")
}

dependencies {
    api(AppDependencies.clistery.appbasic)
    AppDependencies.baseLibs.forEach { implementation(it) }
}
