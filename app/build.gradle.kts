@file:Suppress("UnstableApiUsage")

import com.android.build.api.dsl.VariantDimension
import java.io.FileInputStream
import java.util.Properties

val localProperties = readLocalProperties()

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.android.navigation.safeargs)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.protobuf)
}

android {
    namespace = "com.ydanneg.erply"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ydanneg.erply"
        minSdk = 31
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        configureErplyClient()
        stringBuildConfig("ERPLY_CLIENT_CODE", localProperties.getProperty("ERPLY_CLIENT_CODE", ""))
        stringBuildConfig("ERPLY_USERNAME", localProperties.getProperty("ERPLY_USERNAME", ""))
        stringBuildConfig("ERPLY_PASSWORD", localProperties.getProperty("ERPLY_PASSWORD", ""))
    }
    buildTypes {
        debug {
            enableUnitTestCoverage = true
        }
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += "-Xjsr305=strict"
    }
    testOptions {
        unitTests {
            isReturnDefaultValues = true
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.5"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

kapt {
    correctErrorTypes = true
}

// Setup protobuf configuration, generating lite Java and Kotlin classes
protobuf {
    protoc {
        artifact = libs.protobuf.protoc.get().toString()
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                register("java") {
                    option("lite")
                }
                register("kotlin") {
                    option("lite")
                }
            }
        }
    }
}

dependencies {
    implementation(projects.api)

    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)

    // Material
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)

    // ProtoBuf
    implementation(libs.protobuf.kotlin.lite)

    // AndroidX
    implementation(libs.androidx.core)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.dataStore.core)
    implementation(libs.androidx.hilt.common)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)
    implementation(libs.androidx.paging.compose)

    // AndroidX Room
    implementation(libs.bundles.androidx.room)
    ksp(libs.androidx.room.compiler)

    // KotlinX
    implementation(libs.kotlinx.datetime)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    kapt(libs.androidx.hilt.compiler)

    // Navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    // Coil (async image loader)
    implementation(libs.coil.compose)

    // Logging
    implementation(libs.slf4j.android)

    // Local Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.hilt.android.testing)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.property)
    testImplementation(libs.mockk)
    testImplementation(libs.slf4j.simple)
    testImplementation(libs.androidx.paging.testing)
    testImplementation(libs.turbine)

    // Instrumented testing
    androidTestImplementation(libs.kotlin.test)
    androidTestImplementation(libs.kotest.assertions.core)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(libs.slf4j.simple)
    androidTestImplementation(libs.androidx.paging.testing)
    androidTestImplementation(libs.turbine)

    // Dev tools
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)
}

ksp {
    // The schemas directory contains a schema file for each version of the Room database.
    // This is required to enable Room auto migrations.
    // See https://developer.android.com/reference/kotlin/androidx/room/AutoMigration.
    arg(RoomSchemaArgProvider(File(projectDir, "schemas")))
}

/**
 * https://issuetracker.google.com/issues/132245929
 * [Export schemas](https://developer.android.com/training/data-storage/room/migrating-db-versions#export-schemas)
 */
class RoomSchemaArgProvider(
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    val schemaDir: File,
) : CommandLineArgumentProvider {
    override fun asArguments() = listOf("room.schemaLocation=${schemaDir.path}")
}

fun VariantDimension.configureErplyClient() {
    stringBuildConfig("CLIENT_USER_AGENT", "com.ydanneg.erply")
    stringBuildConfig("CLIENT_PIM_BASE_URL", "https://api-pim-eu10.erply.com")
    stringBuildConfig("CLIENT_CDN_BASE_URL", "https://cdn-sb.erply.com")
    clientLogLevelConfig(ClientLogLevels.ALL)
    intBuildConfig("CLIENT_CONNECT_TIMEOUT_SECONDS", 10)
    intBuildConfig("CLIENT_READ_TIMEOUT_SECONDS", 60)
    intBuildConfig("CLIENT_WRITE_TIMEOUT_SECONDS", 30)
}

enum class ClientLogLevels {
    ALL, HEADERS, BODY, INFO, NONE
}

fun VariantDimension.clientLogLevelConfig(level: ClientLogLevels) {
    stringBuildConfig("CLIENT_LOG_LEVEL", level.name)
}

object BuildConfigTypes {
    const val BOOLEAN = "boolean"
    const val STRING = "String"
    const val INT = "int"
}

object BuildConfigValues {
    const val TRUE = "true"
    const val FALSE = "false"
}

fun VariantDimension.boolBuildConfig(name: String, value: Boolean) {
    buildConfigField(BuildConfigTypes.BOOLEAN, name, if (value) BuildConfigValues.TRUE else BuildConfigValues.FALSE)
}

fun VariantDimension.intBuildConfig(name: String, value: Int) {
    buildConfigField(BuildConfigTypes.INT, name, value.toString())
}

fun VariantDimension.longBuildConfig(name: String, value: Long) {
    buildConfigField(BuildConfigTypes.INT, name, value.toString())
}

fun VariantDimension.stringBuildConfig(name: String, value: String) {
    buildConfigField(BuildConfigTypes.STRING, name, value.quoted())
}

fun String.quoted(): String = "\"$this\""

fun readLocalProperties(): Properties =
    Properties().apply {
        val file = file("../local.properties")
        if (file.exists()) {
            load(FileInputStream(file))
        }
    }
