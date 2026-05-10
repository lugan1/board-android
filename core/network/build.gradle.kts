import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}


val properties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    properties.load(localPropertiesFile.inputStream())
}

// Android의 buildConfigField로 String 타입의 값을 넘길 때는, 최종적으로 자동 생성되는 BuildConfig.java 코드 내부에서
// 해당 값이 "(큰따옴표)로 감싸져 있어야 문법 오류가 발생하지 않는다.
fun toJavaStringLiteral(value: String): String =
    "\"${value.replace("\\", "\\\\").replace("\"", "\\\"")}\""

val local = toJavaStringLiteral(
    properties.getProperty("BASE_URL_LOCAL") ?: "http://10.0.2.2:8080/"
)
val service = toJavaStringLiteral(
    properties.getProperty("BASE_URL_SERVICE") ?: ""
)

android {
    namespace = "com.example.network"
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

    buildFeatures {
        buildConfig = true
    }


    flavorDimensions += "env"
    productFlavors {
        create("local") {
            dimension = "env"
            buildConfigField("String", "BASE_URL", local)
        }
        create("service") {
            dimension = "env"
            buildConfigField("String", "BASE_URL", service)
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // 공통 Model
    implementation(project(":core:model"))
    // 공통 Result Dto
    implementation(project(":core:common"))
    // 토큰 저장소
    implementation(project(":core:datastore"))

    implementation(libs.datastore)
    implementation(libs.androidx.core.ktx)
    implementation(libs.bundles.network)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
}