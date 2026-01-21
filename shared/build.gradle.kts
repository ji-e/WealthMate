import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidTarget {
//        compilerOptions {
//            jvmTarget.set(JvmTarget.JVM_11)
//        }
    }

    // iOS 타겟 설정
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    )
    
    jvm()

    sourceSets {
        androidMain.dependencies {
            implementation(libs.sqldelight.android.driver)
            implementation(libs.koin.android)

            implementation(libs.androidx.compose.ui)
            implementation(libs.androidx.ui.graphics)

        }
        iosMain.dependencies {
            implementation(libs.sqldelight.native.driver)
        }
        commonMain.dependencies {
            implementation(libs.kotlinx.datetime)
            implementation(libs.sqldelight.coroutines.extensions)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.jie.wealthmate.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}

sqldelight {
    databases {
        create("WMDatabase") { // 생성될 데이터베이스 클래스의 이름
            packageName.set("com.jie.wealthmate.database") // 생성될 파일들의 패키지 경로
        }
    }
}
