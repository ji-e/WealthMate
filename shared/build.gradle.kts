plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    androidTarget()

    // iOS 타겟 설정
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    )

    jvm()

    sourceSets {
        androidMain.dependencies {
            implementation(libs.koin.android)

            implementation(libs.androidx.compose.ui)
            implementation(libs.androidx.ui.graphics)

            implementation(libs.androidx.room.sqlite.wrapper)

        }
        iosMain.dependencies {

        }
        commonMain.dependencies {
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)

            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)

            implementation(libs.uuid4)
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

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    with(libs.androidx.room.compiler) {
        add("kspAndroid", this)
        add("kspIosX64", this)
        add("kspIosArm64", this)
        add("kspIosSimulatorArm64", this)
    }
}

// 각 타겟의 스키마 복사 태스크가 서로 다른 경로를 바라보게 설정
tasks.withType<androidx.room.gradle.RoomSchemaCopyTask>().configureEach {
    val targetName = name.substringAfter("copyRoomSchemas").replaceFirstChar { it.lowercase() }
    schemaDirectory.set(file("$projectDir/schemas/$targetName"))
}