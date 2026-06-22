plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

fun String.envOrProperty(): String? =
    providers.environmentVariable(this).orNull ?: providers.gradleProperty(this).orNull

android {
    namespace = "com.krzysztofcal.mileconverter"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.krzysztofcal.mileconverter"
        minSdk = 24
        targetSdk = 35
        versionCode = 4
        versionName = "1.0.3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            val keystorePath = "KEYSTORE_FILE".envOrProperty()

            if (!keystorePath.isNullOrBlank()) {
                storeFile = file(keystorePath)
                storePassword = "STORE_PASSWORD".envOrProperty()
                keyAlias = "KEY_ALIAS".envOrProperty()
                keyPassword = "KEY_PASSWORD".envOrProperty()
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            if (!"KEYSTORE_FILE".envOrProperty().isNullOrBlank()) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.06.00")

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.activity:activity-compose:1.9.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.3")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.3")
    implementation("com.google.android.material:material:1.12.0")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    testImplementation("junit:junit:4.13.2")
}
