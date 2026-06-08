import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

val apiKey = project.findProperty("API_KEY") as String? ?: ""

android {
    namespace = "com.facens.bibliotecagps"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.facens.BibliotecaGPS"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        // 2. Lógica para ler o arquivo local.properties
        val properties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            properties.load(localPropertiesFile.inputStream())
        }

        // Pega o valor da chave. Se não existir, retorna uma string vazia.
        val apiKey = properties.getProperty("API_KEY") ?: ""

        // 3. Cria a variável para o Java
        buildConfigField("String", "API_KEY", "\"$apiKey\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
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
}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    implementation(platform("com.google.firebase:firebase-bom:34.14.0"))
    implementation(libs.play.services.location)
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation(libs.firebase.firestore)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}