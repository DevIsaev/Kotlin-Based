plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.musicplayerbasics"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.musicplayerbasics"
        minSdk = 26
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures{
        viewBinding= true
        dataBinding=true
    }
}

dependencies {
    implementation("androidx.activity:activity-ktx:1.2.2")
    implementation("androidx.fragment:fragment-ktx:1.3.2")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity:1.8.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("androidx.media:media:1.7.0")
    implementation("com.github.bumptech.glide:glide:4.12.0")

    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
    implementation ("com.squareup.picasso:picasso:2.71828")
    implementation("com.google.code.gson:gson:2.8.6")

    implementation ("com.github.wseemann:FFmpegMediaMetadataRetriever-core:1.0.19")
    implementation ("com.github.wseemann:FFmpegMediaMetadataRetriever-native:1.0.19")


    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    implementation ("com.github.lukelorusso:VerticalSeekBar:1.2.7")

    implementation ("com.github.xeinebiu:android_audioeffects:1.4.2")

    implementation ("com.github.khaouitiabdelhakim:ProSoundEQ:1.1.0")

    implementation ("io.coil-kt:coil:2.6.0")

    implementation ("io.github.smartsensesolutions:SegmentedVerticalSeekBar:1.0.0")

    implementation ("com.github.qamarelsafadi:CurvedBottomNavigation:0.1.3")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
}