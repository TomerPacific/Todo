plugins {
    id("com.google.protobuf") version "0.9.4"
    id("org.jetbrains.kotlin.android")
    id("com.android.application")
    id("kotlin-android-extensions")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.tomerpacific.todo"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.tomerpacific.todo"
        minSdk = 21
        targetSdk = 35
        versionCode = 11
        versionName = "2.4.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.0-alpha01"
    }
}

dependencies {

    implementation(libs.constraintlayout)
    implementation(libs.coordinatorlayout)
    implementation(libs.corektx)
    implementation(libs.compose.ui)
    implementation(libs.compose.material)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.activity.compose)
    implementation(libs.datastore)
    implementation(libs.protobuf.javalite)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.compose.material3)
    implementation(libs.appcompat)

    // Test dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.compose.ui.test.junit4)
    debugImplementation(libs.compose.ui.test.manifest)

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // Constraints
    constraints {
        implementation(libs.kotlin.stdlib.jdk7) {
            because("kotlin-stdlib-jdk7 is now part of kotlin-stdlib")
        }
        implementation(libs.kotlin.stdlib.jdk8) {
            because("kotlin-stdlib-jdk8 is now part of kotlin-stdlib")
        }
    }
}


protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.14.0"
    }

    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                maybeCreate("java").apply {
                    option("lite")
                }
            }
        }
    }
}