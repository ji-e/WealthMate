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

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "shared"
        }
    }

    jvm()

    sourceSets {
        androidMain.dependencies {
            implementation(libs.koin.android)
            implementation(libs.androidx.compose.ui)
            implementation(libs.androidx.ui.graphics)
            implementation(libs.androidx.room.sqlite.wrapper)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.androidx.security.crypto)
            
            // 안드로이드 구글 로그인 SDK 의존성 명시적 추가
            implementation(libs.google.play.services.auth)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
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
            implementation(libs.bundles.ktor.common)
            implementation(libs.bundles.kmpAuth.common)
            implementation(libs.okio)
            implementation(libs.multiplatform.settings)
            implementation(libs.napier)
            implementation(libs.bundles.firebase.common)
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

tasks.withType<androidx.room.gradle.RoomSchemaCopyTask>().configureEach {
    val targetName = name.substringAfter("copyRoomSchemas").replaceFirstChar { it.lowercase() }
    schemaDirectory.set(file("$projectDir/schemas/$targetName"))
}
