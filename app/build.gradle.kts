plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
}

android {
    namespace = "com.example.expensemanager.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.expensemanager.app"
        minSdk = 24
        targetSdk = 34
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
    kotlinOptions {
        jvmTarget = "11"
    }
    packagingOptions {
        resources {
            excludes += "META-INF/DEPENDENCIES"
        }
    }
}


dependencies {

    implementation("com.google.android.gms:play-services-ads:22.4.0")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    /*pi chart */
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    implementation("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")

    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    /*icon picker*/

    implementation ("com.maltaisn:icondialog:3.3.0")
    implementation ("com.maltaisn:iconpack-default:1.1.0")

    implementation("androidx.recyclerview:recyclerview:1.2.1")

    //noinspection RiskyLibrary
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")

    implementation("com.itextpdf:itext7-core:7.2.3")

    implementation("com.github.duanhong169:colorpicker:1.1.6")

    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.api-client:google-api-client-android:1.33.2")
    implementation("com.google.apis:google-api-services-drive:v3-rev136-1.25.0")
    implementation("com.google.api-client:google-api-client-gson:1.33.2")


}