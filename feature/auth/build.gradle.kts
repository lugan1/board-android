plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.example.auth"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
    }

}

dependencies {
    implementation(project(":core:ui"))
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(project(":core:navigation"))
    implementation(project(":core:domain"))
    // Android 코어(Context, Intent, Bundle 등)에 대해 ktx (코틀린 확장) 을 제공
    implementation(libs.androidx.core.ktx)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)

    implementation(libs.hilt.android)
    // hilt viewmodel 사용하려면 hilt navigation이 필요하다.
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.compiler)


    testImplementation(libs.junit)

    // ViewModel Unit 테스트
    testImplementation(libs.bundles.test.unit)

    // UI 및 안드로이드 테스트
    androidTestImplementation(libs.bundles.test.android.ui)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    debugImplementation(libs.bundles.compose.debug)
}