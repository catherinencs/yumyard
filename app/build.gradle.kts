plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.yumyard"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.yumyard"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "YELP_API_KEY", "\"${project.findProperty("yelpApiKey")}\"")
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
        buildConfig = true // Enable custom BuildConfig fields
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-messaging") // Firebase Cloud Messaging
    implementation("com.google.android.gms:play-services-location:21.0.1") // Google Location Services
    implementation("com.google.android.libraries.places:places:3.1.0") // Google Places
    implementation("com.google.android.gms:play-services-maps:18.1.0") // Google Maps
    implementation("com.squareup.retrofit2:retrofit:2.9.0") // Retrofit for Yelp API
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // Retrofit GSON converter
    implementation ("com.squareup.okhttp3:okhttp:4.9.3")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.3")
    implementation("com.github.bumptech.glide:glide:4.15.1") // Glide for image loading
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))

    // TODO: Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    implementation("com.google.firebase:firebase-analytics")

    implementation("androidx.navigation:navigation-fragment-ktx:2.6.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.6.0")


    implementation ("androidx.preference:preference:1.2.0")
    implementation ("com.google.maps.android:android-maps-utils:2.3.0")
    implementation("com.google.maps:google-maps-services:2.2.0")

    implementation ("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation ("com.google.firebase:firebase-messaging:23.0.0")
}

// Apply the google-services plugin
apply(plugin = "com.google.gms.google-services")
