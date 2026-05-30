import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hiltAndroid)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "com.test.android.siddhant"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.test.android.siddhant"
        targetSdk = 36
        minSdk = 23
        versionCode = 27
        versionName = "27.0"
        testInstrumentationRunner = "com.test.android.siddhant.HiltTestRunner"

        // Use local.properties to avoid hardcoding secrets
        buildConfigField("String", "BASE_URL", "\"https://api.nytimes.com/svc/\"")
        buildConfigField("String", "API_KEY", "\"cpf0GcsMK5wpzsi0iz2gRntnNgTqmc0r\"")
    }

    buildTypes {
        getByName("debug") {
            isDebuggable = true
        }
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.lifecycleViewModel)
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

    androidTestImplementation(libs.espressoCore)
    androidTestImplementation(libs.espressoIntents)
    androidTestImplementation(libs.androidxTestRunner)
    androidTestImplementation(libs.androidxTestCore)
    androidTestImplementation(libs.junitKtx)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.android.compiler)

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
