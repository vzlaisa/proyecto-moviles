plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
}

android {
    namespace = "valenzuela.isabel.proyectofinalmoviles_253088_253301_241556"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "valenzuela.isabel.proyectofinalmoviles_253088_253301_241556"
        minSdk = 27
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)

    implementation("androidx.compose.material:material-icons-extended")

    implementation("androidx.datastore:datastore-preferences:1.2.1")
    implementation("io.coil-kt:coil-compose:2.6.0")

    implementation("androidx.room:room-runtime:2.8.4")
    implementation("androidx.room:room-common:2.8.4")
    implementation("androidx.room:room-ktx:2.8.4")
    implementation(libs.androidx.compose.foundation.layout)
    ksp("androidx.room:room-compiler:2.8.4")

    // Encriptación de contraseña
    implementation("org.mindrot:jbcrypt:0.4")

    // Biometrico
    implementation("androidx.biometric:biometric:1.2.0-alpha05")

    // Permisos de ubicación
    implementation("com.google.android.gms:play-services-location:21.2.0")

    // Nominatim para ubicación
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Para ver los logs
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}