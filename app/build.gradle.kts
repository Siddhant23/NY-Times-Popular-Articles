import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hiltAndroid)
}

android {
    namespace = "com.test.android.siddhant"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.test.android.siddhant"
        targetSdk = 35
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
        viewBinding = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
}

dependencies {
    // AndroidX Libraries
    implementation(libs.appcompat)
    implementation(libs.recyclerView)
    implementation(libs.cardView)
    implementation(libs.activityKtx)
    implementation(libs.constraintLayout)
    implementation(libs.androidx.core)

    // Lifecycle and ViewModel
    implementation(libs.lifecycleViewModel)

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
