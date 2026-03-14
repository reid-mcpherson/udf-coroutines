plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.compiler)
}

android {
    compileSdk =
        libs.versions.compileSdk
            .get()
            .toInt()
    namespace = "com.composure.ui"
    defaultConfig {
        minSdk =
            libs.versions.minSdk
                .get()
                .toInt()
        consumerProguardFiles("consumer-rules.pro")
    }
    buildTypes { release { isMinifyEnabled = false } }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures { compose = true }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xexplicit-api=strict")
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8)
    }
}

dependencies {
    implementation(project(":arch"))
    implementation(project(":arch-android"))
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
}
