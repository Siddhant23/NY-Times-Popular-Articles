import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hiltAndroid)
    id("org.jetbrains.kotlin.plugin.parcelize")
}

android {
    namespace = "com.test.android.siddhant"
    compileSdk = 36
    defaultConfig {
        applicationId = "com.test.android.siddhant"
        targetSdk = 36
        minSdk = 23
        versionCode = 25
        versionName = "25.0"
        testInstrumentationRunner = "com.test.android.siddhant.HiltTestRunner"
    }
    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", "\"https://api.nytimes.com/svc/\"")
            buildConfigField("String", "API_KEY", "\"cpf0GcsMK5wpzsi0iz2gRntnNgTqmc0r\"")
        }
        release {
            buildConfigField("String", "BASE_URL", "\"https://api.nytimes.com/svc/\"")
            buildConfigField("String", "API_KEY", "\"cpf0GcsMK5wpzsi0iz2gRntnNgTqmc0r\"")
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    // Core AndroidX
    implementation(libs.androidx.core)

    // Compose BOM — keeps all Compose versions in sync
    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Compose UI
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)
    implementation(libs.compose.material.icons.extended)
    implementation("com.google.android.material:material:1.12.0")

    // Material 3
    implementation(libs.compose.material3)

    // Compose UI testing
    androidTestImplementation(libs.compose.ui.test.junit4)
    debugImplementation(libs.compose.ui.test.manifest)

    // Activity Compose
    implementation(libs.activity.compose)
    implementation("androidx.hilt:hilt-navigation-compose:1.3.0")

    // Lifecycle + ViewModel Compose
    implementation(libs.lifecycleViewModel)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.runtime.compose)

    // Navigation 3
    implementation(libs.navigation3.runtime)
    implementation(libs.navigation3.ui)
    implementation(libs.navigationevent.compose)

    // Kotlin Serialization (for NavKey routes)
    implementation(libs.kotlin.serialization.json)

    // Coroutines
    implementation(libs.coroutinesAndroid)

    // Retrofit & OkHttp
    implementation(libs.retrofit)
    implementation(libs.gsonConverter)
    implementation(libs.okhttp)
    implementation(libs.loggingInterceptor)

    // Hilt Dependency Injection
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Testing Libraries
    androidTestImplementation(libs.espressoCore)
    androidTestImplementation(libs.espressoIntents)
    androidTestImplementation(libs.androidxTestRunner)
    androidTestImplementation(libs.androidxTestCore)
    androidTestImplementation(libs.junitKtx)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.android.compiler)

    // Local Unit Tests
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.mockitoCore)
    testImplementation(libs.mockitoKotlin)
    testImplementation(libs.mockitoInline)
    testImplementation(libs.core)
    testImplementation(libs.coroutinesTest)
    testImplementation(libs.hilt.android.testing)
    kspTest(libs.hilt.android.compiler)
}
