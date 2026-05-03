plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm)
}

dependencies {
    // NetworkMonitor Interface 에서 Flow를 쓰고 있으므로 명시적으로 선언
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.junit)
}