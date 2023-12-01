plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.example.findaseat"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.findaseat"
        minSdk = 24
        targetSdk = 33
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

    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    viewBinding{
        enable = true
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-database:20.3.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation ("androidx.annotation:annotation:1.7.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation ("com.google.firebase:firebase-firestore:24.9.1")
    implementation ("com.google.firebase:firebase-core:21.1.1")

    testImplementation("org.robolectric:robolectric:4.7.3")
    testImplementation("androidx.arch.core:core-testing:2.2.0")

    implementation("com.squareup.picasso:picasso:2.8")


    // NEW FOR ESPRESSO //
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    debugImplementation("androidx.fragment:fragment-testing:1.6.2")

    testImplementation ("org.mockito:mockito-core:5.7.0")

}

//import java.util.*
//
//plugins {
//    id("com.android.application")
//    id("com.google.gms.google-services")
//}
//
//// Load the local.properties file
//val localProperties = Properties()
//val localPropertiesFile = rootProject.file("local.properties")
//if (localPropertiesFile.exists()) {
//    localProperties.load(localPropertiesFile.inputStream())
//}
//
//// Retrieve the Google Maps API key from local.properties
//val googleMapsApiKey = localProperties.getProperty("GOOGLE_MAPS_API_KEY") ?: "No API Key"
//
//android {
//    namespace = "com.example.findaseat"
//    compileSdk = 34
//
//    defaultConfig {
//        applicationId = "com.example.findaseat"
//        minSdk = 24
//        targetSdk = 33
//        versionCode = 1
//        versionName = "1.0"
//
//        // Add the Google Maps API key to the build config field
//
//        buildConfigField("String", "GOOGLE_MAPS_API_KEY", "\"$googleMapsApiKey\"")
//
//        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//    }
//
//    buildTypes {
//        getByName("release") {
//            isMinifyEnabled = false
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
//        }
//    }
//
//    buildFeatures {
//        viewBinding = true
//        buildConfig = true
//    }
//
//    compileOptions {
//        sourceCompatibility = JavaVersion.VERSION_1_8
//        targetCompatibility = JavaVersion.VERSION_1_8
//    }
//}
//
//dependencies {
//    implementation("androidx.appcompat:appcompat:1.6.1")
//    implementation("com.google.android.material:material:1.10.0")
//    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
//    implementation("com.google.firebase:firebase-database:20.3.0")
//    testImplementation("junit:junit:4.13.2")
//    androidTestImplementation("androidx.test.ext:junit:1.1.5")
//    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
//    implementation("androidx.annotation:annotation:1.7.0")
//    implementation("com.google.android.gms:play-services-maps:18.2.0")
//    implementation("com.google.firebase:firebase-firestore:24.9.1")
//    implementation("com.google.firebase:firebase-core:21.1.1")
//}

