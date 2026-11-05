import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

// Read secrets from local.properties (git-ignored) with an env-var fallback for CI.
val nytApiKey: String =
    Properties().apply {
        val f = rootProject.file("local.properties")
        if (f.exists()) f.inputStream().use { load(it) }
    }.getProperty("NYT_API_KEY")
        ?: System.getenv("NYT_API_KEY")
        ?: ""

if (nytApiKey.isBlank()) {
    logger.warn("w: NYT_API_KEY is not set in local.properties or the environment; API calls will fail with HTTP 401.")
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hiltAndroid)
}

val mockitoAgent =
    configurations.create("mockitoAgent") {
        isCanBeConsumed = false
        isCanBeResolved = true
        isTransitive = false
    }

android {
    namespace = "com.test.android.siddhant"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.test.android.siddhant"
        targetSdk = 37
        minSdk = 24
        versionCode = 28
        versionName = "28.0"
        testInstrumentationRunner = "com.test.android.siddhant.HiltTestRunner"

        // BASE_URL is not a secret; API_KEY is read from local.properties / NYT_API_KEY env var.
        buildConfigField("String", "BASE_URL", "\"https://api.nytimes.com/svc/\"")
        buildConfigField("String", "API_KEY", "\"$nytApiKey\"")
    }

    buildTypes {
        getByName("debug") {
            isDebuggable = true
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

tasks.withType<Test>().configureEach {
    jvmArgumentProviders.add(
        CommandLineArgumentProvider {
            listOf("-javaagent:${mockitoAgent.singleFile.absolutePath}")
        },
    )
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

dependencies {
    implementation(libs.androidx.core)

    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.compose.material3)

    androidTestImplementation(libs.compose.ui.test.junit4)
    debugImplementation(libs.compose.ui.test.manifest)

    implementation(libs.activity.compose)
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)

    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.runtime.compose)

    implementation(libs.navigation3.runtime)
    implementation(libs.navigation3.ui)
    implementation(libs.navigationevent.compose)

    implementation(libs.kotlin.serialization.json)
    implementation(libs.coroutinesAndroid)

    implementation(libs.retrofit)
    implementation(libs.kotlinx.serialization.converter)
    implementation(libs.okhttp)
    implementation(libs.loggingInterceptor)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    androidTestImplementation(libs.androidxTestRunner)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.android.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.mockitoCore)
    testImplementation(libs.mockitoKotlin)
    mockitoAgent(libs.mockitoCore)
    testImplementation(libs.coroutinesTest)
    testImplementation(libs.hilt.android.testing)
    testImplementation(libs.compose.ui.test.junit4)
    kspTest(libs.hilt.android.compiler)
}
